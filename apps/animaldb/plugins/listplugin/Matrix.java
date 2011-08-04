package plugins.listplugin;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
import org.molgenis.util.CsvPrintWriter;
import org.molgenis.util.SpreadsheetWriter;

/**
 * An interface to a matrix, not unlike the Matrix class in the R-project. Row
 * and column names are required! (this may be loosened in a later version)
 * 
 * @author Morris Swertz, Joeri van der Velde
 * @param <E>
 *            the generic type of the matrix. E.g. String, Double etc.
 */
public abstract class Matrix<E>
{

	Logger logger = Logger.getLogger(getClass().getSimpleName());

	private String name;
	private List<String> rowNames;
	private List<String> colNames;
	private int numberOfRows;
	private int numberOfCols;

	// getters for vectors
	/**
	 * Retrieve a matrix column by index
	 * 
	 * @param colIndex
	 *            in 1..ncol()
	 * @return array with column values
	 * @throws Exception
	 *             if the column index is not known
	 */
	public abstract Object[] getCol(int colIndex) throws Exception;

	/**
	 * Retrieve a matrix row by index
	 * 
	 * @param rowIndex
	 *            ind 1..nrow()
	 * @return array with row values
	 * @throws Exception
	 *             if the rowindex is not known
	 */
	public abstract Object[] getRow(int rowIndex) throws Exception;
	
	/**
	 * Retrieve all of this matrix' elements in a twodimensional primitive array (Object[nrows][ncols])
	 * @return
	 * @throws Exception
	 */
	public abstract Object[][] getElements() throws Exception;

	/**
	 * Retrieve a matrix column by column name
	 * 
	 * @param colName
	 * @return array with column values
	 * @throws Exception 
	 */
	//public abstract E[] col(String colName) throws Exception;
	public Object[] getCol(String colName) throws Exception{
		return getCol(colNames.indexOf(colName));
	}
	
	/**
	 * Retrieve a matrix row by row name
	 * 
	 * @param rowName
	 * @return array with row values
	 * @throws Exception 
	 */
	//public abstract E[] row(String rowName) throws Exception;
	public Object[] getRow(String rowName) throws Exception{
		return getRow(rowNames.indexOf(rowName));
	}

	/**
	 * Retrieve a partition of the matrix by listing rowname(s) and colname(s)
	 * 
	 * @param rowNames
	 * @param colNames
	 * @return a matrix object giving access to the sub matrix
	 * @throws Exception
	 *             if row or column names are not known
	 */
	//public abstract Matrix<E> get(List<String> rowNames, List<String> colNames) throws Exception;
	public Matrix<E> getSubMatrix(List<String> rowNames, List<String> colNames) throws Exception{
		int[] rowIndices = new int[rowNames.size()];
		int[] colIndices = new int[colNames.size()];
		
		for(int i=0; i<rowNames.size(); i++){
			rowIndices[i] = this.rowNames.indexOf(rowNames.get(i));
		}
		
		for(int i=0; i<colNames.size(); i++){
			colIndices[i] = this.colNames.indexOf(colNames.get(i));
		}
		
		return getSubMatrix(rowIndices, colIndices);
	}

	/**
	 * Retrieve a partition of the matrix by listing row and column indices
	 * 
	 * @param rowIndices
	 * @param colIndices
	 * @return a matrix object giving access to the sub matrix
	 * @throws Exception
	 *             if row or column names are not known
	 */
	public abstract Matrix<E> getSubMatrix(int[] rowIndices, int[] colIndices) throws Exception;


	/** Retrieve a partition of the matrix by listing rowindex, rownlenght, colindex and collenght
	 * @throws Exception 
	 * 
	 */
	public abstract Matrix<E> getSubMatrix(int row, int nRows, int col, int nCols) throws Exception;
	
	/**
	 * Retrieve a partition of the matrix, using row/colnames and offsets
	 * @param rowName
	 * @param nRows
	 * @param colName
	 * @param nCols
	 * @return
	 * @throws Exception 
	 */
	public Matrix<E> getSubMatrix(String rowName, int nRows, String colName, int nCols) throws Exception{
		return getSubMatrix(this.rowNames.indexOf(rowName), nRows, this.colNames.indexOf(colName), nCols);
	}
	
	/**
	 * Retrieve one cell of the matrix. This may be very inefficient unless the
	 * matrix has been read in memory
	 * 
	 * @param rowIndex
	 * @param colIndex
	 * @return one E or null if not known
	 */
	public abstract Object getElement(int rowIndex, int colIndex) throws Exception;

	/**
	 * Retrieve the row index for a row name
	 * 
	 * @param rowName
	 * @return rowIndex
	 * @throws Exception
	 *             if the rowname is not known
	 */
	public int getRowIndexForName(String rowName) throws Exception
	{
		if (!rowNames.contains(rowName)) throw new Exception("rowname " + rowName + " not known in matrix");
		return this.rowNames.indexOf(rowName);
	}

	/**
	 * Retrieve the column index for a column name
	 * 
	 * @param colName
	 * @return colindex
	 * @throws Exception
	 *             if the column name is not known
	 */
	public int getColIndexForName(String colName) throws Exception
	{
		if (!colNames.contains(colName)) throw new Exception("colname " + colName + " not known in matrix");
		return this.colNames.indexOf(colName);
	}

