package org.molgenis.compute.host;

import java.io.IOException;
import java.util.UUID;

import org.molgenis.util.SimpleTuple;
import org.molgenis.util.SshResult;

/** Facade to use Glite over ssh */
public class Glite extends AbstractComputeHost implements ComputeHost
{
	public Glite(String host, String user, String password) throws IOException
	{
		super(host, user, password, 22);
		this.close();
	}

	@Override
	public void submit(Job job) throws IOException
	{
		this.connect();

		// if no name set, create one
		if (job.getName() == null) job.setName(UUID.randomUUID().toString().replace("-", ""));

		// create prefix path
		String path = getWorkingDir() + ("".equals(this.getWorkingDir()) ? "" : "/") + job.getName();

		// create standard jdl
//		String jdl = String.format("Type=\"Job\";" + "\nJobType=\"Normal\";" + "\n" + "\nExecutable = \"/bin/sh\";"
//                + "\nVirtualOrganisation = \"lsgrid\";"
//				+ "\nArguments = \"%1$s.sh\";" + "\n" + "\nStdError = \"%1$s.err\";" + "StdOutput = \"%1$s.out\";"
//				+ "\n" + "InputSandbox = {\"$HOME/%2$s.sh\"};" + "\nOutputSandbox = {\"%1$s.err\", \"%1$s.out\"};"
//                + "\nMyProxyServer = \"px.grid.sara.nl\";",
//				job.getName(), path);

        String jdl = String.format("# General\n" +
                "Type = \"Job\";\n" +
                "VirtualOrganisation = \"bbmri.nl\";\n" +
                "DefaultNodeShallowRetryCount = 5;\n" +
                "\n" +
                "# Executables, input and output\n" +
                "Executable = \"/bin/sh\";\n" +
                "Arguments = \"%1$s.sh\";\n" +
                "StdOutput = \"%1$s.out\";\n" +
                "StdError = \"%1$s.err\";\n" +
                "InputSandbox = {\"$HOME/%2$s.sh\"};\n" +
                "OutputSandbox = {\"%1$s.err\",\"%1$s.out\"};\n" +
                "MyProxyServer = \"px.grid.sara.nl\";\n" +
                "RetryCount = 0;",
                job.getName(), path);


		// copy .sh and .jdl
		String filename = job.getName() + ".sh";
		logger.debug("uploading script as file: " + filename);
        String script = job.getScript();
        script = script.replaceAll("\r","");
		this.uploadStringToFile(script, filename, this.getWorkingDir());

		filename = job.getName() + ".jdl";
		logger.debug("uploading jdl as file: " + filename);
		this.uploadStringToFile(jdl, filename, this.getWorkingDir());

		// start the scrip
		String command = String.format("glite-wms-job-submit  -d $USER -o %1$s $HOME/%2$s.jdl", job.getName(), path);

		// cd to working directory (otherwise stuff is in wrong dir)
		if (!"".equals(getWorkingDir())) command = "cd " + getWorkingDir() + " && " + command;

		SshResult result = this.executeCommand(command);

		if (!"".equals(result.getStdErr()))
		{
			throw new IOException(result.getStdErr());
		}

		// get the id
		for (String line : result.getStdOut().split("\n"))
		{
			if (line.startsWith("https")) job.setId(line);
		}

		// set the paths, incl working dir
		job.setError_path(path + ".err");
		job.setOutput_path(path + ".out");

		logger.debug("job sumitted: " + job);

		// remember job
		this.jobs.put(job.getId(), job);

		this.close();
	}

    public void submitPilot(Job job) throws IOException
    {
		// if no name set, create one
		if (job.getName() == null) job.setName(UUID.randomUUID().toString().replace("-", ""));

		// create prefix path
		String path = getWorkingDir() + ("".equals(this.getWorkingDir()) ? "" : "/") + job.getName();

		// start the scrip
		String command = String.format(
				"glite-wms-job-submit  -d $USER -o %1$s $HOME/maverick/maverick.jdl", job.getName(), path);

		//cd to working directory (otherwise stuff is in wrong dir)
		if(!"".equals(getWorkingDir())) command = "cd "+getWorkingDir()+" && "+command;

		SshResult result = this.executeCommand(command);

		if (!"".equals(result.getStdErr()))
		{
			throw new IOException(result.getStdErr());
		}

		// get the id
		for (String line : result.getStdOut().split("\n"))
		{
			if (line.startsWith("https")) job.setId(line);
		}

		//set the paths, incl working dir
//		job.setError_path( path +".err");
//		job.setOutput_path( path + ".out");
		logger.debug("job sumitted: " + job);

		// remember job
		this.jobs.put(job.getId(), job);
    }

