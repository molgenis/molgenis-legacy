package plugins.tool.computeframework;

public enum ComputeState
{
	/** The compute job is initialized but not yet submitted for computation*/
	INITIALIZED,
	/** The ComputeJob is submitted but not yet queued*/
	SUBMITTED,
	/** The ComputeJob is queued on a calculation resource*/
	QUEUED,
	/** The compute job is running */
	RUNNING,
	/** The compute job has complete without errors */
	COMPLETED,
	/** The compute job has been stopped by the compute manager*/
	STOPPED,
	/** Execution failed*/
	FAILED;
}
