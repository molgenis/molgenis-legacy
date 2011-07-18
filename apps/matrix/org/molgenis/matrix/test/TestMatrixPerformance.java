package org.molgenis.matrix.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.molgenis.matrix.MatrixException;

public class TestMatrixPerformance extends TestMatrix
{
	//List<String> methods;

	public TestMatrixPerformance(TestMatrixParams params) throws MatrixException
	{
		super(params);
		params.runPerformanceTests = true;
		logger.info("testing: \n"+params);
	}

	
	/**
	 * Test Matrix.getValue(int,int)
	 * 
	 * @throws Exception
	 */
	@Test
	public void readSpeed_getValueByIndex() throws Exception
	{
		if (params.runPerformanceTests)
		{
			long start = System.currentTimeMillis();
			int rows = matrix.getRowCount();
			int cols = matrix.getColCount();
			for (int row = 0; row < rows; row++)
			{
				for (int col = 0; col < cols; col++)
				{
					// bm.get(row, col);
					Object o = matrix.getValue(row, col);
				}
			}
			long end = System.currentTimeMillis();
			long time = (end - start);
			simplePrint(time, "elementbyindex");
			int elementsPerSec = (int) ((matrix.getColCount() * matrix.getRowCount()) / (time / 1000.0));

			logger.info("readSpeed_elementbyindex: " + elementsPerSec
					+ " elementsPerSec");
		}
	}

	/**
	 * Test Matrix.getRow(int)
	 */
	@Test
	public void readSpeed_getRowByIndex() throws Exception
	{
		if (params.runPerformanceTests)
		{
			long start = System.currentTimeMillis();
			int rows = matrix.getRowCount();
			for (int row = 0; row < rows; row++)
			{
				// bm.row(row);
				Object[] o = matrix.getRow(row);
			}
			long end = System.currentTimeMillis();
			long time = (end - start);
			simplePrint(time, "rowbyindex");
			int elementsPerSec = (int) ((matrix.getColCount() * matrix.getRowCount()) / (time / 1000.0));

			logger.info("readSpeed_rowbyindex: " + elementsPerSec
					+ " elementsPerSec");
		}
	}

	/**
	 * Test Matrix.getCol(int)
	 */
	@Test
	public void readSpeed_getColByIndex() throws Exception
	{
		if (params.runPerformanceTests)
		{
			long start = System.currentTimeMillis();
			int cols = matrix.getColCount();
			for (int col = 0; col < cols; col++)
			{
				// bm.col(col);
				Object[] o = matrix.getCol(col);
			}
			long end = System.currentTimeMillis();
			long time = (end - start);
			simplePrint(time, "colbyindex");
			int elementsPerSec = (int) ((matrix.getColCount() * matrix.getRowCount()) / (time / 1000.0));

			logger.info("readSpeed_colbyindex: " + elementsPerSec
					+ " elementsPerSec");
		}
	}

	/**
	 * Test Matrix.submatrix(int[],int[]) TODO: make it random!
	 */
	@Test
	public void readSpeed_getSubMatrixByIndexLists() throws Exception
	{
		if (params.runPerformanceTests)
		{
			long start = System.currentTimeMillis();

			List<Integer> rowIndices = new ArrayList<Integer>();
			List<Integer> colIndices = new ArrayList<Integer>();
			for (int row = 0; row < matrix.getRowCount(); row++)
			{
				rowIndices.add(row);
			}
			for (int col = 0; col < matrix.getColCount(); col++)
			{
				colIndices.add(col);
			}

			matrix.getSubMatrixByIndex(rowIndices, colIndices);

			long end = System.currentTimeMillis();
			long time = (end - start);
			simplePrint(time, "submatrixbyindexlist");
			int elementsPerSec = (int) ((matrix.getColCount() * matrix.getRowCount()) / (time / 1000.0));

			logger.info("readSpeed_colbyindex: " + elementsPerSec
					+ " elementsPerSec");
		}
	}

	/**
	 * Test Matrix.getSubMatrixByOffset(int,int,int,int)
	 */
	@Test
	public void readSpeed_getSubMatrixByOffset() throws Exception
	{
		if (params.runPerformanceTests)
		{
			long start = System.currentTimeMillis();

			matrix.getSubMatrixByOffset(0, matrix.getRowCount(), 0, matrix.getColCount());

			long end = System.currentTimeMillis();
			long time = (end - start);
			simplePrint(time, "submatrixbyindexoffset");
			int elementsPerSec = (int) ((matrix.getColCount() * matrix.getColCount()) / (time / 1000.0));

			logger.info("readSpeed_submatrixbyindexoffset: " + elementsPerSec
					+ " elementsPerSec");
		}
	}

	private static void writeHeader(BufferedWriter bfw, List<String> colNames)
			throws IOException
	{
		String write = "";
		for (String s : colNames)
		{
			write += "\t" + s;
		}
		bfw.write(write + "\n");
	}

	public void simplePrint(long time, String method)
	{
		int elementsPerSec = (int) ((matrix.getColCount() * matrix.getColCount()) / (time / 1000.0));
		logger.info("readSpeed_" + method + " (" + params.source + "): " + time
				+ " ms. (" + elementsPerSec + " elem/sec)");

	}
}
