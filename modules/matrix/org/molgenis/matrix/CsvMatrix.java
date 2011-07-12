package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.matrix.convertors.ValueConvertor;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

/**
 * Matrix reader that reads each value from a file. Not very efficient but does scale memorywise.
 * 
 * Example usage:
 * 
 * <pre>
 * Matrix m = new CsvMatrix(File file);
 * </pre>
 * 
 * @param <E>
 */
public class CsvMatrix<E, A, V> extends MemoryMatrix<E, A, V>
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	// for reading the csv
	private CsvReader csvReader;

	// convertor to read the values
	private ValueConvertor<E> rowConvertor;
	private ValueConvertor<A> colConvertor;
	private ValueConvertor<V> valueConvertor;

	// class that holds the valueType
	private Class<E> valueType;

	/**
	 * Copy constructor for CsvMatrix
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param values
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 */
	public CsvMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			Matrix<E, A, V> values) throws MatrixException
	{
		super(values);
		this.rowConvertor = rowConvertor;
		this.colConvertor = colConvertor;
		this.valueConvertor = valueConvertor;
	}

	/**
	 * Creates a MemoryMatrix from Csv file. It uses the convertors to convert
	 * rowheader, colunmnheaders and values
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param f
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 */
	public CsvMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			File f) throws FileNotFoundException, MatrixException
	{
		// put the rownames and colnames in parent
		try
		{
			this.rowConvertor = rowConvertor;
			this.colConvertor = colConvertor;
			this.valueConvertor = valueConvertor;
			this.csvReader = new CsvFileReader(f);

			List<E> rowNames = new ArrayList<E>();
			for (String s : csvReader.rownames())
				rowNames.add(this.rowConvertor.read(s));
			
			List<A> colNames = new ArrayList<A>();
			for (String s : csvReader.colnames().subList(1,
					csvReader.colnames().size()))
				colNames.add(this.colConvertor.read(s));

			this.setRowNames(rowNames);
			this.setColNames(colNames);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
	}

	@Override
	public V[] getCol(final int colIndex) throws MatrixException
	{
		final V[] result = create(getRowCount());
		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					if (line_number > 0)
					{
						result[line_number - 1] = getValue(tuple, colIndex + 1);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
		return result;
	}

	@Override
	public V getValue(final int rowIndex, final int colIndex)
			throws MatrixException
	{
		// naive implementation
		final finalObject<V> finalResult = new finalObject<V>();
		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					// skip header line
					if (line_number > 0 && line_number - 1 == rowIndex)
					{
						// first column is rowname
						finalResult.set(getValue(tuple, colIndex + 1));
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
		return finalResult.get();
	}

	@Override
	public V[][] getValues() throws MatrixException
	{
		final V[][] result = create(getRowCount(), getColCount());
		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					for (int col = 0; col < tuple.size() - 1; col++)
					{
						result[line_number - 1][col] = getValue(tuple, col + 1);
					}

				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
		return result;
	}

	@Override
	public V[] getRow(int rowIndex) throws MatrixException
	{
		final int finalRowIndex = rowIndex;
		final V[] result = create(getColCount());
		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					if (line_number > 0 && line_number - 1 == finalRowIndex)
					{
						for (int col = 0; col < tuple.size() - 1; col++)
						{
							result[col] = getValue(tuple, col + 1);
						}
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
		return result;
	}

	@Override
	public Matrix<E, A, V> getSubMatrixByIndex(List<Integer> rowIndices,
			List<Integer> colIndices) throws MatrixException
	{
		// optimalization: sort ascending in primitive array, then dont use
		// .contains on list (slow) but smart counter
		Matrix<E, A, V> result = null;
		final V[][] elements = create(rowIndices.size(), colIndices.size());

		final ArrayList<Integer> rowIndicesList = new ArrayList<Integer>(
				rowIndices.size());
		for (int i : rowIndices)
		{
			rowIndicesList.add(i);
		}
		final ArrayList<Integer> colIndicesList = new ArrayList<Integer>(
				colIndices.size());
		for (int i : colIndices)
		{
			colIndicesList.add(i);
		}

		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					if (line_number > 0
							&& rowIndicesList.contains(line_number - 1))
					{
						for (int col = 0; col < tuple.size() - 1; col++)
						{
							if (colIndicesList.contains(col))
							{
								elements[line_number - 1][col] = getValue(
										tuple, col + 1);
							}
						}
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}

		List<E> rowNames = new ArrayList<E>();
		List<A> colNames = new ArrayList<A>();

		for (int rowIndex : rowIndices)
		{
			rowNames.add(this.getRowNames().get(rowIndex));
		}

		for (int colIndex : colIndices)
		{
			colNames.add(this.getColNames().get(colIndex));
		}

		result = new MemoryMatrix<E, A, V>(rowNames, colNames, elements);
		return result;
	}

	@Override
	public Matrix<E, A, V> getSubMatrixByOffset(int row, int nRows, int col,
			int nCols) throws MatrixException
	{
		final int finalRow = row;
		final int finalNRows = nRows;
		final int finalCol = col;
		final int finalNCols = nCols;
		Matrix<E, A, V> result = null;
		final V[][] elements = create(nRows, nCols);

		try
		{
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{

				int rowCount = 0;
				int colCount = 0;

				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{

					if (line_number > 0 && line_number - 1 >= finalRow
							&& line_number - 1 < finalRow + finalNRows)
					{
						for (int col = 0; col < tuple.size() - 1; col++)
						{
							if (col >= finalCol && col < finalCol + finalNCols)
							{
								elements[rowCount][colCount] = getValue(tuple,
										(col + 1));
								colCount++;
							}
						}
						rowCount++;
						colCount = 0;
					}
				}
			});

			List<E> rowNames = getRowNames().subList(row, row + nRows);
			List<A> colNames = getColNames().subList(col, col + nCols);

			result = new MemoryMatrix<E, A, V>(rowNames, colNames, elements);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	/** Helper method for unchecked cast*/
	private V getValue(Tuple tuple, int index)
	{
		return valueConvertor.read(tuple.getString(index));
	}

	@Override
	public Class<V> getValueType()
	{
		return this.valueConvertor.getValueType();
	}
	
	public void write(CsvWriter writer) throws MatrixException
	{
		// NB this only works if names are unique!!!
		// set headers
		List<String> headers = new ArrayList<String>();
		for (A value : getColNames())
			headers.add(this.colConvertor.write(value));
		writer.setHeaders(headers);
		writer.writeHeader();
		for (E rowName : getRowNames())
		{
			writer.writeValue(this.rowConvertor.write(rowName));
			for (V value : getRowByName(rowName))
			{
				writer.writeSeparator();
				writer.writeValue(this.valueConvertor.write(value));
			}
			writer.writeEndOfLine();
		}

		writer.close();
	}
}

/**
 * Inner class as container for CsvReaderListener.handleLine because we need
 * 'final' parameters in these handlers.
 * 
 * @param <E>
 */
class finalObject<E>
{
	E obj;

	E get()
	{
		return obj;
	}

	void set(E obj)
	{
		this.obj = obj;
	}
}