package org.molgenis.download;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DownloadExample implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		
		
		try
		{
			JobDataMap job_data = context.getJobDetail().getJobDataMap();
			
			String path         = job_data.getString("__path");

			FileWriter fstream  = new FileWriter(path);
	        BufferedWriter out  = new BufferedWriter(fstream);
	        out.write("Hello Java");
	        //Close the output stream
	        out.close();
		}
		catch (IOException e)
		{
			throw new JobExecutionException(e);
		}

	}

}
