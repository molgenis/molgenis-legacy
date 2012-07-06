//package org.molgenis.matrix.test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.apache.log4j.Logger;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//import org.junit.runners.Parameterized.Parameters;
//import org.molgenis.matrix.Matrix;
//import org.molgenis.matrix.MatrixException;
//import org.molgenis.matrix.MemoryMatrix;
//import org.molgenis.pheno.ObservedValue;
//
//import regressiontest.util.Util;
//
///** Test and benchmark all matrices. */
//@RunWith(value = Parameterized.class)
//public class TestMatrix extends TestCase
//{
//	@Parameters
//	public static Collection<Object[]> data() {
//		Object[][] data = new Object[][] {
//				//			           DIM DIM  TEXT	FIX.T?  SPARSE? R.TEST? P.TEST? SKIPEL.?
//				{ new TestMatrixParams(  1,   1,   0,	false,	false,	true,	false,	false) },
//				{ new TestMatrixParams(  1,   1,   0,	true,	true,	true,	false,	false) },
//				{ new TestMatrixParams(  1,   1,   1,	false,	true,	true,	false,	false) },
//				{ new TestMatrixParams(  1,   1,   1,	true,	false,	true,	false,	false) },
//				{ new TestMatrixParams( 20,  10,  50,	false,	true,	true,	false,	false) },
//				{ new TestMatrixParams( 10,  20,  50,	true,	false,	true,	false,	false) },
//				{ new TestMatrixParams( 20,  10,  50,	false,	false,	true,	false,	false) },
//				{ new TestMatrixParams( 10,  20,  50,	true,	true,	true,	false,	false) },
//				{ new TestMatrixParams( 75,  95,   2,	false,	true,	true,	false,	true)  },
//				{ new TestMatrixParams( 95,  75,   2,	true,	false,	true,	false,	true)  },
//				{ new TestMatrixParams(100,   5, 127,	false,	false,	true,	false,	true)  },
//				{ new TestMatrixParams(  5, 100, 127,	true,	true,	true,	false,	true)  }
//				};
//		return Arrays.asList(data);
//	}
//	
//	public Logger logger = Logger.getLogger(this.getClass());
//	public TestMatrixParams params;
//	public Matrix matrix;
//	
//	public TestMatrix(TestMatrixParams params) throws MatrixException
//	{
//		this.params = params;
//		matrix = this.createTestMatrix(params);
//		
////		if (!regularFullTest)
////		{
////			// default high-performance-only test settings, dont edit
////			p.rowCount = 500; // 500
////			p.colCount = 500; // 500
////			p.maxTextLength = 2; // 2
////			p.fixedTextLength = true; // true
////			p.sparse = false; // false
////			p.runRegressionTests = false; // false
////			p.runPerformanceTests = true; // true
////			p.skipPerElement = true; // true
////		}
////		else
////		{
////			// default settings for a general regression test, dont edit
////			p.rowCount = 10; // 10
////			p.colCount = 20; // 20
////			p.maxTextLength = 5; // 5
////			p.fixedTextLength = false; // false
////			p.sparse = true; // true
////			p.runRegressionTests = true; // true
////			p.runPerformanceTests = false; // false
////			p.skipPerElement = false; // false
////		}
//	}
//
//	/**
//	 * print the matrix to a string.
//	 * 
//	 * @param m
//	 * @return
//	 * @throws MatrixException
//	 */
//	private String matrixToString(Matrix m) throws MatrixException
//	{
//		// print headers
//		String result = "\nMatrix: \n";
//		List<String> colnames = m.getColNames();
//		for (String colName : colnames)
//		{
//			result += "\t" + colName;
//		}
//
//		// print rows
//		List<String> rownames = m.getRowNames();
//		for (int i = 0; i < rownames.size(); i++)
//		{
//			result += "\n" + rownames.get(i);
//			for (int j = 0; j < colnames.size(); j++)
//			{
//				if (m.getValue(i, j) instanceof ObservedValue)
//				{
//					result += "\tvalue:"
//							+ ((ObservedValue) m.getValue(i, j)).getValue();
//				}
//				else
//				{
//					result += "\t" + m.getValue(i, j);
//				}
//			}
//		}
//
//		return result + "\n";
//	}
//	
//	/**
//	 * Creates a test matrix in memory based on params
//	 * @param p
//	 * @return
//	 * @throws MatrixException
//	 */
//	public MemoryMatrix createTestMatrix(TestMatrixParams p) throws MatrixException
//	{
//
//		Object[][] elements = null;
//
//		if (p.valueType.equals(Double.class))
//		{
//			elements = new Double[p.rowCount][p.colCount];
//			for (int rowIndex = 0; rowIndex < p.rowCount; rowIndex++)
//			{
//				for (int colIndex = 0; colIndex < p.colCount; colIndex++)
//				{
//					if (p.sparse)
//					{
//						if (Util.getRandomBoolean() == true)
//						{
//							double rand = Util.getRandomDouble();
//							elements[rowIndex][colIndex] = rand;
//						}
//						else
//						{
//							elements[rowIndex][colIndex] = null;
//						}
//					}
//					else
//					{
//						double rand = Util.getRandomDouble();
//						elements[rowIndex][colIndex] = rand;
//					}
//				}
//			}
//		}
//		else
//		{
//			// for decimal, swap row with col dimension size
//			elements = new Object[p.rowCount][p.colCount];
//			for (int rowIndex = 0; rowIndex < p.rowCount; rowIndex++)
//			{
//				for (int colIndex = 0; colIndex < p.colCount; colIndex++)
//				{
//					if (p.sparse)
//					{
//						if (Util.getRandomBoolean() == true)
//						{
//							String rand = Util.getRandomString(p.maxTextLength,
//									p.fixedTextLength);
//							elements[rowIndex][colIndex] = rand;
//						}
//						else
//						{
//							elements[rowIndex][colIndex] = null;
//						}
//					}
//					else
//					{
//						String rand = Util.getRandomString(p.maxTextLength,
//								p.fixedTextLength);
//						elements[rowIndex][colIndex] = rand;
//					}
//				}
//			}
//		}
//
//		List<String> rowNames = new ArrayList<String>();
//		for(int i = 0; i < p.rowCount; i++)
//		{
//			rowNames.add(Util.getRandomString(10,false)+"_row"+i);
//		}
//		
//		List<String> colNames = new ArrayList<String>();
//		for(int i = 0; i < p.colCount; i++)
//		{
//			colNames.add(Util.getRandomString(10,false)+"_col"+i);
//		}
//		
//		MemoryMatrix mm = new MemoryMatrix(rowNames, colNames, elements);
//		// mm.changeDataName(data.getName());
//
//		return mm;
//	}
//
////	private HashMap<String, Integer> runTests(Matrix mm, TestMatrixParams params) throws MatrixException, Exception
////	{
////		long start = System.currentTimeMillis();
////		/**
////		 * Assumption: the list of the traits/subjects that are created of size
////		 * N match the corresponding size N of totalCols/totalRows of the
////		 * randomized matrices and are therefore used 1:1 as row/colnames
////		 */
////		
////		HashMap<String, Integer> performanceResults = new HashMap<String, Integer>();
////
////		logger.debug("TextMatrix run\n"+params.toString()+" using matrix "+this.matrixToString(mm));
////
////		if (params.runRegressionTests)
////		{
////
////			logger.info("Regression tests..");
////			String[] methods = new String[]
////			{ "elementbyindex", "elementbyname", "rowbyindex", "rowbyname", "colbyindex", "colbyname",
////					"submatrixbyindexlist", "submatrixbynamelist", "submatrixbyindexoffset", "submatrixbynameoffset" };
////
////			if (params.skipPerElement)
////			{
////				methods = new String[]
////				{ "rowbyindex", "rowbyname", "colbyindex", "colbyname", "submatrixbyindexlist", "submatrixbynamelist",
////						"submatrixbyindexoffset", "submatrixbynameoffset" };
////
////			}
////
////				for (String method : methods)
////				{
//////					assertTrue(TestingMethods.parseToPlainAndCompare(logger, mm, 
//////							new File(params.dir), "comparePlainAndMemory", method, true, true));
////				}
////		}
////
////		if (params.runPerformanceTests)
////		{
////			logger.info("Performance tests..");
////
////				if (!params.skipPerElement)
////				{
////					performanceResults.put(mm.getValueType()+"_element", TestMatrixPerformance.readSpeed_elementbyindex(logger, mm, new File(params.dir)));
////				}
////				performanceResults.put(mm.getValueType()+"_row", TestMatrixPerformance.readSpeed_rowbyindex(logger, mm, new File(params.dir)));
////				performanceResults.put(mm.getValueType()+"_col", TestMatrixPerformance.readSpeed_colbyindex(logger, mm, new File(params.dir)));
////				performanceResults.put(mm.getValueType()+"_sublist", TestMatrixPerformance.readSpeed_submatrixbyindexlist(logger, mm, new File(params.dir)));
////				performanceResults.put(mm.getValueType()+"_suboffs", TestMatrixPerformance.readSpeed_submatrixbyindexoffset(logger, mm, new File(params.dir)));
////		}
////
////		long stop = System.currentTimeMillis();
////		logger.info("Regression test took: " + ((stop - start) / 1000.0) + " seconds");
////		
////		return performanceResults;
////
////	}
//}
