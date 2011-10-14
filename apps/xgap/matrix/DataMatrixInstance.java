package matrix;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.matrix.MatrixException;

public interface DataMatrixInstance
{

	/**
	 * Get the data description for this matrix. For example, xgap.Data: a
	 * generic structure for describing data matrices such as genotype result,
	 * gene expression measurement, QTL calculation, etc.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return data from Matrix
	 * @throws Exception
	 */
	public Data getData();
	
	/**
	 * Get the number of columns in this matrix.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return int
	 */
	public int getNumberOfCols();
	
	/**
	 * Get the number of rows in this matrix.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return int
	 */
	public int getNumberOfRows();
	
	/**
	 * All column names for this matrix in order of ascending column index.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return List of String
	 */
	public List<String> getColNames();
	
	/**
	 * All row names for this matrix in order of ascending row index.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return List of String
	 */
	public List<String> getRowNames();

	/**
	 * Retrieve one cell of the matrix. This may be very inefficient unless the
	 * matrix has been read in memory.
	 * 
	 * @param rowIndex
	 * @param colIndex
	 * @return A single element
	 */
	public Object getElement(int rowIndex, int colIndex) throws Exception;

	/**
	 * Get value by row and column name.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param rowName
	 * @param colName
	 * @return A single element
	 * @throws Exception
	 */
	public Object getElement(String rowName, String colName) throws Exception;

	/**
	 * Retrieve all of this matrix' elements in a twodimensional primitive
	 * array.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return Array of size nrows x ncols
	 * @throws Exception
	 */
	public Object[][] getElements() throws MatrixException;

	/**
	 * Retrieve a matrix column by index.
	 * 
	 * @param colIndex
	 *            in 1..ncol()
	 * @return Array with values of this column
	 * @throws Exception
	 *             if the column index is not known
	 */
	public Object[] getCol(int colIndex) throws Exception;

	/**
	 * Retrieve a matrix column by name.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param colName
	 * @return Array with values of this column
	 * @throws Exception
	 */
	public Object[] getCol(String colName) throws Exception;

	/**
	 * Retrieve the column index for a column name.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param colName
	 * @return colindex
	 * @throws Exception
	 *             if the column name is not known
	 */
	public int getColIndexForName(String colName) throws Exception;

	/**
	 * Retrieve a matrix row by index.
	 * 
	 * @param rowIndex
	 *            ind 1..nrow()
	 * @return Array with values of this row
	 * @throws Exception
	 *             if the rowindex is not known
	 */
	public Object[] getRow(int rowIndex) throws Exception;

	/**
	 * Retrieve a matrix row by name.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param rowName
	 * @return Array with values of this row
	 * @throws Exception
	 */
	public Object[] getRow(String rowName) throws Exception;

	/**
	 * Retrieve the row index for a row name.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param rowName
	 * @return rowIndex
	 * @throws Exception
	 *             if the rowname is not known
	 */
	public int getRowIndexForName(String rowName) throws Exception;

	/**
	 * Retrieve a partition of the matrix by listing row and column indices.

	 * @param rowIndices
	 * @param colIndices
	 * @return A submatrix, which is a new matrix itself
	 * @throws Exception
	 *             if row or column names are not known
	 */
	public DataMatrixInstance getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException;
	
	/**
	 * Retrieve a partition of the matrix by listing rowname(s) and colname(s).
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param rowNames
	 * @param colNames
	 * @return A submatrix, which is a new matrix itself
	 * @throws Exception
	 *             if row or column names are not known
	 */
	public DataMatrixInstance getSubMatrix(List<String> rowNames, List<String> colNames) throws Exception;
	
	/**
	 * Retrieve a partition of the matrix by listing rowindex, rownlenght,
	 * colindex and collenght.
	 * 
	 * @param row
	 * @param nRows
	 * @param col
	 * @param nCols
	 * @return A submatrix, which is a new matrix itself
	 * @throws Exception
	 * 
	 */
	public DataMatrixInstance getSubMatrixByOffset(int row, int nRows, int col, int nCols) throws Exception;

