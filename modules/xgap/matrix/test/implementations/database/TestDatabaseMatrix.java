package matrix.test.implementations.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.general.DataMatrixHandler;
import matrix.implementations.database.DatabaseDataMatrixInstance;
import matrix.implementations.database.DatabaseDataMatrixWriter;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.testng.Assert;

public class TestDatabaseMatrix
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
	public TestDatabaseMatrix(Database db, Params params)
			throws Exception
	{
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Database";

		logger.info("Creating database instance and erasing all existing data..");
		Helper h = new Helper(db);
		h.printSettings(storage, params);
		h.prepareDatabaseAndFiles(storage, params);

		logger.info("Importing matrices..");
		new DatabaseDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db, false);
		
		List<DatabaseDataMatrixInstance> dmList = new ArrayList<DatabaseDataMatrixInstance>();
		for (Data data : h.getDataList())
		{
			DatabaseDataMatrixInstance dm = (DatabaseDataMatrixInstance) new DataMatrixHandler(db).createInstance(data, db);
			dmList.add(dm);
		}

		logger.info("Regression tests..");
		String[] methods = new String[]
		{ "elementbyindex", "elementbyname", "rowbyindex", "rowbyname", "colbyindex", "colbyname",
				"submatrixbyindexlist", "submatrixbynamelist", "submatrixbyindexoffset", "submatrixbynameoffset" };

		if (params.skipPerElement)
		{
			methods = new String[]
			{ "rowbyindex", "rowbyname", "colbyindex", "colbyname", "submatrixbyindexlist", "submatrixbynamelist",
					"submatrixbyindexoffset", "submatrixbynameoffset" };
		}

		for (DatabaseDataMatrixInstance dm : dmList)
		{
			for (String method : methods)
			{
				Assert.assertTrue(TestingMethods.parseToPlainAndCompare(logger, dm, dm.getData(),
						h.getInputFilesDir(), method, true, true));
			}
		}
		
		db.close();
	}
}
