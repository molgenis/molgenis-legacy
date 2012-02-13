package plugins.cluster.implementations;

import java.util.List;

import org.molgenis.cluster.Job;
import org.molgenis.cluster.SelectedData;
import org.molgenis.cluster.SelectedParameter;
import org.molgenis.cluster.Subjob;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

import plugins.cluster.interfaces.JobManager;

/**
 * Database implementation of JobManager
 * @author joerivandervelde
 *
 */
public class DatabaseJobManager implements JobManager
{

	@Override
	public void deleteJob(int jobID, Database db) throws Exception
	{

		Job j = db.find(Job.class, new QueryRule("id", Operator.EQUALS, jobID)).get(0);
		List<SelectedParameter> sp = db.find(SelectedParameter.class, new QueryRule("job", Operator.EQUALS, jobID));
		List<SelectedData> sd = db.find(SelectedData.class, new QueryRule("job", Operator.EQUALS, jobID));
		List<Subjob> sj = db.find(Subjob.class, new QueryRule("job", Operator.EQUALS, jobID));

		db.remove(sp);
		db.remove(sd);
		db.remove(sj);
		db.remove(j);

	}

	@Override
	public plugins.cluster.interfaces.Job createJob() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
