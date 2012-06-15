package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.SimpleExpression;

public class QueryTable implements TupleTable
{
	private final SQLQueryImpl query;
	private final LinkedHashMap<String, SimpleExpression<? extends Object>> select;
	private final LinkedHashMap<String, Field> columns;
	
	public QueryTable(SQLQueryImpl query, LinkedHashMap<String, SimpleExpression<? extends Object>> select, List<Field> columns) {
		this.query = query;
		this.select = select;
		
		this.columns = new LinkedHashMap<String, Field>();
		for(Field col : columns) {
			this.columns.put(col.getName(), col);
		}
	}
	
	@Override
	public List<Field> getColumns() throws TableException
	{
		return new ArrayList<Field>(columns.values());
	}
	
	public Field getColumn(String name) {
		return columns.get(name);
	}
	
	@Override
	public List<Tuple> getRows() throws TableException
	{
		final List<Tuple> tuples = new ArrayList<Tuple>();
		
		
		int valSize = select.values().size();
		Expression<?>[] selectArray = new Expression<?>[valSize];
		int idx = 0;
		for (Iterator iterator = select.values().iterator(); iterator.hasNext();)
		{			
			selectArray[idx] = (Expression<?>) iterator.next();
			++idx;
		}
		
		final List<Object[]> rows = query.list(selectArray);
		final ArrayList<Field> cols = new ArrayList<Field>(columns.values());
		for(final Object[] obj : rows) {
			
			final SimpleTuple row = new SimpleTuple();
			for(int i = 0; i < cols.size(); ++i) {
				row.set(cols.get(i).getName(), obj[i]);
			}			
			tuples.add(row);
		}
		return tuples;
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
	public int getRowCount() throws TableException
	{
		return (int)query.count();
	}

	@Override
	public void close() throws TableException
	{
	}

	@Override
	public void setQueryRules(List<QueryRule> rules)
	{
		
	}

	@Override
	public List<QueryRule> getFilters()
	{
		return Collections.emptyList();
	}


	public SQLQuery getQuery()
	{
		return query;
	}
	
	public LinkedHashMap<String, SimpleExpression<? extends Object>> getSelect() {
		return select;
	}
	
}
