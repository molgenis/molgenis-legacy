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
	private boolean uniqueRowNames = true;
	
	private List<MatrixDimension> rows = new ArrayList<MatrixDimension>();
	private Map<String, MatrixDimension> rowNameMap = new LinkedHashMap<String, MatrixDimension>();
	
	private boolean uniqueColNames = true;
	private List<MatrixDimension> cols = new ArrayList<MatrixDimension>();
	private Map<String, MatrixDimension> colNameMap = new LinkedHashMap<String, MatrixDimension>();
	
	private E[][] values;

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
		// E[] result = create(values.length);
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
	public Matrix<E> getSubMatrixByIndex(int[] rowIndices, int[] colIndices)
			throws MatrixException
	{
		List<String> rows = new ArrayList<String>(rowIndices.length);
		List<String> cols = new ArrayList<String>(colIndices.length);
		E[][] elements = create(rowIndices.length, colIndices.length);
		E[][] allAlements = this.getValues();

		for (int rowIndicesIndex = 0; rowIndicesIndex < rowIndices.length; rowIndicesIndex++)
		{
			rows.add(this.getRowNames().get(rowIndices[rowIndicesIndex]));
			for (int colIndicesIndex = 0; colIndicesIndex < colIndices.length; colIndicesIndex++)
			{
				elements[rowIndicesIndex][colIndicesIndex] = allAlements[rowIndices[rowIndicesIndex]][colIndices[colIndicesIndex]];
			}
		}

		for (int colIndicesIndex = 0; colIndicesIndex < colIndices.length; colIndicesIndex++)
		{
			cols.add(this.getColNames().get(colIndices[colIndicesIndex]));
		}

		return new MemoryMatrix<E>(rows, cols, elements);
	}

	@Override
	public int getColCount()
	{
		return this.cols.size();
	}

	@Override
	public List<String> getColNames() throws MatrixException
	{
		if (this.hasUniqueNames())
		{
			return new ArrayList<String>(this.colNameMap.keySet());
		}
		else
		{
			throw new MatrixException(
					"This matrix doesn't have unique column names");
		}
	}

	@Override
	public List<MatrixDimension> getCols()
	{
		return Collections.unmodifiableList(this.cols);
	}

	@Override
	public int getRowCount()
	{
		return this.rows.size();
	}

	@Override
	public List<String> getRowNames() throws MatrixException
	{
		if (this.hasUniqueNames())
		{
			return new ArrayList<String>(this.rowNameMap.keySet());
		}
		else
		{
			throw new MatrixException(
					"This matrix doesn't have unique column names");
		}
	}

	@Override
	public List<MatrixDimension> getRows()
	{
		return Collections.unmodifiableList(this.rows);
	}

	@Override
	public Matrix<E> getSubMatrixByDimension(
			List<MatrixDimension> rowSelection,
			List<MatrixDimension> colSelection) throws MatrixException
	{
		int[] rowIndices = new int[rowSelection.size()];
		int[] colIndices = new int[colSelection.size()];

		// get column indices
		for (int i = 0; i < colSelection.size(); i++)
		{
			MatrixDimension col = colSelection.get(i);
			if (this.cols.contains(col))
			{
				colIndices[i] = this.cols.indexOf(col);
			}
			else
			{
				throw new MatrixException("couldn't find column "
						+ colSelection.get(i));
			}
		}

		// get row rowIndices
		for (int i = 0; i < rowSelection.size(); i++)
		{
			MatrixDimension row = rowSelection.get(i);
			if (this.rows.contains(row))
			{
				rowIndices[i] = this.rows.indexOf(row);
			}
			else
			{
				throw new MatrixException("couldn't find row "
						+ rowSelection.get(i));
			}
		}

		return this.getSubMatrixByIndex(rowIndices, colIndices);
	}

	@Override
	public Matrix<E> getSubMatrixByName(List<String> rowSelection,
			List<String> colSelection) throws MatrixException
	{
		if (!this.hasUniqueNames())
		{
			throw new MatrixException(
					"getSubMatrixByName failed because this matrix has non-unique column or row names");
		}

		List<MatrixDimension> rowDimensions = new ArrayList<MatrixDimension>();
		List<MatrixDimension> colDimensions = new ArrayList<MatrixDimension>();

		// get row dimensions
		for (String rowName : rowSelection)
		{
			if (this.rowNameMap.containsKey(rowName))
			{
				rowDimensions.add(this.rowNameMap.get(rowName));
			}
			else
			{
				throw new MatrixException("couldn't find row by name "
						+ rowName);
			}
		}

		// get column dimensions rowIndices
		for (String colName : colSelection)
		{
			if (this.colNameMap.containsKey(colName))
			{
				colDimensions.add(this.colNameMap.get(colName));
			}
			else
			{
				throw new MatrixException("couldn't find columns by name "
						+ colName);
			}
		}

		return this.getSubMatrixByDimension(rowDimensions, colDimensions);

	}

	@Override
	public E getValueByName(String rowName, String colName)
			throws MatrixException
	{
		if (!this.hasUniqueNames())
		{
			throw new MatrixException(
					"getValueByName failed because this matrix has non-unique column or row names");
		}
		if (!this.rowNameMap.containsKey(rowName))
		{
			throw new MatrixException("getValueByName failed: row name '"
					+ rowName + "' unknown");
		}
		if (!this.colNameMap.containsKey(colName))
		{
			throw new MatrixException("getValueByName failed: column name '"
					+ colName + "' unknown");
		}

		return this.getValue(this.rowNameMap.get(rowName), this.colNameMap
				.get(colName));

	}

	@Override
	public E getValue(MatrixDimension row, MatrixDimension col)
			throws MatrixException
	{
		return getValue(rows.indexOf(row), cols.indexOf(col));
	}

	@Override
	public void transpose() throws MatrixException
	{
		// copy rows
		List<MatrixDimension> copy = this.rows;
		Map<String, MatrixDimension> copyMap = this.rowNameMap;

		// swap rows and column metadata
		this.rows = this.cols;
		this.rowNameMap = this.colNameMap;
		this.cols = copy;
		this.colNameMap = copyMap;

		// copy the data, swapped
		E[][] newValues = create(this.rows.size(), this.cols.size());
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

	@Override
	public boolean hasUniqueNames()
	{
		return this.uniqueRowNames && this.uniqueColNames;
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

	protected void setCols(List<MatrixDimension> columnDimension)
	{
		// delete current metatadata
		resetCols();

		// load dimensions and optionally 'ByName' map
		String colName;
		for (MatrixDimension col : columnDimension)
		{
			this.cols.add(col);
			colName = col.getName();

			if (uniqueColNames == true)
			{
				if (this.colNameMap.containsKey(colName))
				{
					uniqueColNames = false;
					this.colNameMap = null;
					this.rowNameMap = null;
				}
				else
				{
					this.colNameMap.put(colName, col);
				}
			}
		}
	}

	protected void setRows(List<MatrixDimension> rowDimension)
	{
		// delete current metatadata
		resetRows();

		// load dimensions and optionally 'ByName' map
		String rowName;
		for (MatrixDimension row : rowDimension)
		{
			this.rows.add(row);
			rowName = row.getName();

			if (uniqueRowNames == true)
			{
				if (this.rowNameMap.containsKey(rowName))
				{
					uniqueRowNames = false;
					this.colNameMap = null;
					this.rowNameMap = null;
				}
				else
				{
					this.rowNameMap.put(rowName, row);
				}
			}
		}

	}

	protected void setRowNames(List<String> rowNames) throws MatrixException
	{
		resetRows();
		
		// add row metadata
		for (String rowName : rowNames)
		{
			MatrixDimension row = new MatrixDimension(rowName);

			this.rows.add(row);

			if (uniqueColNames == true)
			{
				if (this.rowNameMap.containsKey(rowName))
				{
					uniqueRowNames = false;
					this.colNameMap = null;
					this.rowNameMap = null;
				}
				else
				{
					this.rowNameMap.put(rowName, row);
				}
			}
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
		//clean existing metadata
		resetCols();
		
		for (String colName : colNames)
		{
			MatrixDimension col = new MatrixDimension(colName);

			this.cols.add(col);

			if (uniqueColNames == true)
			{
				if (this.colNameMap.containsKey(colName))
				{
					uniqueRowNames = false;
					this.colNameMap = null;
					this.rowNameMap = null;
				}
				else
				{
					this.colNameMap.put(colName, col);
				}
			}
		}
	}
	
	private void resetRows()
	{
		this.rows = new ArrayList<MatrixDimension>();
		this.rowNameMap = new LinkedHashMap<String, MatrixDimension>();
		this.uniqueRowNames = true;
	}
	
	private void resetCols()
	{
		this.colNameMap = new LinkedHashMap<String,MatrixDimension>();
		this.cols = new ArrayList<MatrixDimension>();
		this.uniqueColNames = true;
	}

	@Override
	public E[] getColByName(String colName) throws MatrixException
	{
		return getCol(this.colNameMap.get(colName));
	}

	@Override
	public E[] getCol(MatrixDimension col) throws MatrixException
	{
		return getCol(this.cols.indexOf(col));
	}

	@Override
	public E[] getRowByName(String name) throws MatrixException
	{
		return getRow(this.rowNameMap.get(name));
	}

	@Override
	public E[] getRow(MatrixDimension row) throws MatrixException
	{
		return getRow(this.rows.indexOf(row));
	}

	@Override
	public Class<E> getValueType() throws MatrixException
	{
			return (Class<E>) this.values.getClass().getComponentType().getComponentType();
	}

}