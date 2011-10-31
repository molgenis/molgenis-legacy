package org.molgenis.compute.queue;

import java.util.List;

import org.molgenis.compute.ComputeApplication;

/**
 * Manage one compute resource. For example, PBS. This manager uses information
 * stored in the database under ComputeResource and ComputeCredentials.
 */
public interface ComputeBackend
{

	
	/** Refresh the status of this list of compute applications */
	public void refresh(List<ComputeApplication> running);

}
