/* Date:        December 13, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.MapperDecorator;

public class DBIndexUpdateDecorator<E extends org.molgenis.bbmri.Biobank> extends MapperDecorator<E>
{
	//JDBCMapper is the generate thing
	//TODO: Danny Parameterize the JDBCMapper object <Object> ??
	public DBIndexUpdateDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		/**
		 * On each DB update add the records in Lucene Index. 
		 */
		// add your pre-processing here, e.g.
		// for (org.molgenis.bbmri.Biobank e : entities)
		// {
		//  	e.setTriggeredField("Before add called!!!");
		// }

		//call AddDocument() from DBIndexPlugin.java
		System.out.println("**************Coming from DBIndexUpdateDecorator");
		System.out.println(entities);

		//plugins.LuceneIndex.AdminIndexes.updateIndex(entities);

		
		//disabled because path breaks on server.
		//plugins.LuceneIndex.AdminIndexes.updateIndex(entities);

		// here we call the standard 'add'
		int count = super.add(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{

		// add your pre-processing here, e.g.
		// for (org.molgenis.bbmri.Biobank e : entities)
		// {
		// 		e.setTriggeredField("Before update called!!!");
		// }

		// here we call the standard 'update'
		int count = super.update(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
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

