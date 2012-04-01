package matrix.test.implementations.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.DataMatrixInstance;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;

import regressiontest.csv.DirectoryCompare;
import decorators.NameConvention;

public class TestingMethods
{
	public static boolean parseToPlainAndCompare(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir, String method,
			boolean fileWrite, boolean simpleOutput) throws Exception
	{
		long start = System.currentTimeMillis();
		long end = 0;
		boolean filesAreEqual = false;
		BufferedWriter bfw = null;
		File exported = null;

		if (fileWrite)
		{
			exported = new File(inputMatrixDir + File.separator + description.getName() + "_" + method
					+ ".txt");
			exported.createNewFile();
			bfw = new BufferedWriter(new FileWriter(exported));
		}

		if (fileWrite)
		{
			writeHeader(bfw, m.getColNames());
		}

		DataMatrixInstance newMatrix = null;
		if (method.substring(0, 9).equals("submatrix"))
		{

			if (method.equals("submatrixbyindexlist"))
			{
				int[] rowIndices = new int[m.getNumberOfRows()];
				int[] colIndices = new int[m.getNumberOfCols()];
				for (int row = 0; row < m.getNumberOfRows(); row++)
				{
					rowIndices[row] = row;
				}
				for (int col = 0; col < m.getNumberOfCols(); col++)
				{
					colIndices[col] = col;
				}

				newMatrix = m.getSubMatrix(rowIndices, colIndices);
			}

			if (method.equals("submatrixbynamelist"))
			{
				newMatrix = m.getSubMatrix(m.getRowNames(), m.getColNames());
			}

			if (method.equals("submatrixbyindexoffset"))
			{
				newMatrix = m.getSubMatrixByOffset(0, m.getNumberOfRows(), 0, m.getNumberOfCols());
			}
			if (method.equals("submatrixbynameoffset"))
			{
				newMatrix = m.getSubMatrixByOffset(m.getRowNames().get(0).toString(), m.getNumberOfRows(), m.getColNames().get(0).toString(), m.getNumberOfCols());
			}

			for (int row = 0; row < newMatrix.getNumberOfRows(); row++)
			{
				Object[] rowObj = newMatrix.getRow(row);
				String rowString = newMatrix.getRowNames().get(row).toString();
				for (int col = 0; col < newMatrix.getNumberOfCols(); col++)
				{
					rowString += "\t" + (rowObj[col] == null ? "" : rowObj[col].toString());
				}
				if (fileWrite)
				{
					bfw.write(rowString + "\n");
				}
			}

		}

		if (method.substring(0, 3).equals("col"))
		{
			Object[][] allObj = new Object[m.getNumberOfCols()][m.getNumberOfRows()];
			for (int col = 0; col < m.getNumberOfCols(); col++)
			{
				if (method.equals("colbyindex"))
				{
					allObj[col] = m.getCol(col);
				}

				if (method.equals("colbyname"))
				{
					allObj[col] = m.getCol((m.getColNames().get(col).toString()));
				}
			}
			for (int row = 0; row < m.getNumberOfRows(); row++)
			{
				String rowString = m.getRowNames().get(row).toString();
				for (int col = 0; col < m.getNumberOfCols(); col++)
				{
					rowString += "\t" + (allObj[col][row] == null ? "" : allObj[col][row].toString());
				}
				if (fileWrite)
				{
					bfw.write(rowString + "\n");
				}
			}
		}

		if (method.substring(0, 7).equals("element") || method.substring(0, 3).equals("row"))
		{

			for (int row = 0; row < m.getNumberOfRows(); row++)
			{
				String rowString = m.getRowNames().get(row).toString();
				Object[] rowObj = null;
				if (method.equals("rowbyindex"))
				{
					rowObj = m.getRow(row);
				}

				if (method.equals("rowbyname"))
				{
					rowObj = m.getRow(m.getRowNames().get(row).toString());
				}

				if (method.substring(0, 3).equals("row"))
				{
					for (int col = 0; col < m.getNumberOfCols(); col++)
					{
						rowString += "\t" + (rowObj[col] == null ? "" : rowObj[col].toString());
					}
				}

				if (method.substring(0, 7).equals("element"))
				{
					for (int col = 0; col < m.getNumberOfCols(); col++)
					{
						if (method.equals("elementbyindex"))
						{
							Object obj = m.getElement(row, col);
							rowString += "\t" + (obj == null ? "" : obj.toString());
						}
						if (method.equals("elementbyname"))
						{
							Object obj = m.getElement(m.getRowNames().get(row).toString(), m.getColNames().get(col).toString());
							rowString += "\t"
									+ (obj == null ? "" : obj.toString());
						}

					}
				}
				if (fileWrite)
				{
					bfw.write(rowString + "\n");
				}
			}
		}

		end = System.currentTimeMillis();

		if (fileWrite)
		{
			bfw.close();
			File original = new File(inputMatrixDir + File.separator + NameConvention.escapeFileName(description.getName()) + ".txt");
			filesAreEqual = DirectoryCompare.compareFileContent(original, exported);
			if (simpleOutput)
			{
				logger.info("\t" + (end - start) + "\t" + description.getValueType() + ", " + method
						+ "\teq: " + (filesAreEqual == true ? "PASS" : "FAIL"));
			}
			else
			{
				logger.info("Exported full matrix of type '" + description.getValueType() + "' ("
						+ m.getNumberOfCols() + "x" + m.getNumberOfRows()
						+ ") to file, using read method '" + method + "', test equality to original ["
						+ (filesAreEqual == true ? "PASS" : "FAIL") + "] in " + (end - start) + " ms.");
			}
		}
		else
		{
			if (simpleOutput)
			{
				logger.info("\t" + (end - start) + "\t" + description.getValueType() + ", " + method
						+ "\tno comp.");
			}
			else
			{
				logger.info("Queried full matrix of type '" + description.getValueType() + "' ("
						+ m.getNumberOfCols() + "x" + m.getNumberOfRows()
						+ "), using read method '" + method + "', without file write or equality test in "
						+ (end - start) + " ms.");
			}
			filesAreEqual = true;
		}
		return filesAreEqual;
	}

