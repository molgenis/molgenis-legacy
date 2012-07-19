package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.SimpleExpression;

/**
 * A class to wrap a specific {@link SQLQuery} in a TupleTable. 
 */
public class QueryTable implements TupleTable
{
	private final SQLQueryImpl query;
	private final LinkedHashMap<String, SimpleExpression<? extends Object>> select;
	private final LinkedHashMap<String, Field> columnsByName;
	private final List<Field> columns;

	public QueryTable(SQLQueryImpl query, LinkedHashMap<String, SimpleExpression<? extends Object>> select,
			List<Field> columns)
	{
		this.query = query;
		this.select = select;

		this.columns = columns;
		this.columnsByName = new LinkedHashMap<String, Field>();
		for (Field col : columns)
		{
			this.columnsByName.put(col.getSqlName(), col);
		}
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		return columns;
	}

	public Field getColumnByName(String name)
	{
		return columnsByName.get(name);
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		final List<Tuple> tuples = new ArrayList<Tuple>();

		final Expression<?>[] selectArray = getSelectAsArray();

		final List<Object[]> rows = query.list(selectArray);
		final ArrayList<Field> cols = new ArrayList<Field>(columns);
		for (final Object[] obj : rows)
		{
			final SimpleTuple row = new SimpleTuple();
			for (int i = 0; i < selectArray.length; ++i)
			{
				row.set(cols.get(i).getSqlName(), obj[i]);
			}
			tuples.add(row);
		}
		return tuples;
	}

	/**
	 * Convert the {@link Collection} of values in the select field to an array of {@link Expression}s,
	 * for use in the {@link SQLQuery.list()} function.
	 * @return The array of expressions.
	 */
	private Expression<?>[] getSelectAsArray()
	{
		final Collection<SimpleExpression<?>> values = select.values();
		int valSize = values.size();
		final Expression<?>[] selectArray = new Expression<?>[valSize];
		int idx = 0;
		for (Iterator<SimpleExpression<? extends Object>> iterator = values.iterator(); iterator.hasNext();)
		{
			selectArray[idx] = iterator.next();
			++idx;
		}
		return selectArray;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			return getRows().iterator();
		}
		catch (TableException e)
		{
			throw new RuntimeException("Problem in QueryTable:iterator()");
		}
	}

	@Override
	public int getCount() throws TableException
	{
		return (int) query.count();
	}

	@Override
	public void close() throws TableException
	{
	}

	public SQLQuery getQuery()
	{
		return query;
	}

	public LinkedHashMap<String, SimpleExpression<? extends Object>> getSelect()
	{
		return select;
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLimit(int limit)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOffset(int offset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColCount() throws TableException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColLimit(int limit)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColOffset(int offset)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDb(Database db)
	{
		// TODO Auto-generated method stub
		
	}
}