    @Override
	public void remove(Job job) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Job job) throws IOException
	{
		if (job.getState() == JobState.COMPLETED) return;
		try
		{
			this.connect();

			// cd to working directory
			String command = "glite-wms-job-status " + job.getId();
			if (!"".equals(getWorkingDir())) command = "cd " + getWorkingDir() + " && " + command;

			// retrieve the state
			SshResult gliteOutput = executeCommand(command);

			this.parse(job, gliteOutput.getStdOut());

			// if complete, retrieve logs
			if (job.getState() == JobState.COMPLETED)
			{
				command = "glite-wms-job-output --dir $HOME/" + getWorkingDir() + " --nosubdir --noint " + job.getId();
				if (!"".equals(getWorkingDir())) command = "cd " + getWorkingDir() + " && " + command;
				SshResult result = executeCommand(command);

				logger.debug(result.getStdOut() + "\n" + result.getStdOut());

				// if(!"".equals(result.getStdErr())) throw new
				// Exception(result.getStdErr());

				job.setError_log(this.downloadFile(job.getError_path()));
				job.setOutput_log(this.downloadFile(job.getOutput_path()));
			}

			this.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void parse(Job job, String log)
	{

		SimpleTuple parse = new SimpleTuple();
		if (log == null || "".equals(log)) return;

		logger.info("parsing log: " + log);
		String[] keyValuePairs = log.split("\n");

		for (String keyValue : keyValuePairs)
		{
			keyValue = keyValue.trim();
			String[] split = null;
			if (keyValue.contains(":")) split = keyValue.split(":");
			else
				split = keyValue.split("=");
			String key = null, value = null;
			if (split.length > 1)
			{
				key = split[0].trim();
				value = split[1].trim();
				parse.set(key, value);
			}
		}

		// translate status
		job.setExec_host(parse.getString("Destination"));
		String status = parse.getString("Current Status");
		if (status != null)
		{
			if (status.startsWith("Submitted")) job.setState(JobState.SUBMITTED);
			else if (status.startsWith("Running")) job.setState(JobState.RUNNING);
			else if (status.startsWith("Scheduled")) job.setState(JobState.QUEUED);
			else if (status.startsWith("Done")) job.setState(JobState.COMPLETED);
			else if (status.startsWith("Aborted")) job.setState(JobState.ERROR);
			else if (status.startsWith("Cancelled")) job.setState(JobState.CANCELLED);
		}
		// else we ignore.

		// Status:
		// SUBMITTED The job has been submitted by the user but not yet
		// processed by the Network Server
		// WAITING The job has been accepted by the Network Server but not yet
		// processed by the Workload Manager
		// READY The job has been assigned to a Computing Element but not yet
		// transferred to it
		// SCHEDULED The job is waiting in the Computing Element's queue
		// RUNNING The job is running
		// DONE The job has finished
		// ABORTED The job has been aborted by the WMS (e.g. because it was too
		// long, or the proxy certificated expired, etc.)
		// CANCELLED The job has been cancelled by the user
		// CLEARED The Output Sandbox has been transferred to the User Interface

		// When you have multiple jobs with same id we will parse last one
		// ======================= glite-wms-job-status Success
		// =====================
		// BOOKKEEPING INFORMATION:
		//
		// Status info for the Job :
		// https://wms4.grid.sara.nl:9000/DoUwj8kcREp95KTx5VSwcQ
		// Current Status: Running
		// Status Reason: unavailable
		// Destination: phoebe.htc.biggrid.nl:8443/cream-pbs-short
		// Submitted: Sat Jul 14 21:18:29 2012 CEST
		// ==========================================================================

	}
}
