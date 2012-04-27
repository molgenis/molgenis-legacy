package org.molgenis.xgap.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.util.DetectOS;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import regressiontest.cluster.DataLoader;
import app.DatabaseFactory;
import boot.RunStandalone;
import core.Helper;
import filehandling.storage.StorageHandler;

/**
 * Database/webserver stress test
 */
public class Stress_XqtlTestNG_IGNORED
{
	//the two key parameters: number of simultaneous threads,
	//and number of iterations per thread
	private int test_number_of_threads = 100;
	private int test_iterations_per_thread = 100;
	
	//other constants
	private Database db;
	private int webserverport;

	@BeforeClass
	public void setup() throws Exception
	{
		//cleanup before we start
		XqtlSeleniumTest.deleteDatabase();
		db = DatabaseFactory.create();
		
		//setup database tables
		String report = ResetXgapDb.reset(db, true);
		Assert.assertTrue(report.endsWith("SUCCESS"));
		StorageHandler sh = new StorageHandler(db);
		Assert.assertFalse(sh.hasFileStorage(false, db));

		// setup file storage
		sh.setFileStorage(storagePath(), db);
		sh.validateFileStorage(db);
		Assert.assertTrue(sh.hasValidFileStorage(db));
		
		//start webserver
		webserverport = Helper.getAvailablePort(11040, 10);
		new RunStandalone(webserverport, true);
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanupAfterClass() throws InterruptedException, Exception
	{
		db.close();
		XqtlSeleniumTest.deleteDatabase();
	}

	@Test
	public void importExampleData() throws Exception
	{
		//load example data
		ArrayList<String> result = DataLoader.load(db, false);
		Assert.assertTrue(result.get(result.size() - 2).equals("Complete success"));
		checkIfExampleDataIsOK();
		
		//give permissions for anonymous to query markers
		//this saves us the trouble of working with sessions
		MolgenisPermission mp = new MolgenisPermission();
		mp.setEntity_ClassName("org.molgenis.xgap.Marker");
		mp.setRole_Name("anonymous");
		mp.setPermission("read");
		db.add(mp);
	}

	@Test(dependsOnMethods = "importExampleData")
	public void stressTest() throws Exception
	{

		//create X amount of threats with N iterations in each thread
		List<downloadMarkers> tests = new ArrayList<downloadMarkers>();
		for (int i = 0; i < test_number_of_threads; i++)
		{
			tests.add(new downloadMarkers(test_iterations_per_thread, webserverport));
		}
		
		//keep track if one of the threads threw an error (failed assert or IO error)
		final Bool testCompletedSuccessfully = new Bool(true);
		
		//total assert fails
		final Int assertFails = new Int(0);
		
		//total IO errors
		final Int ioErrors = new Int(0);
		
		//start all threads
		List<Thread> threads = new ArrayList<Thread>();
		for (final downloadMarkers t : tests)
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					try
					{
						t.downloadAndAssert();
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
						testCompletedSuccessfully.setVal(false);
						ioErrors.setVal(ioErrors.getVal()+1);
					}
					catch (AssertionError ae)
					{
						ae.printStackTrace();
						testCompletedSuccessfully.setVal(false);
						assertFails.setVal(assertFails.getVal()+1);
					}
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
			threads.add(thread);
		}

		// wait for all threads to complete
		for (Thread thread : threads)
		{
			thread.join();
		}
		
		System.out.println("** TOTAL ASSERT FAILS: " + assertFails.getVal() + " **");
		System.out.println("** TOTAL IO ERRORS: " + ioErrors.getVal() + " **");
		
		//check if one (or more) threads failed
		Assert.assertTrue(testCompletedSuccessfully.getVal());
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

class downloadMarkers
{
	int webserverport;
	int repeats;
	
	public downloadMarkers(int repeats, int webserverport)
	{
		this.webserverport = webserverport;
		this.repeats = repeats;
	}
	
	private ArrayList<String> getUrl(String path) throws IOException
	{
		ArrayList<String> res = new ArrayList<String>();
		URL xqtl = new URL("http://localhost:"+webserverport+path);
		URLConnection xc = xqtl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(xc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		{
			res.add(inputLine);
		}
		in.close();
		return res;
	}
	
	public void downloadAndAssert() throws AssertionError, IOException
	{
		for(int i = 0; i < repeats; i ++)
		{
			String req = "/xqtl/api/find/Marker";
			ArrayList<String> res = getUrl(req);
			Assert.assertEquals(res.size(), 118);
		}
	}
}

class Bool
{
	private boolean val;
	public Bool(boolean val){ this.val = val; }
	public boolean getVal(){ return val; }
	public void setVal(boolean val){ this.val = val; }
}

class Int
{
	private int val;
	public Int(int val){ this.val = val; }
	public int getVal(){ return val; }
	public void setVal(int val){ this.val = val; }
}