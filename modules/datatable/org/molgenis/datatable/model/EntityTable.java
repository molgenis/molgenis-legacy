package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
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
	private List<Field> columns;

	// current query rules (includes all: limit, offset, paging, filters)
	private List<QueryRule> filters = new ArrayList<QueryRule>();

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
		if (columns != null) return columns;

		try
		{
			columns = db.getMetaData().getEntity(entityClass.getSimpleName()).getAllFields();
			return columns;
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
			List<? extends Entity> entities;
			if (filters.size() > 0) entities = db.find(entityClass, filters.toArray(new QueryRule[filters.size()]));
			else
				entities = db.find(entityClass);

			List<Tuple> result = new ArrayList<Tuple>();
			for (Entity e : entities)

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
		// should be optimized
		return this.getRows().iterator();
	}

	@Override
	public void close() throws TableException
	{
	}

	@Override
	public int getRowCount() throws TableException
	{
		// should get rid of limit clause!
		try
		{
			if (filters.size() > 0) return db.count(entityClass, filters.toArray(new QueryRule[filters.size()]));
			else
				return db.count(entityClass);
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}

	public List<QueryRule> getFilters()
	{
		return filters;
	}

	public Database getDb()
	{
		return db;
	}

	public void setDb(Database db)
	{
		this.db = db;
	}

	@Override
	public void setFilters(List<QueryRule> filters)
	{
		assert (filters != null);
		this.filters = filters;
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		// TODO remove previous limit/offset
		List<QueryRule> newFilters = new ArrayList<QueryRule>();
		for (QueryRule r : this.filters)
		{
			if (!Operator.LIMIT.equals(r.getOperator()) && !Operator.OFFSET.equals(r.getOperator()))
			{
				newFilters.add(r);
			}
		}
		this.filters = newFilters;

		this.filters.add(new QueryRule(Operator.LIMIT, limit));
		this.filters.add(new QueryRule(Operator.OFFSET, offset));
	}

}