	/**
	 * get value by row and column name
	 * 
	 * @throws Exception
	 */
	public Object getElement(String rowName, String colName) throws Exception
	{
		return getElement(this.getRowIndexForName(rowName), this.getColIndexForName(colName));
	}
	
	/**
	 * Prints the complete matrix including headers. Uses fast retrieval but can cost a lot of memory.
	 */
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		try{
			for (String col : getColNames())
			{
				result.append("\t" + col);
			}
			result.append("\n");
			Object[][] elements = getElements();
			for(int rowIndex = 0; rowIndex < elements.length; rowIndex++){
				result.append(getRowNames().get(rowIndex));
				for(int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++){
					if(elements[rowIndex][colIndex] == null){
						result.append("\t");
					}else{
						result.append("\t" + elements[rowIndex][colIndex]);
					}
				}
				result.append("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}
	
	// Used in a plugin context
	public void writeToCsvWriter(PrintWriter out) throws Exception{
		SpreadsheetWriter cfr = new CsvPrintWriter(out);
		cfr.writeMatrix(getRowNames(), getColNames(), getElements());
		cfr.close();
	}
	
	// Used in a servlet context
	public void writeToPrintWriter(PrintWriter out) throws Exception{
		Object[][] elements = getElements();
		for (String col : getColNames())
		{
			out.write("\t" + col);
		}
		out.write("\n");
		for(int rowIndex = 0; rowIndex < elements.length; rowIndex++){
			out.write(getRowNames().get(rowIndex));
			for(int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++){
				if(elements[rowIndex][colIndex] == null){
					out.write("\t");
				}else{
					out.write("\t" + elements[rowIndex][colIndex]);
				}
			}
			out.write("\n");
		}
	}
	
	public File writeToExcelFile() throws Exception{
		/* Create tmp file */
		File excelFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + this.getName() + ".xls");
		
		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(excelFile,
				ws);
		WritableSheet s = workbook.createSheet("Sheet1", 0);
		
		/* Format the fonts */
	    WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 
	      10, WritableFont.BOLD);
	    WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
	    headerFormat.setWrap(false);
	    WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 
	  	      10, WritableFont.NO_BOLD);
	  	   WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	  	    cellFormat.setWrap(false);
	    
	    /* Write column headers */
	    List<String> colNames = this.getColNames();
	    for(int i=0; i<colNames.size(); i++){
	    	Label l = new Label(i+1, 0, colNames.get(i), headerFormat);
	    	s.addCell(l);
	    }
	    
	    /* Write row headers */
	    List<String> rowNames = this.getRowNames();
	    for(int i=0; i<rowNames.size(); i++){
	    	Label l = new Label(0, i+1, rowNames.get(i), headerFormat);
	    	s.addCell(l);
	    }
	    
	    /* Write elements */
	    Object[][] elements = getElements();
	    if(elements[0][0] instanceof Number){
	    	//TODO: format numbers?
	    	for(int i=0; i<this.getNumberOfCols(); i++){
		    	for(int j=0; j<this.getNumberOfRows(); j++){
		    		Label l = new Label(i+1, j+1, elements[j][i].toString(), cellFormat);
			    	s.addCell(l);
		    	}
		    }
	    }else{
	    	for(int i=0; i<this.getNumberOfCols(); i++){
		    	for(int j=0; j<this.getNumberOfRows(); j++){
		    		if(elements[j][i] != null){
		    			Label l = new Label(i+1, j+1, elements[j][i].toString(), cellFormat);
		    			s.addCell(l);
		    		}else{
		    			s.addCell(new Label(i+1, j+1, "", cellFormat));
		    		}
			    	
		    	}
		    }
	    }
	    
		
	    /* Close workbook */
		workbook.write();
	    workbook.close(); 

	   return excelFile;
	}
	
	/**
	 * Get the matrix as a single file representation. Eg. the binary implementation returns the location of the source file, while the database implementation must first export the data and return this file. This exported file has the proper escaped investigation/data+.txt name syntax, but resides in the tmp directory.
	 * @return
	 * @throws Exception
	 */
	public abstract File getAsFile() throws Exception;

	// GETTERS AND SETTERS
	
	/** The name of this matrix */
	public String getName(){
		return name;
	}
	
	/** The number of columns in the matrix */
	public int getNumberOfCols() {
		return numberOfCols;
	}

	/** The number of rows in the matrix */
	public int getNumberOfRows()
	{
		return numberOfRows;
	}

	/** All column names for this matrix in order of ascending column index */
	public List<String> getColNames()
	{
		return colNames;
	}

	/** All row names for this matrix in order of ascending row index */
	public List<String> getRowNames()
	{
		return rowNames;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	/** protected method for subclasses to label the columns */
	protected void setColNames(List<String> list)
	{
		this.colNames = list;
	}

	/** protected method for subclasses to label the rows */
	protected void setRowNames(List<String> list)
	{
		this.rowNames = list;
	}
	
	/** protected method to set number of columns */
	protected void setNumberOfCols(int numberOfCols) {
		this.numberOfCols = numberOfCols;
	}

	/** protected method to set number of rows */
	protected void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
}
