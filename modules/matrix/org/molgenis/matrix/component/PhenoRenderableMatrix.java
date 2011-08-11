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

public class PhenoRenderableMatrix implements RenderableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>{
	
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
	Logger logger = Logger.getLogger(PhenoRenderableMatrix.class);
	
	public PhenoRenderableMatrix(Database db) throws DatabaseException {
		
		rowIndex = 0;
		visibleRows = db.find(ObservationTarget.class, new QueryRule(Operator.LIMIT, 100)); // TODO: remove limit once paging is possible
		totalNumberOfRows = visibleRows.size();
		
		colIndex = 0;
		visibleCols = db.find(ObservableFeature.class);
		totalNumberOfCols = visibleCols.size();
		
		visibleValues = new List[totalNumberOfRows][totalNumberOfCols];
		
		int row = 0;
		for (ObservationTarget t : visibleRows) {
			int col = 0;
			for (ObservableFeature f : visibleCols) {
				List<ObservedValue> valueList;
				try {
					valueList = db.find(ObservedValue.class, 
							new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, f.getId()), 
							new QueryRule(ObservedValue.TARGET, Operator.EQUALS, t.getId()));
					visibleValues[row][col] = valueList;
				} catch (Exception e) {
					logger.info("Filling PhenoRenderableMatrix failed at " + row + ", " + col);
					e.printStackTrace();
				}
				col++;
			}
			row ++;
		}
		
		// Filter shizzle: TODO
		filters = new ArrayList<Filter>();
		filteredNumberOfRows = totalNumberOfRows;
		filteredNumberOfRows = totalNumberOfCols;
		constraintLogic = "";
		
	}
	
	public String toHtml() {
		String returnString = "<table><tr><th></th>";
		for (ObservableFeature f : visibleCols) {
			returnString += ("<th>" + f.getName() + "</th>");
		}
		returnString += "</tr>";
		int rowIndex = 0;
		for (ObservationTarget t : visibleRows) {
			returnString += ("<th>" + t.getName() + "</th>");
			returnString += "<tr>";
			for (int colIndex = 0; colIndex < visibleCols.size(); colIndex++) {
				returnString += ("<td>" + renderValue(visibleValues[rowIndex][colIndex]) + "</td>");
				colIndex++;
			}
			returnString += "</tr>";
			rowIndex++;
		}
		returnString += "</table>";
		return returnString;
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

	
	

}
