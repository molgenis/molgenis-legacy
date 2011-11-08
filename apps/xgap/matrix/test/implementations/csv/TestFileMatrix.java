package matrix.test.implementations.csv;

import java.util.ArrayList;
import java.util.List;

import matrix.general.DataMatrixHandler;
import matrix.implementations.csv.CSVDataMatrixInstance;
import matrix.implementations.csv.CSVDataMatrixWriter;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.testng.Assert;

public class TestFileMatrix
{
	List<String> uniqueNames = new ArrayList<String>();
	Logger logger = Logger.getLogger(getClass().getSimpleName());

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
	 * @throws Exception
	 */
	public TestFileMatrix(Database db, Params params)
			throws Exception
	{
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */
		
		String storage = "CSV";
		
		logger.info("Creating database instance and erasing all existing data..");
		Helper h = new Helper(db);
		h.printSettings(storage, params);
		h.prepareDatabaseAndFiles(storage, params);

		logger.info("Transforming the files into their plain counterpart in the storage directory..");
		new CSVDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db);
				
		logger.info("Instantiating the matrices..");
		List<CSVDataMatrixInstance> fmList = new ArrayList<CSVDataMatrixInstance>();
		for (Data data : h.getDataList())
		{
			CSVDataMatrixInstance fm = (CSVDataMatrixInstance) new DataMatrixHandler(db).createInstance(data, db);
			fmList.add(fm);
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
		for (CSVDataMatrixInstance fm : fmList)
		{
			for (String method : methods)
			{
				Assert.assertTrue(TestingMethods.parseToPlainAndCompare(logger, fm, fm.getData(),
						h.getInputFilesDir(), method, true, true));
			}
		}
	}
}
