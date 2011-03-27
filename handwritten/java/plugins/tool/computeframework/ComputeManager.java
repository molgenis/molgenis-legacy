package plugins.tool.computeframework;

import java.util.List;

/**
 * The ComputeService enables the queuing and executing of ComputeJobs in an execution environment.
 *
 * The compute service is a facade on top of Java standard Thread, Runnable and Executor components.
 * This makes it easy to add different implementations of this standard such as GridGain.
 */
public interface ComputeManager
{
	/**
	 * List Jobs currently active<br>
	 *
	 * @return
	 */
	public List<ComputeJob> list();

	/** Gets the process by processing id as assigned by the manager. */
	public ComputeJob getJob(String id);

	/**
	 * adds a new process to be managed by this process manager. Will update the
	 * id of this process.
	 * @throws ComputeException
	 * @return the session id for this compute job
	 */
	public String setJob(ComputeJob p) throws ComputeException;

	/** remove a process from the manager. */
	public void remove(ComputeJob p);

	/** Stop the process manager, persisting any in memoery data etc */
	public void shutdown();

	/** Start the manager */
	public void start();
}
