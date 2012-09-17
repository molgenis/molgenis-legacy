package org.molgenis.compute.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.compute.ComputeJob;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;

/**
 * This class is responsible for keepin track of compute applications.
 * <ul>
 * <li>Compute applications with state 'submitted' should be tried to submitted
 * to the desired resource, queue, credentials
 * <li>Compute applications with state 'running' or 'hold' should be monitored
 * <li>Compute applications that have ended should be cleaned out of the compute
 * resource
 * 
 * TODO some compute applications should be submitted in a group. How will we
 * model that?
 * 
 * @See database updater...
 */
public class ComputeApplicationManager
{
	Database db;
	SubmissionStrategy submitter;
	List<ComputeBackend> backends;

	/**
	 * Register a compute backend
	 * 
	 * @param backend
	 */
	public void registerBackend(ComputeBackend backend)
	{
		if (backends == null) backends = new ArrayList<ComputeBackend>();
		if (backend == null) throw new IllegalArgumentException(
				"backend cannot be null");
		backends.add(backend);
	}

	/**
	 * Queue one compute application for execution. Note that it will inspect
	 * dependent jobs and if needed at those to the execution as well.
	 * 
	 * @throws DatabaseException
	 */
	public void add(ComputeJob ca) throws DatabaseException
	{
		ComputeJob[] array = new ComputeJob[]
		{ ca };
		this.add(Arrays.asList(array));
	}

	/**
	 * Mark this compute applications to be run.
	 * 
	 * @throws DatabaseException
	 */
	public void add(List<ComputeJob> tasks) throws DatabaseException
	{
		for (ComputeJob c : tasks)
		{
			c.setStatusCode("queued");
		}
		db.add(tasks);
	}

	/**
	 * This method refreshes the status of all active compute applications. If
	 * complete the results will be retrieved to the database and the
	 * computeApplication archived.
	 * 
	 * @throws DatabaseException
	 */
	public void refreshRunningJobs() throws DatabaseException
	{
		// retrieve all submitted and running jobs
		Query<ComputeJob> q = db.query(ComputeJob.class);
		q.equals(ComputeJob.STATUSCODE, "submitted");
		q.or();
		q.equals(ComputeJob.STATUSCODE, "running");
		List<ComputeJob> running = q.find();

		// get a status update on those running by querying the respective
		// compute resources
		for (ComputeBackend b : backends)
			b.refresh(running);

		// for all completed, retrieve logs etc.
		for (ComputeJob ca : running)
		{
			if (ca.getStatusCode().equals("completed"))
			{
				this.completeApplication(ca);
			}
		}

		// update database accordingly
		db.update(running);
	}

	/**
	 * If a computeApplication is just completed then this method takes care of
	 * retrieving logs ets.
	 */
	private void completeApplication(ComputeJob ca)
	{
		// TODO Auto-generated method stub

	}
}
