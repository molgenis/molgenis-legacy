package org.molgenis.xgap.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.CsvToDatabase.ImportResult;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.util.DetectOS;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.TarGz;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import regressiontest.cluster.DataLoader;
import app.CsvExport;
import app.CsvImport;
import app.DatabaseFactory;
import filehandling.storage.StorageHandler;

/**
 * Test data matrix import and export across all backends, all retrieval
 * functions, data types, and most dimensions, transpositions, sparsities, and
 * text length variation.
 * 
 * To be used in xQTL automated test cases
 * 
 */
public class Archiver_XqtlTestNG
{

	Database db;
	File archive;

	@BeforeClass
	public void setup() throws Exception
	{
		//cleanup before we start
		XqtlSeleniumTest.deleteDatabase();

		//create db and file storage
		db = DatabaseFactory.create();
		StorageHandler sh = new StorageHandler(db);

		// assert db is empty
		Assert.assertFalse(sh.hasFileStorage(false, db));
		try
		{
			db.find(Investigation.class).get(0);
			Assert.fail("DatabaseException expected");
		}
		catch (DatabaseException expected)
		{
			// DatabaseException was thrown
		}

		// setup database tables
		String report = ResetXgapDb.reset(db, true);
		Assert.assertTrue(report.endsWith("SUCCESS"));

		// setup file storage
		sh.setFileStorage(storagePath(), db);
		sh.validateFileStorage(db);
		Assert.assertTrue(sh.hasValidFileStorage(db));
	}

	@Test
	public void importExampleData() throws Exception
	{
		System.out.println("** importing example data **");
		ArrayList<String> result = DataLoader.load(db, false);
		Assert.assertTrue(result.get(result.size() - 2).equals("Complete success"));
		checkIfExampleDataIsOK();
		System.out.println("** example data OK **");
	}

	@Test(dependsOnMethods = "importExampleData")
	public void exportArchive() throws Exception
	{
		System.out.println("** exporting to archive **");
		File tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "dbexport_" + System.nanoTime());
		tmpDir.mkdir();
		if (!tmpDir.exists()) throw new Exception("Could not create tmp folder: " + tmpDir.getAbsolutePath());
		List<Class<? extends Entity>> specialCases = new ArrayList<Class<? extends Entity>>();
		specialCases.add(org.molgenis.auth.MolgenisGroup.class);
		specialCases.add(org.molgenis.auth.MolgenisPermission.class);
		specialCases.add(org.molgenis.auth.MolgenisRoleGroupLink.class);
		specialCases.add(org.molgenis.auth.MolgenisUser.class);
		specialCases.add(org.molgenis.core.MolgenisEntity.class);
		new CsvExport().exportSpecial(tmpDir, db, specialCases, true);
		archive = TarGz.tarDir(tmpDir);
		Assert.assertTrue(archive.exists());
		System.out.println("** tarred into: " + archive.getAbsolutePath() + " **");
	}

	@Test(dependsOnMethods = "exportArchive")
	public void softResetDb() throws Exception
	{
		System.out.println("** soft reset of database (removing relational data but not files) **");
		String report = ResetXgapDb.reset(db, false);
		Assert.assertTrue(report.endsWith("SUCCESS"));
		Assert.assertTrue(db.find(Marker.class).size() == 0);
		Assert.assertTrue(db.find(Individual.class).size() == 0);
		Assert.assertTrue(db.find(Data.class).size() == 0);
		System.out.println("** soft reset of database OK **");
	}

	@Test(dependsOnMethods = "softResetDb")
	public void importArchive() throws Exception
	{
		System.out.println("** importing archive **");
		File extractDir = TarGz.tarExtract(archive);
		ImportResult i = CsvImport.importAll(extractDir, db, new SimpleTuple(), true);
		Assert.assertTrue(i.getErrorItem().equals("no error found"));
		checkIfExampleDataIsOK();
		System.out.println("** archive import went OK **");
	}

	@Test(dependsOnMethods = "importArchive")
	public void hardResetDb() throws Exception
	{
		System.out.println("** hard reset of database (removing everything) **");
		String report = ResetXgapDb.reset(db, true);
		Assert.assertTrue(report.endsWith("SUCCESS"));
		Assert.assertTrue(db.find(Marker.class).size() == 0);
		Assert.assertTrue(db.find(Individual.class).size() == 0);
		Assert.assertTrue(db.find(Data.class).size() == 0);
		System.out.println("** hard reset of database OK **");
	}

	@Test(dependsOnMethods = "hardResetDb")
	public void importArchiveAgain() throws Exception
	{
		System.out.println("** importing archive again **");
		File extractDir = TarGz.tarExtract(archive);
		ImportResult i = CsvImport.importAll(extractDir, db, new SimpleTuple(), true);
		Assert.assertTrue(i.getErrorItem().equals("no error found"));

		// we expect to see database records
		Assert.assertTrue(db.find(Marker.class).size() > 0);
		Assert.assertTrue(db.find(Individual.class).size() > 0);
		Assert.assertTrue(db.find(Data.class).size() > 0);
		Data geno = db.find(Data.class, new QueryRule(Data.NAME, Operator.EQUALS, "genotypes")).get(0);

		try
		{
			// but not binary file for data matrix
			new DataMatrixHandler(db).createInstance(geno, db);
			Assert.fail("DatabaseException expected");
		}
		catch (FileNotFoundException expected)
		{
			// FileNotFoundException was thrown as expected
		}

		System.out.println("** archive import went OK again, file now missing as expected **");
	}

	/**
	 * Helper function, use to see if the example data is all there.
	 */
	private void checkIfExampleDataIsOK() throws Exception
	{
		Assert.assertTrue(db.find(Marker.class).size() > 0);
		Assert.assertTrue(db.find(Individual.class).size() > 0);
		Assert.assertTrue(db.find(Data.class).size() > 0);

		Data geno = db.find(Data.class, new QueryRule(Data.NAME, Operator.EQUALS, "genotypes")).get(0);
		DataMatrixInstance dm = new DataMatrixHandler(db).createInstance(geno, db);

		Assert.assertTrue(dm.getElement(0, 0).equals("A"));
		Assert.assertTrue(dm.getElement(1, 2).equals("B"));
	}

	/**
	 * Helper function. Get the storage path to use in test.
	 */
	public String storagePath()
	{
		String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_archiver_test_data";
		if (DetectOS.getOS().startsWith("windows"))
		{
			return storagePath.replace("\\", "/");
		}
		else
		{
			return storagePath;
		}
	}

}