package org.molgenis.matrix;

import java.util.List;

/**
 * MOLGENIS interface to handle matrix like 'EAV' data sets:
 * <ul>
 * <li>Contains a two dimensional data set with each value identified by row and
 * column index.
 * <li>Optionally, it can be used using column/row identifiers. In this case the
 * column identifiers are unique.
 * <li>The matrix is decoupled from the conceptual and physical data model so it
 * can be used with various backends.
 * <li>The typing of row and column identifiers and the values are parameterized
 * by E,A,V (Entity=row, Attribute=col, Value=value)
 */
public interface Matrix<E, A, V>
{
	/**
	 * Get row names
	 * 
	 * @return list of names
	 * @throws MatrixException
	 */
	public List<E> getRowNames() throws MatrixException;

	/**
	 * Get columns names
	 * 
	 * @return list of names
	 * @throws MatrixException
	 *             if the names are not unique
	 */
	public List<A> getColNames() throws MatrixException;

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
	public V getValue(int row, int col) throws MatrixException;

	/**
	 * Get a column based on its index
	 * 
	 * @param colIndex
	 * @return column data
	 * @throws MatrixException
	 */
	public V[] getCol(int colIndex) throws MatrixException;

	/**
	 * Get a row based on its index
	 * 
	 * @param rowIndex
	 * @return row data
	 * @throws MatrixException
	 */
	public V[] getRow(int rowIndex) throws MatrixException;

	/**
	 * Get one value from the matrix by names
	 * 
	 * @throws MatrixException
	 */
	public V getValue(E rowName, A colName) throws MatrixException;

	/**
	 * Get a column by its names
	 * 
	 * @param name
	 * @return column as array
	 * @throws MatrixExpception
	 */
	public V[] getColByName(A name) throws MatrixException;

	/**
	 * Get a row based on its name.
	 * 
	 * @param name
	 * @return row as array
	 */
	public V[] getRowByName(E name) throws MatrixException;

	/**
	 * 
	 * @return
	 * @throws MatrixException
	 */
	public Class<V> getValueType() throws MatrixException;

	/**
	 * Transpose the matrix switching columns and rows.
	 * 
	 * @throws MatrixException
	 */
	public Matrix<A, E, V> transpose() throws MatrixException;

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
	public Matrix<E, A, V> getSubMatrixByOffset(int rowStart, int numRows,
			int colStart, int numCols) throws MatrixException;

	/**
	 * Slice out a sub matrix based on a list of row and column indexes. If one
	 * of the parameters is null, you will get all values of that dimension.
	 * (e.g. null on rowIndices will return all rows).
	 * 
	 * @param rowIndices
	 * @param colIndices
	 * @return slice of the matrix based on row and column indexes
	 * @throws MatrixException
	 */
	public Matrix<E, A, V> getSubMatrixByIndex(List<Integer> rowIndices,
			List<Integer> colIndices) throws MatrixException;

	/**
	 * Get a subset from the matrix using the column and row names.
	 * 
	 * @param columns
	 * @param rows
	 * @return subset of the matrix as 2-dim array
	 * @throws MatrixException
	 */
	public Matrix<E, A, V> getSubMatrixByName(List<E> columns, List<A> rows)
			throws MatrixException;

	/**
	 * Retrieve all values of this matrix as 2-D array
	 * 
	 * @throws MatrixException
	 */
	public V[][] getValues() throws MatrixException;

	/** 
	 * Retrieve column names only for the selected index,offset
	 * @param index
	 * @param offset
	 * @return
	 * @throws MatrixException 
	 */
	public List<A> getColNamesByOffset(int index, int offset) throws MatrixException;

}
