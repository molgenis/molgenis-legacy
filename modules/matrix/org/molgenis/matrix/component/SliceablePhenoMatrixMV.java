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
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.sqlbackend.EAVRelationalBackend;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.sqlbackend.Backend;
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
        SliceableMatrix<R, C, ObservedValue>, DatabaseMatrix {

    private final EntityManager em;
    private final Investigation investigation;
    private final LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol;
    private final String JOIN_COLUMN = "PA_ID";
    
    private final Backend backend;
    
    
    
    
    private Protocol sortProtocol;
    private Measurement sortMeasurement;
    private String sortOrder;
        
    public SliceablePhenoMatrixMV(Database database, 
            Class<R> rowClass, Class<C> colClass, 
            Investigation investigation, LinkedHashMap<Protocol, List<Measurement>> mesurementByProtocol) {
        //this.database = database;
        this.rowClass = rowClass;
        this.colClass = colClass;
        this.valueClass = ObservedValue.class;
        this.investigation = investigation;
        this.mesurementsByProtocol = mesurementByProtocol;
        this.em = database.getEntityManager();

        //this.backend = new EAVViewBackend(this, "TEST001_");
        this.backend = new EAVRelationalBackend(this);
    }
    
    Database db;
	
	public void setDatabase(Database db)
	{
		this.db = db;
	}

    public void setSort(Protocol protocol, Measurement measurement, String sortOrder) {
        this.sortProtocol = protocol;
        this.sortMeasurement = measurement;
        this.sortOrder = sortOrder;
    }
    
    
    
    @Override
    public List<R> getRowHeaders() throws MatrixException {
        // reload the rowheaders if filters have changed.
        if (rowDirty) {
            try {
                Query<R> query = this.createSelectQuery(getRowClass(), db);
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
            System.out.println(query);
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
                Column c = new Column(entry.getKey(), measurement);
                result.add(c);
            }
        }        
        return result;
    }
    
    @Override
    public Integer getColCount() throws MatrixException {
        // fire count query on col headers
        try {
            return this.createCountQuery(getColClass(), db).count();
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
            Class<D> xClass, Database db) throws MatrixException {
        return this.createQuery(xClass, true, db);
    }

    /** Helper method to produce a selection query for columns or rows */
    private <D extends ObservationElement> Query<D> createSelectQuery(
            Class<D> xClass, Database db) throws MatrixException {
        return this.createQuery(xClass, false, db);
    }

    /**
     * 
     * @param field
     *            , either ObservedValue.FEATURE or ObservedValue.TARGET
     * @throws MatrixException
     */
    private <D extends ObservationElement> Query<D> createQuery(
            Class<D> xClass, boolean countAll, Database db) throws MatrixException {
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

            Query<D> xQuery = db.query(xClass);


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
        return backend.createQuery(false, rules);
    }

    private String createCountQuery() throws Exception {
        return backend.createQuery(true, rules);
    }

    @Override
    public List<ObservedValue>[][] getValueLists() throws MatrixException {
        try {
            int columnCount = getVisableColumnCount();

            String sql = createQuery();
            System.out.println(sql);

            int offset = getRowLimit() * (getRowOffset()-1);
            if(offset < 0) {
                offset = 0;
            }
            List<Object[]> data = em.createNativeQuery(sql).setMaxResults(getRowLimit()).setFirstResult(offset).getResultList();

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
        List<ObservedValue>[][] values = getValueLists();
        
        int rowCnt = values.length;
        int colCnt = values[0].length;
        ObservedValue[][] result = new ObservedValue[rowCnt][colCnt];
        for(int i = 0; i < rowCnt; ++i) {
            for(int j = 0; j < colCnt; ++j) {
                result[i][j] = values[i][j].get(0);
            }
        }
        return result;
    }

    public String getJOIN_COLUMN() {
        return JOIN_COLUMN;
    }

    public EntityManager getEm() {
        return em;
    }

    public LinkedHashMap<Protocol, List<Measurement>> getMesurementsByProtocol() {
        return mesurementsByProtocol;
    }

    public Measurement getSortMeasurement() {
        return sortMeasurement;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public Protocol getSortProtocol() {
        return sortProtocol;
    }

    public Investigation getInvestigation() {
        return investigation;
    }
    
    
}
