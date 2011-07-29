package org.molgenis.util;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Pbs extends Ssh
{
	private Map<String,PbsJob> jobs = new LinkedHashMap<String,PbsJob>();
	
	public static enum State
	{
		SUBMITTED, QUEUED, RUNNING, COMPLETED, HELD, ERROR
	};

	/**
	 * Construct a PBS connector over ssh using host, user, password, and port
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param port
	 * @throws IOException
	 */
	public Pbs(String host, String user, String password, int port)
			throws IOException
	{
		super(host, user, password, port);
	}

	public Pbs(String host, String user, String password) throws IOException
	{
		super(host, user, password);
	}

	/**
	 * Submit the script; returns submission identifier.
	 * 
	 * @param script
	 * @return submission identifier
	 * @throws IOException
	 */
	public PbsJob submitScript(String script) throws IOException
	{
		// random filename
		String filename = UUID.randomUUID().toString().replace("-", ".")
				+ ".sh";
		logger.debug("uploading script as file: " + filename);

		// copy the script to scheduler
		this.uploadStringToFile(script, filename);

		// start the script
		SshResult result = this.executeCommand("qsub " + filename);

		//check for errors in submission
		if (result.getStdErr() != null && !result.getStdErr().trim().equals("")) throw new IOException(
				result.getStdErr());

		//return the id (e.g. for dependencies)
		String id = result.getStdOut();
		this.jobs.put(id, new PbsJob(id));
		return this.jobs.get(id);
	}
	
	public void refresh(PbsJob job) throws IOException
	{
		job.refresh(this);
	}

	public Collection<PbsJob> getJobs()
	{
		return this.jobs.values();
	}

	public boolean hasActiveJobs()
	{
		for(PbsJob job: jobs.values())
		{
			if(!job.getState().equals(State.COMPLETED)) return true;
		}
		return false;
	}

	/** Refresh all jobs in batch 
	 * @throws IOException */
	public void refreshJobs() throws IOException
	{
		
		
		//get all ids (do we need to filter missing??)
		String ids = "";
		for(String id: this.jobs.keySet()) ids +=" "+id;
		
		// retrieve the state
		SshResult pbsOutput = executeCommand("qstat -f " + ids);

		// parse the result
		String[] result = pbsOutput.getStdOut().split("\n\n");
		
		//create map of reports
		Map<String,String> idReportMap = new LinkedHashMap<String,String>();
		for(String res: result)
		{
			
		}
		
		throw new UnsupportedOperationException("not completely implemented");
		
		
	}
}
