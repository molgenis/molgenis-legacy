package plugins.cluster.helper;

/**
 * 
 * @author joerivandervelde
 * @params command
 * @params waitFor
 * @params expectReturn
 * 
 */
public class Command
{
	/**
	 * Simple constructor with just String. Assumes we want to wait for the
	 * process and it will be executed in the tmp dir.
	 * 
	 * @param command
	 */
	public Command(String command)
	{
		this.command = command;
		this.waitFor = true;
		this.expectReturn = false;
		this.tmpDirExecute = true;
	}

	/**
	 * Advanced constructor with all options.
	 * 
	 * @param command
	 * @param waitFor
	 * @param expectReturn
	 * @param tmpDirExecute
	 */
	public Command(String command, boolean waitFor, boolean expectReturn, boolean tmpDirExecute)
	{
		this.command = command;
		this.waitFor = waitFor;
		this.expectReturn = expectReturn;
		this.tmpDirExecute = true;
	}

	private String command;
	private boolean waitFor;
	private boolean expectReturn;
	private boolean tmpDirExecute;

	public String getCommand()
	{
		return command;
	}

	public boolean isWaitFor()
	{
		return waitFor;
	}

	public boolean isExpectReturn()
	{
		return expectReturn;
	}

	public boolean isTmpDirExecute()
	{
		return tmpDirExecute;
	}


}