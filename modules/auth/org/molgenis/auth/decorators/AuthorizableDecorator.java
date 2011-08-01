/* Date:        July 29, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.auth.decorators;

import java.lang.reflect.Method;
import java.util.List;

import org.molgenis.auth.MolgenisRole;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.organization.Investigation;

// TODO: get rid of 'extends Investigation' but how??
public class AuthorizableDecorator<E extends Investigation> extends MappingDecorator<E>
{
	// JDBCMapper is the generate thing
	// public AuthorizableDecorator(JDBCMapper generatedMapper)
	// {
	// super(generatedMapper);
	// }

	// Mapper is the generate thing
	public AuthorizableDecorator(Mapper generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here

		// Set owner to 'admin' if not set by user
		for (E e : entities)
		{
			try
			{
				Class entityClass = e.getClass();
				Method getOwns = entityClass.getDeclaredMethod("getOwns");
				Class partypes[] = new Class[1];
				partypes[0] = Integer.TYPE;
				Method setOwns = entityClass.getDeclaredMethod("setOwns_Id", partypes);
				Object result = getOwns.invoke(e);
				if (result == null)
				{
					// This is how it should become:
					// Investigation inv = getDatabase()
					// .getEntityManager()
					// .createQuery("SELECT i FROM Investigation i WHERE i.name = 'admin'",
					// Investigation.class)
					// .getSingleResult();

					int adminId = getDatabase()
							.find(MolgenisRole.class, new QueryRule(MolgenisRole.NAME, Operator.EQUALS, "admin"))
							.get(0).getId();
					// e.setOwns_Id(adminId);
					Object arglist[] = new Object[1];
					arglist[0] = adminId;
					setOwns.invoke(e, arglist);
				}
			}
			catch (Exception e1)
			{
				throw new DatabaseException(e1);
			}
		}

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
		// for (org.molgenis.organization.Investigation e : entities)
		// {
		// e.setTriggeredField("Before update called!!!");
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
		// if(true) throw new
		// SQLException("Because of a post trigger the remove is cancelled.");

		return count;
	}
}