	/**
	 * Retrieve a partition of the matrix, using row/colnames and offsets.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param rowName
	 * @param nRows
	 * @param colName
	 * @param nCols
	 * @return A submatrix, which is a new matrix itself
	 * @throws Exception
	 */
	public DataMatrixInstance getSubMatrixByOffset(String rowName, int nRows, String colName, int nCols) throws Exception;
	
	
	/**
	 * QueryRules version of index retrieve
	 */
	public DataMatrixInstance getSubMatrixFilterByIndex(QueryRule... rules) throws Exception;
	
	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the row entities. For example, a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'gender', 'equals', 'male'. This
	 * results in the same amount of Phenotypes, but a reduced amount of
	 * Patients. Remember this applies to attributes of Patient, NOT to values
	 * stored in the matrix, though of course the values are reordered.
	 * ('gender' is a property, not an instance) The reason for not merging this
	 * function with getSubMatrixFilterByColEntityValues() is that rows and
	 * columns may be the same type of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return A new matrix
	 * @throws Exception
	 */
	public DataMatrixInstance getSubMatrixFilterByRowEntityValues(Database db, QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a row of values in this matrix. For example, a matrix where rows are
	 * Patients and columns are Phenotypes, a query could be 'JohnDoe', 'greater
	 * than', '35'. This results in the same amount of Patients, but a reduced
	 * amount of Phenotypes. Remember that 'JohnDoe' is an instance of Patient
	 * and thus applies to (dataelement) values stored in this matrix. The
	 * reason for not merging this function with
	 * getSubMatrixFilterByColMatrixValues() is that rows and columns may be the
	 * same refer to the same instances of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return A new matrix
	 * @throws Exception
	 */
	public DataMatrixInstance getSubMatrixFilterByRowMatrixValues(QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to the column entities. For example, a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'unit', 'equals', 'gram'. This
	 * results in the same amount of Patients, but a reduced amount of
	 * Phenotypes. Remember this applies to attributes of Phenotype, NOT to
	 * values stored in the matrix, though of course the values are reordered.
	 * ('unit' is a property, not an instance) The reason for not merging this
	 * function with getSubMatrixFilterByRowEntityValues() is that rows and
	 * columns may be the same type of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return A new matrix
	 * @throws Exception
	 */
	public DataMatrixInstance getSubMatrixFilterByColEntityValues(Database db, QueryRule... rules) throws Exception;

	/**
	 * Get a submatrix from this matrix by applying a generic filter ('where')
	 * to a column of values in this matrix. For example, a matrix where rows
	 * are Patients and columns are Phenotypes, a query could be 'height',
	 * 'greater than', '175'. This results in the same amount of Phenotypes, but
	 * a reduced amount of Patients. Remember that 'height' is an instance of
	 * Phenotype and thus applies to (dataelement) values stored in this matrix.
	 * The reason for not merging this function with
	 * getSubMatrixFilterByRowMatrixValues() is that rows and columns may be the
	 * same refer to the same instances of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return A new matrix
	 * @throws Exception
	 */
	public DataMatrixInstance getSubMatrixFilterByColMatrixValues(QueryRule... rules) throws Exception;

	/**
	 * Get a sorted copy of this matrix. The sorting is applied to the
	 * attributes of the row entity instances. For example, a matrix where rows
	 * are Patients and columns are Phenotypes, the sorting request could be
	 * 'gender'. The rows are then sorted according to the alphabetic/numeric
	 * values of 'gender'. (resulting in, for example, 'Female', 'Male',
	 * 'Unknown' ordering) Remember this function applies to attributes of
	 * Patient, NOT to values stored in the matrix, though of course these
	 * values are reordered. ('gender' is a property, not an instance) The
	 * reason for not merging this function with
	 * getMatrixSortByColEntityValues() is that rows and columns may be the same
	 * type of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance getMatrixSortByRowEntityValues(boolean asc) throws Exception;

	/**
	 * Get a sorted copy of this matrix. The sorting is applied to the
	 * attributes of the column entity instances. For example, a matrix where
	 * rows are Patients and columns are Phenotypes, the sorting request could
	 * be 'unit'. The columns are then sorted according to the
	 * alphabetic/numeric values of 'unit'. (resulting in, for example, 'gram',
	 * 'liter', 'meter' ordering) Remember this function applies to attributes
	 * of Phenotype, NOT to values stored in the matrix, though of course these
	 * values are reordered. ('unit' is a property, not an instance) The reason
	 * for not merging this function with getMatrixSortByRowEntityValues() is
	 * that rows and columns may be the same type of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance getMatrixSortByColEntityValues(Database db, boolean asc) throws Exception;
	
	/**
	 * Get a sorted copy of this matrix. The sorting is applied to a row of
	 * values in this matrix. For example, a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'JohnDoe'. The columns
	 * (Phenotypes) are then sorted according to the alphabetic/numeric values
	 * of Patient 'JohnDoe'. The reason for not merging this function with
	 * getMatrixSortByColMatrixValues() is that rows and columns may be the same
	 * refer to the same instances of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance getMatrixSortByRowMatrixValues(boolean asc) throws Exception;

	/**
	 * Get a sorted copy of this matrix. The sorting is applied to a column of
	 * values in this matrix. For example, a matrix where rows are Patients and
	 * columns are Phenotypes, a query could be 'height'. The rows (Patients)
	 * are then sorted according to the alphabetic/numeric values of Phenotype
	 * 'height'. The reason for not merging this function with
	 * getMatrixSortByRowMatrixValues() is that rows and columns may be the same
	 * refer to the same instances of entity.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance getMatrixSortByColMatrixValues(Database db, boolean asc) throws Exception;

	/**
	 * Make a logical union with another matrix and return the resulting matrix.
	 * Say 'T' is this matrix, and 'N' the input matrix, we perform 'T||N'.
	 * (sql: 'full outer join')
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance performUnion(DataMatrixInstance N) throws Exception;
	
	/**
	 * Make a logical intersection with another matrix and return the resulting
	 * matrix. Say 'T' is this matrix, and 'N' the input matrix, we perform
	 * 'T&&N'. (sql: 'inner join')
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance performIntersection(DataMatrixInstance N) throws Exception;
	
	/**
	 * Make a logical difference with another matrix and return the resulting
	 * matrix. Say 'T' is this matrix, and 'N' the input matrix, we perform
	 * 'T&&!N'. Eg. we 'cut out' a part of this matrix by removing the
	 * overlapping elements it has with the input matrix. (sql: 'left outer join
	 * .. where N is null')
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 */
	public DataMatrixInstance performDifference(DataMatrixInstance N) throws Exception;
	
	/**
	 * Make a logical exclusion with another matrix and return the resulting
	 * matrix. Say 'T' is this matrix, and 'N' the input matrix, we perform
	 * '!T||!N'. Eg. we make a merge (union) of both matrices, but remove the
	 * overlapping elements (intersection). (sql: 'full outer join .. where T is
	 * null or N is null')
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return
	 */
	public DataMatrixInstance performExclusion(DataMatrixInstance N) throws Exception;
	
	/**
	 * Transpose this matrix into a new matrix.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param I
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance performTransposition(DataMatrixInstance N) throws Exception;

	
	/**
	 * Write this matrix to an Excel file and return the File object.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return This matrix in an Excel file
	 * @throws Exception
	 */
	public File getAsExcelFile() throws Exception;

	/**
	 * Get the matrix as a single file representation. Eg. the binary
	 * implementation returns the location of the source file, while the
	 * database implementation must first export the data and return this file.
	 * This exported file has the proper escaped investigation/data+.txt name
	 * syntax, but resides in the tmp directory.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @return This matrix as the closest file representation. For example, a
	 *         *.bin file for binary or *.txt for relational storage, after
	 *         exporting.
	 * @throws Exception
	 */
	public File getAsFile() throws Exception;

	/**
	 * Write this matrix to a CsvWriter, wrapped around a PrintWriter. Used in
	 * plugin context.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeToCsvWriter(PrintWriter out) throws Exception;

	/**
	 * Write this matrix to a PrintWriter. Used in servlet context.
	 * 
	 * Implemented in abstract class 'Matrix'.
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeToPrintWriter(PrintWriter out) throws Exception;
	
	/**
	 * TODO: DEFINITION<br>
	 * Add a row to this matrix at position X, with values 1..Y, bound to a new
	 * row header Z.
	 * 
	 * @throws Exception
	 */
	public void addRow() throws Exception;

	/**
	 * TODO: DEFINITION<br>
	 * Add a column to this matrix at position X, with values 1..Y, bound to a
	 * new column header Z.
	 * 
	 * @throws Exception
	 */
	public void addColumn() throws Exception;

	/**
	 * TODO: DEFINITION<br>
	 * Add or update a single matrix element ('cell') at position X,Y.
	 * 
	 * @throws Exception
	 */
	public void updateElement() throws Exception;
	
	/**
	 * Returns this matrix as a string. Uses fast retrieval but can cost a lot
	 * of memory.
	 * 
	 * @return The matrix formatted as a string.
	 */
	public String toString();

	/**
	 * TODO: 2D filter doc
	 * @param q
	 * @return
	 */
	public DataMatrixInstance getSubMatrix2DFilterByRow(QueryRule... rules)  throws Exception;

	/**
	 * TODO: 2D filter doc
	 * @param q
	 * @return
	 */
	public DataMatrixInstance getSubMatrix2DFilterByCol(QueryRule... rules)  throws Exception;

	/**
	 * Get this matrix as an R 'matrix' object
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public String getAsRobject(boolean b) throws Exception;

	/**
	 * Get this matrix as an SPSS file (*.sav)
	 * @return
	 * @throws Exception 
	 */
	public File getAsSpssFile() throws Exception;

	/**
	 * Write this matrix to a PrintStream
	 * @param p
	 */
	public void toPrintStream(PrintStream p);

}
