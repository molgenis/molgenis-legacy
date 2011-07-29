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
		
		//load test settings from properties file outside svn???
	}

	@Test
	public void testSubmit() throws IOException
	{
		Pbs pbs = new Pbs(host,user,password);
		
		//submit script
		String script = "#!/bin/bash\n" +
				"#PBS -N myjob\n" +
				"#PBS -l nodes=1:ppn=2\n" +
				"#PBS -l walltime=02:00:00\n\n" +
				"sleep 30";
		
		PbsJob job = pbs.submitScript(script);
		
		logger.debug(job);
		
		//monitor until it is done

		while(  job.getState() != Pbs.State.COMPLETED && job.getState() != Pbs.State.ERROR)
		{
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pbs.refresh(job);
			logger.debug("current status: "+job);
		}
	}
	
//	@Test
//	public void testSubmitMultiple() throws IOException
//	{
//		Pbs pbs = new Pbs(host,user,password);
//		
//		//submit script
//		String script = "#!/bin/bash\n" +
//				"#PBS -N myjob\n" +
//				"#PBS -l nodes=1:ppn=2\n" +
//				"#PBS -l walltime=02:00:00\n\n" +
//				"sleep 30";
//		
//		PbsJob job = pbs.submitScript(script);
//		
//		PbsJob job2 = pbs.submitScript(script);
//		
//		logger.debug(job);
//		
//		//monitor until it is done
//
//		while(pbs.hasActiveJobs() )
//		{
//			logger.debug("jobs running: ");
//			for(PbsJob j: pbs.getJobs() )
//			{
//				logger.debug(j);
//			}
//			
//			try
//			{
//				Thread.sleep(5000);
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
