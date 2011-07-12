package org.molgenis.matrix.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.molgenis.matrix.DoubleCsvMemoryMatrix;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.MemoryMatrix;
import org.molgenis.util.CsvStringWriter;

public class TestMemoryMatrix
{
	@Test
	public void test1() throws MatrixException, IOException
	{
		cleanMemory("start", 0);
		
		List<String> rowNames = new ArrayList<String>();
		rowNames.add("row1");
		rowNames.add("row2");
		List<String> colNames = new ArrayList<String>();
		colNames.add("col1");
		colNames.add("col2");
		colNames.add("col3");
		Double[][] values = new Double[2][3];
		values[0] = new Double[]
		{ 1.1, 1.2, 1.3 };
		values[1] = new Double[]
		{ 2.1, 2.2, 2.3 };

		Matrix<String, String, Double> m = new MemoryMatrix<String, String, Double>(
				rowNames, colNames, values);
		
		assertEquals(m.getValue("row1","col1"), new Double(1.1d));
		assertEquals(m.getValue("row2","col3"), new Double(2.3d));
		
		assertEquals(m.getValue("row2","col2"), m.getValue(1,1));
		assertEquals(m.getValue("row1","col3"), m.getValue(0,2));
		
		assertEquals(m.getRow(0),values[0]);
		
		DoubleCsvMemoryMatrix m2  = new DoubleCsvMemoryMatrix(m);
		
		CsvStringWriter csvWriter = new CsvStringWriter(new StringWriter());
		m2.write(csvWriter);
		
		System.out.println(csvWriter.toString());
		
		
		cleanMemory("end", rowNames.size()*colNames.size());
		
		
	}
	
	@Test
	public void testMemoryUsage() throws MatrixException
	{
		cleanMemory("start", 0);
		
		Matrix<String,String,Double> matrix = MemoryMatrixFactory.create(1000,2000);
		
		cleanMemory("end", matrix.getRowNames().size()*matrix.getColNames().size());
		
		matrix.getCol(0);
	}
	
	private static void cleanMemory(String message, int count)
	{
		timestampAfter = System.currentTimeMillis();

		Runtime runtime = Runtime.getRuntime();

		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		after = allocatedMemory - freeMemory;
		for (int i = 0; i < 500; ++i)
		{
			runtime.runFinalization();
			runtime.gc();

			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				// ignore - should not happen
			}

			allocatedMemory = runtime.totalMemory();
			freeMemory = runtime.freeMemory();
			if (after == allocatedMemory - freeMemory) break;
			after = allocatedMemory - freeMemory;
			// System.out.println( "after " + after );
		}

		long maxMemory = runtime.maxMemory();
		long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);

		after = allocatedMemory - freeMemory;

		if ((after - before) > (1024 * 1024)) logger.debug(message + ": used "
				+ dFormat.format((double) (after - before) / (double) (1024 * 1024)) + " MB for "
				+ dFormat.format(count) + " objects, " + Math.round((double) (after - before) / (double) count)
				+ " Bytes per object, " + (timestampAfter - timestampBefore) + " milliseconds, "
				+ dFormat.format(Math.round((double) count * 1000 / (double) (timestampAfter - timestampBefore + 1)))
				+ " objects per second");
		else
			logger.debug(message
					+ ": used "
					+ dFormat.format((double) (after - before) / (double) 1024)
					+ " KB for "
					+ dFormat.format(count)
					+ " objects, "
					+ Math.round((double) (after - before) / (double) count)
					+ " Bytes per object, "
					+ (timestampAfter - timestampBefore)
					+ " milliseconds, "
					+ dFormat.format(Math
							.round((double) count * 1000 / (double) (timestampAfter - timestampBefore + 1)))
					+ " objects per second");

		before = after;
		timestampBefore = System.currentTimeMillis();
	}

	public static long timestampBefore = System.currentTimeMillis();
	public static long timestampAfter = System.currentTimeMillis();
	public static long after = 0;
	public static long before = 0;
	public static Logger logger = Logger.getLogger(TestMemoryMatrix.class);
	private static DecimalFormat dFormat = new DecimalFormat("#,###,###,###.###");
}
