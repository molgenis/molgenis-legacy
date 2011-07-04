package org.molgenis.matrix;

import java.util.List;

/**
 * MOLGENIS interface to handle matrix like data sets:
 * <ul>
 * <li>Its row and column dimensions have metadata attached on type and any
 * other information the user would like to add. In this case the row/column
 * names don't need to be unique.
 * <li>Optionally, it can be used using only column/row names. In this case the
 * column names are unique.
 * <li>The matrix is decoupled from the conceptual and physical data model so it
 * can be used with various backends.
 */
public interface Matrix<E>
{	
	/**
	 * Simplified version of getRows() that only returns the row names and not
	 * metadata on the rows.
	 * 
	 * 
	 * @return list of names
	 * @throws MatrixException 
	 */
	public List<String> getRowNames() throws MatrixException;
	
	/**
	 * Simplified version of getCols that only returns column names and not
	 * metadata on the columns
	 * 
	 * @return list of names
	 * @throws MatrixException if the names are not unique
	 */
	public List<String> getColNames() throws MatrixException;

	/**
	 * Set columns dimensions
	 */
	//public void setCols(List<MatrixDimension> columns);

	/**
	 * Set row dimensions
	 * 
	 * @return
	 */
	//public void setRows(List<MatrixDimension> rows);

	/**
	 * Short for setCols(MatrixDimension) by only providing column names. All
	 * other dimension properties will be default (e.g.,
	 * getColumns().get(i).getType() == STRING)
	 * 
	 * This method will throw an error if the names within the matrix are not
	 * unique.
	 */
	//public void setColNames(List<String> colNames);

	/**
	 * Short for setRows(MatrixDimension) by only providing column names. All
	 * other dimension properties will be default (e.g.,
	 * getColumns().get(i).getType() == STRING)
	 * 
	 * This method will throw an error if the names within the matrix are not
	 * unique.
	 */
	//public void setRowNames(List<String> rowNames);

	/**
	 * Get data from window within the matrix based from rowIndex and colIndex
	 * with as size rowSize an colSize. Useful for dealing with subsets of the
	 * matrix
	 * 
	 * @param rowStart
	 * @param numRows
	 * @param colStart
	 * @param numCols
	 * @return values with rows in first and columns in second dimension
	 * @throws MatrixException 
	 */
	public Matrix<E> getSubMatrixByOffset(int rowStart, int numRows, int colStart,
			int numCols) throws MatrixException;

	/**
	 * Get a subset from the matrix using the column and row names
	 * 
	 * This method will throw an error if the names within the matrix are not
	 * unique.
	 * 
	 * @param columns
	 * @param rows
	 * @return subset of the matrix as 2-dim array
	 * @throws MatrixException 
	 */
	public Matrix<E> getSubMatrixByName(List<String> columns, List<String> rows) throws MatrixException;

	/**
	 * Get one value from the matrix by index
	 * 
	 * @param row
	 * @param col
	 * @return a value
	 * @throws MatrixException 
	 */
	public E getValue(int row, int col) throws MatrixException;

	/**
	 * Get one value from the matrix by names
	 * 
	 * This method will throw an error if the names within the matrix are not
	 * unique.
	 * @throws MatrixException 
	 */
	public E getValueByName(String rowName, String colName) throws MatrixException;

	/**
	 * Count of rows.
	 * 
	 * @return count
	 */
	public int getRowCount();

	/**
	 * Count of columns
	 * 
	 * @return count
	 */
	public int getColCount();

	/**
	 * Transpose the matrix switching columns and rows
	 * @throws MatrixException 
	 */
	public void transpose() throws MatrixException;


	/**
	 * Get a column based on its index
	 * 
	 * @param colIndex
	 * @return column data
	 * @throws MatrixException
	 */
	public E[] getCol(int colIndex) throws MatrixException;
	
	/**
	 * Get a column by its names
	 * 
	 * @param name
	 * @return column as array
	 * @throws MatrixExpception
	 */
	public E[] getColByName(String name) throws MatrixException;
	
	/**
	 * Get a row based on its name.
	 * 
	 * @param name
	 * @return row as array
	 */
	public E[] getRowByName(String name) throws MatrixException;


	/**
	 * Get a row based on its index
	 * 
	 * @param rowIndex
	 * @return row data
	 * @throws MatrixException
	 */
	public E[] getRow(int rowIndex) throws MatrixException;


	/**
	 * Slice out a sub matrix based on a list of row and column indexes.
	 * @param rowIndices
	 * @param colIndices
	 * @return slice of the matrix based on row and column indexes
	 * @throws MatrixException
	 */
	public Matrix<E> getSubMatrixByIndex(List<Integer> rowIndices, List<Integer> colIndices)
			throws MatrixException;

	/**
	 * 
	 * @return
	 * @throws MatrixException 
	 */
	public Class<E> getValueType() throws MatrixException;
}
