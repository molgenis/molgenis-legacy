/* Date:        May 8, 2012
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.ngs.decorators;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;

public class FlowcellDecorator<E extends org.molgenis.ngs.Flowcell> extends MapperDecorator<E>
{
	//Mapper is the generate thing
	public FlowcellDecorator(Mapper generatedMapper)
	{
		super(generatedMapper);
	}

	private void fourDigits(List<E> entities) {
		
		
		for (org.molgenis.ngs.Flowcell e : entities)
		{
			String run = e.getRun();
			for (int i = 0; i < 4 - e.getRun().length(); i++)
				run = "0" + run;
			
			e.setRun(run);
		}
		
	}
	
	public int add(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here, e.g.
		// for (org.molgenis.ngs.Flowcell e : entities)
		// {
		//  	e.setTriggeredField("Before add called!!!");
		// }

		fourDigits(entities);
		
		// here we call the standard 'add'
		int count = super.add(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	
	public int update(List<E> entities) throws DatabaseException
	{

		// add your pre-processing here, e.g.
		// for (org.molgenis.ngs.Flowcell e : entities)
		// {
		// 		e.setTriggeredField("Before update called!!!");
		// }

		fourDigits(entities);
		
		// here we call the standard 'update'
		int count = super.update(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	
	public int remove(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here

		// here we call the standard 'remove'
		int count = super.remove(entities);

		// add your post-processing here, e.g.
		// if(true) throw new SQLException("Because of a post trigger the remove is cancelled.");

		return count;
	}
}

