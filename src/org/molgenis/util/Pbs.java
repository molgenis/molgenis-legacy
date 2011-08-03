package org.molgenis.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pbs extends Ssh
{
	private Map<String, PbsJob> jobs = new LinkedHashMap<String, PbsJob>();

	public static enum State
	{
		SUBMITTED("SUBMITTED"), QUEUED("Q"), RUNNING("R"), COMPLETED("C"), HELD(
				"H"), ERROR("ERROR"), EXITED("E"), TRANSFERED("T"), WAITING("W"), SUSPEND(
				"S");

		private String text;

		State(String text)
		{
			this.text = text;
		}

		public String getText()
		{
			return this.text;
		}

		public static State fromString(String text)
		{
			if (text != null)
			{
				for (State b : State.values())
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
		PbsJob job = new PbsJob(script);
		this.submit(job);
		return job;
	}

	public void submit(PbsJob job) throws IOException
	{
		// create the PBS headers
		String script = "#!/bin/bash\n";

		// if no name set, create one
		if (job.getName() == null) job.setName(UUID.randomUUID().toString()
				.replace("-", ""));
		script += "#PBS -N " + job.getName() + "\n";

		if (job.getNodes() != null) script += "#PBS -l nodes=" + job.getNodes()
				+ "\n";
		if (job.getQueue() != null) script += "#PBS -q " + job.getQueue()
				+ "\n";
		if (job.getWalltime() != null) script += "#PBS -l walltime="
				+ job.getWalltime() + "\n";

		if (job.getOutput_path() != null) script += "#PBS -o "
				+ job.getOutput_path() + "\n";
		if (job.getError_path() != null) script += "#PBS -e "
				+ job.getError_path() + "\n";

		script += "\n\n";
		script += job.getScript();

		logger.debug("submitting script:\n" + script);

		// random filename
		String filename = UUID.randomUUID().toString().replace("-", ".")
				+ ".sh";
		logger.debug("uploading script as file: " + filename);

		// copy the script to scheduler
		this.uploadStringToFile(script, filename);

		// start the script
		SshResult result = this.executeCommand("qsub " + filename);

		// check for errors in submission
		if (result.getStdErr() != null && !result.getStdErr().trim().equals("")) throw new IOException(
				result.getStdErr());

		// return the id (e.g. for dependencies)
		String id = result.getStdOut().trim();
		job.setId(id);
		this.jobs.put(id, job);
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
		for (PbsJob job : jobs.values())
		{
			if (!job.getState().equals(State.COMPLETED)) return true;
		}
		return false;
	}

	/**
	 * Refresh all jobs in batch
	 * 
	 * @throws IOException
	 */
	public void refreshJobs() throws IOException
	{
		logger.debug("start refresh jobs");

		// //get all ids (do we need to filter missing??)
		// String ids = "";
		// for(String id: this.jobs.keySet()) ids +=" "+id;
		//
		// // retrieve the state
		// SshResult pbsOutput = executeCommand("qstat -f " + ids);
		//
		// // parse the result
		// String[] result = pbsOutput.getStdOut().split("\n\n");
		//
		// //create map of reports
		// Map<String,String> idReportMap = new LinkedHashMap<String,String>();
		// for(String res: result)
		// {
		//
		// }

		for (PbsJob j : this.jobs.values())
		{
			j.refresh(this);
		}

		logger.debug("refresh jobs done");

	}

	public List<PbsJob> getQstat() throws IOException
	{
		// retrieve the state
		SshResult pbsOutput = executeCommand("qstat -f -1 ");

		// split the log in string per job
		String[] allLogs = pbsOutput.getStdOut().split("\n\n");

		List<PbsJob> result = new ArrayList<PbsJob>();
		// set all the properties of the job
		for (String log : allLogs)
		{
			result.add(new PbsJob(this.parse(log)));
		}

		return result;

	}

	private Tuple parse(String log)
	{
		SimpleTuple t = new SimpleTuple();
		String[] keyValuePairs = log.split("\n");

		for (String keyValue : keyValuePairs)
		{
			keyValue = keyValue.trim();
			String[] split = null;
			if (keyValue.contains("=")) split = keyValue.split("=");
			else
				split = keyValue.split(":");
			String key = split[0].trim();
			String value = split[1].trim();
			t.set(key, value);
		}
		return t;
	}
}
