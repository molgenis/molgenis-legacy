package matrix.test.implementations.binary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixWriter;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;

public class TestBinMatrix extends TestCase
{
	List<String> uniqueNames = new ArrayList<String>();
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	HashMap<String, Integer> performanceResults = new HashMap<String, Integer>();

	public HashMap<String, Integer> getPerformanceResults()
	{
		return performanceResults;
	}

	/**
	 * WARNING: running this test will empty the contents of your selected
	 * database and possibly overwrite matrix backend data! Consider carefully
	 * before running!
	 * 
	 * @param matrixDimension1
	 * @param matrixDimension2
	 * @param maxTextLength
	 * @param fixedTextLength
	 * @param sparse
	 * @param runRegressionTests
	 * @param runPerformanceTests
	 * @throws Exception
	 */
	public TestBinMatrix(Database db, int matrixDimension1, int matrixDimension2, int maxTextLength, boolean fixedTextLength,
			boolean sparse, boolean runRegressionTests, boolean runPerformanceTests, boolean skipPerElement)
			throws Exception
	{
		long start = System.currentTimeMillis();
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Binary";
		logger.info("Creating database instance and erasing all existing data..");
		//JDBCDatabase db = new JDBCDatabase("handwritten/properties/gcc.test.properties");
		
		Helper h = new Helper(db);
		
		h.printSettings(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse,
				runRegressionTests, runPerformanceTests, skipPerElement);

		h.prepareDatabaseAndFiles(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse);

		logger.info("Transforming the files into their binary counterpart in the storage directory..");
		long storeTimerStart = System.currentTimeMillis();
		
		new BinaryDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db);
		
		long storeTimerStop = System.currentTimeMillis();
		long storeTime = storeTimerStop - storeTimerStart;
		int elems = ((matrixDimension1 * matrixDimension2) * 2);
		int elemsec = (int) ((elems) / (storeTime / 1000.0));
		this.performanceResults.put("Write", elemsec);
		logger.info(" -> Wrote " + elems + " elements in " + (storeTime / 1000.0) + " seconds (" + elemsec
				+ " elem/sec)");

		logger.info("Instantiating the matrices..");
		List<BinaryDataMatrixInstance> bmList = new ArrayList<BinaryDataMatrixInstance>();
		for (Data data : h.getDataList())
		{
			BinaryDataMatrixInstance bm = (BinaryDataMatrixInstance) new DataMatrixHandler(db).createInstance(data);
			bmList.add(bm);
		}

		if (runRegressionTests)
		{

			logger.info("Regression tests..");
			String[] methods = new String[]
			{ "elementbyindex", "elementbyname", "rowbyindex", "rowbyname", "colbyindex", "colbyname",
					"submatrixbyindexlist", "submatrixbynamelist", "submatrixbyindexoffset", "submatrixbynameoffset" };

			if (skipPerElement)
			{
				methods = new String[]
				{ "rowbyindex", "rowbyname", "colbyindex", "colbyname", "submatrixbyindexlist", "submatrixbynamelist",
						"submatrixbyindexoffset", "submatrixbynameoffset" };

			}
			for (BinaryDataMatrixInstance bm : bmList)
			{
				for (String method : methods)
				{
					assertTrue(TestingMethods.parseToPlainAndCompare(logger, bm, bm.getData(),
							h.getInputFilesDir(), method, true, true));
					// TestingMethods.parseToPlainAndCompare(logger, bm,
					// inputMatrixDir, method, true, true);
				}
			}
		}

		if (runPerformanceTests)
		{

			logger.info("Performance tests..");
			for (BinaryDataMatrixInstance bm : bmList)
			{
				if (!skipPerElement)
				{
					performanceResults.put(bm.getData().getValueType()+"_element", TestingMethods.readSpeed_elementbyindex(logger, bm, bm.getData(), h.getInputFilesDir()));
				}
				performanceResults.put(bm.getData().getValueType()+"_row", TestingMethods.readSpeed_rowbyindex(logger, bm, bm.getData(), h.getInputFilesDir()));
				performanceResults.put(bm.getData().getValueType()+"_col", TestingMethods.readSpeed_colbyindex(logger, bm, bm.getData(), h.getInputFilesDir()));
				performanceResults.put(bm.getData().getValueType()+"_sublist", TestingMethods.readSpeed_submatrixbyindexlist(logger, bm, bm.getData(), h.getInputFilesDir()));
				performanceResults.put(bm.getData().getValueType()+"_suboffs", TestingMethods.readSpeed_submatrixbyindexoffset(logger, bm, bm.getData(), h.getInputFilesDir()));
			}
		}

		long stop = System.currentTimeMillis();
		logger.info("Regression test took: " + ((stop - start) / 1000.0) + " seconds");

	}
}
