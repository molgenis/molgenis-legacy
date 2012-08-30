package plugins.cluster.interfaces;

import org.molgenis.framework.db.Database;

/**
 * Not currently used in this form
 * @author joerivandervelde
 *
 */
public interface JobManager
{

	public Job createJob() throws Exception;
	
	public void deleteJob(int jobId, Database db) throws Exception;
	

	
}
