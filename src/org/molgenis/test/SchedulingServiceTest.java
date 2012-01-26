package org.molgenis.test;

import java.util.Date;
import java.util.HashMap;

import org.molgenis.services.SchedulingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SchedulingServiceTest implements Job
{
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException
	{
		System.out.println("Job: Here something very time intensive starts: " + new Date());
		try
		{
			Thread.sleep(10000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Job: Here something very time intensive ends: " + new Date());
	}

	public static void main(String args[])
	{
		SchedulingService hs = null;

		try
		{
			System.out.println("Main: Starting scheduler: " + new Date());

			hs = new SchedulingService();
			hs.schedule(new HashMap<Object, Object>(), SchedulingServiceTest.class);

			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Main: Something in between: " + new Date());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				System.out.println("Main: Stopping scheduler: " + new Date());
				if (hs != null)
					hs.shutdown(); // waits for running job
				System.out.println("Main: Stopped scheduler: " + new Date());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
