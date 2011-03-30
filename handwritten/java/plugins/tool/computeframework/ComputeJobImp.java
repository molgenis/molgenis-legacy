package plugins.tool.computeframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ComputeJobImp implements ComputeJob {

	private static final long serialVersionUID = 2178787788340086891L;
	private String commandLine;
	private ResultData data;
	private String id;
	private List<String> inputFileNames = new ArrayList<String>();
	private List<String> outputFileNames = new ArrayList<String>();
	private ComputeState state = ComputeState.INITIALIZED;
	
	@Override
	public String getCommandLine() {
		// TODO Auto-generated method stub
		return commandLine;
	}

	@Override
	public ResultData getData() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public String getExecutionEnvironmentID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getInputFileName(int i) {
		// TODO Auto-generated method stub
		return this.inputFileNames.get(i);
	}

	@Override
	public List<String> getInputFiles() {
		// TODO Auto-generated method stub
		return inputFileNames;
	}

	@Override
	public int getNumberOfInputFiles() {
		return this.inputFileNames.size();
	}

	@Override
	public List<String> getOutputFileNames() {
		// TODO Auto-generated method stub
		return this.outputFileNames;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComputeState getState() {
		// TODO Auto-generated method stub
		return state;
	}

	@Override
	public void registerObserver(ComputeObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommandLine(String commandline) {
		this.commandLine = commandline;
		
	}

	@Override
	public void setData(ResultData data) {
		this.data = data;
		
	}

	@Override
	public void setId(String id) {
		this.id = id;
		
	}

	@Override
	public void setState(ComputeState state) {
		this.state = state;
	}

}
