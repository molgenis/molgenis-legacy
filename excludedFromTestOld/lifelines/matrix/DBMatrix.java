package lifelines.matrix;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;
import org.richfaces.model.Ordering;

import app.JpaDatabase;

public class DBMatrix<T> implements PagableMatrix<Column, Integer> {
	private T[][] data;
	private static JpaDatabase db;
	private Investigation investigation;
	
	final String qlTargetIds = "SELECT ot.id FROM ObservationTarget ot WHERE ot.investigation = :investigation";
	final String qlTarget = "SELECT ov.target.id FROM ObservedValue ov WHERE ov.investigation = :investigation AND ov.feature.name = '%s' AND ov.value %s '%s' %s";
	final String qlTargetIdsByFeatureAndTargetIds = "SELECT ov.target.id FROM ObservedValue ov WHERE " +
			"ov.investigation = :investigation AND " +
			"ov.feature.name = :featureName AND ov.target.id IN (%s) ORDER BY ov.value %s";
	final String qlGetData = "SELECT ov FROM ObservedValue ov WHERE " +
			"ov.investigation = :investigation AND " +
			"ov.feature.name IN (%s) AND ov.target.id IN (%s)";
	
	static {
		try {
			db = new JpaDatabase();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<Column> columns = new ArrayList<Column>();
	private List<Integer> observationTargetIds;
	
	private Class<T> klass;
	
	private HashMap<Integer, Integer> targetMap;
	private HashMap<Integer, Integer> targetMapInv;
	
	private HashMap<String, Integer> observeMap;
	
	private int pageSize;
	
	public DBMatrix(Class<T> klass, int pageSize)  {
		this.klass = klass;
		this.pageSize = pageSize;
	}
	
	public void setInvestigation(Investigation investigation) {
		this.investigation = investigation;
	}
	
	
	public T[][] getData() {
		return data;
	}
	
	
	public void addColumn(Column column) {
		this.columns.add(column);
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	public List<Column> getColumns() {
		return this.columns;
	}

	private boolean observationsLoaded = false;
	
	@Override
	public void loadData(int numberOfRows, int offset) throws NumberFormatException, DatabaseException, SQLException, ParseException
	{
		if(!observationsLoaded) {
			loadObservationTargets();
		}
		if(numberOfRows < 0) {
			numberOfRows = pageSize;
		}
		loadDataFromDatabase(numberOfRows, offset);		
	}
	
	private void loadObservationTargets() throws NumberFormatException, DatabaseException, SQLException {
		observationsLoaded = false;
		observationTargetIds = getTargetsIds();

		for(Column column : columns) {
			String filter = (String)column.getFilter();
			if(filter != null && !filter.isEmpty()) {
				observationTargetIds = andIds(observationTargetIds, 
						executeCondition(column.getName(), "=" , filter, observationTargetIds));
			}			
		}		
	}

	protected List<Integer> getTargetsIds() {
		return
			db.getEntityManager().createQuery(qlTargetIds, Integer.class)
			.setParameter("investigation", investigation)
			.getResultList();
	}


	public List<Integer> executeCondition(String featureName, String operator, Object value, List<Integer> potentialTargets) throws DatabaseException, SQLException {
		String ql = String.format(qlTarget, featureName, operator, value, "");
		
		List<Integer> targetIds = new ArrayList<Integer>();
		List<Integer> rs = (List<Integer>)db.getEntityManager()
				.createQuery(ql, Integer.class)
				.setParameter("investigation", investigation)
				.getResultList();
		for(Iterator<Integer> i = rs.iterator(); i.hasNext();) {
			targetIds.add(i.next());
		}
		return targetIds;
	}
	
	public List<Integer> andIds(List<Integer> l0, List<Integer> l1) {
		if(l0 == null || l0.size() == 0) {
			return l1;
		}
		
		if(l1.size() > l0.size()) {
			return andIds(l1, l0);
		}
		
		List<Integer> result = new ArrayList<Integer>();
		for(Integer i : l0) {
			if(l1.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}
	
	private List<Integer> SortByColumn(Column c, List<Integer> observationTargetIds) {
		String orderDir = null; 
		if(c.getOrdering().equals(Ordering.ASCENDING)) {
			orderDir = "ASC";
		} else if (c.getOrdering().equals(Ordering.DESCENDING)) {
			orderDir = "DESC";
		}
			
		String ql = String.format(qlTargetIdsByFeatureAndTargetIds, SQLUtils.toSqlArray(observationTargetIds), orderDir);
          //String ql = String.format(qlTargetIdsByFeatureAndTargetIds, SQLUtils.toSqlArray(observationTargetIds), c.getSort().toString());

            return db.getEntityManager().createQuery(ql, Integer.class)
                    .setParameter("featureName", c.getName())
                    .setParameter("investigation", investigation)
                    .getResultList();

	}
	
	private Column getSortColumn() {
		for(Column c : columns) {
			if(!c.getOrdering().equals(Ordering.UNSORTED)) {
			//if(!c.getSort().equals(Column.Sort.NONE)) {
				return c;
			}
		}
		return null;
	}

	public void loadDataFromDatabase(int numberOfRows, int offset) throws DatabaseException, ParseException, SQLException {
		System.err.println("loadDataFromDatabase");
		data = (T[][])Array.newInstance(klass, numberOfRows, columns.size());
	
		Column sortColumn = getSortColumn();
		if(sortColumn != null) {
			observationTargetIds = SortByColumn(sortColumn, observationTargetIds);
		} else {
			Collections.sort(observationTargetIds); //list is sorted
		}		
		
		targetMap = new HashMap<Integer, Integer>();
		targetMapInv = new HashMap<Integer, Integer>();
		
		observeMap = new HashMap<String, Integer>();
		
		int size = observationTargetIds.size() < offset+numberOfRows ? observationTargetIds.size() : numberOfRows+offset; 
		for(int i = offset, idx = 0; i < size; ++i, ++idx) {
			targetMap.put(observationTargetIds.get(i), idx);
			targetMapInv.put(idx, observationTargetIds.get(i));
		}
		
		List<String> observableFeatureNames = new ArrayList<String>();
		int idx = 0;
		for(Column c : columns) {
			observeMap.put(c.getName(), idx++);
			observableFeatureNames.add(c.getName());
		}
	
		if(targetMap.keySet().size() > 0) {
			String ql = String.format(qlGetData, SQLUtils.escapeSql(SQLUtils.toSqlArray(observableFeatureNames)), SQLUtils.toSqlArray(targetMap.keySet()));
			List<ObservedValue> values = 
				db.getEntityManager()
						.createQuery(ql, ObservedValue.class)
						.setParameter("investigation",investigation)
						.getResultList();

			for(ObservedValue ov : values) {
				int targetId = ov.getTarget().getId();
				String featureName = ov.getFeature().getName();
				data[targetMap.get(targetId)][observeMap.get(featureName)] = (T)ov.getValue();
			}		
		} else {
			System.err.println("no targets");
		}
		

		
		System.err.println("data loaded");
		observationsLoaded = false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.length; ++i) {
			sb.append(targetMapInv.get(i) + "|");			
			for(int j = 0; j < data[i].length; ++j) {
				sb.append(data[i][j]);
				sb.append(" | ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public List<ArrayList<String>> toList() {
		List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < data.length; ++i) {
			result.add(new ArrayList<String>());
			result.get(0).add("" + targetMapInv.get(i));
		
			for(int j = 0; j < data[i].length; ++j) {
				result.get(0).add("" + data[i][j]);
			}
		}
		return result;
	}
	



	public void addColumn(String columnName) {
		this.columns.add(new Column(columnName, Column.ColumnType.String, ""));
	}

	@Override
	public List<Integer> getRows() {
		return this.observationTargetIds;
	}

	@Override
	public int getNumberOfRows() {
		try {
			loadObservationTargets();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return observationTargetIds.size();
	}

	@Override
	public Investigation getInvestigation() {
		return investigation;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public SimplePager getColumnPager() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setColumnPager(SimplePager pager) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

//	@Override
//	public boolean isColumnPageChanged() {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public void setColumnPageChanged(boolean columnPageChanged) {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException();
//	}

    @Override
    public List<Column> getVisableColumns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDirty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDirty(boolean dirty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}