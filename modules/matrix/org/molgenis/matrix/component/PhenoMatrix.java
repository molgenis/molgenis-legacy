package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrix implements RenderableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
	SliceableMatrix {
	
	private List<ObservationTarget> visibleRows;
	private List<ObservableFeature> visibleCols;
	private List<ObservedValue>[][] visibleValues;
	private int totalNumberOfRows;
	private int totalNumberOfCols;
	private int filteredNumberOfRows;
	private int filteredNumberOfCols;
	private int rowIndex;
	private int colIndex;
	private List<Filter> filters;
	private String constraintLogic;
	private String screenName;
	private Database db;
	Logger logger = Logger.getLogger(PhenoMatrix.class);
	
	public PhenoMatrix(Database db, String screenName) throws DatabaseException {
		
		this.db = db;
		this.screenName = screenName;
		
		rowIndex = 0;
		colIndex = 0;
		
		totalNumberOfRows = db.count(ObservationTarget.class);
		totalNumberOfCols = db.count(ObservableFeature.class);
		
		// Filter shizzle: TODO
		filters = new ArrayList<Filter>();
		filteredNumberOfRows = totalNumberOfRows;
		filteredNumberOfRows = totalNumberOfCols;
		constraintLogic = "";
	}
	
	public PhenoMatrix(RenderableMatrix matrix) throws Exception {
		this.filters = matrix.getFilters();
		this.constraintLogic = matrix.getConstraintLogic();
		this.colIndex = matrix.getColIndex();
		this.filteredNumberOfCols = matrix.getFilteredNumberOfCols();
		this.filteredNumberOfRows = matrix.getFilteredNumberOfRows();
		this.rowIndex = matrix.getRowIndex();
		this.screenName = matrix.getScreenName();
		this.totalNumberOfCols = matrix.getTotalNumberOfCols();
		this.totalNumberOfRows = matrix.getTotalNumberOfRows();
		this.visibleCols = matrix.getVisibleCols();
		this.visibleRows = matrix.getVisibleRows();
		this.visibleValues = (List<ObservedValue>[][]) matrix.getVisibleValues();
	}
	
	public PhenoMatrix(RenderableMatrix matrix, List<ObservationTarget> visibleRows,
			List<ObservableFeature> visibleCols, List<ObservedValue> vals) throws Exception {
		
		// Step 1: make copy
		this(matrix);
		
		// Step 2: overrides from parameters
		this.visibleCols = visibleCols;
		this.visibleRows = visibleRows;
		this.visibleValues = new List[visibleRows.size()][visibleCols.size()];
		
		int i = 0;
		for (ObservationTarget target : visibleRows) {
			int j = 0;
			for (ObservableFeature feature : visibleCols) {
				for (ObservedValue val : vals) {
					if (val.getTarget_Id().intValue() == target.getId().intValue() && val.getFeature_Id().intValue() == feature.getId().intValue()) {
						if (visibleValues[i][j] == null) {
							visibleValues[i][j] = new ArrayList<ObservedValue>();
						}
						visibleValues[i][j].add(val);
						vals.remove(val);
						break;
					}
				}
				j++;
			}
			i++;
		}
	}

	public String toString() {

		return "";
	}
	
	@Override
	public String renderValue(List<ObservedValue> values) {
		if (values != null && values.size() > 0) {
			if (values.get(0).getValue() != null) {
				return values.get(0).getValue();
			} else {
				if (values.get(0).getRelation_Name() != null) {
					return values.get(0).getRelation_Name();
				} else {
					return "";
				}
			}
		} else {
			return "";
		}
	}

	@Override
	public String renderRow(ObservationTarget row) {
		return row.getName();
	}

	@Override
	public String renderCol(ObservableFeature col) {
		return col.getName();
	}

	@Override
	public List<ObservationTarget> getVisibleRows() {
		return visibleRows;
	}

	@Override
	public List<ObservableFeature> getVisibleCols() {
		return visibleCols;
	}

	@Override
	public List<ObservedValue>[][] getVisibleValues() {
		return visibleValues;
	}

	@Override
	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public int getColIndex() {
		return colIndex;
	}

	@Override
	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	public String getConstraintLogic() {
		return constraintLogic;
	}

	@Override
	public int getTotalNumberOfRows() {
		return totalNumberOfRows;
	}

	@Override
	public int getTotalNumberOfCols() {
		return totalNumberOfCols;
	}

	@Override
	public int getFilteredNumberOfRows() {
		return filteredNumberOfRows;
	}

	@Override
	public int getFilteredNumberOfCols() {
		return filteredNumberOfCols;
	}
	
	@Override
	public String getScreenName() {
		return screenName;
	}

	@Override
	public RenderableMatrix getSubMatrixByOffset(RenderableMatrix matrix,
			int rowStart, int nRows, int colStart, int nCols) throws Exception  {
		
		visibleRows = db.find(ObservationTarget.class, new QueryRule(Operator.OFFSET, rowStart),
				new QueryRule(Operator.LIMIT, nRows));
		
		visibleCols = db.find(ObservableFeature.class, new QueryRule(Operator.OFFSET, colStart),
				new QueryRule(Operator.LIMIT, nCols));
		
		List<Integer> targetIdList = new ArrayList<Integer>();
		for (ObservationTarget t : visibleRows) {
			targetIdList.add(t.getId());
		}
		
		List<Integer> featureIdList = new ArrayList<Integer>();
		for (ObservableFeature f : visibleCols) {
			featureIdList.add(f.getId());
		}
		
		List<ObservedValue> vals = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET, Operator.IN, targetIdList),
				new QueryRule(ObservedValue.FEATURE, Operator.IN, featureIdList));
		
		return new PhenoMatrix(matrix, visibleRows, visibleCols, vals);
		
	}

	@Override
	public RenderableMatrix getSubMatrixByRowValueFilter(
			RenderableMatrix matrix, QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderableMatrix getSubMatrixByRowHeaderFilter(
			RenderableMatrix matrix, QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderableMatrix getSubMatrixByColValueFilter(
			RenderableMatrix matrix, QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderableMatrix getSubMatrixByColHeaderFilter(
			RenderableMatrix matrix, QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
