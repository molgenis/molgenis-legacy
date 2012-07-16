package org.molgenis.datatable.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.Tuple;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;

/**
 * A class to wrap a specific {@link SQLQuery} in a TupleTable.
 */
public class QueryTable extends AbstractFilterableTupleTable {

	private final SQLQueryImpl query;
	private final LinkedHashMap<String, SimpleExpression<? extends Object>> select;
	private final LinkedHashMap<String, Field> columnsByName;
	private final List<Field> columns;

	public QueryTable(SQLQueryImpl query, LinkedHashMap<String, SimpleExpression<? extends Object>> select,
			List<Field> columns) {
		this.query = query;
		this.select = select;

		this.columns = columns;
		columnsByName = new LinkedHashMap<String, Field>();
		for (final Field col : columns) {
			columnsByName.put(col.getSqlName(), col);
		}
	}

	@Override
	public List<Field> getColumns() throws TableException {
		return columns;
	}

	public Field getColumnByName(String name) {
		return columnsByName.get(name);
	}

	@Override
	public List<Tuple> getRows() throws TableException {
		final List<Tuple> result = new ArrayList<Tuple>();
		for (final Tuple tuple : result) {
			result.add(tuple);
		}
		return result;
	}

	/**
	 * Convert the {@link Collection} of values in the select field to an array
	 * of {@link Expression}s, for use in the {@link SQLQuery.list()} function.
	 *
	 * @return The array of expressions.
	 */
	private Expression<?>[] getSelectAsArray() {
		final Collection<SimpleExpression<?>> values = select.values();
		final int valSize = values.size();
		final Expression<?>[] selectArray = new Expression<?>[valSize];
		int idx = 0;
		for (final SimpleExpression<? extends Object> simpleExpression : values) {
			selectArray[idx] = simpleExpression;
			++idx;
		}
		return selectArray;
	}

	private ResultSet rs = null;

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Tuple> iterator() {
		try {
			final Expression<?>[] selectArray = getSelectAsArray();
			final BooleanExpression where = convertQueryRulesToQueryExpression();
			if(where != null) {
				query.where(where);
			}
			rs = query.getResults(selectArray);

			return new RSIterator(new ResultSetTuple(rs));
			//			final ResultSetIterator resultSetIterator = new ResultSetIterator(rs);
			//			//return
			//
			//
			//			return IteratorUtils.transformedIterator(resultSetIterator, new Transformer() {
			//
			//				@Override
			//				public Object transform(Object o) {
			//					final Tuple tuple = new SimpleTuple();
			//					final Object[] record = (Object[]) o;
			//					try {
			//						int idx = 0;
			//						for (final Field f : getColumns()) {
			//							tuple.set(f.getSqlName(), record[idx]);
			//							idx++;
			//						}
			//					} catch (final TableException ex) {
			//						Logger.getLogger(QueryTable.class.getName()).log(Level.SEVERE, null, ex);
			//						return null;
			//					}
			//
			//					return tuple;
			//				}
			//			});
		} catch (final Exception ex) {
			Logger.getLogger(QueryTable.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	@Override
	public int getCount() throws TableException {
		return (int) query.count();
	}

	@Override
	public void close() throws TableException {
		if(rs != null) {
			try {
				rs.close();
			} catch (final SQLException ex) {
				throw new TableException(ex);
			}
		}
	}

	public SQLQuery getQuery() {
		return query;
	}

	public LinkedHashMap<String, SimpleExpression<? extends Object>> getSelect() {
		return select;
	}

	@SuppressWarnings("unchecked")
	private static BooleanExpression getExpression(QueryRule rule,
			final SimpleExpression<? extends Object> selectExpr, final Field column) throws ParseException {
		final Operator op = rule.getOperator();
		final MolgenisFieldTypes.FieldTypeEnum type = column.getType().getEnumType();
		final String value = rule.getValue().toString();
		BooleanExpression expr = null;
		switch (type) {
		case DECIMAL: {
			final Double val = (Double) column.getType().getTypedValue(value);

			switch (op) {
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
				throw new UnsupportedOperationException(
						String.format("Operation: %s not implemented yet for type %s!",
								op, type));
			}
			break;
		}
		case INT: {
			final Integer val = (Integer) column.getType().getTypedValue(value);

			switch (op) {
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
				throw new UnsupportedOperationException(
						String.format("Operation: %s not implemented yet for type %s!",
								op, type));
			}
		}
		break;

		case STRING: {
			final String val = (String) column.getType().getTypedValue(value);
			switch (op) {
			case EQUALS:
				expr = ((StringExpression) selectExpr).eq(val);
				break;
			case LIKE:
				expr = ((StringExpression) selectExpr).like(val + "%");
			default:
				throw new UnsupportedOperationException(
						String.format("Operation: %s not implemented yet for type %s!",
								op, type));
			}
		}
		break;
		default:
			throw new UnsupportedOperationException(
					String.format("Operation: %s not implemented yet for type %s!",
							op, type));
		}
		return expr;
	}

	@SuppressWarnings("rawtypes")
	private BooleanExpression convertQueryRulesToQueryExpression() throws TableException {
		try {
			final List<QueryRule> filters = super.getFilters();
			BooleanExpression expr = null;
			for (final QueryRule rule : filters) {
				final Operator op = rule.getOperator();
				final String value = rule.getValue().toString();
				final String fieldName = rule.getField() == null ? value : rule.getField();

				if(op == Operator.LIMIT) {
					query.limit(Long.parseLong(value));
				} else if(op == Operator.OFFSET) {
					query.offset(Long.parseLong(value));
				} else if(op == Operator.SORTASC || op == Operator.SORTDESC) {
					final SimpleExpression<? extends Object> sortField = select.get(fieldName);
					if(op == Operator.SORTASC) {
						query.orderBy(((ComparableExpressionBase)sortField).asc());
					} else {
						query.orderBy(((ComparableExpressionBase)sortField).desc());
					}
				} else {
					final SimpleExpression<? extends Object> selectExpr = select.get(fieldName);
					final Field column = getColumnByName(fieldName);
					final BooleanExpression rhs = getExpression(rule, selectExpr, column);
					if (expr != null) {
						expr = expr.and(rhs);
					} else {
						expr = rhs;
					}
				}
			}
			return expr;
		} catch (final Exception ex) {
			throw new TableException(ex);
		}
	}
}
