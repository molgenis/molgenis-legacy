package org.molgenis.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.molgenis.util.Pbs.State;

/**
 * Wrapper for the job status
 */
public class PbsJob
{
	private String statusString;
	private State state = State.SUBMITTED;
	private String id;
	private String name;
	private String queue;
	private String exec_host;
	private String qtime;

	private Logger logger = Logger.getLogger(PbsJob.class);

	/**
	 * Parses the output of qstat -f $id
	 * 
	 * @param statusString
	 * @throws IOException
	 */
	protected PbsJob(String id) throws IOException
	{
		this.id = id;
	}
	
	protected void refresh(Pbs pbs) throws IOException
	{
		// retrieve the state
		SshResult pbsOutput = pbs.executeCommand("qstat -f " + id);

		if (pbsOutput.getStdErr() != null
				&& !pbsOutput.getStdErr().trim().equals(""))
		{
			if(pbsOutput.getStdErr().contains("Unknown Job Id"))
			{
				this.state = State.COMPLETED;
				return;
			}
			throw new IOException(
				pbsOutput.getStdErr());
		}

		// parse the result
		this.refresh(pbsOutput.getStdOut());
	}
	
	protected void refresh(String statusString)
	{
		this.statusString = statusString;
		//default state to error
		this.state = State.ERROR;
		
		// parse the string
		String[] result = statusString.split("\n");
		for (String res : result)
		{
			if (res.contains("job_state"))
			{
				String job_state = res.split("=")[1].trim();
				if ("Q".equals(job_state)) this.state = State.QUEUED;
				if ("H".equals(job_state)) this.state = State.HELD;
				if ("C".equals(job_state)) this.state = State.COMPLETED;
				if ("R".equals(job_state)) this.state = State.RUNNING;
			}
			
			if(res.contains("queue"))
			{
				this.queue = res.split("=")[1].trim();
			}
			
			if(res.contains("exec_host"))
			{
				exec_host = res.split("=")[1].trim();
			}
			
			if(res.contains("qtime"))
			{
				qtime = res.split("=")[1].trim();
			}
			
		}
	}
	

	public String getStatusString()
	{
		return statusString;
	}



	public State getState()
	{
		return state;
	}



	public String getId()
	{
		return id;
	}



	public String getName()
	{
		return name;
	}

	public String getQueue()
	{
		return queue;
	}

	public String getExec_host()
	{
		return exec_host;
	}

	public String toString()
	{
		return String.format("Job(id=%s, state=%s, queue=%s, exec_host=%s, qtime=%s)", id, state, queue, exec_host, qtime);
	}

}