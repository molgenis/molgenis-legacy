package plugins.tool.computeframework;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Observable;
import java.util.concurrent.Callable;

/**
 * An atomic unit of computation that can be run using the ComputeService backend
 * services.
 */
public interface ComputeJob extends Serializable
{
	/** Set the id*/
	public void setId(String id);

	/** @return the computer id (assigned by the compute manager)*/
	public String getId();

	/** @return the status on this job */
	public ComputeState getState();

	/** Change the state on the job*/
	public void setState(ComputeState state);

	/** Register observer that reacts on state changes (for call back) */
	public void registerObserver(ComputeObserver observer);

	/** Get the commandline associated to this job*/
	public void setCommandLine(String format);


	public Properties getProperties();

	public String getCommandLine();

	public List<String> getInputFiles();

    /** @return the number of input files */    
    public int getNumberOfInputFiles();

    /** @return the input file name */
    public String getInputFileName(int i);

    /** @return the name of the execution outfile*/
	public List<String> getOutputFileNames();

    /** @return the name of the specified execution environment, if exists*/
	public String getExecutionEnvironmentID();


	//please provide a way to read files that are produced by the current thread.
    // maybe at the second iteration
    // still it is hardly possible when running on the cluster due to caching during the execution
	public ResultData getData();

    public void setData(ResultData data);

}
