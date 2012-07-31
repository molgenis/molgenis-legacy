package org.molgenis.compute.host;

import java.io.IOException;
import java.util.Collection;

/**
 * Minimal abstraction to submit jobs to cluster and grid schedulers such as Pbs and gLite.
 */
public interface ComputeHost
{
	/** Submit ssh script using the default settings of the job manager
	 * 
	 * @param script
	 * @return
	 * @throws IOException
	 */
	Job submitScript(String script) throws IOException;

	/** Submit ssh script as part of a job where you can specific specific settings
	 * 
	 * @param job
	 * @throws IOException
	 */
	void submit(Job job) throws IOException;

    /** Submit pilot maverick job
    	 *
    	 * @param job
    	 * @throws IOException
    	 */
    void submitPilot(Job job) throws IOException;

	/**
	 * Remove a job. If it is still running it will be stopped.
	 * 
	 * @param job
	 * @throws IOException
	 */
	void remove(Job job) throws IOException;

	/**
	 * Refresh the status information on this job. NB please don't do this too often as it can bring the scheduler down.
	 * 
	 * @param job
	 * @throws IOException
	 */
	void refresh(Job job) throws IOException;
	
	/**
	 * Refresh all jobs
	 * @throws IOException
	 */
	void refreshJobs() throws IOException;

	/**
	 * Get the current collection of jobs in this JobManager
	 * @return
	 */
	Collection<Job> getJobs();

	/**
	 * List the jobs currently active, i.e. not yet completed
	 * @return
	 */
	boolean hasActiveJobs();

	/** If not using the root directory in the host, this will return the working directory where all job *.sh, *.err, *.out live*/
	String getWorkingDir();

	/** Set the working directory where all *.sh, *.err, *.out live*/
	void setWorkingDir(String workingDir);
}
