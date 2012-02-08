package org.molgenis.framework.server.services;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.util.TarGz;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

/**
 * A MolgenisService to clean the tmp dir every hour. Files older than 12 hours
 * are attempted to be deleted. The handleRequest of this service should never
 * be called, instead it is just initialized and uses the FrontController
 * scheduler to start the Quartz cleaning job. This job will be triggered every
 * hour.
 * 
 * Does not work for some reason.
 * 
 * @author joerivandervelde
 * 
 */
public class MolgenisCleanTmpDirService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisCleanTmpDirService.class);

	public MolgenisCleanTmpDirService(MolgenisContext mc)
	{
		try
		{
			Scheduler sched = mc.getScheduler();
			JobDetail jobDetail = new JobDetail("MolgenisCleanTmpDirService", sched.DEFAULT_GROUP, CleanTmpDir.class);
		
			Trigger trigger = TriggerUtils.makeHourlyTrigger();
			trigger.setStartTime(new Date());
			trigger.setName("MolgenisCleanTmpDirServiceTrigger");
	
			sched.scheduleJob(jobDetail, trigger);
			
			if(!sched.isStarted())
			{
				throw new SchedulerException("Scheduler is not active");
			}
			
			System.out.println("MolgenisCleanTmpDirService initialized.");
		}
		catch (SchedulerException e)
		{
			System.err.println("FATAL EXCEPTION: failure in starting MolgenisCleanTmpDirService.");
			e.printStackTrace();
			System.exit(0);
		}

	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
		throw new IOException("This service does not accept requests.");
	}
}

class CleanTmpDir implements Job
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		try
		{
			// delete all files in tmpdir older than 12 hours
			System.out.println("MolgenisCleanTmpDirService: executing cleaning job!");
			
			String tmpDirLoc = System.getProperty("java.io.tmpdir");
			File tmpDir = new File(tmpDirLoc);

			long curDate = new Date().getTime();
			long twelveHours = 1000 * 60 * 60 * 12;

			for (File f : tmpDir.listFiles())
			{
				// TODO: directory recursion..
				if (!f.isDirectory())
				{
					long lastMod = f.lastModified();
					long age = lastMod - curDate;

					if (age > twelveHours)
					{
						System.out.println(f.getAbsolutePath() + " is older than 12 hrs, deleting...");
						TarGz.delete(f, false);
					}
					else{
						System.out.println(f.getAbsolutePath() + " is younger than 12 hrs");
					}
				}
			}

		}
		catch (Exception e)
		{
			throw new JobExecutionException(e);
		}
	}
}
