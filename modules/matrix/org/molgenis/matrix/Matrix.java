package org.molgenis.matrix;

import java.util.List;


/**
 * MOLGENIS interface to handle matrix like data sets:
 * <ul>
 * <li>Contains a two dimensional data set with each value identified by row and
 * column id.
 * <li>Optionally, it can be used using column/row names. In this case the
 * column names are unique.
 * <li>The matrix is decoupled from the conceptual and physical data model so it
 * can be used with various backends.
 */
public interface Matrix<E>
{
	/**
	 * Get row names
	 * 
	 * @return list of names
	 * @throws MatrixException
	 */
	public List<String> getRowNames() throws MatrixException;

	/**
	 * Get columns names
	 * 
	 * @return list of names
	 * @throws MatrixException
	 *             if the names are not unique
	 */
	public List<String> getColNames() throws MatrixException;

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
	 * Get one value from the matrix by index
	 * 
	 * @param row
	 * @param col
	 * @return a value
	 * @throws MatrixException
	 */
	public E getValue(int row, int col) throws MatrixException;

	/**
	 * Get a column based on its index
	 * 
	 * @param colIndex
	 * @return column data
	 * @throws MatrixException
	 */
	public E[] getCol(int colIndex) throws MatrixException;

	/**
	 * Get a row based on its index
	 * 
	 * @param rowIndex
	 * @return row data
	 * @throws MatrixException
	 */
	public E[] getRow(int rowIndex) throws MatrixException;

	/**
	 * Get one value from the matrix by names
	 * 
	 * @throws MatrixException
	 */
	public E getValue(String rowName, String colName) throws MatrixException;

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
	 * 
	 * @return
	 * @throws MatrixException
	 */
	public Class<E> getValueType() throws MatrixException;

	/**
	 * Transpose the matrix switching columns and rows.
	 * 
	 * @throws MatrixException
	 */
	public void transpose() throws MatrixException;

	/**
	 * Get data from window within the matrix based from rowIndex and colIndex
	 * with as size rowSize an colSize. Useful for dealing with subsets of the
	 * matrix.
	 * 
	 * @param rowStart
	 * @param numRows
	 * @param colStart
	 * @param numCols
	 * @return values with rows in first and columns in second dimension
	 * @throws MatrixException
	 */
	public Matrix<E> getSubMatrixByOffset(int rowStart, int numRows,
			int colStart, int numCols) throws MatrixException;

	/**
	 * Slice out a sub matrix based on a list of row and column indexes.
	 * 
	 * @param rowIndices
	 * @param colIndices
	 * @return slice of the matrix based on row and column indexes
	 * @throws MatrixException
	 */
	public Matrix<E> getSubMatrixByIndex(List<Integer> rowIndices,
			List<Integer> colIndices) throws MatrixException;

	/**
	 * Get a subset from the matrix using the column and row names.
	 * 
	 * @param columns
	 * @param rows
	 * @return subset of the matrix as 2-dim array
	 * @throws MatrixException
	 */
	public Matrix<E> getSubMatrixByName(List<String> columns,
			List<String> rows) throws MatrixException;

}
