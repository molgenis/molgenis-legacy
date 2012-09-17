//package org.molgenis.lifelinesresearchportal.models;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.persistence.EntityManager;
//
//import org.apache.commons.collections.Closure;
//import org.apache.commons.collections.CollectionUtils;
//import org.hibernate.ScrollableResults;
//import org.hibernate.Session;
//import org.hibernate.ejb.EntityManagerImpl;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.lifelinesresearchportal.backend.PhenoMatrixViewBackend;
//import org.molgenis.matrix.MatrixException;
//import org.molgenis.matrix.TableModel;
//import org.molgenis.matrix.component.Column;
//import org.molgenis.matrix.component.general.MatrixQueryRule;
//import org.molgenis.matrix.component.sqlbackend.Backend;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Category;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.ObservationTarget;
//import org.molgenis.pheno.ObservedValue;
//import org.molgenis.protocol.Protocol;
//
///**
// * Sliceable version of the PhenoMatrix. This assumes the rows are
// * ObservationTarget, the columns ObservableFeature and there can be zero or
// * more ObservedValue for each combination (hence return List &lt; ObservedValue
// * &gt; for each value 'V')
// * 
// * Slicing will be done by setting filters.
// * 
// * The data is retrieved by (a) retrieving visible columns and rows and (2)
// * retrieval of the matching data using columns and rows as filters. The whole
// * set is filtered by investigation.
// * 
// */
//
//public class MatrixModel<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> implements TableModel<R, C, V>
//{
//	private static final String PRIMARY_KEY_TABLE = "PATIENT";
//	private static final String VIEW_PREFIX = "LL_VWM_";
//	private final EntityManager em;
//	private final Investigation investigation;
//	private final LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol;
//	
////	private final Map<Measurement, List<Category>> categoryByMeasurement = new HashMap<Measurement, List<Category>>();
//	private final Map<Measurement, Map<String, Category>> categoryByCode = new HashMap<Measurement, Map<String,Category>>();
//	
//	public final String JOIN_COLUMN = "PA_ID";
//
//	private Backend backend;
//	private List<MatrixQueryRule> rules = new ArrayList<MatrixQueryRule>();
//	private int rowLimit;
//	private int rowOffset;
//	private Protocol sortProtocol;
//	private Measurement sortMeasurement;
//	private String sortOrder;
//	private boolean displayLabels = true; //display category labels
//	private boolean formatDate = true;
//	
//	public MatrixModel(EntityManager em, Investigation investigation,
//			LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol) throws MatrixException
//	{
//		this.em = em;
//		this.investigation = investigation;
//		this.measurementsByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
//		
//		//initialize Primary Key
//		final Protocol pkProto = em.createQuery("from Protocol p where p.name = :name", Protocol.class)
//				.setParameter("name", PRIMARY_KEY_TABLE)
//				.getSingleResult();
//		final Measurement pk = em.createQuery("from Measurement m where m.name = :name", Measurement.class)
//				.setParameter("name", JOIN_COLUMN)
//				.getSingleResult();		
//		
//		List<Measurement> pkMeasurements = new ArrayList<Measurement>();
//		pkMeasurements.add(pk);
//		
//		this.measurementsByProtocol.put(pkProto, pkMeasurements);
//		
//		for(Entry<Protocol, List<Measurement>> entry : measurementByProtocol.entrySet()) {
//			if(this.measurementsByProtocol.containsKey(entry.getKey())) {
//				this.measurementsByProtocol.get(entry.getKey()).addAll(entry.getValue());
//			} else {
//				this.measurementsByProtocol.put(entry.getKey(), entry.getValue());
//			}
//		}
//		
//		
//		TableModel<ObservationTarget, Measurement, ObservedValue> m = (TableModel<ObservationTarget, Measurement, ObservedValue>) this;
//		//this.backend = new EAVViewBackend(null, VIEW_PREFIX, PRIMARY_KEY_TABLE);
//		this.backend = new PhenoMatrixViewBackend(em, m, VIEW_PREFIX, PRIMARY_KEY_TABLE);
//		try
//		{
//			loadCategories();
//		}
//		catch (DatabaseException ex)
//		{
//			throw new MatrixException(ex);
//		}
//	}
//
//	private void loadCategories() throws DatabaseException {
//		final String qlString = "SELECT m FROM Measurement m JOIN FETCH m.categories c WHERE m.investigation = :investigation";
//		final List<Measurement> measCats = em.createQuery(qlString, Measurement.class)
//					.setParameter("investigation", investigation)
//					.getResultList();
//		
//		for (final Measurement m : measCats)
//		{
//			for (final Category c : m.getCategories())
//			{
//				if(categoryByCode.containsKey(m)) {
//					categoryByCode.get(m).put(c.getCode_String(), c);
//				} else {
//					final HashMap<String, Category> codeCatMap = new HashMap<String, Category>(m.getCategories().size());
//					codeCatMap.put(c.getCode_String().toLowerCase(), c);
//					categoryByCode.put(m, codeCatMap);
//				}
//			}
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getRowCount()
//	 */
//	@Override
//	public Integer getRowCount() throws MatrixException
//	{
//		try
//		{
//			String query = createCountQuery();
//			Number count = (Number) em.createNativeQuery(query).getSingleResult();
//			return count.intValue();
//		}
//		catch (Exception e)
//		{
//			throw new MatrixException(e);
//		}
//	}
//
//	@Override
//	public List<C> getMeasurements() 
//	{
//		final List<C> result = new ArrayList<C>();
//		final List<Column> columns = getColumns();
//		CollectionUtils.forAllDo(columns, new Closure()
//		{
//			@Override
//			public void execute(Object column)
//			{
//				Column c = (Column) column;
//				result.add((C) c.getMeasurement());
//			}
//		});
//		return result;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getRowLimit()
//	 */
//	@Override
//	public int getRowLimit()
//	{
//		return rowLimit;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#setRowLimit(int)
//	 */
//	@Override
//	public void setRowLimit(int rowLimit)
//	{
//		this.rowLimit = rowLimit;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getRowOffset()
//	 */
//	@Override
//	public int getRowOffset()
//	{
//		return rowOffset;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#setRowOffset(int)
//	 */
//	@Override
//	public void setRowOffset(int rowOffset)
//	{
//		this.rowOffset = rowOffset;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getColumns()
//	 */
//	@Override
//	public List<Column> getColumns()
//	{
//		List<Column> result = new ArrayList<Column>();
//		for (Map.Entry<Protocol, List<Measurement>> entry : getMeasurementsByProtocol().entrySet())
//		{
//			for (Measurement measurement : entry.getValue())
//			{
//				Column c = new Column(entry.getKey(), measurement);
//				result.add(c);
//			}
//		}
//		return result;
//	}
//
//	public String createQuery()
//	{
//		return backend.createQuery(false, rules);
//	}
//
//	private String createCountQuery() throws Exception
//	{
//		return backend.createQuery(true, rules);
//	}
//
//	// Todo add category (labels)
//	@SuppressWarnings("unchecked")
//	public List<Object[]> getTypedValues() throws MatrixException
//	{
//		final String sql = createQuery();
//		System.out.println(sql);
//		
//		final List<Object[]> result = em.createNativeQuery(sql)
//			.setMaxResults(getRowLimit())
//			.setFirstResult(getRowOffset())
//			.getResultList();
//		
//		
//		final List<Column> column = getColumns();
//		for(final Object[] row : result) {
//			for(int iCol = 0; iCol < row.length-1; ++iCol) {
//				if(row[iCol] != null) {
//					final Measurement m = column.get(iCol).getMeasurement();
//					
//					if(displayLabels) {
//						row[iCol] =	getCategoryLabel(m, row[iCol].toString());
//					}
//					
//					if(formatDate && column.get(iCol).getType() == Column.ColumnType.DATE) {
//						row[iCol] = row[iCol].toString().split(" ")[0];
//					}					
//				}
//			}
//		}
//		
//		
//			
//		return result;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getScrollableValues(boolean)
//	 */
//	@Override
//	public ScrollableResults getScrollableValues(boolean exportVisibleRows) throws Exception
//	{
//		String sql = createQuery();
//		Session session = ((EntityManagerImpl) em).getSession();
//
//		ScrollableResults sr;
//		if (exportVisibleRows)
//		{
//			int offset = getRowOffset();
//			int limit = getRowLimit();
//			sr = session.createSQLQuery(sql).setFirstResult(offset).setMaxResults(limit).scroll();
//		}
//		else
//		{
//			sr = session.createSQLQuery(sql).scroll();
//		}
//		return sr;
//	}
//	
//	private String getCategoryLabel(final Measurement measurement, final String value) {
//		final Map<String, Category> codeCat = categoryByCode.get(measurement);
//		if(codeCat != null) {
//			final Category category = codeCat.get(value.toLowerCase());
//			if(category != null) {
//				return category.getLabel();
//			} else {
//				return "unkown label!";
//			}
//		}
//		return value;
//	}
//	
//	public Investigation getInvestigation()
//	{
//		return investigation;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#getMeasurementsByProtocol()
//	 */
//	@Override
//	public LinkedHashMap<Protocol, List<Measurement>> getMeasurementsByProtocol()
//	{
//		return measurementsByProtocol;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.molgenis.lifelinesresearchportal.models.PhenoMatrix#setSort(org.molgenis.protocol.Protocol, org.molgenis.pheno.Measurement, java.lang.String)
//	 */
//	@Override
//	public void setSort(Protocol protocol, Measurement measurement, String sortOrder)
//	{
//		this.sortProtocol = protocol;
//		this.sortMeasurement = measurement;
//		this.sortOrder = sortOrder;
//	}
//
//	public void addCondition(int protocolId, int measurementId, String op, Operator operator, String value)
//	{
//		MatrixQueryRule matrixQueryRule = new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, operator, value);
//		matrixQueryRule.setDimIndex(measurementId);
//		matrixQueryRule.setProtocolId(protocolId);
//		rules.add(matrixQueryRule);
//	}
//
//	@Override
//	public Protocol getSortProtocol()
//	{
//		return sortProtocol;
//	}
//
//	@Override
//	public String getSortOrder()
//	{
//		return sortOrder;
//	}
//
//	@Override
//	public Measurement getSortMeasurement()
//	{
//		return sortMeasurement;
//	}
//
//	@Override
//	public String getJoinColumn()
//	{
//		return JOIN_COLUMN;
//	}
//}
