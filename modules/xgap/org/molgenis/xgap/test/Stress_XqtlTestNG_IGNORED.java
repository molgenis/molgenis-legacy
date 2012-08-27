package org.molgenis.xgap.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.apache.commons.dbcp.BasicDataSource;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.DetectOS;
import org.molgenis.util.TupleWriter;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import regressiontest.cluster.DataLoader;
import app.DatabaseFactory;
import boot.Helper;
import boot.RunStandalone;
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
		new RunStandalone(webserverport);
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

	/**
	 * Webserver and database stress test
	 */
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
	 * Database-only stress test
	 */
	@Test(dependsOnMethods = "importExampleData")
	public void stressDatabaseTest() throws Exception
	{
		DataSource ds = createDataSource();

		//create X amount of threats with N iterations in each thread
		List<queryMarkers> tests = new ArrayList<queryMarkers>();
		for (int i = 0; i < test_number_of_threads; i++)
		{
			tests.add(new queryMarkers(test_iterations_per_thread, ds));
		}
		
		//keep track if one of the threads threw an error (failed assert or IO error)
		final Bool testCompletedSuccessfully = new Bool(true);
		
		//total assert fails
		final Int assertFails = new Int(0);
		
		//total db errors
		final Int dbErrors = new Int(0);
		
		//total sql errors
		final Int sqlErrors = new Int(0);
		
		
		//start all threads
		List<Thread> threads = new ArrayList<Thread>();
		for (final queryMarkers t : tests)
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					try
					{
						t.queryAndAssert();
					}
					catch (DatabaseException dbe)
					{
						dbe.printStackTrace();
						testCompletedSuccessfully.setVal(false);
						dbErrors.setVal(dbErrors.getVal()+1);
					}
					catch (SQLException se)
					{
						se.printStackTrace();
						testCompletedSuccessfully.setVal(false);
						sqlErrors.setVal(sqlErrors.getVal()+1);
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
		System.out.println("** TOTAL DB ERRORS: " + dbErrors.getVal() + " **");
		System.out.println("** TOTAL SQL ERRORS: " + sqlErrors.getVal() + " **");
		
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
	
	//manually create a connection pool for the database-only stress test
	//NOTE: we also start a RunStandalone server with FrontController,
	// which also creates 1 datasource.. problem?
	private DataSource createDataSource() {
		//NOTE: these are custom settings for high performance
		BasicDataSource data_src = new BasicDataSource();
		data_src.setDriverClassName("org.hsqldb.jdbcDriver");
		data_src.setUsername("sa");
		data_src.setPassword("");
		data_src.setUrl("jdbc:hsqldb:file:hsqldb/molgenisdb;shutdown=true"); // a path within the src folder?
		data_src.setMaxIdle(10);
		data_src.setMaxWait(1000);
		data_src.setMaxActive(-1);
		return (DataSource) data_src;	
	}

}

class queryMarkers
{
	int iterations;
	DataSource ds;
	
	public queryMarkers(int iterations, DataSource ds)
	{
		this.iterations = iterations;
		this.ds = ds;
	}
	
	public void queryAndAssert() throws SQLException, DatabaseException, AssertionError
	{
		//get a fresh database connection for each batch of queries
		Database db = getDatabase();
		for(int i = 0; i < iterations; i ++)
		{	
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TupleWriter writer = new CsvWriter(out);
			db.find(Marker.class, writer, new QueryRule[]{});
			int lines = countLines(new ByteArrayInputStream(out.toByteArray()));
			Assert.assertEquals(lines, 118); //117 markers + 1 for col.header
		}
		db.close(); //not needed
	}
	
	private int countLines(InputStream is)
	{
		String content = convertStreamToString(is);
		String[] lines = content.split("\r\n|\r|\n");
		return lines.length;
	}
	
	public String convertStreamToString(java.io.InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
	private Database getDatabase() throws SQLException, DatabaseException
	{
		Connection conn = ds.getConnection();
		Database db = DatabaseFactory.create(conn);
		return db;
	}
}

class downloadMarkers
{
	int webserverport;
	int iterations;
	
	public downloadMarkers(int iterations, int webserverport)
	{
		this.webserverport = webserverport;
		this.iterations = iterations;
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
		for(int i = 0; i < iterations; i ++)
		{
			String req = "/xqtl/api/find/Marker";
			ArrayList<String> res = getUrl(req);
			Assert.assertEquals(res.size(), 118); //117 markers + 1 for col.header
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