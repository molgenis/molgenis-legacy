package matrix;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import matrix.general.DataMatrixHandler;
import matrix.general.MatrixReadException;

import org.apache.log4j.Logger;
import org.molgenis.core.Nameable;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.Filter;
import org.molgenis.matrix.component.RenderableMatrix;
import org.molgenis.matrix.component.SliceableMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.CsvFileWriter;

/**
 * Abstract implementation for MatrixInterface. Some functions require XGAP
 * components, therefore this class should be seperated into Matrix.java, a
 * generic abstract class with functions that work on generic types, and
 * XgapMatrix.java, with functions that require a few XGAP specific data types.
 * 
 * @author Morris Swertz, Joeri van der Velde
 * @param <E>
 *            the generic type of the matrix. E.g. String, Double etc.
 */
public abstract class AbstractDataMatrixInstance<E> implements DataMatrixInstance, RenderableMatrix<ObservationElement, ObservationElement, Object>, SliceableMatrix
{

	/**
	 * Protected method for subclasses to add the Data description. This method
	 * MUST be called in the constructor to instantiate the matrix.
	 */
	protected void setData(Data data)
	{
		this.data = data;
	}

	/**
	 * Protected method for subclasses to label the columns. This method MUST be
	 * called in the constructor to instantiate the matrix.
	 */
	protected void setColNames(List<String> list)
	{
		this.colNames = list;
	}

	/**
	 * Protected method for subclasses to label the rows. This method MUST be
	 * called in the constructor to instantiate the matrix.
	 */
	protected void setRowNames(List<String> list)
	{
		this.rowNames = list;
	}

	/**
	 * Protected method to set number of columns. This method MUST be called in
	 * the constructor to instantiate the matrix.
	 */
	protected void setNumberOfCols(int numberOfCols)
	{
		this.numberOfCols = numberOfCols;
	}

	/**
	 * Protected method to set number of rows. This method MUST be called in the
	 * constructor to instantiate the matrix.
	 */
	protected void setNumberOfRows(int numberOfRows)
	{
		this.numberOfRows = numberOfRows;
	}

	// Local variables
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	private Data data;
	private List<String> rowNames;
	private List<String> colNames;
	private int numberOfRows;
	private int numberOfCols;

	// Implementations of MatrixInterface
	public Data getData()
	{
		return data;
	}

	public Object[] getCol(String colName) throws Exception
	{
		return getCol(colNames.indexOf(colName));
	}

	public Object[] getRow(String rowName) throws Exception
	{
		return getRow(rowNames.indexOf(rowName));
	}

