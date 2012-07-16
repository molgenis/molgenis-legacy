package org.molgenis.datatable.model;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.dbutils.ResultSetIterator;
import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

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
        final Iterator<Tuple> iterator = iterator();
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
                     final MolgenisFieldTypes.FieldTypeEnum type = column.getType().getEnumType();
                     final BooleanExpression rhs = getExpression(rule, selectExpr, column);
                     if (expr != null) {
                         expr = expr.and(rhs);
//                     if (groupOp.equals("AND")) {
//                         expr = expr.and(rhs);
//                     } else if (groupOp.equals("OR")) {
//                         expr = expr.or(rhs);
//                     } else {
//                         throw new IllegalArgumentException(String.format("Unkown groupOp: %s", groupOp));
//                     }
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
}
