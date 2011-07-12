package org.molgenis.matrix;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple 'in-memory' implementation of the Matrix. Data is stored as
 * two-dimensional array.
 * 
 * @param <E>
 */
public class MemoryMatrix<E, A, V> implements EditableMatrix<E, A, V>
{
	private V[][] values;
	private List<E> rowNames = new ArrayList<E>();
	private List<A> colNames = new ArrayList<A>();
	private Class<V> valueType = null;

	/** Creata an empty matrix using dimensions */
	public MemoryMatrix(List<E> rowNames, List<A> colNames, Class<V> valueType)
			throws MatrixException
	{
		this.valueType = valueType;
		
		// add row metadata
		this.setColNames(colNames);
		this.setRowNames(rowNames);

		// set the values
		this.setValues(this.create(rowNames.size(), colNames.size(), valueType));
	}

	public MemoryMatrix(List<E> rowNames, List<A> colNames, V[][] values)
			throws MatrixException
	{		
		//get from a not-null value the valueType
		this.valueType = getValueType(values);
		
		// add row metadata
		this.setColNames(colNames);
		this.setRowNames(rowNames);

		// set the values
		this.setValues(values);
	}

	@SuppressWarnings("unchecked")
	private Class<V> getValueType(V[][] values2) throws MatrixException
	{
		for(int i = 0; i < values.length; i++)
		{
			for(int j = 0; j< values.length; j++)
			{
				if(values[i][j] != null)
				{
					return (Class<V>) values[i][j].getClass();
				}
			}
		}
		throw new MatrixException("MemoryMatrix needs at least one value in the [][] to establish type");
	}

	/** Protected constructor for the subclasses */
	protected MemoryMatrix()
	{
	}

	/**
	 * Copy constructor
	 * 
	 * @param values
	 * @throws MatrixException
	 */
	public MemoryMatrix(Matrix<E, A, V> matrix) throws MatrixException
	{
		this(matrix.getRowNames(), matrix.getColNames(), matrix.getValues());
	}

