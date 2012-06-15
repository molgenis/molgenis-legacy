package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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
	private final LinkedHashMap<String, Field> columnsByName;
	private final List<Field> columns;
	
	public QueryTable(SQLQueryImpl query, LinkedHashMap<String, SimpleExpression<? extends Object>> select, List<Field> columns) {
		this.query = query;
		this.select = select;
		
		this.columns = columns;
		this.columnsByName = new LinkedHashMap<String, Field>();
		for(Field col : columns) {
			this.columnsByName.put(col.getName(), col);
		}
	}
	
	@Override
	public List<Field> getColumns() throws TableException
	{
		return columns;
	}
	
	public Field getColumn(String name) {
		return columnsByName.get(name);
	}
	
	@Override
	public List<Tuple> getRows() throws TableException
	{
		final List<Tuple> tuples = new ArrayList<Tuple>();
		
		
		int valSize = select.values().size();
		Expression<?>[] selectArray = new Expression<?>[valSize];
		int idx = 0;
		for (Iterator<SimpleExpression<? extends Object>> iterator = select.values().iterator(); iterator.hasNext();)
		{			
			selectArray[idx] = iterator.next();
			++idx;
		}
		
		final List<Object[]> rows = query.list(selectArray);
		final ArrayList<Field> cols = new ArrayList<Field>(columns);
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
