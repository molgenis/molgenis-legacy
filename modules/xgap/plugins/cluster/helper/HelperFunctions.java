package plugins.cluster.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.molgenis.cluster.Job;
import org.molgenis.cluster.Subjob;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

/**
 * Some helpers for ClusterPlugin
 * @author joerivandervelde
 *
 */
public class HelperFunctions
{
	public static int countMaxSubjobs(List<Subjob> subjobs, List<Job> jobs)
	{
		int maxJobs = 0;
		for (Job j : jobs)
		{
			int tmp = 0;
			for (Subjob sj : subjobs)
			{
				if (sj.getJob_Id().equals(j.getId()))
				{
					tmp++;
				}
			}
			if (tmp > maxJobs)
			{
				maxJobs = tmp;
			}
		}
		return maxJobs;
	}
	
	public static String dateTimeToMysqlFormat(Date date)
	{
		// mysql: 9999-12-31 23:59:59
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static boolean checkIfNameExists(Database db, String putativeName) throws DatabaseException
	{
		if (db.find(Job.class, new QueryRule("outputdataname", Operator.EQUALS, putativeName)).size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
