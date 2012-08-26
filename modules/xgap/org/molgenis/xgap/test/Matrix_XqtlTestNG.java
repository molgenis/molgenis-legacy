package org.molgenis.xgap.test;

import java.io.File;

import matrix.test.implementations.binary.TestBinMatrix;
import matrix.test.implementations.binary.TestBinMatrix2;
import matrix.test.implementations.csv.TestFileMatrix;
import matrix.test.implementations.database.TestDatabaseMatrix;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.memory.TestMemoryMatrix;

import org.molgenis.framework.db.Database;
import org.molgenis.util.DetectOS;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import app.DatabaseFactory;
import app.servlet.UsedMolgenisOptions;
import filehandling.storage.StorageHandler;

/**
 * Test data matrix import and export across all backends, all retrieval functions,
 * data types, and most dimensions, transpositions, sparsities, and text length variation.
 * 
 * To be used in xQTL automated test cases
 *
 */
public class Matrix_XqtlTestNG
{

	Database db;

	@DataProvider(name = "params")
	public static Object[][] data() {
		Object[][] data = new Object[][] {
				//			 DIM DIM TEXT	FIX.T?  SPARSE?	SKIPEL.?
				{ new Params(1,	 1,  0,		true,	false,	false) },
				{ new Params(1,	 1,  1,		true,	false,	false) },
				{ new Params(20, 1, 10,		true,	true,	false) },
				{ new Params(1, 20, 10,		false,	false,	false) },
				{ new Params(50, 10, 127,	false,	true,	false) },
				{ new Params(10, 50, 127,	true,	false,	false) },
				};
		return data;
	}

	@BeforeClass(alwaysRun = true)
	public void setupBeforeClass() throws Exception
	{
		//cleanup before we start
		XqtlSeleniumTest.deleteDatabase();
		db = DatabaseFactory.create();

		//setup database tables
		String report = ResetXgapDb.reset(db, false);
		Assert.assertTrue(report.endsWith("SUCCESS"));

		// setup file storage
		StorageHandler sh = new StorageHandler(db);
		sh.setFileStorage(storagePath(), db);
		sh.validateFileStorage(db);
		Assert.assertTrue(sh.hasValidFileStorage(db));
	}

	/**
	 * Helper function. Get the storage path to use in test.
	 */
	public String storagePath()
	{
		String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_matrix_test_data";
		if (DetectOS.getOS().startsWith("windows"))
		{
			return storagePath.replace("\\", "/");
		}
		else
		{
			return storagePath;
		}
	}

	@AfterClass(alwaysRun = true)
	public void cleanupAfterClass() throws InterruptedException, Exception
	{
		db.close();
		XqtlSeleniumTest.deleteStorage(new UsedMolgenisOptions().appName);
	}

	@Test(dataProvider = "params")
	public void binary(Params params) throws Exception
	{
		new TestBinMatrix(db, params);
	}
	
	@Test(dataProvider = "params")
	public void binary2(Params params) throws Exception
	{
		new TestBinMatrix2(db, params);
	}

	@Test(dataProvider = "params")
	public void database(Params params) throws Exception
	{
		new TestDatabaseMatrix(db, params);
	}

	@Test(dataProvider = "params")
	public void file(Params params) throws Exception
	{
		new TestFileMatrix(db, params);
	}

	@Test(dataProvider = "params")
	public void memory(Params params) throws Exception
	{
		new TestMemoryMatrix(db, params);
	}

}