	@Override
	public V[] getCol(int i) throws MatrixException
	{
		V[] result = create(getRowCount());
		try
		{
			for (int j = 0; j < getRowCount(); j++)
			{
				result[j] = getValue(j, i);
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new MatrixException("column with index " + i
					+ " doesn't exist");
		}
		return result;
	}

	@Override
	public V[] getRow(int i) throws MatrixException
	{
		try
		{
			return getValues()[i].clone();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new MatrixException("row with index " + i + " doesn't exist");
		}
	}

	@Override
	public V getValue(int row, int col) throws MatrixException
	{
		if (row >= this.getRowCount()) throw new MatrixException(
				"row > rowCount");
		if (col >= this.getColCount()) throw new MatrixException(
				"col > colCount");
		return this.values[row][col];
	}

	@Override
	public Matrix<E, A, V> getSubMatrixByOffset(int row, int nrows, int col,
			int ncols) throws MatrixException
	{
		List<E> rows = new ArrayList<E>(nrows);
		List<A> cols = new ArrayList<A>(ncols);
		V[][] elements = (V[][]) create(nrows, ncols, this.valueType);
		V[][] allAlements = this.getValues();

		rows = this.getRowNames().subList(row, row + nrows);
		cols = this.getColNames().subList(col, col + ncols);

		for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
		{
			for (int colIndex = col; colIndex < col + ncols; colIndex++)
			{
				elements[rowIndex][colIndex] = allAlements[rowIndex][colIndex];
			}
		}
		return new MemoryMatrix<E, A, V>(rows, cols, elements);
	}

	@Override
	public Matrix<E, A, V> getSubMatrixByIndex(List<Integer> rowIndices,
			List<Integer> colIndices) throws MatrixException
	{
		List<E> rows = new ArrayList<E>(rowIndices.size());
		List<A> cols = new ArrayList<A>(colIndices.size());
		V[][] elements = (V[][]) create(rowIndices.size(), colIndices.size(), this.valueType);
		V[][] allAlements = this.getValues();

		for (int rowIndicesIndex = 0; rowIndicesIndex < rowIndices.size(); rowIndicesIndex++)
		{
			rows.add(this.getRowNames().get(rowIndices.get(rowIndicesIndex)));
			for (int colIndicesIndex = 0; colIndicesIndex < colIndices.size(); colIndicesIndex++)
			{
				elements[rowIndicesIndex][colIndicesIndex] = allAlements[rowIndices
						.get(rowIndicesIndex)][colIndices.get(colIndicesIndex)];
			}
		}

		for (int colIndicesIndex = 0; colIndicesIndex < colIndices.size(); colIndicesIndex++)
		{
			cols.add(this.getColNames().get(colIndices.get(colIndicesIndex)));
		}

		return new MemoryMatrix<E, A, V>(rows, cols, elements);
	}

	@Override
	public int getColCount()
	{
		return this.colNames.size();
	}

	@Override
	public List<A> getColNames() throws MatrixException
	{
		return Collections.unmodifiableList(this.colNames);
	}

	@Override
	public int getRowCount()
	{
		return this.rowNames.size();
	}

	@Override
	public List<E> getRowNames() throws MatrixException
	{
		return Collections.unmodifiableList(this.rowNames);
	}

	@Override
	public Matrix<E, A, V> getSubMatrixByName(List<E> rowSelection,
			List<A> colSelection) throws MatrixException
	{
		List<Integer> rowDimensions = new ArrayList<Integer>();
		List<Integer> colDimensions = new ArrayList<Integer>();

		// get row dimensions
		for (E rowName : rowSelection)
		{
			rowDimensions.add(getRowId(rowName));

		}

		// get column dimensions rowIndices
		for (A colName : colSelection)
		{
			colDimensions.add(getColId(colName));
		}

		return this.getSubMatrixByIndex(rowDimensions, colDimensions);

	}

	public Integer getRowId(E rowName) throws MatrixException
	{
		int rowid = this.rowNames.indexOf(rowName);
		if (rowid == -1) throw new MatrixException("couldn't find row by name "
				+ rowName);
		return rowid;
	}

	public Integer getColId(A colName) throws MatrixException
	{
		int colid = this.colNames.indexOf(colName);
		if (colid == -1) throw new MatrixException("couldn't find col by name "
				+ colName);
		return colid;
	}

	@Override
	public V getValue(E rowName, A colName) throws MatrixException
	{
		return this.getValue(getRowId(rowName), getColId(colName));
	}

	@Override
	public Matrix<A, E, V> transpose() throws MatrixException
	{
		// copy the data, swapped
		V[][] newValues = (V[][]) create(this.rowNames.size(), this.colNames
				.size(), this.valueType);
		for (int i = 0; i < this.getValues().length; i++)
		{
			for (int j = 0; j < this.getValues().length; j++)
			{
				newValues[j][i] = this.getValues()[i][j];
			}
		}

		// return a new memory matrix
		return new MemoryMatrix<A, E, V>(this.colNames, this.rowNames,
				newValues);
	}

	public V[][] getValues() throws MatrixException
	{
		return this.values;
	}

	protected void setValues(V[][] values) throws MatrixException
	{
		// checks number of rows
		if (getRowCount() != values.length) throw new MatrixException(
				"rows(values) and getRowCount() are of different sizes: rowCount="
						+ getRowCount() + " vs value.lenght=" + values.length);
		int i = 0;

		// check length of each row to be equal to number of columns
		for (V[] row : values)
		{
			if (getColCount() != row.length) throw new MatrixException(
					"values on row=" + (i + 1)
							+ " has a different size than getColCount(): "
							+ getColCount() + " vs " + row.length);
			i++;
		}

		this.values = values;
	}

	public void setValue(E row, A col, V value) throws MatrixException
	{
		this.values[getRowId(row)][getColId(col)] = value;

	}

	public void setValue(int row, int col, V value) throws MatrixException
	{
		this.values[row][col] = value;

	}

	@SuppressWarnings("unchecked")
	protected V[] create(int rows)
	{
		return (V[]) new Object[rows];
	}

	@SuppressWarnings("unchecked")
	public V[][] create(int rows, int cols, Class<V> valueType)
	{		   		
		// create all empty rows as well
		V[][] data = (V[][])Array.newInstance(valueType,rows,cols);
		for (int i = 0; i < data.length; i++)
		{
			data[i] = (V[])Array.newInstance(valueType, cols);
		}

		return data;
	}
	
	protected void setRowNames(List<E> rowNames) throws MatrixException
	{
		resetRows();

		// add row metadata
		for (E rowName : rowNames)
		{
			if (this.rowNames.contains(rowName))
			{
				throw new MatrixException("RowNames must be unique");
			}
			this.rowNames.add(rowName);
		}
	}

	/**
	 * Change column names.
	 * 
	 * @param colNames
	 * @throws MatrixException
	 */
	protected void setColNames(List<A> colNames) throws MatrixException
	{
		// clean existing metadata
		resetCols();

		for (A colName : colNames)
		{

			if (this.colNames.contains(colName))
			{
				throw new MatrixException("column names must be unique");
			}

			this.colNames.add(colName);
		}
	}

	private void resetRows()
	{
		this.rowNames = new ArrayList<E>();
	}

	private void resetCols()
	{
		this.colNames = new ArrayList<A>();
	}

	@Override
	public V[] getColByName(A colName) throws MatrixException
	{
		return getCol(getColId(colName));
	}

	@Override
	public V[] getRowByName(E name) throws MatrixException
	{
		return getRow(getRowId(name));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<V> getValueType() throws MatrixException
	{
		return (Class<V>) this.values.getClass().getComponentType()
				.getComponentType();
	}

	@Override
	public void setCol(int col, List<V> colValues) throws MatrixException
	{
		// colValues must be of same lenght as values
		if (this.values.length != colValues.size()) throw new MatrixException(
				"setCol failed: colValues != getRowCount()");

		// col must be inside getColCount
		if (col >= getColCount()) throw new MatrixException(
				"setCol failed: col >= getColCount()");

		// in each row set
		for (int i = 0; i < colValues.size(); i++)
		{
			this.values[i][col] = colValues.get(i);
		}

	}

	@Override
	public void setCol(A col, List<V> colValues) throws MatrixException
	{
		this.setCol(this.getColId(col), colValues);

	}

	@Override
	public void setRow(int row, List<V> rowValues) throws MatrixException
	{
		// rowValues must be in size of colCount
		if (rowValues.size() != this.getColCount()) throw new MatrixException(
				"setRow failed: rowValues.size() != getColCount()");

		// row must be in size of rowCount
		if (row >= getRowCount()) throw new MatrixException(
				"setRow failed: row >= getRowCount()");

		// iterate
		for (int i = 0; i < rowValues.size(); i++)
		{
			this.values[row][i] = rowValues.get(i);
		}

	}

	@Override
	public void setRow(E row, List<V> rowValues) throws MatrixException
	{
		this.setRow(getRowId(row), rowValues);

	}

	@Override
	public void store() throws MatrixException
	{
		throw new UnsupportedOperationException(
				"in memory database cannot be stored. Use CsvMemoryMatrix or BinaryMemoryMatrix instead");
	}

}