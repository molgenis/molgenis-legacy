package matrix;

import java.util.ArrayList;
import java.util.List;

import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.Filter;
import org.molgenis.matrix.component.RenderableMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;

public class XgapRenderableMatrix implements RenderableMatrix<ObservationElement, ObservationElement, Object>{
	
	private List<ObservationElement> visibleRows;
	private List<ObservationElement> visibleCols;
	private Object[][] visibleValues;
	private int totalNumberOfRows;
	private int totalNumberOfCols;
	private int filteredNumberOfRows;
	private int filteredNumberOfCols;
	private int rowIndex;
	private int colIndex;
	private List<Filter> filters;
	private String constraintLogic;
	private String screenName;
	
	public XgapRenderableMatrix(Database db, Data data, DataMatrixHandler dmh, String screenName) throws Exception {
		
		boolean verifiedBackend = false;
		verifiedBackend = dmh.isDataStoredIn(data, data.getStorage());
		AbstractDataMatrixInstance<Object> dmi = null;
		this.screenName = screenName;
		
		if (verifiedBackend)
		{
			dmi = dmh.createInstance(data);
		}
		else
		{
			throw new Exception("Could not verify existence of data source");
		}
		
		rowIndex = 0;
		visibleRows = db.find(ObservationElement.class, new QueryRule("name", Operator.IN, dmi.getRowNames()));
		System.out.println("dmi.getRowNames() = " + dmi.getRowNames());
		System.out.println("visibleRows size = " + visibleRows.size());
		totalNumberOfRows = visibleRows.size();
		
		colIndex = 0;
		visibleCols = db.find(ObservationElement.class, new QueryRule("name", Operator.IN, dmi.getColNames()));
		System.out.println("dmi.getColNames() = " + dmi.getColNames());
		System.out.println("visibleCols size = " + visibleCols.size());
		totalNumberOfCols = visibleCols.size();
		
		visibleValues = dmi.getElements();
		
		// Filter shizzle: TODO
		filters = new ArrayList<Filter>();
		filteredNumberOfRows = totalNumberOfRows;
		filteredNumberOfCols = totalNumberOfCols;
		constraintLogic = "";
		
	}

	
	@Override
	public String renderValue(Object value) {
		if(value == null){
			return "";
		}else{
			return value.toString();
		}
	}

	@Override
	public String renderRow(ObservationElement row) {
		return row.getName();
	}

	@Override
	public String renderCol(ObservationElement col) {
		return col.getName();
	}

	@Override
	public List<ObservationElement> getVisibleRows() {
		return visibleRows;
	}

	@Override
	public List<ObservationElement> getVisibleCols() {
		return visibleCols;
	}

	@Override
	public Object[][] getVisibleValues() {
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

	
	

}
