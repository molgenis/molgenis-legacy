package plugins.tool.computeframework;

//** A ComputeObserver can observe ComputeJob instances and is notified when ComputeState changes (e.g., COMPLETES) */
public interface ComputeObserver
{
	/**
	 * The observer will be notified by the job, at least at each ComputeState
	 * change
	 */
	public void notify(ComputeJob job);
}
