package matrix.implementations.memory;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.general.MatrixReadException;

import org.molgenis.data.Data;
import org.molgenis.matrix.MatrixException;

import decorators.NameConvention;

/**
 * In memory representation of a matrix
 * 
 * @author Morris Swertz
 */
public class MemoryDataMatrixInstance<E> extends AbstractDataMatrixInstance<E>
{
	// matrix of row,col
	E[][] values;
	
	
	public MemoryDataMatrixInstance(List<String> rownames, List<String> colnames, List<E[]> valueList, Data data) throws MatrixException
	{
		int nrOfRows = valueList.size();
		int nrOfCols = valueList.get(0).length;
		
		//see: http://stackoverflow.com/questions/2927391/whats-the-reason-i-cant-create-generic-array-types-in-java
		E[][] values = (E[][]) new Object[nrOfRows][nrOfCols];
		
		for(int rowIndex = 0; rowIndex < nrOfRows; rowIndex++)
		{
			E[] row = valueList.get(rowIndex);
			if(row.length != nrOfCols)
			{
				throw new MatrixException("Unequal number of columns in this matrix is not allowed");
			}
			for(int colIndex = 0; colIndex < row.length; colIndex++)
			{
				values[rowIndex][colIndex] = row[colIndex];
			}
		}
		
		// checks
		if (rownames.size() != values.length) throw new MatrixException(
				"rownames.length and values[] (rows) are of different sizes: " + rownames.size() + " vs "
						+ values.length);
		int i = 0;
		for (E[] row : values)
		{
			if (colnames.size() != row.length) throw new MatrixException("colnames.length and values[" + i
					+ "].length (col) are of different sizes: " + colnames.size() + " vs " + row.length);
			i++;
		}

		if(data == null){
			data = new Data();
			data.setName("nameless_memorymatrix");
		}

		// configure
		this.setColNames(colnames);
		this.setRowNames(rownames);
		this.setNumberOfCols(colnames.size());
		this.setNumberOfRows(rownames.size());
		this.values = values;
		this.setData(data);
	}
	
	public MemoryDataMatrixInstance(List<String> rownames, List<String> colnames, E[][] values, Data data) throws MatrixException
	{
		// checks
		if (rownames.size() != values.length) throw new MatrixException(
				"rownames.length and values[] (rows) are of different sizes: " + rownames.size() + " vs "
						+ values.length);
		int i = 0;
		for (E[] row : values)
		{
			if (colnames.size() != row.length) throw new MatrixException("colnames.length and values[" + i
					+ "].length (col) are of different sizes: " + colnames.size() + " vs " + row.length);
			i++;
		}

		if(data == null){
			data = new Data();
			data.setName("nameless_memorymatrix");
		}

		// configure
		this.setColNames(colnames);
		this.setRowNames(rownames);
		this.setNumberOfCols(colnames.size());
		this.setNumberOfRows(rownames.size());
		this.values = values;
		this.setData(data);
	}

	public void changeDataName(String name)
	{
		Data tmp = new Data();
		tmp.setName(name);
		this.setData(tmp);
	}

	@Override
	public Object[] getCol(int i) throws MatrixException
	{
		// E[] result = create(values.length);
		Object[] result = new Object[values.length];
		try
		{
			for (int j = 0; j < values.length; j++)
			{
				result[j] = values[j][i];
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new MatrixException("column with index " + i + " doesn't exist");
		}
		return result;
	}

	@Override
	public E[] getRow(int i) throws MatrixReadException
	{
		try
		{
			return values[i];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new MatrixReadException("row with index " + i + " doesn't exist");
		}
	}

	@Override
	public E getElement(int row, int col)
	{
		return values[row][col];
	}

	private E[] create(int rows)
	{
		return (E[]) new Double[rows];
	}

	private E[][] create(int rows, int cols)
	{
		// FIXME
		return (E[][]) new Double[rows][cols];
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int row, int nrows, int col, int ncols) throws Exception
	{
		List<String> rows = new ArrayList<String>(nrows);
		List<String> cols = new ArrayList<String>(ncols);
		Object[][] elements = new Object[nrows][ncols];
		Object[][] allAlements = this.getElements();

		rows = this.getRowNames().subList(row, row + nrows);
		cols = this.getColNames().subList(col, col + ncols);

		int rowPos = 0;
		for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
		{
			int colPos = 0;
			for (int colIndex = col; colIndex < col + ncols; colIndex++)
			{
				elements[rowPos][colPos] = allAlements[rowIndex][colIndex];
				colPos++;
			}
			rowPos++;
		}
		return new MemoryDataMatrixInstance(rows, cols, elements, this.getData());
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException
	{
		List<String> rows = new ArrayList<String>(rowIndices.length);
		List<String> cols = new ArrayList<String>(colIndices.length);
		Object[][] elements = new Object[rowIndices.length][colIndices.length];
		Object[][] allAlements = this.getElements();
		
			for(int rowIndicesIndex = 0; rowIndicesIndex < rowIndices.length; rowIndicesIndex++){
			rows.add(this.getRowNames().get(rowIndices[rowIndicesIndex]));
			for(int colIndicesIndex = 0; colIndicesIndex < colIndices.length; colIndicesIndex++){
				elements[rowIndicesIndex][colIndicesIndex] = allAlements[rowIndices[rowIndicesIndex]][colIndices[colIndicesIndex]];
			}
		}
		
		for(int colIndicesIndex = 0; colIndicesIndex < colIndices.length; colIndicesIndex++){
			cols.add(this.getColNames().get(colIndices[colIndicesIndex]));
		}
		
		return new MemoryDataMatrixInstance(rows, cols, elements, this.getData());
	}

	@Override
	public E[][] getElements() throws MatrixException
	{
		return values;
	}

	@Override
	public File getAsFile() throws Exception
	{
		File tmp = new File(System.getProperty("'java.io.tmpdir") + File.separator
				+ NameConvention.escapeFileName(this.getData().getName()) + "_" + System.nanoTime());
		boolean createTmp = tmp.createNewFile();
		if (!createTmp)
		{
			throw new Exception("Creation of tmp file " + tmp.getAbsolutePath() + " failed.");
		}
		PrintWriter out = new PrintWriter(tmp);
		this.writeToCsvWriter(out);
		out.close(); // FIXME: close 'out'?
		return null;
	}

	@Override
	public void addColumn() throws Exception
	{
		throw new Exception("Action not possible");
	}

	@Override
	public void addRow() throws Exception
	{
		throw new Exception("Action not possible");
	}

	@Override
	public void updateElement() throws Exception
	{
		throw new Exception("Action not possible");
	}
}
