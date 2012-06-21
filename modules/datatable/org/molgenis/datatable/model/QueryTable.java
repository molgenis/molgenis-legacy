package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
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

	public Field getColumn(String name)
	{
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

	public String getDynaTreeNodes()
	{
		// K = tableName, V = Field
		final ImmutableListMultimap<String, Field> fieldsByTable = Multimaps.index(columns,
				new Function<Field, String>()
				{
					@Override
					public String apply(Field field)
					{
						if (StringUtils.isEmpty(field.getTableName()))
						{
							if(StringUtils.contains(field.getName(), ".")) {
								return StringUtils.substringBefore(field.getName(), ".");	
							} else {
								return "Other";
							}
						}
						return field.getTableName();
					}
				});

		final Map<String, String> rs = new LinkedHashMap<String, String>();
		for (final String tableName : fieldsByTable.keys())
		{
			final StringBuilder tableNode = new StringBuilder();
			final ImmutableList<Field> fieldByTable = fieldsByTable.get(tableName);
			tableNode.append("{");
			tableNode.append(String.format("title : \"%s\", ", tableName));
			if (CollectionUtils.isNotEmpty(fieldByTable))
			{
				tableNode.append("isFolder: true,");
				tableNode.append(String.format("children: [%s]",
						StringUtils.join(CollectionUtils.collect(fieldByTable, new Transformer()
						{
							@Override
							public Object transform(Object arg0)
							{
								final Field f = (Field) arg0;
								return String.format("{title : \"%s\", path : \"%s\"}", f.getName(), f.getSqlName());
							}
						}), ",")));
			}
			tableNode.append("}");
			rs.put(tableName, tableNode.toString());
		}
		return String.format("[%s]", StringUtils.join(rs.values(), ","));
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
		return (int) query.count();
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

	public LinkedHashMap<String, SimpleExpression<? extends Object>> getSelect()
	{
		return select;
	}
}