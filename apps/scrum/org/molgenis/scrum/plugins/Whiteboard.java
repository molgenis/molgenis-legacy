/*
 * Date: March 25, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.scrum.plugins;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.scrum.Sprint;
import org.molgenis.scrum.Story;
import org.molgenis.scrum.Task;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class Whiteboard extends PluginModel<Entity>
{
	private List<Story> stories = new ArrayList<Story>();
	private List<Task> tasks = new ArrayList<Task>();
	private Task taskEdit = null;
	private Story storyEdit = null;

	public Whiteboard(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\"res/css/org.molgenis.scrum.css\">";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_scrum_plugins_Whiteboard";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/scrum/plugins/Whiteboard.ftl";
	}
	
	private Sprint getSelectedSprint() {
		ScreenController parent = this.getParent().getParent();
		FormModel<Sprint> form = (FormModel<Sprint>) parent.getModel();
		List<Sprint> sprintList = form.getRecords();
		return sprintList.get(0);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getAction();
			Task t = db.findById(Task.class, request.getInt("__task"));

			if ("taskCheckout".equals(action))
			{
				t.setStatus("checked");
				t.setChangedOn(new Date());
				db.update(t);
			}
			else if ("taskDone".equals(action))
			{
				t.setStatus("done");
				t.setChangedOn(new Date());				
				db.update(t);
			}
			else if ("taskBack".equals(action))
			{
				t.setStatus("scheduled");
				t.setChangedOn(new Date());				
				db.update(t);
			}
			else if ("taskEdit".equals(action))
			{
				this.taskEdit = t;
			}
			else if ("taskSave".equals(action))
			{
				t.setDescription(request.getString("description"));
				t.setStoryPoints(request.getDouble("storyPoints"));
				t.setChangedOn(new Date());				
				db.update(t);
				this.taskEdit = null;
			}
			else if ("taskCancel".equals(action))
			{
				this.taskEdit = null;
			}
			else if ("taskDelete".equals(action))
			{
				t.setStatus("removed");
				t.setChangedOn(new Date());
				db.update(t);
				this.taskEdit = null;
			}
			else if ("taskNew".equals(action))
			{
				Task task = new Task();
				task.setDescription("please edit");
				task.setStory(request.getInt("__story"));
				task.setChangedOn(new Date());
				
				db.add(task);
				this.taskEdit = task;
			}
			else if("storyEdit".equals(action))
			{
				this.storyEdit = db.findById(Story.class, request.getInt("__story"));
			}
			else if ("storySave".equals(action))
			{
				Story story = db.findById(Story.class, request.getInt("__story"));
				story.setName(request.getString(Story.NAME));
				story.setImportance(request.getString(Story.IMPORTANCE));
				story.setHowToDemo(request.getString(Story.HOWTODEMO));
				story.setLinkToDemo(request.getString(Story.LINKTODEMO));			
				db.update(story);
				this.storyEdit = null;
			}
			else if ("storyCancel".equals(action))
			{
				this.storyEdit = null;
			}
			else if ("storyDelete".equals(action))
			{
				Story story = db.findById(Story.class, request.getInt("__story"));
				db.remove(story);
				this.storyEdit = null;
			}
			else if ("storyNew".equals(action))
			{
				Story story = new Story();
				story.setName("please edit");
				story.setHowToDemo("how to demo?");
				story.setSprint(getSelectedSprint().getId());
				db.add(story);
				this.storyEdit = story;
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.setMessages(new ScreenMessage("" + e.getMessage(), false));
		}

	}

	@Override
	public void reload(Database db)
	{
		try
		{
			// load all stories for this sprint
			stories = db.query(Story.class).eq("sprint", getSelectedSprint().getId()).sortDESC(Story.IMPORTANCE).find();

			// load all tasks for all stories in this sprint
			List<Integer> storyIds = new ArrayList<Integer>();
			for (Story story : stories)
				storyIds.add(story.getId());
			tasks = db.query(Task.class).in("Story", storyIds).find();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.setMessages(new ScreenMessage("" + e.getMessage(), false));
		}
	}
	
	// summing methods
	public double countSp(String status)
	{
		double result = 0;
		for(Task t: this.tasks)
		{
			if(t.getStatus().equals(status))
			{
				result += t.getStoryPoints();
			}
		}
		return result;
	}
	
	public double countSp(Story story)
	{
		double result = 0;
		for(Task t: this.tasks)
		{
			if(t.getStory_Id().equals(story.getId()))
			{
				result += t.getStoryPoints();
			}
		}
		return result;
	}
	
	// Getters for the view.
	public List<Story> getStories()
	{
		return stories;
	}

	public List<Task> getTasks()
	{
		return tasks;
	}

	public Task getTaskEdit()
	{
		return taskEdit;
	}
	
	public Story getStoryEdit()
	{
		return storyEdit;
	}
}
