package matrix.test.implementations.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import matrix.general.DataMatrixHandler;
import matrix.implementations.database.DatabaseDataMatrixInstance;
import matrix.implementations.database.DatabaseDataMatrixWriter;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;

public class TestDatabaseMatrix extends TestCase
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
	public TestDatabaseMatrix(Database db, int matrixDimension1, int matrixDimension2, int maxTextLength, boolean fixedTextLength,
			boolean sparse, boolean runRegressionTests, boolean runPerformanceTests, boolean skipPerElement)
			throws Exception
	{
		long start = System.currentTimeMillis();
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Database";
		logger.info("Creating database instance and erasing all existing data..");
	//	JDBCDatabase db = new JDBCDatabase("handwritten/properties/gcc.test.properties");
		
		Helper h = new Helper(db);
		
		h.printSettings(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse,
				runRegressionTests, runPerformanceTests, skipPerElement);

		h.prepareDatabaseAndFiles(storage, matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse);

		logger.info("Importing matrices..");
		long storeTimerStart = System.currentTimeMillis();
		
		new DatabaseDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db, false);
		
		long storeTimerStop = System.currentTimeMillis();
		long storeTime = storeTimerStop - storeTimerStart;
		int elems = ((matrixDimension1 * matrixDimension2) * 2);
		int elemsec = (int) ((elems) / (storeTime / 1000.0));
		this.performanceResults.put("Write", elemsec);
		logger.info(" -> Wrote " + elems + " elements in " + (storeTime / 1000.0) + " seconds (" + elemsec
				+ " elem/sec)");

		logger.info("Instantiating the matrices..");
		List<DatabaseDataMatrixInstance> dmList = new ArrayList<DatabaseDataMatrixInstance>();
		for (Data data : h.getDataList())
		{
			DatabaseDataMatrixInstance dm = (DatabaseDataMatrixInstance) new DataMatrixHandler(db).createInstance(data);
			dmList.add(dm);
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

			for (DatabaseDataMatrixInstance dm : dmList)
			{
				for (String method : methods)
				{
					assertTrue(TestingMethods.parseToPlainAndCompare(logger, dm, dm.getData(),
							h.getInputFilesDir(), method, true, true));
				}
			}
		}

		if (runPerformanceTests)
		{

			logger.info("Performance tests..");
			for (DatabaseDataMatrixInstance dm : dmList)
			{
				if (!skipPerElement)
				{
					performanceResults.put(dm.getData().getValueType()+"_element", TestingMethods.readSpeed_elementbyindex(logger, dm, dm.getData(), h.getInputFilesDir()));
				}
				performanceResults.put(dm.getData().getValueType()+"_row", TestingMethods.readSpeed_rowbyindex(logger, dm, dm.getData(), h.getInputFilesDir()));
				performanceResults.put(dm.getData().getValueType()+"_col", TestingMethods.readSpeed_colbyindex(logger, dm, dm.getData(), h.getInputFilesDir()));
				performanceResults.put(dm.getData().getValueType()+"_sublist", TestingMethods.readSpeed_submatrixbyindexlist(logger, dm, dm.getData(), h.getInputFilesDir()));
				performanceResults.put(dm.getData().getValueType()+"_suboffs", TestingMethods.readSpeed_submatrixbyindexoffset(logger, dm, dm.getData(), h.getInputFilesDir()));
			}
		}
		
		db.close();

		long stop = System.currentTimeMillis();
		logger.info("Regression test took: " + ((stop - start) / 1000.0) + " seconds");

	}
}
