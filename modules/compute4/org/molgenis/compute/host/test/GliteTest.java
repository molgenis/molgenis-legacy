package org.molgenis.compute.host.test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.compute.host.Glite;
import org.molgenis.compute.host.Job;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GliteTest
{
	String host = "ui.grid.sara.nl";
	String user = "xxxx";
	String password = "xxxx";
	String dir = "morris20120714";
	
	Logger logger = Logger.getLogger(GliteTest.class);

	@BeforeClass
	public void setup()
	{
		BasicConfigurator.configure();

		// load test settings from properties file outside svn???
	}
	
//	@Test
//	public void testLogMonitoring() throws IOException
//	{
//		JobManager scheduler = new GliteSsh(host, user, password);
//		
//		//just simple shell
//		Job job = new Job("sleep 5\necho 20%\nsleep 5\necho 40%\nsleep 5\necho 60%\nsleep 5\necho 60%\nsleep 5\necho 100%\n");
//		
//		//of course you can use defaults ;-)
//		job.setQueue("short");
//		job.setName("myjob_"+UUID.randomUUID().toString().replace("-", ""));
//		//job.setNodes("1:ppn=2");
//		//job.setMem("2gb");
//		//job.setWalltime("00:10:00");
//
//		scheduler.submit(job);
//
//		logger.debug(job);
//
//		// monitor until it is done
//		while (job.getState() != AbstractJobManager.JobState.COMPLETED
//				&& job.getState() != AbstractJobManager.JobState.ERROR)
//		{
//			try
//			{
//				Thread.sleep(5000);
//			}
//			catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			scheduler.refresh(job);
//			logger.debug("current state: "+job);
//		}
//		scheduler.refresh(job);
//		
//		logger.debug("done. log: \n" + job);
//	}
//	
//	@Test
//	public void testAllJobsMonitoring() throws IOException
//	{
//		Pbs pbs = new Pbs(host, user, password);
//
//		for(PbsJob job: pbs.getQstat())
//		{
//			logger.debug(job);
//		}
//
//	}

	@Test
	public void testSubmitMultiple() throws IOException, InterruptedException
	{
		Glite glite = new Glite(host, user, password);
		glite.setWorkingDir("morris20120714");

		long time = System.currentTimeMillis();
		
		// submit script
		for (int i = 0; i < 5; i++)
		{
			Job job = new Job("sleep 120\necho \"hello\"\necho \"some error\" >2");
			
			//of course you can use defaults ;-)
			job.setQueue("short");
			job.setName("myjob"+i+"_"+time);
			job.setNodes("1:ppn=2");
			job.setMem("2gb");
			job.setWalltime("00:05:00");
			
			glite.submit(job);
			
			Thread.sleep(1000);

			logger.debug(job);
		}

		// monitor until it is done

		while (glite.hasActiveJobs())
		{
			logger.debug("jobs running: ");
			for (Job j : glite.getJobs())
			{
				glite.refresh(j);
				logger.debug("\n"+j);
			}

			try
			{
				Thread.sleep(30000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			glite.refreshJobs();
		}
		
		//one last view
		for (Job j : glite.getJobs())
		{
			logger.debug("\n"+j);
		}
	}
}
