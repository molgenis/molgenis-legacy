/*
 * Date: March 25, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.scrum.plugins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.scrum.Sprint;
import org.molgenis.scrum.Story;
import org.molgenis.scrum.TaskHistory;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class BurndownChart extends PluginModel<Entity>
{
	List<TaskHistory> taskHistory = new ArrayList<TaskHistory>();
	List<Double> burndown = new ArrayList<Double>();
	List<Double> unplanned = new ArrayList<Double>();
	List<String> dates = new ArrayList<String>();

	public BurndownChart(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_scrum_plugins_BurndownChart";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/scrum/plugins/BurndownChart.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// no edits
	}
	
	private Sprint getSelectedSprint() {
		ScreenController parent = this.getParent().getParent();
		FormModel<Sprint> form = (FormModel<Sprint>) parent.getModel();
		List<Sprint> sprintList = form.getRecords();
		return sprintList.get(0);
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			this.burndown.clear();
			this.unplanned.clear();
			this.dates.clear();
			this.taskHistory.clear();
			
			// load all stories for this sprint
			Sprint sprint = getSelectedSprint();
			List<Story> stories = db.query(Story.class).eq(Story.SPRINT, sprint.getId()).find();

			// load the story ids
			List<Integer> storyIds = new ArrayList<Integer>();
			for (Story story : stories) {
				storyIds.add(story.getId());
			}

			// get the current history
			taskHistory = db.query(TaskHistory.class).in(TaskHistory.STORY, storyIds)
					.sortASC(TaskHistory.CHANGEDON).find();
			
			if(taskHistory.size() == 0) throw new Exception("no task history known for this sprint (did it already start?)");
			
			// check sprint start and end date
			Calendar start = Calendar.getInstance();
			start.setTime(sprint.getStartOfSprint());
			start.set(Calendar.HOUR, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND,0);
			
			Calendar end = Calendar.getInstance();
			end.setTime(sprint.getEndOfSprint());
			end.set(Calendar.HOUR, 0);
			end.set(Calendar.MINUTE, 0);
			end.set(Calendar.SECOND,0);
			end.add(Calendar.DATE, 1); //so it actually ends at 0:00:00 next day.
			
			if (end.before(start) ) throw new Exception("start of sprint data is later than end of sprint date");
			
			//we use a calendar to iterate through the days (we count each day by 23:59:59 hours)
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.setTime(start.getTime());
			calendar.set(Calendar.HOUR, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND,59);
			
			//per day, we get for each tasks the (last known) state and then count
			
			//the board is a map of taskId and its details
			Map<Integer,TaskHistory> currentBoard = new LinkedHashMap<Integer,TaskHistory>();
			//we manage unplanned separately
			Map<Integer,TaskHistory> currentUnplanned = new LinkedHashMap<Integer,TaskHistory>();
			
			//first fill the board for start of sprint
			Date firstScrumDay = calendar.getTime();
			int historyIndex = 0;
			TaskHistory currentHistory = taskHistory.get(historyIndex);
			while (currentHistory.getChangedOn().equals(firstScrumDay) && historyIndex < taskHistory.size() - 1)
			{
				currentBoard.put(currentHistory.getHistoryForTask_Id(), currentHistory);
				currentHistory = taskHistory.get(++historyIndex);
			}
			DateFormat df = new SimpleDateFormat("dd/MMM");
			this.burndown.add(storyPointCount(currentBoard, false));
			this.unplanned.add(0.0);
			this.dates.add(df.format(calendar.getTime()));
			
			//then iterate through the days until end of sprint OR today, update board and check for unplanned
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR, 23);
			now.set(Calendar.MINUTE, 59);
			now.set(Calendar.SECOND,59);
			//if now is weekend, change it to next monday
			if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) now.add(Calendar.DATE,2);
			if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) now.add(Calendar.DATE,2);
			
			while(calendar.getTime().before(end.getTime()) )
			{
				calendar.add(Calendar.DATE, 1);
				Date scrumDay = calendar.getTime();
				
				//get the board updated for the current day
				while (currentHistory != null && (currentHistory.getChangedOn().before(scrumDay) || currentHistory.getChangedOn().equals(scrumDay)))
				{
					//scan for new/unplanned tickets
					if( !currentBoard.containsKey(currentHistory.getHistoryForTask_Id()) || 
							currentUnplanned.containsKey(currentHistory.getHistoryForTask_Id()) )
					{
						currentUnplanned.put(currentHistory.getHistoryForTask_Id(), currentHistory);
					}
					//put current tickets on the board
					currentBoard.put(currentHistory.getHistoryForTask_Id(), currentHistory);
					
					if(historyIndex < taskHistory.size())
						currentHistory = taskHistory.get(historyIndex++);
					else
						currentHistory = null;
				}
				
				//we don't scrum on weekends, unless it is today, otherwise update the count
				if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				{
					//update the currentBoard list
					if(calendar.getTime().before(now.getTime()) || calendar.getTime().equals(now.getTime()))
					{
						this.burndown.add(storyPointCount(currentBoard, false));
						this.unplanned.add(storyPointCount(currentUnplanned, true));
					}
					this.dates.add(df.format(calendar.getTime()));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}

	}

	private Double storyPointCount(Map<Integer, TaskHistory> tasks, boolean all)
	{
		Double count = 0.0;
		for (TaskHistory t : tasks.values())
		{
			if (all || !(t.getStatus().equals("done") || t.getStatus().equals("removed")))
			{
				count += t.getStoryPoints();
			}
		}
		return count;
	}
	
	//getter to freemarker
	public String getBurndownJSON()
	{
		//[25,19,15,17,13];
		String result = "";
		for(int i = 0; i < this.burndown.size(); i++ )
		{
			if(i != 0) result += ",";
			result += this.burndown.get(i);
		}
		return "["+result+"]";
	}
	public String getUnplannedJSON()
	{
		String result = "";
		for(int i = 0; i < this.unplanned.size(); i++ )
		{
			if(i != 0) result += ",";
			result += this.unplanned.get(i);
		}
		return "["+result+"]";
	}
	public String getDaysJSON()
	{
		String result = "";
		for(int i = 0; i < this.dates.size(); i++ )
		{
			if(i != 0) result += ",";
			result += "'"+this.dates.get(i)+"'";
		}
		return "["+result+"]";
	}
}
