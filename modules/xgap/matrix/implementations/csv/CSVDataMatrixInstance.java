package matrix.implementations.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.memory.MemoryDataMatrixInstance;

import org.apache.log4j.Logger;
import org.molgenis.core.MolgenisFile;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.matrix.MatrixException;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

public class CSVDataMatrixInstance extends AbstractDataMatrixInstance<Object>
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	private CsvReader reader;
	Data dataDescription;
	File src;

	public CSVDataMatrixInstance(Database db, MolgenisFile mf) throws Exception
	{
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		new CSVDataMatrixInstance(dmh.findData(mf, db), dmh.getFile(mf, db));
	}

	public CSVDataMatrixInstance(Data data, File file) throws Exception
	{
		src = file;
		reader = new CsvFileReader(file);

		this.setData(data);

		// put the rownames and colnames in parent
		this.setRowNames(reader.rownames());
		this.setColNames(reader.colnames().subList(1, reader.colnames().size()));

		this.setNumberOfCols(this.getColNames().size());
		this.setNumberOfRows(this.getRowNames().size());

	}

	@Override
	public Object[] getCol(int colIndex) throws Exception
	{
		final int finalColIndex = colIndex;
		final Object[] result = new Object[getNumberOfRows()];
		reader.reset();
		int line_number = 1;
		for (Tuple tuple : reader)
		{
			result[line_number - 1] = tuple.getObject(finalColIndex + 1);
			line_number++;
		}

		return result;
	}

	@Override
	public Object getElement(int rowIndex, int colIndex) throws Exception
	{
		final int finalColIndex = colIndex;
		final int finalRowIndex = rowIndex;
		// naive implementation
		final finalObject finalResult = new finalObject();
		reader.reset();
		int row = 0;
		for (Tuple tuple : reader)
		{
			if (row == rowIndex) finalResult.set(tuple.getObject(finalColIndex + 1));
			row++;
		}

		return finalResult.get();
	}

	@Override
	public Object[][] getElements() throws MatrixException
	{
		try
		{
			final Object[][] result = new Object[getNumberOfRows()][getNumberOfCols()];
			reader.reset();
			int line_number = 1;
			for (Tuple tuple : reader)
			{

				for (int col = 0; col < tuple.size() - 1; col++)
				{
					result[line_number - 1][col] = tuple.getObject(col + 1);
				}
				line_number++;

			}

			return result;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}

	}

	@Override
	public Object[] getRow(int rowIndex) throws Exception
	{
		final int finalRowIndex = rowIndex;
		final Object[] result = new Object[getNumberOfCols()];
		reader.reset();
		int row = 0;
		for (Tuple tuple : reader)
		{
			if (row == rowIndex)
			{
				for (int col = 0; col < tuple.size() - 1; col++)
				{
					result[col] = tuple.getObject(col + 1);
				}
			}
			row++;
		}

		return result;
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException
	{
		try
		{
			// the optimized way: find out of indices form a single block
			// if so, used offset retrieval instead
			boolean offsetAble = true;
			for (int i = 0; i < rowIndices.length - 1; i++)
			{
				if (rowIndices[i] != (rowIndices[i + 1] + 1))
				{
					offsetAble = false;
					break;
				}

			}
			if (offsetAble)
			{
				for (int i = 0; i < colIndices.length - 1; i++)
				{
					if (colIndices[i] != (colIndices[i + 1] + 1))
					{
						offsetAble = false;
						break;
					}
				}
			}
			if (offsetAble)
			{
				return getSubMatrixByOffset(rowIndices[0], rowIndices.length, colIndices[0], colIndices.length);
			}

			// optimalization: sort ascending in primitive array, then dont use
			// contains on list (slow) but smart counter
			// TODO: probably broken!! assign elements[line_number - 1][col] is
			// wrong..
			// use:
			// HashMap<Integer, Integer> rowIndexPositions = new
			// HashMap<Integer,
			// Integer>();
			// HashMap<Integer, Integer> colIndexPositions = new
			// HashMap<Integer,
			// Integer>();

			AbstractDataMatrixInstance<Object> result = null;
			final Object[][] elements = new Object[rowIndices.length][colIndices.length];

			final ArrayList<Integer> rowIndicesList = new ArrayList<Integer>(rowIndices.length);
			for (int i : rowIndices)
			{
				rowIndicesList.add(i);
			}
			final ArrayList<Integer> colIndicesList = new ArrayList<Integer>(colIndices.length);
			for (int i : colIndices)
			{
				colIndicesList.add(i);
			}
			// final finalObject finalResult = new finalObject();
			reader.reset();
			int row = 0;
			for (Tuple tuple : reader)
			{
				if (rowIndicesList.contains(row))
				{
					for (int col = 1; col <= tuple.size(); col++)
					{
						if (colIndicesList.contains(col-1))
						{
							elements[row][col-1] = tuple.getObject(col);
						}
					}
				}
				row++;
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

			result = new MemoryDataMatrixInstance<Object>(rowNames, colNames, elements, this.getData());
			return result;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int startRow, int numRows, int startCol, int numCols)
			throws Exception
	{;
		AbstractDataMatrixInstance<Object> result = null;
		final Object[][] elements = new Object[numRows][numCols];

		// final finalObject finalResult = new finalObject();
		reader.reset();
		int row = 0;
		int rowCount = 0;
		int colCount = 0;
		for (Tuple tuple : reader)
		{
			if (row >= startRow && row < startRow + numRows)
			{
				colCount = 0;
				for (int col = 0; col < tuple.size() - 1; col++)
				{
					if (col >= startCol && col < startCol + numCols)
					{
						elements[rowCount][colCount] = tuple.getObject(col + 1);
						colCount++;
					}
				}
				rowCount++;
			}
			row++;
		}

		List<String> rowNames = getRowNames().subList(startRow, startRow + numRows);
		List<String> colNames = getColNames().subList(startCol, startCol + numCols);

		result = new MemoryDataMatrixInstance<Object>(rowNames, colNames, elements, this.getData());
		return result;
	}

	public File getAsFile() throws Exception
	{
		return src;
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

class finalObject
{
	Object obj;

	Object get()
	{
		return obj;
	}

	void set(Object obj)
	{
		this.obj = obj;
	}
}