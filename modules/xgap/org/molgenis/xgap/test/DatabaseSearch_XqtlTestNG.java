package org.molgenis.xgap.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Entity;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.DetectOS;
import org.molgenis.xgap.Metabolite;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import plugins.cluster.demo.ClusterDemo;
import regressiontest.cluster.DataLoader;
import app.DatabaseFactory;
import app.servlet.UsedMolgenisOptions;
import filehandling.storage.StorageHandler;

public class DatabaseSearch_XqtlTestNG
{
	
	private Database db;
	
	@Test
	public void entityPropertyLimitations() throws Exception
	{	
//		System.out.println("s1 " + db.getMetaData().getEntities(false, false).size());
//		System.out.println("s2 " + db.getMetaData().getEntities(true, false).size());
//		System.out.println("s3 " + db.getMetaData().getEntities(false, true).size());
//		System.out.println("s4 " + db.getMetaData().getEntities(true, true).size());
//		
//		for(Entity e : db.getMetaData().getEntities(false, false))
//		{
//			System.out.println("F/F: " + e.getName());
//		}
//		
//		for(Entity e : db.getMetaData().getEntities(true, false))
//		{
//			System.out.println("T/F: " + e.getName());
//		}
//		
//		for(Entity e : db.getMetaData().getEntities(false, true))
//		{
//			System.out.println("F/T: " + e.getName());
//		}
//		
//		for(Entity e : db.getMetaData().getEntities(true, true))
//		{
//			System.out.println("T/T: " + e.getName());
//		}
		
		Assert.assertEquals(db.getMetaData().getEntities(false, false).size(), 66);
		Assert.assertEquals(db.getMetaData().getEntities(true, false).size(), 72);
		Assert.assertEquals(db.getMetaData().getEntities(false, true).size(), 73);
		Assert.assertEquals(db.getMetaData().getEntities(true, true).size(), 81);
	}
	
	
	@Test
	public void doSearch() throws Exception
	{	
		// Add some additional info to a certain metabolite
		// NOTE THAT: Formula is specific for Metabolite, while Description is common for ObservationElement
		Metabolite hydroxy = db.find(Metabolite.class, new QueryRule(Metabolite.NAME, Operator.EQUALS, "Hydroxypropyl")).get(0);
		hydroxy.setFormula("CH2CH(OH)CH3");
		hydroxy.setDescription("a semisynthetic, inert, viscoelastic polymer used as an ophthalmic lubricant");
		db.update(hydroxy);
		
		// Search 1: Find a subclass-specific attribute using search() on the subclass type
		List<Metabolite> metabs = db.search(Metabolite.class, "CH(OH)");
		Assert.assertEquals(metabs.size(), 1);
		System.out.println("Search 1: " + metabs.get(0).toString());
		
		// Search 2: Find a subclass-specific attribute using search() on the superclass type
		// NOTE: should yield no results (TODO???)
		List<ObservationElement> observationElements = db.search(ObservationElement.class, "CH(OH)");
		Assert.assertEquals(observationElements.size(), 0);
		System.out.println("Search 2: " + "NULL");
		
		// Search 3: Find a common attribute using search() on the subclass type
		List<Metabolite> metabs2 = db.search(Metabolite.class, "viscoelastic");
		Assert.assertEquals(metabs2.size(), 1);
		System.out.println("Search 3: " + metabs2.get(0).toString());
		
		// Search 4: Find a common attribute using search() on the superclass type
		List<ObservationElement> observationElements2 = db.search(ObservationElement.class, "viscoelastic");
		Assert.assertEquals(observationElements2.size(), 1);
		System.out.println("Search 4: " + observationElements2.get(0).toString());
		
		// Also test the requerying of entities using load() where subclass attributes are added
		Assert.assertEquals(observationElements2.get(0).get(Metabolite.FORMULA), "");
		List<Metabolite> refinedEntities = (List<Metabolite>) db.load(ObservationElement.class, observationElements2);
		Assert.assertEquals(refinedEntities.get(0).get(Metabolite.FORMULA), "CH2CH(OH)CH3");
		
	}
	
	@BeforeClass
	public void setup() throws Exception
	{
		//cleanup before we start
		XqtlSeleniumTest.deleteDatabase();
		db = DatabaseFactory.create();

		String name = "";
		
		// assert db has no tables
		try
		{
			 name = db.find(Investigation.class).get(0).getName();
					 System.out.println("jaap " + name);
		//	Assert.fail("DatabaseException expected");
		}
		catch (DatabaseException expected)
		{
			System.out.println("jaap " + name);
			// Good: DatabaseException was thrown
			// FIXME: only because this is the first test to be run?
		}
		
		//setup database tables
		String report = ResetXgapDb.reset(db, true);
		Assert.assertTrue(report.endsWith("SUCCESS"));
		StorageHandler sh = new StorageHandler(db);
		Assert.assertFalse(sh.hasFileStorage(false, db));

		// setup file storage
		sh.setFileStorage(storagePath(), db);
		sh.validateFileStorage(db);
		Assert.assertTrue(sh.hasValidFileStorage(db));
		
		Assert.assertEquals(db.find(MolgenisUser.class).size(), 2);
		ClusterDemo.addExampleUsers(db);
		ClusterDemo.giveExtraNeededPermissions(db);
		
		ArrayList<String> result = DataLoader.load(db, false);
		
		Assert.assertEquals(result.get(result.size()-2), "Complete success");
			
		//query the data to find out if it is really there
		Data metab = db.find(Data.class, new QueryRule("name", Operator.EQUALS, "metaboliteexpression")).get(0);
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		DataMatrixInstance instance = dmh.createInstance(metab, db);
		double element = (Double) instance.getSubMatrixByOffset(1, 1, 1, 1).getElement(0, 0);
		Assert.assertEquals(element, 4.0);
		
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanupAfterClass() throws InterruptedException, Exception
	{
		db.close();
		XqtlSeleniumTest.deleteStorage(new UsedMolgenisOptions().appName);
		XqtlSeleniumTest.deleteDatabase();
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
