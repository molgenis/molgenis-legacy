package org.molgenis.datatable.model;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.springframework.util.StringUtils;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;

/**
 * A class to allow the use of {@link SQLQuery}s on a TupleTable.
 */
public class QueryTable extends AbstractFilterableTupleTable
{
	private final QueryCreator queryCreator;
	private final Connection conn;
	private final SQLTemplates dialect;

	private final LinkedHashMap<String, Field> columnsByName;
	private final List<String> hiddenColumns;
	private CloseableIterator<Object[]> rs;

	public QueryTable(final QueryCreator queryCreator, final Connection conn, final SQLTemplates dialect)
	{
		this.queryCreator = queryCreator;
		this.conn = conn;
		this.dialect = dialect;

		columnsByName = new LinkedHashMap<String, Field>();
		for (final Field col : queryCreator.getFields())
		{
			columnsByName.put(col.getSqlName(), col);
		}
		List<String> tmpHiddenFieldNames = queryCreator.getHiddenFieldNames();
		hiddenColumns = tmpHiddenFieldNames == null ? Collections.<String> emptyList() : tmpHiddenFieldNames;
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		return queryCreator.getFields();
	}

	public Field getColumnByName(String name)
	{
		return columnsByName.get(name);
	}

	private SQLQueryImpl createQuery() throws TableException
	{
		final SQLQueryImpl query = queryCreator.createQuery(conn, dialect);
		final BooleanExpression where = convertQueryRulesToQueryExpression(query);
		if (where != null)
		{
			query.where(where);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			final SQLQueryImpl query = createQuery();
			final LinkedHashMap<String, SimpleExpression<? extends Object>> select = queryCreator
					.getAttributeExpressions();
			CollectionUtils.select(select.entrySet(), new Predicate()
			{
				@Override
				public boolean evaluate(Object arg0)
				{
					final Entry<String, SimpleExpression<? extends Object>> entry = (Entry<String, SimpleExpression<? extends Object>>) arg0;
					return hiddenColumns.contains(entry.getKey());
				}
			});

			final Expression<?>[] selectArray = select.values().toArray(new Expression<?>[0]);
			final ArrayList<String> names = new ArrayList<String>(select.keySet());
			for (int i = 0; i < selectArray.length; ++i)
			{
				final Expression<?> expr = selectArray[i];
				final String alias = StringUtils.replace(names.get(i), ".", "_");
				selectArray[i] = ((SimpleExpression<?>) expr).as(alias);
			}

			int limit = getLimit();
			if (limit > 0)
			{
				query.limit(limit);
			}

			int offset = getOffset();
			if (offset > 0)
			{
				query.offset(offset);
			}
			// TODO: replace by log4j if configured correctly
			System.err.println(query.toString());

			rs = query.iterate(selectArray);
			return IteratorUtils.transformedIterator(rs, new Transformer()
			{
				@Override
				public Object transform(Object o)
				{
					final Tuple tuple = new SimpleTuple();
					final Object[] record = (Object[]) o;
					try
					{
						int idx = 0;
						for (final Field f : getColumns())
						{
							tuple.set(f.getSqlName(), record[idx]);
							idx++;
						}
					}
					catch (final TableException ex)
					{
						Logger.getLogger(QueryTable.class.getName()).log(Level.SEVERE, null, ex);
						return null;
					}

					return tuple;
				}
			});
		}
		catch (final Exception ex)
		{
			Logger.getLogger(QueryTable.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	@Override
	public int getCount() throws TableException
	{
		return (int) createQuery().count();
	}

	@Override
	public void close() throws TableException
	{
		if (rs != null)
		{
			rs.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static BooleanExpression getExpression(QueryRule rule, final SimpleExpression<? extends Object> selectExpr,
			final Field column) throws ParseException
	{
		final Operator op = rule.getOperator();
		final MolgenisFieldTypes.FieldTypeEnum type = column.getType().getEnumType();
		final String value = rule.getValue().toString();
		BooleanExpression expr = null;
		switch (type)
		{
			case DECIMAL:
			{
				final Double val = (Double) column.getType().getTypedValue(value);

				switch (op)
				{
					case EQUALS:
						expr = ((NumberExpression<Double>) selectExpr).eq(val);
						break;
					case LESS:
						expr = ((NumberExpression<Double>) selectExpr).lt(val);
						break;
					case LESS_EQUAL:
						expr = ((NumberExpression<Double>) selectExpr).loe(val);
						break;
					case GREATER:
						expr = ((NumberExpression<Double>) selectExpr).gt(val);
						break;
					case GREATER_EQUAL:
						expr = ((NumberExpression<Double>) selectExpr).goe(val);
						break;
					default:
						throw new UnsupportedOperationException(String.format(
								"Operation: %s not implemented yet for type %s!", op, type));
				}
				break;
			}
			case INT:
			{
				final Integer val = (Integer) column.getType().getTypedValue(value);

				switch (op)
				{
					case EQUALS:
						expr = ((NumberExpression<Integer>) selectExpr).eq(val);
						break;
					case LESS:
						expr = ((NumberExpression<Integer>) selectExpr).lt(val);
						break;
					case LESS_EQUAL:
						expr = ((NumberExpression<Integer>) selectExpr).loe(val);
						break;
					case GREATER:
						expr = ((NumberExpression<Integer>) selectExpr).gt(val);
						break;
					case GREATER_EQUAL:
						expr = ((NumberExpression<Integer>) selectExpr).goe(val);
						break;
					default:
						throw new UnsupportedOperationException(String.format(
								"Operation: %s not implemented yet for type %s!", op, type));
				}
				break;
			}
			case STRING:
			{
				final String val = (String) column.getType().getTypedValue(value);
				switch (op)
				{
					case EQUALS:
						expr = ((StringExpression) selectExpr).eq(val);
						break;
					case LIKE:
						expr = ((StringExpression) selectExpr).like(val + "%");
						break;
					default:
						throw new UnsupportedOperationException(String.format(
								"Operation: %s not implemented yet for type %s!", op, type));
				}
				break;
			}
			default:
				throw new UnsupportedOperationException(String.format("Operation: %s not implemented yet for type %s!",
						op, type));
		}
		return expr;
	}

	@SuppressWarnings("rawtypes")
	private BooleanExpression convertQueryRulesToQueryExpression(final SQLQueryImpl query) throws TableException
	{
		try
		{
			final LinkedHashMap<String, SimpleExpression<? extends Object>> select = queryCreator
					.getAttributeExpressions();

			final List<QueryRule> filters = super.getFilters();
			BooleanExpression expr = null;
			for (final QueryRule rule : filters)
			{
				final Operator op = rule.getOperator();
				final String value = rule.getValue().toString();
				final String fieldName = rule.getField() == null ? value : rule.getField();

				if (op == Operator.SORTASC || op == Operator.SORTDESC)
				{
					final SimpleExpression<? extends Object> sortField = select.get(fieldName);
					if (op == Operator.SORTASC)
					{
						query.orderBy(((ComparableExpressionBase) sortField).asc());
					}
					else
					{
						query.orderBy(((ComparableExpressionBase) sortField).desc());
					}
				}
				else
				{
					final SimpleExpression<? extends Object> selectExpr = select.get(fieldName);
					Field column = getColumnByName(fieldName);
					final BooleanExpression rhs = getExpression(rule, selectExpr, column);
					if (expr != null)
					{
						expr = expr.and(rhs);
					}
					else
					{
						expr = rhs;
					}
				}
			}
			return expr;
		}
		catch (final Exception ex)
		{
			throw new TableException(ex);
		}
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
