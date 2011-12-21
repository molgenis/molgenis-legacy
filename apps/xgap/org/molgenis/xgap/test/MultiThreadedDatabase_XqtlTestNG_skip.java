package org.molgenis.xgap.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Individual;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.servlet.MolgenisServlet;

public class MultiThreadedDatabase_XqtlTestNG_skip
{
	int amountInBatch = 100; //amount of Individuals per add/remove test
	int amountOfTests = 100; //amount of test threads running simultaneously

	MolgenisServlet molgServ;

	@BeforeClass
	public void cleanDb() throws FileNotFoundException, SQLException, IOException, Exception{
		new Molgenis("org/molgenis/xgap/xqtlworkbench/xqtl.properties").updateDb(false);
	}
	
	@Test
	public void multiThreadAddRemove() throws Exception
	{
		
		molgServ = new MolgenisServlet();

		final Bool testCompletedSuccessfully = new Bool(true);

		List<TestDbThread> tests = new ArrayList<TestDbThread>();
		for (int i = 0; i < amountOfTests; i++)
		{
			tests.add(new TestDbThread(molgServ, amountInBatch));
		}

		List<Thread> threads = new ArrayList<Thread>();
		for (final TestDbThread t : tests)
		{
			Runnable runnable = new Runnable()
			{

				public void run()
				{
					try
					{
						t.add();
						t.remove();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						testCompletedSuccessfully.setVal(false);
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
		
		Assert.assertTrue(testCompletedSuccessfully.getVal());

	}
}

class Bool
{
	boolean val;
	
	public Bool(boolean val)
	{
		this.val = val;
	}

	public boolean getVal()
	{
		return val;
	}

	public void setVal(boolean val)
	{
		this.val = val;
	}
	
	
}

class TestDbThread
{
	Database db;
	List<Individual> individuals;
	int amount = 1000;

	public TestDbThread(MolgenisServlet molgServ, int amount) throws Exception
	{
		//this is the behaviour of e.g. handleUpload in AbstrMolgenisServlet
		this.db = molgServ.getDatabase();
		individuals = new ArrayList<Individual>();
		this.amount = amount;
		for (int i = 0; i < amount; i++)
		{
			Individual ind = new Individual();
			ind.setName("indv" + UUID.randomUUID().toString().replace("-", ""));
			individuals.add(ind);
		}
	}

	public void add() throws DatabaseException
	{
		db.add(individuals);
	}

	public void remove() throws DatabaseException
	{
		db.remove(individuals);
	}
}