package org.molgenis.compute.host;

import java.io.IOException;

import org.molgenis.compute.host.AbstractComputeHost.JobState;

/**
 * Wrapper for the job parameters. Use 'refresh' to retrieve latest parameters
 * (if the job is still running).
 */
public class Job
{
	private JobState state = JobState.SUBMITTED;
	
	/** Technical id, given by the host */
	private String id;
	
	/** User readible id, given by our system*/
	private String name;

	/** the script to be executed. This is always .sh*/
	private String script;
	
	/** Queue used, if applicable */
	private String queue;
	
	/** physical machine this job is running on*/
	private String exec_host;
	private String qtime;
	
	/** log of the std.out, given when job is completed */
	private String output_log;
	
	/** log of std.error, retrieved when job is completed */
	private String error_log;
	
	/** host where this job is executed */
	private String host;
	
	//optional parameters
	private String walltime;
	private String mem;
	private String nodes;
	
	//technical parameter, where the logs are stored
	private String output_path;
	private String error_path;

	public String toString()
	{
		return String
				.format(
						"Job(\n\tid=%s, \n\tname=%s, \n\tstate=%s, \n\tqueue=%s,\n\texec_host=%s, \n\tqtime=%s, \n\twalltime=%s, \n\tmem=%s, \n\tnodes=%s, \n\terror_path=%s, \n\toutput_path=%s, \n\terror_log=%s \n\toutput_log=%s\n)",
						id, name, state, queue, exec_host, qtime, walltime,
						mem, nodes, error_path, output_path, error_log, output_log);
	}

	/**
	 * Parses the output of qstat -f $id
	 * 
	 * @param statusString
	 * @throws IOException
	 */
	public Job(String script) throws IOException
	{
		this.script = script;
	}

	public Job()
	{
		// TODO Auto-generated constructor stub
	}

	public JobState getState()
	{
		return state;
	}

	public void setState(JobState state)
	{
		this.state = state;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		if (this.getOutput_path() == null) this.setOutput_path(name + ".out");
		if (this.getError_path() == null) this.setError_path(name + ".err");
	}

	public String getQueue()
	{
		return queue;
	}

	public void setQueue(String queue)
	{
		this.queue = queue;
	}

	public String getExec_host()
	{
		return exec_host;
	}

	public void setExec_host(String execHost)
	{
		exec_host = execHost;
	}

	public String getQtime()
	{
		return qtime;
	}

	public void setQtime(String qtime)
	{
		this.qtime = qtime;
	}

	public String getScript()
	{
		return script;
	}

	public void setScript(String script)
	{
		this.script = script;
	}

	public String getWalltime()
	{
		return walltime;
	}

	public void setWalltime(String walltime)
	{
		this.walltime = walltime;
	}

	public String getOutput_path()
	{
		return output_path;
	}

	public void setOutput_path(String outputPath)
	{
		output_path = outputPath;
	}

	public String getOutput_log()
	{
		return output_log;
	}

	public void setOutput_log(String outputLog)
	{
		output_log = outputLog;
	}

	public String getError_path()
	{
		return error_path;
	}

	public void setError_path(String errorPath)
	{
		error_path = errorPath;
	}

	public String getError_log()
	{
		return error_log;
	}

	public void setError_log(String errorLog)
	{
		error_log = errorLog;
	}

	public String getMem()
	{
		return mem;
	}

	public void setMem(String mem)
	{
		this.mem = mem;
	}

	public String getNodes()
	{
		return nodes;
	}

	public void setNodes(String nodes)
	{
		this.nodes = nodes;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}
}