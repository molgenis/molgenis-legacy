package org.molgenis.util.test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.util.Pbs;
import org.molgenis.util.PbsJob;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PbsTest
{
	String host = "millipede.service.rug.nl";
	String user = "BLAAT";
	String password = "BLAAT";
	
	Logger logger = Logger.getLogger(PbsTest.class);

	@BeforeClass
	public void setup()
	{
		BasicConfigurator.configure();

		// load test settings from properties file outside svn???
	}

//	@Test
//	public void testSubmit() throws IOException
//	{
//		Pbs pbs = new Pbs(host, user, password);
//		
//		PbsJob job = new PbsJob("sleep 120");
//		
//		//of course you can use defaults ;-)
//		job.setQueue("short");
//		job.setName("myjob");
//		job.setNodes("1:ppn=2");
//		job.setMem("2gb");
//		job.setWalltime("00:01:00");
//
//		pbs.submit(job);
//
//		logger.debug(job);
//
//		// monitor until it is done
//
//		while (job.getState() != Pbs.State.COMPLETED
//				&& job.getState() != Pbs.State.ERROR)
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
//			pbs.refresh(job);
//			logger.debug("current status: \n" + job);
//		}
//	}
	
//	@Test
//	public void testLogMonitoring() throws IOException
//	{
//		Pbs pbs = new Pbs(host, user, password);
//		
//		PbsJob job = new PbsJob("sleep 5\necho 20%\nsleep 5\necho 40%\nsleep 5\necho 60%\nsleep 5\necho 60%\nsleep 5\necho 100%\n");
//		
//		//of course you can use defaults ;-)
//		job.setQueue("short");
//		job.setName("myjob_"+UUID.randomUUID().toString().replace("-", ""));
//		job.setNodes("1:ppn=2");
//		job.setMem("2gb");
//		job.setWalltime("00:10:00");
//
//		pbs.submit(job);
//
//		logger.debug(job);
//
//		// monitor until it is done
//
//		while (job.getState() != Pbs.State.COMPLETED
//				&& job.getState() != Pbs.State.ERROR)
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
//			job.refresh(pbs);
//			logger.debug("current state: "+job);
//		}
//		job.refreshLogs(pbs);
//		logger.debug("done. log: \n" + job.getOutput_log());
//	}
	
	@Test
	public void testAllJobsMonitoring() throws IOException
	{
		Pbs pbs = new Pbs(host, user, password);

		for(PbsJob job: pbs.getQstat())
		{
			logger.debug(job);
		}

	}

//	@Test
//	public void testSubmitMultiple() throws IOException, InterruptedException
//	{
//		Pbs pbs = new Pbs(host, user, password);
//
//		long time = System.currentTimeMillis();
//		
//		// submit script
//		for (int i = 0; i < 5; i++)
//		{
//			PbsJob job = new PbsJob("sleep 120");
//			
//			//of course you can use defaults ;-)
//			job.setQueue("short");
//			job.setName("myjob"+i+"_"+time);
//			job.setNodes("1:ppn=2");
//			job.setMem("2gb");
//			job.setWalltime("00:01:00");
//			
//			pbs.submit(job);
//			
//			Thread.sleep(1000);
//
//			logger.debug(job);
//		}
//
//		// monitor until it is done
//
//		while (pbs.hasActiveJobs())
//		{
//			logger.debug("jobs running: ");
//			for (PbsJob j : pbs.getJobs())
//			{
//				logger.debug(j);
//			}
//
//			try
//			{
//				Thread.sleep(30000);
//			}
//			catch (InterruptedException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			pbs.refreshJobs();
//		}
//	}
}
