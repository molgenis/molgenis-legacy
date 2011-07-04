package org.molgenis.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple 'in-memory' implementation of the Matrix. Data is stored as
 * two-dimensional array.
 * 
 * @param <E>
 */
public class MemoryMatrix<E> implements Matrix<E>
{
	private E[][] values;
	private List<String> rowNames = new ArrayList<String>();
	private List<String> colNames = new ArrayList<String>();

	/** Creata an empty matrix using dimensions */
	public MemoryMatrix(List<String> rowNames, List<String> colNames)
			throws MatrixException
	{
		// add row metadata
		this.setColNames(colNames);
		this.setRowNames(rowNames);

		// set the values
		this.setValues(this.create(rowNames.size(), colNames.size()));
	}

	public MemoryMatrix(List<String> rowNames, List<String> colNames,
			E[][] values) throws MatrixException
	{
		// add row metadata
		this.setColNames(colNames);
		this.setRowNames(rowNames);

		// set the values
		this.setValues(values);
	}

	/** Protected constructor for the subclasses */
	protected MemoryMatrix()
	{
	}

	@Override
	public E[] getCol(int i) throws MatrixException
	{
		E[] result = create(getRowCount());
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
	public E[] getRow(int i) throws MatrixException
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
	public E getValue(int row, int col) throws MatrixException
	{
		return getValues()[row][col];
	}

	@Override
	public Matrix<E> getSubMatrixByOffset(int row, int nrows, int col, int ncols)
			throws MatrixException
	{
		List<String> rows = new ArrayList<String>(nrows);
		List<String> cols = new ArrayList<String>(ncols);
		E[][] elements = create(nrows, ncols);
		E[][] allAlements = this.getValues();

		rows = this.getRowNames().subList(row, row + nrows);
		cols = this.getColNames().subList(col, col + ncols);

		for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
		{
			for (int colIndex = col; colIndex < col + ncols; colIndex++)
			{
				elements[rowIndex][colIndex] = allAlements[rowIndex][colIndex];
			}
		}
		return new MemoryMatrix<E>(rows, cols, elements);
	}

	@Override
	public Matrix<E> getSubMatrixByIndex(List<Integer> rowIndices,
			List<Integer> colIndices) throws MatrixException
	{
		List<String> rows = new ArrayList<String>(rowIndices.size());
		List<String> cols = new ArrayList<String>(colIndices.size());
		E[][] elements = create(rowIndices.size(), colIndices.size());
		E[][] allAlements = this.getValues();

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

		return new MemoryMatrix<E>(rows, cols, elements);
	}

	@Override
	public int getColCount()
	{
		return this.colNames.size();
	}

	@Override
	public List<String> getColNames() throws MatrixException
	{
		return Collections.unmodifiableList(this.colNames);
	}

	@Override
	public int getRowCount()
	{
		return this.rowNames.size();
	}

	@Override
	public List<String> getRowNames() throws MatrixException
	{
		return Collections.unmodifiableList(this.rowNames);
	}

	@Override
	public Matrix<E> getSubMatrixByName(List<String> rowSelection,
			List<String> colSelection) throws MatrixException
	{
		List<Integer> rowDimensions = new ArrayList<Integer>();
		List<Integer> colDimensions = new ArrayList<Integer>();

		// get row dimensions
		for (String rowName : rowSelection)
		{
			rowDimensions.add(getRowId(rowName));

		}

		// get column dimensions rowIndices
		for (String colName : colSelection)
		{
			colDimensions.add(getColId(colName));
		}

		return this.getSubMatrixByIndex(rowDimensions, colDimensions);

	}

	public Integer getRowId(String rowName) throws MatrixException
	{
		int rowid = this.rowNames.indexOf(rowName);
		if (rowid == -1) throw new MatrixException("couldn't find row by name "
				+ rowName);
		return rowid;
	}

	public Integer getColId(String colName) throws MatrixException
	{
		int colid = this.colNames.indexOf(colName);
		if (colid == -1) throw new MatrixException("couldn't find col by name "
				+ colName);
		return colid;
	}

	@Override
	public E getValueByName(String rowName, String colName)
			throws MatrixException
	{
		return this.getValue(getRowId(rowName), getColId(colName));
	}

	@Override
	public void transpose() throws MatrixException
	{
		// swap rows and column metadata
		List<String> copyRowNames = this.rowNames;
		this.rowNames = this.colNames;
		this.colNames = copyRowNames;

		// copy the data, swapped
		E[][] newValues = create(this.rowNames.size(), this.colNames.size());
		for (int i = 0; i < this.getValues().length; i++)
		{
			for (int j = 0; j < this.getValues().length; j++)
			{
				newValues[j][i] = this.getValues()[i][j];
			}
		}

		// replace values with newValues
		this.setValues(newValues);
	}

	protected E[][] getValues() throws MatrixException
	{
		return this.values;
	}

	protected void setValues(E[][] values) throws MatrixException
	{
		// checks number of rows
		if (getRowCount() != values.length) throw new MatrixException(
				"rows(values) and getRowCount() are of different sizes: rowCount="
						+ getRowCount() + " vs value.lenght=" + values.length);
		int i = 0;

		// check length of each row to be equal to number of columns
		for (E[] row : values)
		{
			if (getColCount() != row.length) throw new MatrixException(
					"values on row=" + (i + 1)
							+ " has a different size than getColCount(): "
							+ getColCount() + " vs " + row.length);
			i++;
		}

		this.values = values;
	}

	public void setValue(String row, String col, E value)
			throws MatrixException
	{
		this.values[getRowId(row)][getColId(col)] = value;

	}

	@SuppressWarnings("unchecked")
	protected E[] create(int rows)
	{
		return (E[]) new Object[rows];
	}

	@SuppressWarnings("unchecked")
	protected E[][] create(int rows, int cols)
	{
		// create all empty rows as well
		Object[][] data = new Object[rows][cols];
		for (int i = 0; i < data.length; i++)
		{
			data[i] = new Object[cols];
		}

		return (E[][]) data;
	}

	protected void setRowNames(List<String> rowNames) throws MatrixException
	{
		resetRows();

		// add row metadata
		for (String rowName : rowNames)
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
	protected void setColNames(List<String> colNames) throws MatrixException
	{
		// clean existing metadata
		resetCols();

		for (String colName : colNames)
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
		this.rowNames = new ArrayList<String>();
	}

	private void resetCols()
	{
		this.colNames = new ArrayList<String>();
	}

	@Override
	public E[] getColByName(String colName) throws MatrixException
	{
		return getCol(getColId(colName));
	}

	@Override
	public E[] getRowByName(String name) throws MatrixException
	{
		return getRow(getRowId(name));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<E> getValueType() throws MatrixException
	{
		return (Class<E>) this.values.getClass().getComponentType()
				.getComponentType();
	}

}