	public AbstractDataMatrixInstance<Object> getSubMatrix(List<String> rowNames, List<String> colNames)
			throws Exception
	{
		int[] rowIndices = new int[rowNames.size()];
		int[] colIndices = new int[colNames.size()];

		for (int i = 0; i < rowNames.size(); i++)
		{
			rowIndices[i] = this.rowNames.indexOf(rowNames.get(i));
		}

		for (int i = 0; i < colNames.size(); i++)
		{
			colIndices[i] = this.colNames.indexOf(colNames.get(i));
		}

		return getSubMatrix(rowIndices, colIndices);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(String rowName, int nRows, String colName, int nCols)
			throws Exception
	{
		return getSubMatrixByOffset(this.rowNames.indexOf(rowName), nRows, this.colNames.indexOf(colName), nCols);
	}

	public int getRowIndexForName(String rowName) throws Exception
	{
		if (!rowNames.contains(rowName)) throw new MatrixReadException("rowname " + rowName + " not known in matrix");
		return this.rowNames.indexOf(rowName);
	}

	public int getColIndexForName(String colName) throws Exception
	{
		if (!colNames.contains(colName)) throw new MatrixReadException("colname " + colName + " not known in matrix");
		return this.colNames.indexOf(colName);
	}

	public Object getElement(String rowName, String colName) throws Exception
	{
		return getElement(this.getRowIndexForName(rowName), this.getColIndexForName(colName));
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();
		try
		{
			for (String col : getColNames())
			{
				result.append("\t" + col);
			}
			result.append("\n");
			Object[][] elements = getElements();
			for (int rowIndex = 0; rowIndex < elements.length; rowIndex++)
			{
				result.append(getRowNames().get(rowIndex));
				for (int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++)
				{
					if (elements[rowIndex][colIndex] == null)
					{
						result.append("\t");
					}
					else
					{
						result.append("\t" + elements[rowIndex][colIndex]);
					}
				}
				result.append("\n");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.toString();
	}

	public File getAsExcelFile() throws Exception
	{
		/* Create tmp file */
		File excelFile = new File(System.getProperty("java.io.tmpdir") + File.separator + this.getData().getName()
				+ ".xls");

		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(excelFile, ws);
		WritableSheet s = workbook.createSheet("Sheet1", 0);

		/* Format the fonts */
		WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
		headerFormat.setWrap(false);
		WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setWrap(false);

		/* Write column headers */
		List<String> colNames = this.getColNames();
		for (int i = 0; i < colNames.size(); i++)
		{
			Label l = new Label(i + 1, 0, colNames.get(i), headerFormat);
			s.addCell(l);
		}

		/* Write row headers */
		List<String> rowNames = this.getRowNames();
		for (int i = 0; i < rowNames.size(); i++)
		{
			Label l = new Label(0, i + 1, rowNames.get(i), headerFormat);
			s.addCell(l);
		}

		/* Write elements */
		Object[][] elements = getElements();
		if (elements[0][0] instanceof Number)
		{
			// TODO: format numbers?
			for (int i = 0; i < this.getNumberOfCols(); i++)
			{
				for (int j = 0; j < this.getNumberOfRows(); j++)
				{
					Label l = new Label(i + 1, j + 1, elements[j][i].toString(), cellFormat);
					s.addCell(l);
				}
			}
		}
		else
		{
			for (int i = 0; i < this.getNumberOfCols(); i++)
			{
				for (int j = 0; j < this.getNumberOfRows(); j++)
				{
					if (elements[j][i] != null)
					{
						Label l = new Label(i + 1, j + 1, elements[j][i].toString(), cellFormat);
						s.addCell(l);
					}
					else
					{
						s.addCell(new Label(i + 1, j + 1, "", cellFormat));
					}

				}
			}
		}

		/* Close workbook */
		workbook.write();
		workbook.close();

		return excelFile;
	}

	public int getNumberOfCols()
	{
		return numberOfCols;
	}

	public int getNumberOfRows()
	{
		return numberOfRows;
	}

	public List<String> getColNames()
	{
		return colNames;
	}

	public List<String> getRowNames()
	{
		return rowNames;
	}
	
	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowEntityValues(Database db, QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowEntityValues((AbstractDataMatrixInstance<Object>) this, db, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColEntityValues(Database db, QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByColEntityValues((AbstractDataMatrixInstance<Object>) this, db, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByRowMatrixValues(QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowMatrixValues((AbstractDataMatrixInstance<Object>) this, rules);
	}

	public AbstractDataMatrixInstance<Object> getSubMatrixFilterByColMatrixValues(QueryRule... rules) throws Exception
	{
		return AbstractDataMatrixQueries.getSubMatrixFilterByColMatrixValues((AbstractDataMatrixInstance<Object>) this, rules);
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByRowEntityValues(boolean asc) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByColEntityValues(Database db, boolean asc)
			throws Exception
	{
		QueryRule sorting = null;
		if (asc)
		{
			sorting = new QueryRule(Operator.SORTASC);
		}
		else
		{
			sorting = new QueryRule(Operator.SORTDESC);
		}
		List<String> rowNames = this.getRowNames();
		List<Nameable> subCol = (List<Nameable>) db.find(db.getClassForName(this.getData().getFeatureType()), sorting);
		List<String> colNames = new ArrayList<String>();
		for (Nameable i : subCol)
		{
			colNames.add(i.getName());
		}
		AbstractDataMatrixInstance res = this.getSubMatrix(rowNames, colNames);
		return res;
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByRowMatrixValues(boolean asc) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> getMatrixSortByColMatrixValues(Database db, boolean asc)
			throws Exception
	{
		List<Data> result = db.find(Data.class, new QueryRule("name", Operator.EQUALS, this.getData().getName()));
		Data thisData = null;
		if (result.size() < 1)
		{
			// no Data object for this one..
			throw new Exception("Matrix has no 'Data' description");
		}
		else if (result.size() > 1)
		{
			// multiple Data objects!
			throw new Exception("Multiple 'Data' descriptions for name '" + this.getData().getName() + "'.");
		}
		else
		{
			thisData = result.get(0);
		}
		Query q = null;
		q.addRules(new QueryRule("data", Operator.EQUALS, thisData.getId()));

		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performUnion(AbstractDataMatrixInstance<Object> N) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performIntersection(AbstractDataMatrixInstance<Object> N)
			throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performDifference(AbstractDataMatrixInstance<Object> I) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performExclusion(AbstractDataMatrixInstance<Object> I) throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public AbstractDataMatrixInstance<Object> performTransposition(AbstractDataMatrixInstance<Object> I)
			throws Exception
	{
		throw new Exception("Unimplemented.");
	}

	public void writeToCsvWriter(PrintWriter out) throws Exception
	{
		CsvWriter cfr = new CsvWriter(out);
		cfr.writeMatrix(getRowNames(), getColNames(), getElements());
		cfr.close();
	}

	public void writeToPrintWriter(PrintWriter out) throws Exception
	{
		Object[][] elements = getElements();
		for (String col : getColNames())
		{
			out.write("\t" + col);
		}
		out.write("\n");
		for (int rowIndex = 0; rowIndex < elements.length; rowIndex++)
		{
			out.write(getRowNames().get(rowIndex));
			for (int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++)
			{
				if (elements[rowIndex][colIndex] == null)
				{
					out.write("\t");
				}
				else
				{
					out.write("\t" + elements[rowIndex][colIndex]);
				}
			}
			out.write("\n");
		}
	}

	/**
	 * Get the matrix in the shape of a one-dimensional list. Instead of a
	 * 'grid' or values, just get a default importable CSV list of, for example
	 * feature - target - value.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> getAsObservedValueList() throws Exception{
		List<String> res = new ArrayList<String>();
		
		//get a shorthand to all the matrix values
		Object[][] elements = getElements();
		
		//add header
		//TODO: finish this part
		res.add(this.getData().getTarget_Name() + "\t" + this.getData().getFeature_Name() + "\t" + "etc");
		
		//iterate over all the values and add in the form of a list
		for (int rowIndex = 0; rowIndex < elements.length; rowIndex++)
		{
			for (int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++)
			{
				res.add(getRowNames().get(rowIndex) + "\t" + getColNames().get(colIndex) + "\t" + elements[rowIndex][colIndex]);
			}
		}
		
		return res;
	}
	
	
	/********************************************************/
	/**************** RENDERABLE & SLICEABLE ****************/
	/********************************************************/
	
	/*
	 * Implementations for RenderableMatrix 'render' functions
	 */
	
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
		return row.getName(); //TODO: render entity attributes
	}

	@Override
	public String renderCol(ObservationElement col) {
		return col.getName(); //TODO: render entity attributes
	}
	
	/*
	 * RenderableMatrix member variables
	 */
	
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
	
	/*
	 * Helper variable: database
	 */
	
	private Database db;

	/*
	 * Getters for RenderableMatrix member variables
	 */
	
	@Override
	public List<ObservationElement> getVisibleRows() {
		return visibleRows;
	}

	@Override
	public List<ObservationElement> getVisibleCols() {
		return visibleCols;
	}

	@Override
	public Object[][] getVisibleValues() throws Exception {
		return this.getElements();
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
		return this.getNumberOfRows();
	}

	@Override
	public int getTotalNumberOfCols() {
		return this.getNumberOfCols();
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
	

	/**
	 * Prepares this AbstractDataMatrixInstance for rendering
	 * @param screenName
	 * @throws Exception
	 */
	public void setupForRendering(String screenName) throws Exception{
		
		this.screenName = screenName;
		
		QueryRule investigation = new QueryRule(Investigation.NAME, Operator.EQUALS, this.getData().getInvestigation_Name());
		QueryRule rowNames = new QueryRule(ObservationElement.NAME, Operator.IN, this.getRowNames());
		QueryRule colNames = new QueryRule(ObservationElement.NAME, Operator.IN, this.getColNames());
		
		//TODO: FAILS FOR BINARY WITHOUT DB ANNOTATIONS
		
		rowIndex = 0;
		visibleRows = db.find
				(ObservationElement.class, 
						investigation, 
						rowNames);
		totalNumberOfRows = visibleRows.size();
		
		colIndex = 0;
		visibleCols = db.find(ObservationElement.class, investigation, colNames);
		totalNumberOfCols = visibleCols.size();
		
		visibleValues = this.getElements();
		
		// Filters/sorting: TODO
		filters = new ArrayList<Filter>();
		filteredNumberOfRows = totalNumberOfRows;
		filteredNumberOfCols = totalNumberOfCols;
		constraintLogic = "";
	}
	
	/*
	 * Sliceable implementation(non-Javadoc)
	 */
	@Override
	public RenderableMatrix getSubMatrixByOffset(RenderableMatrix matrix,
			int rowIndex, int nRows, int colIndex, int nCols) throws Exception {
		return this.getSubMatrixByOffset(rowIndex, nRows, colIndex, nCols);
	}

	@Override
	public RenderableMatrix getSubMatrixByRowValueFilter(
			RenderableMatrix matrix, QueryRule q) throws Exception {
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowMatrixValues((AbstractDataMatrixInstance<Object>) this, q);
	}

	@Override
	public RenderableMatrix getSubMatrixByRowHeaderFilter(
			RenderableMatrix matrix, QueryRule q) throws Exception {
		return AbstractDataMatrixQueries.getSubMatrixFilterByRowEntityValues((AbstractDataMatrixInstance<Object>) this, db, q);
	}

	@Override
	public RenderableMatrix getSubMatrixByColValueFilter(
			RenderableMatrix matrix, QueryRule q) throws Exception {
		return AbstractDataMatrixQueries.getSubMatrixFilterByColMatrixValues((AbstractDataMatrixInstance<Object>) this, q);
	}

	@Override
	public RenderableMatrix getSubMatrixByColHeaderFilter(
			RenderableMatrix matrix, QueryRule q) throws Exception {
		return AbstractDataMatrixQueries.getSubMatrixFilterByColEntityValues((AbstractDataMatrixInstance<Object>) this, db, q);
	}
	
}
