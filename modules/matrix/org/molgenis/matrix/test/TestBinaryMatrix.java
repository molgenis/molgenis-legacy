package org.molgenis.matrix.test;
//package org.molgenis.matrix.test;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.apache.log4j.Logger;
//import org.molgenis.data.Data;
//import org.molgenis.matrix.BinaryMatrix;
//
//public class TestBinMatrix extends TestCase
//{
//	List<String> uniqueNames = new ArrayList<String>();
//	Logger logger = Logger.getLogger(getClass().getSimpleName());
//	HashMap<String, Integer> performanceResults = new HashMap<String, Integer>();
//
//	public HashMap<String, Integer> getPerformanceResults()
//	{
//		return performanceResults;
//	}
//
//	/**
//	 * WARNING: running this test will empty the contents of your selected
//	 * database and possibly overwrite matrix backend data! Consider carefully
//	 * before running!
//	 * 
//	 * @param matrixDimension1
//	 * @param matrixDimension2
//	 * @param maxTextLength
//	 * @param fixedTextLength
//	 * @param sparse
//	 * @param runRegressionTests
//	 * @param runPerformanceTests
//	 * @throws Exception
//	 */
//	public TestBinMatrix(File binaryFile, int matrixDimension1, int matrixDimension2, int maxTextLength, boolean fixedTextLength,
//			boolean sparse, boolean runRegressionTests, boolean runPerformanceTests, boolean skipPerElement)
//			throws Exception
//	{
//		long start = System.currentTimeMillis();
//		/**
//		 * Assumption: the list of the traits/subjects that are created of size
//		 * N match the corresponding size N of totalCols/totalRows of the
//		 * randomized matrices and are therefore used 1:1 as row/colnames
//		 */
//
//		String storage = "Binary";
//		logger.info("Creating database instance and erasing all existing data..");
//		//JDBCDatabase db = new JDBCDatabase("handwritten/properties/gcc.test.properties");
//		
//		Helper h = new Helper();
//		h.printSettings(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse,
//				runRegressionTests, runPerformanceTests, skipPerElement);
//
//		h.prepareDatabaseAndFiles(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse);
//
//		logger.info("Transforming the files into their binary counterpart in the storage directory..");
//		long storeTimerStart = System.currentTimeMillis();
//		
//		new BinaryDataMatrixWriter(h.getDataList(), h.getInputFilesDir());
//		
//		long storeTimerStop = System.currentTimeMillis();
//		long storeTime = storeTimerStop - storeTimerStart;
//		int elems = ((matrixDimension1 * matrixDimension2) * 2);
//		int elemsec = (int) ((elems) / (storeTime / 1000.0));
//		this.performanceResults.put("Write", elemsec);
//		logger.info(" -> Wrote " + elems + " elements in " + (storeTime / 1000.0) + " seconds (" + elemsec
//				+ " elem/sec)");
//
//		logger.info("Instantiating the matrices..");
//		List<BinaryMatrix> bmList = new ArrayList<BinaryMatrix>();
//		for (Data data : h.getDataList())
//		{
//			BinaryMatrix bm = (BinaryMatrix) new BinaryMatrix(binaryFile);
//			bmList.add(bm);
//		}
//
//		if (runRegressionTests)
//		{
//
//			logger.info("Regression tests..");
//			String[] methods = new String[]
//			{ "elementbyindex", "elementbyname", "rowbyindex", "rowbyname", "colbyindex", "colbyname",
//					"submatrixbyindexlist", "submatrixbynamelist", "submatrixbyindexoffset", "submatrixbynameoffset" };
//
//			if (skipPerElement)
//			{
//				methods = new String[]
//				{ "rowbyindex", "rowbyname", "colbyindex", "colbyname", "submatrixbyindexlist", "submatrixbynamelist",
//						"submatrixbyindexoffset", "submatrixbynameoffset" };
//
//			}
//			for (BinaryMatrix bm : bmList)
//			{
//				for (String method : methods)
//				{
//					assertTrue(TestingMethods.parseToPlainAndCompare(logger, bm,
//							h.getInputFilesDir(), "binPlainCompare", method, true, true));
//					// TestingMethods.parseToPlainAndCompare(logger, bm,
//					// inputMatrixDir, method, true, true);
//				}
//			}
//		}
//
//		if (runPerformanceTests)
//		{
//
//			logger.info("Performance tests..");
//			for (BinaryMatrix bm : bmList)
//			{
//				if (!skipPerElement)
//				{
//					performanceResults.put(bm.getValueType()+"_element", TestingMethods.readSpeed_elementbyindex(logger, bm, h.getInputFilesDir()));
//				}
//				performanceResults.put(bm.getValueType()+"_row", TestingMethods.readSpeed_rowbyindex(logger, bm, h.getInputFilesDir()));
//				performanceResults.put(bm.getValueType()+"_col", TestingMethods.readSpeed_colbyindex(logger, bm, h.getInputFilesDir()));
//				performanceResults.put(bm.getValueType()+"_sublist", TestingMethods.readSpeed_submatrixbyindexlist(logger, bm, h.getInputFilesDir()));
//				performanceResults.put(bm.getValueType()+"_suboffs", TestingMethods.readSpeed_submatrixbyindexoffset(logger, bm, h.getInputFilesDir()));
//			}
//		}
//
//		long stop = System.currentTimeMillis();
//		logger.info("Regression test took: " + ((stop - start) / 1000.0) + " seconds");
//
//	}
//}
