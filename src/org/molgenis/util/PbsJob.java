package org.molgenis.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.molgenis.util.Pbs.State;

/**
 * Wrapper for the job parameters. Use 'refresh' to retrieve latest parameters
 * (if the job is still running).
 */
public class PbsJob
{
	private State state = State.SUBMITTED;
	private String id;
	private String name;
	private String queue;
	private String exec_host;
	private String qtime;
	private String script;
	private String walltime = "00:30:00";
	private String output_path;
	private String output_log;
	private String error_path;
	private String error_log;
	private String mem = "2gb";
	private String nodes = "1:ppn=1";
	private String owner;

	private Logger logger = Logger.getLogger(PbsJob.class);

	public String toString()
	{
		return String
				.format(
						"Job(id=%s, name=%s, state=%s, queue=%s, exec_host=%s, qtime=%s, walltime=%s, mem=%s, nodes=%s, owner=%s)",
						id, name, state, queue, exec_host, qtime, walltime,
						mem, nodes, owner);
	}

	/**
	 * Parses the output of qstat -f $id
	 * 
	 * @param statusString
	 * @throws IOException
	 */
	public PbsJob(String script) throws IOException
	{
		this.script = script;
	}

	public PbsJob()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a PbsJob based on a QSTAT map
	 * @param parse
	 */
	public PbsJob(Tuple parse)
	{
		this.setId(parse.getString("Job Id"));
		this.setName(parse.getString("Job_Name"));
		this.setState(State.fromString(parse.getString("job_state")));
		this.setQueue(parse.getString("queue"));
		this.setOwner(parse.getString("Job_Owner"));
		this.setError_path(parse.getString("Error_Path"));
		this.setOutput_path(parse.getString("Output_Path"));
		this.setNodes(parse.getString("Resource_List.nodes"));
		this.setWalltime(parse.getString("Resource_List.walltime"));
		this.setExec_host(parse.getString("exec_host"));
		
//		Job Id: 1910636.millipede.cm.cluster
//	    Job_Name = wikiassoc-en
//	    Job_Owner = s1254871@login01.cm.cluster
//	    job_state = Q
//	    queue = short
//	    server = millipede.cm.cluster
//	    Checkpoint = u
//	    ctime = Mon Jul  4 19:08:33 2011
//	    Error_Path = login01.cm.cluster:/home/s1254871/wikiassoc-en.e1910636
//	    Hold_Types = n
//	    Join_Path = n
//	    Keep_Files = n
//	    Mail_Points = abe
//	    Mail_Users = larsmans@gmail.com
//	    mtime = Mon Jul  4 19:08:33 2011
//	    Output_Path = login01.cm.cluster:/home/s1254871/wikiassoc-en.o1910636
//	    Priority = 0
//	    qtime = Mon Jul  4 19:08:33 2011
//	    Rerunable = True
//	    Resource_List.nodect = 1
//	    Resource_List.nodes = 1:ppn=24
//	    Resource_List.walltime = 00:10:00
//	    etime = Mon Jul  4 19:08:33 2011
//	    submit_args = skl-old.job
//	    fault_tolerant = False
//	    submit_host = login01.cm.cluster
//	    init_work_dir = /home/s1254871

	}

	public void refresh(Pbs pbs) throws IOException
	{
		// retrieve the state
		SshResult pbsOutput = pbs.executeCommand("qstat -f -1 " + id);

		if (pbsOutput.getStdErr() != null
				&& !pbsOutput.getStdErr().trim().equals(""))
		{
			if (pbsOutput.getStdErr().contains("Unknown Job Id"))
			{
				this.state = State.COMPLETED;
				refreshLogs(pbs);
				return;
			}
			throw new IOException(pbsOutput.getStdErr());
		}

		// parse the result
		this.refresh(pbsOutput.getStdOut());
	}

	public void refreshLogs(Pbs pbs) throws IOException
	{
		if (this.state.equals(Pbs.State.RUNNING)
				|| this.state.equals(Pbs.State.COMPLETED))
		{
			try
			{
				// retrieve the output log
				this.setOutput_log(pbs.downloadFile(this.getOutput_path()));

				// retrieve the error log
				this.setError_log(pbs.downloadFile(this.getError_path()));
			}
			catch (IOException e)
			{
				;// no file is not unexpected.
			}
		}
	}

	protected void refresh(String statusString)
	{
		// default state to error
		this.state = State.ERROR;

		// parse the string
		String[] result = statusString.split("\n");
		for (String res : result)
		{
			if (res.contains("job_state"))
			{
				String job_state = res.split("=")[1].trim();
				this.state = State.fromString(job_state);
			}

			if (res.contains("exec_host"))
			{
				exec_host = res.split("=")[1].trim();
			}

			if (res.contains("qtime"))
			{
				qtime = res.split("=")[1].trim();
			}

		}
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
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

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

}