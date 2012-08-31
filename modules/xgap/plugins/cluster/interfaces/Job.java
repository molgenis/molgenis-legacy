package plugins.cluster.interfaces;

import java.util.List;

/**
 * Not currently used in this form
 * @author joerivandervelde
 *
 */
public interface Job
{
	
	enum states{
		ERROR, SUBMITTED, QUEUED, RUNNING, COMPLETED
	}
	
	//double percentageDone;
	
	public boolean start();
	
	public boolean stop();
	
	public boolean kill();
	
	public boolean hold();
	
	public void dependsOnCompletionOf(List<Job> jobs);
	
	public void consistsOf(Job job);

}
