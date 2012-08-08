package org.molgenis.compute.host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.util.SimpleTuple;
import org.molgenis.util.SshResult;
import org.testng.log4testng.Logger;

public class Pbs extends AbstractComputeHost implements ComputeHost
{
	private Logger logger = Logger.getLogger(Pbs.class);

	/**
	 * Construct a PBS connector over ssh using host, user, password, and port
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param port
	 * @throws IOException
	 */
	public Pbs(String host, String user, String password, int port) throws IOException
	{
		super(host, user, password, port);
	}

	public Pbs(String host, String user, String password) throws IOException
	{
		super(host, user, password, 22);
	}

	/** Submit a configure job with manually entered settings; returns the job */
	@Override
	public void submit(Job job) throws IOException
	{
		String path = getWorkingDir() + ("".equals(this.getWorkingDir()) ? "" : "/") + job.getName();
		
		//set error paths relative to job dir
		job.setOutput_path(path +".out");
		job.setError_path(path +".err");
		
		// set defaults
		if (job.getWalltime() == null) job.setWalltime("00:30:00");
		if (job.getMem() == null) job.setMem("2gb");
		if (job.getNodes() == null) job.setNodes("1:ppn=1");

		// create the PBS headers
		String script = "#!/bin/bash\n";

		// if no name set, create one
		if (job.getName() == null) job.setName(UUID.randomUUID().toString().replace("-", ""));
		script += "#PBS -N " + job.getName() + "\n";

		if (job.getNodes() != null) script += "#PBS -l nodes=" + job.getNodes() + "\n";
		if (job.getQueue() != null) script += "#PBS -q " + job.getQueue() + "\n";
		if (job.getWalltime() != null) script += "#PBS -l walltime=" + job.getWalltime() + "\n";

		if (job.getOutput_path() != null) script += "#PBS -o " + job.getOutput_path() + "\n";
		if (job.getError_path() != null) script += "#PBS -e " + job.getError_path() + "\n";

		script += "\n\n";
		script += job.getScript();

		logger.debug("submitting script:\n" + script);

		// random filename
		String filename = job.getName() + ".sh";
		logger.debug("uploading script as file: " + filename);
		this.uploadStringToFile(script, filename, getWorkingDir());

		// start the script
		SshResult result = this.executeCommand("qsub " + path +".sh");

		// check for errors in submission
		if (result.getStdErr() != null && !result.getStdErr().trim().equals("")) throw new IOException(
				result.getStdErr());

		// return the id (e.g. for dependencies)
		String id = result.getStdOut().trim();
		job.setId(id);
		this.jobs.put(id, job);
	}

    public void submitPilot(Job job) throws IOException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
	 * Remove the job remotely and remove from local list of jobs.
	 * 
	 * @param job
	 * @throws IOException
	 */
	@Override
	public void remove(Job job) throws IOException
	{
		// kill on cluster
		this.executeCommand("qdel " + job.getId());

		// remove job from 'jobs'
		this.jobs.remove(job.getName());
	}

	@Override
	public void refresh(Job job) throws IOException
	{
		if (JobState.COMPLETED != job.getState()) try
		{
			// retrieve the state
			SshResult pbsOutput = executeCommand("qstat -f -1 " + job.getId());

			if (pbsOutput.getStdErr() != null && !pbsOutput.getStdErr().trim().equals(""))
			{
				if (pbsOutput.getStdErr().contains("Unknown Job Id"))
				{
					job.setState(JobState.WAITING_FOR_LOGS);

				}
			}
			else
			{
				this.parse(job, pbsOutput.getStdOut());
			}

			// try retrieve logs
			if (JobState.WAITING_FOR_LOGS == job.getState())
			{
				Thread.sleep(500);

				// retrieve the output log, we need to strip host information
				// (i.e. stuff before ":")
				job.setOutput_log(downloadFile(job.getOutput_path()
						.substring(job.getOutput_path().lastIndexOf(":") + 1)));

				Thread.sleep(500);

				// retrieve the error log, we need to strip host information
				// (i.e. stuff before ":")
				job.setError_log(downloadFile(job.getError_path().substring(job.getError_path().lastIndexOf(":") + 1)));

				job.setState(JobState.COMPLETED);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// protected void refresh(String statusString)
	// {
	// // default state to error
	// this.state = JobState.ERROR;
	//
	// // parse the string
	// String[] result = statusString.split("\n");
	// for (String res : result)
	// {
	// if (res.contains("job_state"))
	// {
	// String job_state = res.split("=")[1].trim();
	// this.state = JobState.fromString(job_state);
	// }
	//
	// if (res.contains("exec_host"))
	// {
	// exec_host = res.split("=")[1].trim();
	// }
	//
	// if (res.contains("qtime"))
	// {
	// qtime = res.split("=")[1].trim();
	// }
	//
	// }
	// }

	public List<Job> getQstat() throws IOException
	{
		// retrieve the state
		SshResult pbsOutput = executeCommand("qstat -f -1 ");

		// split the log in string per job
		String[] allLogs = pbsOutput.getStdOut().split("\n\n");

		List<Job> result = new ArrayList<Job>();
		// set all the properties of the job
		for (String log : allLogs)
		{
			Job job = new Job();
			this.parse(job, log);
			result.add(job);
		}

		return result;

	}

	private void parse(Job job, String log)
	{
		SimpleTuple parse = new SimpleTuple();
		if (log == null || "".equals(log)) return;

		logger.debug("parsing log: " + log);
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
			parse.set(key, value);
		}

		job.setId(parse.getString("Job Id"));
		job.setName(parse.getString("Job_Name"));
		job.setState(JobState.fromString(parse.getString("job_state")));
		job.setQueue(parse.getString("queue"));
		// job.setOwner(parse.getString("Job_Owner"));
		job.setError_path(parse.getString("Error_Path"));
		job.setOutput_path(parse.getString("Output_Path"));
		job.setNodes(parse.getString("Resource_List.nodes"));
		job.setWalltime(parse.getString("Resource_List.walltime"));
		job.setExec_host(parse.getString("exec_host"));

		// Job Id: 1910636.millipede.cm.cluster
		// Job_Name = wikiassoc-en
		// Job_Owner = s1254871@login01.cm.cluster
		// job_state = Q
		// queue = short
		// server = millipede.cm.cluster
		// Checkpoint = u
		// ctime = Mon Jul 4 19:08:33 2011
		// Error_Path = login01.cm.cluster:/home/s1254871/wikiassoc-en.e1910636
		// Hold_Types = n
		// Join_Path = n
		// Keep_Files = n
		// Mail_Points = abe
		// Mail_Users = larsmans@gmail.com
		// mtime = Mon Jul 4 19:08:33 2011
		// Output_Path = login01.cm.cluster:/home/s1254871/wikiassoc-en.o1910636
		// Priority = 0
		// qtime = Mon Jul 4 19:08:33 2011
		// Rerunable = True
		// Resource_List.nodect = 1
		// Resource_List.nodes = 1:ppn=24
		// Resource_List.walltime = 00:10:00
		// etime = Mon Jul 4 19:08:33 2011
		// submit_args = skl-old.job
		// fault_tolerant = False
		// submit_host = login01.cm.cluster
		// init_work_dir = /home/s1254871
	}
}
