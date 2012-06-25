package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Wrap an Entity (that is stored in a database) into a TupleTable
 */
public class EntityTable extends AbstractFilterableTupleTable
{
	// database connection
	private Database db;

	// class to query
	private Class<? extends Entity> entityClass;

	// copy of the fields from meta database
	private List<Field> fields;
	
	/**
	 * Constructor
	 * 
	 * @param database
	 *            containing the entity
	 * @param entityClass
	 *            class of entities to query
	 */
	public EntityTable(final Database database, final Class<? extends Entity> entityClass)
	{
		super();
		if(database == null) {
			throw new NullPointerException("database can't be null");
		}
		if(entityClass == null) {
			throw new NullPointerException("entityClass can't be null");
		}

		this.db = database;
		this.entityClass = entityClass;
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		if (fields != null) {
			return fields;
		}
		try
		{
			fields = db.getMetaData().getEntity(entityClass.getSimpleName()).getAllFields();
			return fields;
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public List<Tuple> getRows()
	{
		try
		{
			final List<? extends Entity> entities = rules.isEmpty() ?
				db.find(entityClass) :
				db.find(entityClass, rules.toArray(new QueryRule[rules.size()]));
			
			final List<Tuple> result = new ArrayList<Tuple>();
			for (final Entity e : entities)
			{
				final Tuple t = new SimpleTuple();
				for (Field f : getColumns())
				{
					t.set(f.getName(), e.get(f.getName()));
				}
				result.add(t);
			}
			return result;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		//should be optimized
		return this.getRows().iterator();
	}

	@Override
	public void close() throws TableException
	{
	}

	@Override
	public int getRowCount() throws TableException
	{
		try
		{
			return rules.isEmpty() ?
				db.count(entityClass):
				db.count(entityClass, rules.toArray(new QueryRule[rules.size()]));
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}
}
