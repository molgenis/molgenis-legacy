package plugins.cluster.implementations;

import java.util.List;

import plugins.cluster.helper.Command;
import plugins.cluster.interfaces.ComputationResource;

public class BotNetworkComputationResource implements ComputationResource{

	@Override
	public boolean installDependencies() throws Exception {
		return false;
	}

	@Override
	public boolean cleanupJob(int jobIs) throws Exception {
		return false;
	}

	@Override
	public String executeCommand(Command command) throws Exception {
		return null;
	}

	@Override
	public List<String> executeCommands(List<Command> commands)	throws Exception {
		return null;
	}

	@Override
	public void addResultLine(String line) {
		
	}

	@Override
	public void addErrorLine(String line) {
		
	}

	@Override
	public String getResultLine() {
		return null;
	}

	@Override
	public String getErrorLine() {
		return null;
	}

}
