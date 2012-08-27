package org.molgenis.compute.host;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.util.Ssh;
import org.testng.log4testng.Logger;

public abstract class AbstractComputeHost extends Ssh implements ComputeHost
{
	/**
	 * Various states of jobs. The available job states may differ between
	 * backends.
	 */
	public static enum JobState
	{
		/** The job is submitted, but we don't know if it succeeded */
		SUBMITTED("SUBMITTED"),
		/** The job has been submitted and we can see it in the queue */
		QUEUED("Q"),
		/** The job is running */
		RUNNING("R"),
		/** The job completed but we didn't retrieve its logs yet */
		COMPLETED("C"),
		/** PBS specific state of 'held' */
		HELD("H"),
		/** There is something wrong with this job */
		ERROR("ERROR"), 
		/** Unclear what these states mean*/
		EXITED("E"), TRANSFERED("T"), WAITING("W"), SUSPEND("S"),
		/** The user cancelled this job */
		CANCELLED("Cancelled"), 
		/** Job is completed, but we still need to get the logs (these are sometimes lagging behind)*/
		WAITING_FOR_LOGS("Get logs");

		private String text;

		JobState(String text)
		{
			this.text = text;
		}

		public String getText()
		{
			return this.text;
		}

		public static JobState fromString(String text)
		{
			if (text != null)
			{
				for (JobState b : JobState.values())
				{
					if (text.equalsIgnoreCase(b.text))
					{
						return b;
					}
				}
			}
			return SUBMITTED;
		}
	};

	Logger logger = Logger.getLogger(this.getClass());

	// evil, protected map
	Map<String, Job> jobs = new LinkedHashMap<String, Job>();
	
	// including '/'
	private String workingDir = "";

	public AbstractComputeHost(String host, String user, String password, int port) throws IOException
	{
		super(host, user, password, port);
	}

	@Override
	public Job submitScript(String script) throws IOException
	{
		Job job = new Job(script);
		this.submit(job);
		return job;
	}

	@Override
	public Collection<Job> getJobs()
	{
		return this.jobs.values();
	}

	@Override
	public boolean hasActiveJobs()
	{
		for (Job job : jobs.values())
		{
			if (!job.getState().equals(JobState.COMPLETED)) return true;
		}
		return false;
	}

	@Override
	public void refreshJobs() throws IOException
	{
		logger.debug("start refresh jobs");

		for (Job job : this.jobs.values())
		{
			this.refresh(job);
		}

		logger.debug("refresh jobs done");
	}

	@Override
	public String getWorkingDir()
	{
		return workingDir;
	}

	@Override
	public void setWorkingDir(String workingDir)
	{
		this.workingDir = workingDir;
	}
}
