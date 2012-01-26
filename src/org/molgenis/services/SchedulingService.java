package org.molgenis.services;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulingService
{
	private Scheduler scheduler;
	private SimpleTrigger simpleTrigger;

	public SchedulingService() throws SchedulerException
	{
		SchedulerFactory sf = new StdSchedulerFactory();
		this.scheduler      = sf.getScheduler();
		this.scheduler.start();
		
		this.simpleTrigger  = new SimpleTrigger("simpletrigger", Scheduler.DEFAULT_GROUP, new Date(), null, 0, 0L);
	}

	public void schedule(HashMap<Object, Object> jobData, Class<?> klazz) throws SchedulerException
	{
		JobDetail jobDetail = new JobDetail();
		jobDetail.setName("molgenis_" + UUID.randomUUID().toString());
		jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
		jobDetail.setJobClass(klazz);
		jobDetail.setJobDataMap(new JobDataMap(jobData));

		this.scheduler.scheduleJob(jobDetail, this.simpleTrigger);
	}

	public void shutdown() throws SchedulerException, InterruptedException
	{
		boolean waitForJobsToComplete = true ;
		this.scheduler.shutdown(waitForJobsToComplete);
		Thread.sleep(1000);
	}
}