	public static int readSpeed_elementbyindex(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir) throws Exception
	{
		long start = System.currentTimeMillis();
		int rows = m.getNumberOfRows();
		int cols = m.getNumberOfCols();
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				m.getElement(row, col);
			}
		}
		long end = System.currentTimeMillis();
		long time = (end - start);
		simplePrint(logger, m, description, time, "elementbyindex");
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		return elementsPerSec;
	}

	public static int readSpeed_rowbyindex(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir)
			throws Exception
	{
		long start = System.currentTimeMillis();
		int rows = m.getNumberOfRows();
		for (int row = 0; row < rows; row++)
		{
			m.getRow(row);
		}
		long end = System.currentTimeMillis();
		long time = (end - start);
		simplePrint(logger, m, description, time, "rowbyindex");
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		return elementsPerSec;
	}

	public static int readSpeed_colbyindex(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir)
			throws Exception
	{
		long start = System.currentTimeMillis();
		int cols = m.getNumberOfCols();
		for (int col = 0; col < cols; col++)
		{
			m.getCol(col);
		}
		long end = System.currentTimeMillis();
		long time = (end - start);
		simplePrint(logger, m, description, time, "colbyindex");
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		return elementsPerSec;
	}

	public static int readSpeed_submatrixbyindexlist(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir)
			throws Exception
	{
		long start = System.currentTimeMillis();

		int[] rowIndices = new int[m.getNumberOfRows()];
		int[] colIndices = new int[m.getNumberOfCols()];
		for (int row = 0; row < m.getNumberOfRows(); row++)
		{
			rowIndices[row] = row;
		}
		for (int col = 0; col < m.getNumberOfCols(); col++)
		{
			colIndices[col] = col;
		}

		m.getSubMatrix(rowIndices, colIndices);

		long end = System.currentTimeMillis();
		long time = (end - start);
		simplePrint(logger, m, description, time, "submatrixbyindexlist");
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		return elementsPerSec;
	}

	public static int readSpeed_submatrixbyindexoffset(Logger logger, DataMatrixInstance m, Data description, File inputMatrixDir)
			throws Exception
	{
		long start = System.currentTimeMillis();

		m.getSubMatrixByOffset(0, m.getNumberOfRows(), 0, m.getNumberOfCols());

		long end = System.currentTimeMillis();
		long time = (end - start);
		simplePrint(logger, m, description, time, "submatrixbyindexoffset");
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		return elementsPerSec;
	}

	private static void writeHeader(BufferedWriter bfw, List<String> colNames) throws IOException
	{
		String write = "";
		for (String s : colNames)
		{
			write += "\t" + s;
		}
		bfw.write(write + "\n");
	}

	public static void simplePrint(Logger logger, DataMatrixInstance m, Data description, long time, String method)
	{
		int elementsPerSec = (int) ((m.getNumberOfCols() * m.getNumberOfRows()) / (time / 1000.0));
		logger.info("readSpeed_" + method + " (" + description.getValueType() + "): " + time + " ms. ("
				+ elementsPerSec + " elem/sec)");

	}
}
