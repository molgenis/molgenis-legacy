package matrix.test.implementations.binary;

import java.util.ArrayList;
import java.util.List;

import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance_NEW;
import matrix.implementations.binary.BinaryDataMatrixWriter;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.testng.Assert;

public class TestBinMatrix2
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
	public TestBinMatrix2(Database db, Params params)
			throws Exception
	{
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Binary";
		
		logger.info("Creating database instance and erasing all existing data..");
		Helper h = new Helper(db);
		h.printSettings(storage, params);
		System.out.println("NEW IMPLEMENTATION");
		h.prepareDatabaseAndFiles(storage, params);

		logger.info("Transforming the files into their binary counterpart in the storage directory..");
		new BinaryDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db);
	
		logger.info("Instantiating the matrices..");
		List<BinaryDataMatrixInstance_NEW> bmList = new ArrayList<BinaryDataMatrixInstance_NEW>();
		for (Data data : h.getDataList())
		{
			BinaryDataMatrixInstance_NEW bm = new BinaryDataMatrixInstance_NEW(new DataMatrixHandler(db).findSourceFile(data, db));
			bmList.add(bm);
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
		for (BinaryDataMatrixInstance_NEW bm : bmList)
		{
			for (String method : methods)
			{
				System.out.println("---> METHOD: " + method);
				Assert.assertTrue(TestingMethods.parseToPlainAndCompare(logger, bm, bm.getData(),
						h.getInputFilesDir(), method, true, true));
			}
		}
	}
}
