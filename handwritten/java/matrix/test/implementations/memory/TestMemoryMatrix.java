package matrix.test.implementations.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import matrix.implementations.memory.MemoryDataMatrixInstance;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;

public class TestMemoryMatrix extends TestCase
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
	public TestMemoryMatrix(Database db, int matrixDimension1, int matrixDimension2, int maxTextLength, boolean fixedTextLength,
			boolean sparse, boolean runRegressionTests, boolean runPerformanceTests, boolean skipPerElement)
			throws Exception
	{
		long start = System.currentTimeMillis();
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Memory";
		logger.info("Creating database instance and erasing all existing data..");
//		JDBCDatabase db = new JDBCDatabase("handwritten/properties/gcc.test.properties");
		
		Helper h = new Helper(db);
		
		h.printSettings(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse,
				runRegressionTests, runPerformanceTests, skipPerElement);

		//set storage of elements to 'Database' because there is no 'Memory'
		//we never write the matrices to this storage though :)
		h.prepareDatabaseAndFiles("Database", matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse);

		List<MemoryDataMatrixInstance<Object>> mmList = new ArrayList<MemoryDataMatrixInstance<Object>>();

		for (Data data : h.getDataList())
		{
			//MemoryMatrix mm = (MemoryMatrix) new CreateInstance(db, data).getInstance();
			mmList.add(h.createAndWriteRandomMemoryMatrix(h.getInputFilesDir(), data, db, matrixDimension2, matrixDimension1,
					maxTextLength, sparse, fixedTextLength));
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
			for (MemoryDataMatrixInstance<Object> mm : mmList)
			{
				for (String method : methods)
				{
					assertTrue(TestingMethods.parseToPlainAndCompare(logger, mm, mm.getData(),
							h.getInputFilesDir(), method, true, true));
				}
			}
		}

		if (runPerformanceTests)
		{

			logger.info("Performance tests..");
			for (MemoryDataMatrixInstance<Object> mm : mmList)
			{
				if (!skipPerElement)
				{
					performanceResults.put(mm.getData().getValueType()+"_element", TestingMethods.readSpeed_elementbyindex(logger, mm, mm.getData(), h.getInputFilesDir()));
				}
				performanceResults.put(mm.getData().getValueType()+"_row", TestingMethods.readSpeed_rowbyindex(logger, mm, mm.getData(), h.getInputFilesDir()));
				performanceResults.put(mm.getData().getValueType()+"_col", TestingMethods.readSpeed_colbyindex(logger, mm, mm.getData(), h.getInputFilesDir()));
				performanceResults.put(mm.getData().getValueType()+"_sublist", TestingMethods.readSpeed_submatrixbyindexlist(logger, mm, mm.getData(), h.getInputFilesDir()));
				performanceResults.put(mm.getData().getValueType()+"_suboffs", TestingMethods.readSpeed_submatrixbyindexoffset(logger, mm, mm.getData(), h.getInputFilesDir()));
			}
		}

		long stop = System.currentTimeMillis();
		logger.info("Regression test took: " + ((stop - start) / 1000.0) + " seconds");

	}
}
