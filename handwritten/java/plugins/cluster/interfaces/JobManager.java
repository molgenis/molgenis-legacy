package plugins.cluster.interfaces;

/**
 * Not currently used in this form
 * @author joerivandervelde
 *
 */
public interface JobManager
{

	public Job createJob() throws Exception;
	
	public void deleteJob(int jobId) throws Exception;
	

	
}
