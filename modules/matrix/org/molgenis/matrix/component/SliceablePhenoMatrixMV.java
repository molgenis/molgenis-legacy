package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.sqlbackend.Backend;
import org.molgenis.matrix.component.sqlbackend.EAVViewBackend;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
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

public class SliceablePhenoMatrixMV<R extends ObservationElement, C extends ObservationElement, V extends ObservedValue>
        extends AbstractObservationElementMatrix<R, C, V> 
		implements SliceableMatrix<R, C, V>, DatabaseMatrix 
{
    private final EntityManager em;
    private final Investigation investigation;
    private final LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol;
    private final Map<Measurement, List<Category>> categoryByMeasurement = new HashMap<Measurement, List<Category>>();
    
    private final String JOIN_COLUMN = "PA_ID";
    
    private final Backend backend;
       
    private Protocol sortProtocol;
    private Measurement sortMeasurement;
    private String sortOrder;
	private Database db;
        
    @SuppressWarnings("unchecked")
	public SliceablePhenoMatrixMV(Database database, 
            Class<R> rowClass, Class<C> colClass, 
            Investigation investigation, LinkedHashMap<Protocol, 
            List<Measurement>> measurementByProtocol) 
    	throws MatrixException
    {
        this.db = database;
        this.rowClass = rowClass;
        this.colClass = colClass;
        this.valueClass = (Class<V>) ObservedValue.class;
        this.investigation = investigation;
        this.measurementsByProtocol = measurementByProtocol;
        this.em = database.getEntityManager();
        this.backend = new EAVViewBackend(this, "LL_VWM_");
        try {
			retrieveCategories();
		} catch (DatabaseException ex) {
			throw new MatrixException(ex);
		}
    }

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public Database getDatabase() {
		return db;
	}
	
	private void retrieveCategories() throws DatabaseException {
		String qlString = "SELECT m, c FROM Measurement m JOIN m.categories c WHERE m.investigation = :investigation";
		List<Object[]> measCats = em.createQuery(qlString)
					.setParameter("investigation", investigation)
					.getResultList();
		
		for(Object[] rec : measCats) {
			Measurement m = (Measurement)rec[0];
			Category c = (Category)rec[1];
			
			if(categoryByMeasurement.containsKey(m)) {
				categoryByMeasurement.get(m).add(c);
			} else {				
				List<Category> cats = new ArrayList<Category>();
				cats.add(c);
				categoryByMeasurement.put(m, cats);
			}
		}
		
		
//		objects.toString();
//		List<Category> categories = db.query(Category.class).eq(Category.INVESTIGATION, investigation.getId()).find();
//		for (Category category : categories) {
//			Collection<Measurement> measurements = category.getCategoriesCollection();			
//			for (Measurement measurement : measurements) {
//				if(categoryByMeasurement.containsKey(measurement)) {
//					categoryByMeasurement.get(measurement).add(category);
//				} else {
//					List<Category> cats = new ArrayList<Category>();
//					cats.add(category);
//					categoryByMeasurement.put(measurement, cats);
//				} 
//			}
//		}
	}
	
    public void setSort(Protocol protocol, Measurement measurement, String sortOrder) {
        this.sortProtocol = protocol;
        this.sortMeasurement = measurement;
        this.sortOrder = sortOrder;
    }
    
    public void setColumns(List<String> columNames) throws MatrixException {
    	for(String colName : columNames) {
    		String protocolName = StringUtils.substringBefore(colName, "_");
    		
    		try {
				Protocol p = db.query(Protocol.class).eq(Protocol.NAME, protocolName).find().get(0);
				Measurement m = db.query(Measurement.class).eq(Measurement.NAME, colName).find().get(0);
				
				if(getMeasurementsByProtocol().containsKey(p)) {
					if(!getMeasurementsByProtocol().get(p).contains(m)) {
						getMeasurementsByProtocol().get(p).add(m);
					}
				} else {
					List<Measurement> ms = new ArrayList<Measurement>();
					ms.add(m);
					getMeasurementsByProtocol().put(p, ms);
				}				
			} catch (DatabaseException e) {
				throw new MatrixException(e);
			}
    	}
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
            Number count = (Number) em.createNativeQuery(query).getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            throw new MatrixException(e);
        }
    }
    
    @Override
    public List<String> getColPropertyNames() {
    	final List<String> result = new ArrayList<String>();
    	try {
    		for(final C col : getColHeaders()) {
				result.add(col.getName());
			}
		} catch (MatrixException e) {
			throw new RuntimeException(e);
		}
    	return result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<C> getColHeaders() throws MatrixException {
        List<Measurement> result = new ArrayList<Measurement>();
        for (Map.Entry<Protocol, List<Measurement>> entry : getMeasurementsByProtocol().entrySet()) {
            result.addAll(entry.getValue());
        }
        return (List<C>) result;
    }
    
    public List<Column> getColumns() {
        List<Column> result = new ArrayList<Column>();
        for (Map.Entry<Protocol, List<Measurement>> entry : getMeasurementsByProtocol().entrySet()) {
            for(Measurement measurement : entry.getValue()) {
                Column c = new Column(entry.getKey(), measurement);
                result.add(c);
            }
        }        
        return result;
    }
    
    @Override
    public Integer getColCount() throws MatrixException {
    	int count = 0;
    	for (Map.Entry<Protocol, List<Measurement>> entry : getMeasurementsByProtocol().entrySet()) {
    		count += entry.getValue().size();
    	}
    	return count;
    }

    @Override
    public BasicMatrix<R, C, V> getResult() throws Exception {
        throw new UnsupportedOperationException();
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
        for (Map.Entry<Protocol, List<Measurement>> entry : getMeasurementsByProtocol().entrySet()) {
            result += entry.getValue().size();
        }
        return result;
    }

    public String createQuery() throws Exception {
        return backend.createQuery(false, rules);
    }

    private String createCountQuery() throws Exception {
        return backend.createQuery(true, rules);
    }

    @Override
    public List<V>[][] getValueLists() throws MatrixException {
        try {
            int columnCount = getVisableColumnCount();

            List<Measurement> colMeasurements = new ArrayList<Measurement>();
            for (Entry<Protocol, List<Measurement>> entry : measurementsByProtocol.entrySet()) {
				for (Measurement value : entry.getValue()) {
					colMeasurements.add(value);
				}
			}
            
            String sql = createQuery();
            System.out.println(sql);

            @SuppressWarnings("unchecked")
			List<Object[]> data = em.createNativeQuery(sql).setMaxResults(getRowLimit()).setFirstResult(getRowOffset()).getResultList();

            final List<V>[][] valueMatrix = create(data.size(), columnCount);

            for (int iRow = 0; iRow < data.size(); ++iRow) {
                for (int iCol = 0; iCol < columnCount; ++iCol) {
                    valueMatrix[iRow][iCol] = new ArrayList<V>();
                    @SuppressWarnings("unchecked")
					V ov = (V)new ObservedValue();
                    if (data.get(iRow)[iCol] != null) {                    	
                    	String value = data.get(iRow)[iCol].toString();
                    	
                    	value = getCategoryLabel(colMeasurements, iCol, value);                    	
                        
						ov.setValue(value);
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

	private String getCategoryLabel(List<Measurement> colMeasurements, int iCol,
			String value) {
		Measurement measurement = colMeasurements.get(iCol); 
		if(categoryByMeasurement.containsKey(measurement)) {
			for(Category category : categoryByMeasurement.get(measurement)) {
				if(category.getCode_String().equalsIgnoreCase(value)) {
					return category.getLabel();
				}
			}
		}
		return value;
	}

    public List<V>[][] create(int rows, int cols) {
        // create all empty rows as well
        @SuppressWarnings("unchecked")
		List<V>[][] data = new ArrayList[rows][cols];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new ArrayList<V>();
            }
        }

        return data;
    }

    public ScrollableResults getScrollableValues(boolean exportVisibleRows) throws Exception {
    	String sql = createQuery();
		Session session = ( (EntityManagerImpl) getDatabase().getEntityManager()).getSession();
		
		ScrollableResults sr;
		if(exportVisibleRows) {
			int offset = getRowOffset();
			int limit = getRowLimit();
			sr = session.createSQLQuery(sql).setFirstResult(offset).setMaxResults(limit).scroll();
		} else {
			sr = session.createSQLQuery(sql).scroll();
		}
		return sr;
    }
   
    @Override
    public V[][] getValues() throws MatrixException {
        List<V>[][] values = getValueLists();
        
        int rowCnt = values.length;
        int colCnt = values[0].length;
        @SuppressWarnings("unchecked")
		V[][] result = (V[][]) new ObservedValue[rowCnt][colCnt];
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

	public LinkedHashMap<Protocol, List<Measurement>> getMeasurementsByProtocol() {
		return measurementsByProtocol;
	}

	public String getSqlQuery() throws Exception {
		return backend.createQuery(false, rules);
	}
    
    
}
