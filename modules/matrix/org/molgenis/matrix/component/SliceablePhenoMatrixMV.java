package org.molgenis.matrix.component;

//import static ch.lambdaj.Lambda.*;
//import org.hamcrest.text.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.lifelines.loaders.EAVToView;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;

/**
 * Sliceable version of the PhenoMatrix. This assumes the rows are
 * ObservationTarget, the columns ObservableFeature and there can be zero or
 * more ObservedValue for each combination (hence return List &lt; ObservedValue
 * &gt; for each value 'V')
 * 
 * Slicing will be done by setting filters.
 * 
 * The data is retrieved by (a) retrieving visible columns and rows and (2)
 * retrieval of the matching data using columns and rows as filters. The whole
 * set is filtered by investigation.
 * 
 */

public class SliceablePhenoMatrixMV<R extends ObservationElement, C extends ObservationElement>
        extends AbstractObservationElementMatrix<R, C, ObservedValue> implements
        SliceableMatrix<R, C, ObservedValue> {

    private final Investigation investigation;
    private final EntityManager em;
    private final LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol;
    private final String JOIN_COLUMN = "PA_ID";
    
    public SliceablePhenoMatrixMV(Database database, 
            Class<R> rowClass, Class<C> colClass, 
            Investigation investigation, LinkedHashMap<Protocol, List<Measurement>> mesurementByProtocol) {
        this.database = database;
        this.rowClass = rowClass;
        this.colClass = colClass;
        this.valueClass = ObservedValue.class;
        this.mesurementsByProtocol = mesurementByProtocol;
        this.em = database.getEntityManager();
        this.investigation = investigation;
    }

    @Override
    public List<R> getRowHeaders() throws MatrixException {
        // reload the rowheaders if filters have changed.
        if (rowDirty) {
            try {
                Query<R> query = this.createSelectQuery(getRowClass());
                this.rowHeaders = query.find();
                rowDirty = false;
            } catch (Exception e) {
                throw new MatrixException(e);
            }
        }
        return rowHeaders;
    }

    @Override
    public Integer getRowCount() throws MatrixException {
        try {
            String query = createCountQuery();
            query = String.format("SELECT count(*) FROM (%s)", query);
            BigDecimal count = (BigDecimal) em.createNativeQuery(query).getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            throw new MatrixException(e);
        }
    }

    @Override
    public List<C> getColHeaders() throws MatrixException {
        List<Measurement> result = new ArrayList<Measurement>();
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            result.addAll(entry.getValue());
        }
        return (List<C>) result;
    }
    
    public List<Column> getColumns() {
        List<Column> result = new ArrayList<Column>();
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            for(Measurement measurement : entry.getValue()) {
                String name = measurement.getName();
                Column.ColumnType columnType = Column.getColumnType(measurement.getDataType());
                Column c = new Column(name, columnType, entry.getKey(), measurement, null);
                result.add(c);
            }
        }        
        return result;
    }
    
    @Override
    public Integer getColCount() throws MatrixException {
        // fire count query on col headers
        try {
            return this.createCountQuery(getColClass()).count();
        } catch (DatabaseException e) {
            throw new MatrixException(e);
        }
    }

    @Override
    public BasicMatrix<R, C, ObservedValue> getResult() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Helper method to create a 'count' query. Difference with a normal query
     * is that there is no limit/offset on it
     */
    private <D extends ObservationElement> Query<D> createCountQuery(
            Class<D> xClass) throws MatrixException {
        return this.createQuery(xClass, true);
    }

    /** Helper method to produce a selection query for columns or rows */
    private <D extends ObservationElement> Query<D> createSelectQuery(
            Class<D> xClass) throws MatrixException {
        return this.createQuery(xClass, false);
    }

    private String getFilterCondition() {
        StringBuilder where = new StringBuilder();
        boolean prev = false;
        for (MatrixQueryRule rule : rules) {
            if (rule.getFilterType().equals(MatrixQueryRule.Type.colValueProperty)) {
                if (prev) {
                    where.append(" AND ");
                }
                prev = true;

                Measurement m = em.find(Measurement.class, rule.getDimIndex());
                Protocol p = em.find(Protocol.class, rule.getProtocolId());
                Column.ColumnType ct = Column.getColumnType(m.getDataType());
                String value = rule.getValue().toString();
                if (ct.isQuote()) {
                    value = "'" + value + "'";
                }
                where.append(String.format("%s_%s %s %s", p.getName(), m.getName(), rule.getOperator(), value));
            }
        }
        return where.toString();
    }

    /**
     * 
     * @param field
     *            , either ObservedValue.FEATURE or ObservedValue.TARGET
     * @throws MatrixException
     */
    private <D extends ObservationElement> Query<D> createQuery(
            Class<D> xClass, boolean countAll) throws MatrixException {
        // If xClass == getRowClass():
        // A. filter on rowIndex + rowHeaderProperty
        // B. filter on colValue: 1 subquery per column
        // C. filter on rowOffset and rowLimit

        try {
            // parameterize the refresh of the dim, either TARGET or FEATURE
            MatrixQueryRule.Type xIndexFilterType = MatrixQueryRule.Type.rowIndex;
            MatrixQueryRule.Type xHeaderFilterType = MatrixQueryRule.Type.rowHeader;
            MatrixQueryRule.Type xValuesFilterType = MatrixQueryRule.Type.colValues;
            MatrixQueryRule.Type xValuePropertyFilterType = MatrixQueryRule.Type.colValueProperty;
            if (xClass.equals(getColClass())) {
                xIndexFilterType = MatrixQueryRule.Type.colIndex;
                xHeaderFilterType = MatrixQueryRule.Type.colHeader;
                xValuesFilterType = MatrixQueryRule.Type.rowValues;
                xValuePropertyFilterType = MatrixQueryRule.Type.rowValueProperty;
            }


            Query<D> xQuery = database.query(xClass);
//			for (MatrixQueryRule rule : rules)
//			{
//                            // only add colValues / rowValues as subquery
//                            if (rule.getFilterType().equals(xValuePropertyFilterType))
//                            {
//                                
//                                String field = m.getName();
//                                
//                                xQuery.addRules(new QueryRule(field, rule.getOperator(), rule.getValue()));
//                            }
//			}                       




            // Impl

//			// Impl A: header query
//			Query<D> xQuery = database.query(xClass);
//			for (MatrixQueryRule rule : rules)
//			{
//				// rewrite rule(type=row/colIndex) to rule(type=row/colHeader, field=id)
//				if (rule.getFilterType().equals(xIndexFilterType))
//				{
//					rule.setField("id");
//					rule.setFilterType(xHeaderFilterType);
//				}
//				// add row/colHeader filters to query / remember sort rules
//				if (rule.getFilterType().equals(xHeaderFilterType))
//				{
//					xQuery.addRules(rule);
//				}
//				// ignore all other rules
//			}
//
//			// select * from Individual where id in (select target from
//			// observedvalue where feature = 1 AND value > 10 AND target in
//			// (select target from observedvalue ));
//
//			// Impl B: create subquery per column, order matters because of
//			// sorting (not supported).
//			Map<Integer, Query<ObservedValue>> subQueries = new LinkedHashMap<Integer, Query<ObservedValue>>();
//			for (MatrixQueryRule rule : rules)
//			{
//				// only add colValues / rowValues as subquery
//				if (rule.getFilterType().equals(xValuePropertyFilterType))
//				{
//					// create a new subquery for each colValues column
//					if (subQueries.get(rule.getDimIndex()) == null)
//					{
//						Query<ObservedValue> subQuery = (Query<ObservedValue>) database.query(this
//								.getValueClass());
//						// filter on data
//						// if(data != null)
//						// subQuery.eq(TextDataElement.DATA, data.getIdValue());
//						// filter on the column/row
//						subQuery.eq(
//								xValuePropertyFilterType == MatrixQueryRule.Type.colValueProperty ? ObservedValue.FEATURE
//										: ObservedValue.TARGET, rule.getDimIndex());
//						subQueries.put(rule.getDimIndex(), subQuery);
//					}
//					subQueries.get(rule.getDimIndex()).addRules(rule);
//				}
//				// ignore all other rules
//			}
//
//			// add each subquery as condition on
//			// ObservedValue.FEATURE/ObservedValue.TARGET
//			for (Query<ObservedValue> q : subQueries.values())
//			{
//				String sqField = (xClass.equals(rowClass) ? 
//						ObservedValue.TARGET : ObservedValue.FEATURE);
//				
////				xClass.equals(rowClass) ? rowClass.g
//			
// 				
//				SubQueryRule subQueryRule = new SubQueryRule("id", Operator.IN, sqField, Integer.class, 
//						ObservedValue.class, q.getRules());
//				xQuery.subQuery(subQueryRule);
////				q.subQuery(ObservationElement.ID, fieldOfSubQuery, Integer.class, rules);
////				String sql = q.createFindSql();
////				// strip 'select ... from' and replace with 'select id from'
////				sql = "SELECT ObservedValue."
////						+ (xClass.equals(rowClass) ? ObservedValue.TARGET
////								: ObservedValue.FEATURE) + " "
////						+ sql.substring(sql.indexOf("FROM"));
////				// use QueryRule.Operator.IN_SUBQUERY
////				xQuery.subquery(ObservationElement.ID, sql);
//				//xQuery.subquery(field, sql)
//				
//			}

            // add limit and offset, unless count
            if (!countAll) {
                if (xClass.equals(getColClass())) {
                    xQuery.limit(colLimit);
                    xQuery.offset(colOffset);
                } else {
                    xQuery.limit(rowLimit);
                    xQuery.offset(rowOffset);
                }
            }

            return xQuery;
        } catch (Exception e) {
            throw new MatrixException(e);
        }
    }

    private int getVisableColumnCount() {
        int result = 0;
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            result += entry.getValue().size();
        }
        return result;
    }

    private String createQuery() throws Exception {
        return createQuery(false);
    }

    private String createCountQuery() throws Exception {
        return createQuery(true);
    }

    private String createQuery(boolean count) throws Exception {

        if (count) {
            //todo: optimalisation, remove columns that are not needed for count!
//                kkfw
////                for(List<Measurement> m : pM.values()) {
////                    List<Measurement> result = filter(having(on(Measurement.class).getName(), IsIn.isIn(asList("PA_ID"))), m);
////                                        
////                }
        }

        StringBuilder query = new StringBuilder("SELECT * FROM ");
        boolean first = true;

        String orderByColumn = null;
        String prevAliasName = null;
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            List<Measurement> measurements = entry.getValue();
            //boolean paIDExists = exists(entry.getValue(), having(on(Measurement.class).getName(), IsEqualIgnoringCase.equalToIgnoringCase(JOIN_COLUMN)));
            boolean paIDExists = false;
            for(Measurement m : entry.getValue()) {
            	if(m.getName().equalsIgnoreCase(JOIN_COLUMN)) {
            		paIDExists = true;
            		break;            		
            	}
            }
            
            if(!paIDExists) {
                Measurement paIdMes = em.createQuery("SELECT m FROM Measurement m JOIN m.featuresCollection p WHERE m.name = :name and p = :protocol", Measurement.class)
                        .setParameter("name", JOIN_COLUMN)
                        .setParameter("protocol", entry.getKey())
                        .getSingleResult();
                measurements.add(paIdMes);
            }
            
            String sql = EAVToView.createQuery(investigation.getId(), entry.getKey(), measurements, database);

            String aliasName = entry.getKey().getName();

            if (first) {
                orderByColumn = aliasName;
                first = false;
                query.append(String.format("(%s) %s", sql, aliasName));
            } else {
                query.append(
                        String.format(" left join (%s) %s on (%s = %s)",
                        sql, aliasName, aliasName + "." + aliasName + "_PA_ID", prevAliasName + "." + prevAliasName + "_PA_ID"));

            }
            prevAliasName = aliasName;
        }
        
        if(!count) {
            query.append(String.format(" order by %s.%s_PA_ID", orderByColumn, orderByColumn));
        }

        String whereFilter = getFilterCondition();
        if (!StringUtils.isEmpty(whereFilter)) {
            return String.format("SELECT * FROM (%s) WHERE %s", query.toString(), whereFilter);
        }
        return query.toString();
    }

    @Override
    public List<ObservedValue>[][] getValueLists() throws MatrixException {
        try {
            int columnCount = getVisableColumnCount();

            String sql = createQuery();

            List<Object[]> data = em.createNativeQuery(sql).setMaxResults(getRowLimit()).setFirstResult(getRowOffset()).getResultList();

            final List<ObservedValue>[][] valueMatrix = create(data.size(), columnCount);

            for (int iRow = 0; iRow < data.size(); ++iRow) {
                for (int iCol = 0; iCol < columnCount; ++iCol) {
                    valueMatrix[iRow][iCol] = new ArrayList<ObservedValue>();
                    ObservedValue ov = new ObservedValue();
                    if (data.get(iRow)[iCol] != null) {
                        ov.setValue(data.get(iRow)[iCol].toString());
                    } else {
                        ov.setValue("null");
                    }

                    valueMatrix[iRow][iCol].add(ov);
                }
            }
            return valueMatrix;
        } catch (Exception ex) {
            throw new MatrixException(ex);
        }
    }

    public List<ObservedValue>[][] create(int rows, int cols) {
        // create all empty rows as well
        List<ObservedValue>[][] data = new ArrayList[rows][cols];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new ArrayList<ObservedValue>();
            }
        }

        return data;
    }

    @Override
    public ObservedValue[][] getValues() throws MatrixException {
        throw new UnsupportedOperationException("use getValueLists");
    }
}
