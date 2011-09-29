package tmp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Individual;
import org.testng.Assert;
import org.testng.annotations.Test;

import app.servlet.MolgenisServlet;

public class MultiThreadedDatabaseTest
{
	int amountInBatch = 100; //amount of Individuals per add/remove test
	int amountOfTests = 100; //amount of test threads running simultaneously



	@Test
	public void test() throws Exception
	{

		boolean testCompletedSuccessfully = false;

		List<TestDbThread> tests = new ArrayList<TestDbThread>();
		for (int i = 0; i < amountOfTests; i++)
		{
			tests.add(new TestDbThread(amountInBatch));
			System.out.println("Added new thread " + (i+1) + "/" + amountOfTests);
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
						System.exit(-1);
					}
				}
			};
			// executor.execute(runnable);
			Thread thread = new Thread(runnable);
			thread.start();
			System.out.println("Started thread");
			threads.add(thread);
		}

		// wait for all threads to complete
		for (Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException ignore)
			{
			}
		}

		testCompletedSuccessfully = true;

		Assert.assertTrue(testCompletedSuccessfully);

	}
}

class TestDbThread
{
	Database db;
	List<Individual> individuals;
	int amount = 1000;

	public TestDbThread(int amount) throws Exception
	{
		db = new MolgenisServlet().getDatabase();
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
		System.out.println("Added " + amount + " individuals, starting with '" + individuals.get(0) + "'");
	}

	public void remove() throws DatabaseException
	{
		db.remove(individuals);
		System.out.println("Removed " + amount + " individuals, starting with '" + individuals.get(0) + "'");
	}
}