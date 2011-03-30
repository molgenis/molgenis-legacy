package plugins.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import plugins.tool.computeframework.ComputeException;
import plugins.tool.computeframework.ComputeJob;
import plugins.tool.computeframework.ComputeManager;

public class SimpleComputeManager implements ComputeManager {
	private Map<String,ComputeJob> allJobs = new LinkedHashMap<String,ComputeJob>();
	private int count = 0;
	@Override
	public ComputeJob getJob(String id) {
		return allJobs.get(id);
	}

	@Override
	public List<ComputeJob> list() {
		return new ArrayList<ComputeJob>(allJobs.values());
	}

	@Override
	public void remove(ComputeJob p) {
		for(String id: allJobs.keySet())
		{
			if(allJobs.get(id) == p)
			{
				allJobs.remove(id);
			}
		}

	}

	@Override
	public String setJob(ComputeJob p) throws ComputeException {
		String id = "job"+count++;
		p.setId(id);
		allJobs.put(id,p);
		return id;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		//loop through list and start jobs that are not yet started
//		for(ComputeJob c: allJobs.values())
//		{
//			
//		}
	}

}
