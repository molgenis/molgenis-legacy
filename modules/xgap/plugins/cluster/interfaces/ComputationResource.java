package plugins.cluster.interfaces;

import java.util.List;

import plugins.cluster.helper.Command;

/**
 * Interface describing the behaviour of a 'computation resource'
 * @author joerivandervelde
 *
 */
public interface ComputationResource
{

	public boolean installDependencies() throws Exception;
	
	public boolean cleanupJob(int jobIs) throws Exception;
	
	public String executeCommand(Command command) throws Exception;
	
	public List<String> executeCommands(List<Command> commands) throws Exception;
	
	public void addResultLine(String line);
	public void addErrorLine(String line);
	
	public String getResultLine();
	public String getErrorLine();
	
}
