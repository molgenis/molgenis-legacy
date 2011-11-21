/*
 * Date: March 25, 2011 Template: MapperDecoratorGen.java.ftl generator:
 * org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.scrum.decorators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.scrum.Task;
import org.molgenis.scrum.TaskHistory;

public class TaskDecorator<E extends org.molgenis.scrum.Task> extends MappingDecorator<E>
{
	// JDBCMapper is the generate thing
	public TaskDecorator(Mapper generatedMapper)
	{
		super(generatedMapper);
	}

	private void addCopyToHistory(List<E> entities) throws DatabaseException
	{
		this.addCopyToHistory(entities, false);
	}
	
	private void addCopyToHistoryRemoved(List<E> entities) throws DatabaseException
	{
		this.addCopyToHistory(entities, true);
	}

	private void addCopyToHistory(List<E> entities, boolean markAsRemoved)
			throws DatabaseException
	{
		try
		{
			// create a copy in the log
			List<TaskHistory> history = new ArrayList<TaskHistory>();
			for (org.molgenis.scrum.Task task : entities)
			{
				TaskHistory h = new TaskHistory();
				// todo need copy constructor

				h.set(task.getValues());
				h.setHistoryForTask(task.getId());
				h.setChangedOn(task.getChangedOn());
				if (markAsRemoved) h.setStatus("removed");
				history.add(h);

			}
			this.getDatabase().add(history);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		for(Task t: entities) t.setOwner(this.getDatabase().getSecurity().getUserId());
		
		int count = super.add(entities);

		addCopyToHistory(entities);

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		for(Task t: entities)
		{
			t.setOwner(this.getDatabase().getSecurity().getUserId());
		}
		
		// here we call the standard 'update'
		int count = super.update(entities);

		addCopyToHistory(entities);

		return count;
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		for(Task t: entities) t.setOwner(this.getDatabase().getSecurity().getUserId());

		// here we call the standard 'remove'
		int count = super.remove(entities);

		addCopyToHistoryRemoved(entities);

		return count;
	}

}
