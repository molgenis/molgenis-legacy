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

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.dbutils.ResultSetIterator;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;
<<<<<<< HEAD
=======
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.dbutils.ResultSetIterator;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
>>>>>>> 6ed4c48bd42e75b2ea5940c67f203e3fc3f23ab1

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
        this.columnsByName = new LinkedHashMap<String, Field>();
        for (Field col : columns) {
            this.columnsByName.put(col.getSqlName(), col);
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
        for (final Iterator<Tuple> it = result.iterator(); it.hasNext();) {
            final Tuple tuple = it.next();
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
        int valSize = values.size();
        final Expression<?>[] selectArray = new Expression<?>[valSize];
        int idx = 0;
        for (final Iterator<SimpleExpression<? extends Object>> iterator = values.iterator(); iterator.hasNext();) {
            selectArray[idx] = iterator.next();
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
            final ResultSetIterator resultSetIterator = new ResultSetIterator(rs);
            return IteratorUtils.transformedIterator(resultSetIterator, new Transformer() {

                @Override
                public Object transform(Object o) {
                    final Tuple tuple = new SimpleTuple();
                    final Object[] record = (Object[]) o;
                    try {
                        int idx = 0;
                        for (final Field f : getColumns()) {
                            tuple.set(f.getSqlName(), record[idx]);
                            idx++;
                        }
                    } catch (TableException ex) {
                        Logger.getLogger(QueryTable.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }

                    return tuple;
                }
            });
        } catch (Exception ex) {
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
            } catch (SQLException ex) {
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
				@SuppressWarnings("unchecked")
				final NumberExpression<Double> decimalSelect = (NumberExpression<Double>) selectExpr;
                switch (op) {
                    case EQUALS:
					expr = decimalSelect.eq(val);
                        break;
                    case LESS:
                        expr = decimalSelect.lt(val);
                        break;
                    case LESS_EQUAL:
                        expr = decimalSelect.loe(val);
                        break;
                    case GREATER:
                        expr = decimalSelect.gt(val);
                        break;
                    case GREATER_EQUAL:
                        expr = decimalSelect.goe(val);
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
				@SuppressWarnings("unchecked")
				NumberExpression<Integer> intExpr = (NumberExpression<Integer>) selectExpr;

                switch (op) {
                    case EQUALS:
					expr = intExpr.eq(val);
                        break;
                    case LESS:
                        expr = intExpr.lt(val);
                        break;
                    case LESS_EQUAL:
                        expr = intExpr.loe(val);
                        break;
                    case GREATER:
                        expr = intExpr.gt(val);
                        break;
                    case GREATER_EQUAL:
                        expr = intExpr.goe(val);
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
				StringExpression stringExpr = (StringExpression) selectExpr;

                switch (op) {
                    case EQUALS:
					expr = stringExpr.eq(val);
                        break;
                    case LIKE:
                        expr = stringExpr.like(val + "%");
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
<<<<<<< HEAD
            for (final QueryRule rule : filters) {

                final String fieldName = rule.getField();

                final SimpleExpression<? extends Object> selectExpr = select.get(fieldName);
                final Field column = getColumnByName(fieldName);
                BooleanExpression rhs = getExpression(rule, selectExpr, column);
                if (expr != null) {
                    expr = expr.and(rhs);
//                if (groupOp.equals("AND")) {
//                    expr = expr.and(rhs);
//                } else if (groupOp.equals("OR")) {
//                    expr = expr.or(rhs);
//                } else {
//                    throw new IllegalArgumentException(String.format("Unkown groupOp: %s", groupOp));
//                }
=======
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
>>>>>>> 6ed4c48bd42e75b2ea5940c67f203e3fc3f23ab1
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
        } catch (Exception ex) {
            throw new TableException(ex);
        }
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

	@Override
	public void setVisibleColumns(List<String> fieldNames) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Field> getVisibleColumns() {
		// TODO Auto-generated method stub
		return null;
	}
}
