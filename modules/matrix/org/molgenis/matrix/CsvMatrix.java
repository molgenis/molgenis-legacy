package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.matrix.convertors.CsvMatrixValueConvertor;
import org.molgenis.matrix.convertors.DoubleCsvMatrixValueConvertor;
import org.molgenis.matrix.convertors.StringCsvMatrixValueConvertor;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

/**
 * A MemoryMatrix that loads its data from a CsvReader.
 * This scales not very well ;-)
 * 
 * Example usage:
 * 
 * <pre>
 * Matrix m = new CsvMatrix(File file);
 * </pre>
 * 
 * @param <E>
 */
public class CsvMatrix<E> extends MemoryMatrix<E>
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	//for reading the csv
	private CsvReader csvReader;
	
	//convertor to read the values
	private CsvMatrixValueConvertor convertor;
	
	//class that holds the valueType
	private Class<E> valueType;
	
	/**
	 * Creates a MemoryMatrix<String>
	 * 
	 * @throws MatrixException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public CsvMatrix(File f) throws FileNotFoundException, MatrixException
	{
		this((Class<E>)String.class, f);
	}
	
	/**
	 * Creates a MemoryMatrix<valueClass>
	 *
	 * @param csvFile
	 */
	public CsvMatrix(Class<E> valueClass, File f) throws FileNotFoundException, MatrixException
	{
		this(valueClass, new CsvFileReader(f));
	}

	/**
	 * Set the value type of the values. 
	 * 
	 * @param valueClass. Currently only differentiates between Double and String
	 * @param reader
	 * @throws MatrixException
	 */
	public CsvMatrix(Class<E> valueClass, CsvReader reader) throws MatrixException
	{
		super();
		//get the convertor to go from Strings to E
		convertor = getConvertor(valueClass);
		valueType = valueClass;
		csvReader = reader;

		// put the rownames and colnames in parent
		try
		{
			this.setRowNames(csvReader.rownames());
			this.setColNames(csvReader.colnames().subList(1,
					csvReader.colnames().size()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}

	}

	/**
	 * Generate a convertor for a class, currently only Double and String
	 * @param valueClass
	 * @return
	 */
	private CsvMatrixValueConvertor getConvertor(Class<E> valueClass)
	{
		if(Double.class.equals(valueClass))
		{
			return new DoubleCsvMatrixValueConvertor();
		}
		else
		{
			return new StringCsvMatrixValueConvertor();
		}
		
	}

	@Override
	public E[] getCol(final int colIndex) throws MatrixException
	{
		final E[] result = create(getRowCount());
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
	public E getValue(final int rowIndex, final int colIndex)
			throws MatrixException
	{
		// naive implementation
		final finalObject<E> finalResult = new finalObject<E>();
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
	protected E[][] getValues() throws MatrixException
	{
		final E[][] result = create(getRowCount(), getColCount());
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
	public E[] getRow(int rowIndex) throws MatrixException
	{
		final int finalRowIndex = rowIndex;
		final E[] result = create(getColCount());
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
	public Matrix<E> getSubMatrixByIndex(List<Integer> rowIndices, List<Integer> colIndices)
			throws MatrixException
	{
		// optimalization: sort ascending in primitive array, then dont use
		// .contains on list (slow) but smart counter
		Matrix<E> result = null;
		final E[][] elements = create(rowIndices.size(), colIndices.size());

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

		List<String> rowNames = new ArrayList<String>();
		List<String> colNames = new ArrayList<String>();

		for (int rowIndex : rowIndices)
		{
			rowNames.add(this.getRowNames().get(rowIndex).toString());
		}

		for (int colIndex : colIndices)
		{
			colNames.add(this.getColNames().get(colIndex).toString());
		}

		result = new MemoryMatrix<E>(rowNames, colNames, elements);
		return result;
	}

	@Override
	public Matrix<E> getSubMatrixByOffset(int row, int nRows, int col, int nCols)
			throws MatrixException
	{
		final int finalRow = row;
		final int finalNRows = nRows;
		final int finalCol = col;
		final int finalNCols = nCols;
		Matrix<E> result = null;
		final E[][] elements = create(nRows, nCols);

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

			List<String> rowNames = getRowNames().subList(row, row + nRows);
			List<String> colNames = getColNames().subList(col, col + nCols);

			result = new MemoryMatrix<E>(rowNames, colNames, elements);
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
	private E getValue(Tuple tuple, int index)
	{
		return (E) convertor.convert(tuple.getString(index));
	}
	
	@Override
	public Class<E> getValueType()
	{
		return this.valueType;
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