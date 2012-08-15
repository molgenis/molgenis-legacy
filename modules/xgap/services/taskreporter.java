package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.molgenis.cluster.Job;
import org.molgenis.cluster.Subjob;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.cluster.helper.Command;
import plugins.cluster.implementations.LocalComputationResource;
import plugins.cluster.interfaces.ComputationResource;

/**
 * TaskReporter servlet receives status updates from subjobs and reports them to
 * the database. In addition, it activates remaining subjobs in some cases.
 * 
 * @author joerivandervelde
 * 
 */
public class taskreporter  implements MolgenisService
{
	
	private MolgenisContext mc;
	
	public taskreporter(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	private static Logger logger = Logger.getLogger(taskreporter.class);

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		// OutputStream out = response.getOutputStream();
		PrintWriter out = response.getResponse().getWriter();

		try
		{

			int jobID = request.getInt("job"); // unique

			int subjobNr = request.getInt("subjob"); // 1..N

			int statusCode = request.getInt("statuscode"); // code

			String statusText = request.getString("statustext"); // text

			Database db = request.getDatabase();
			

			QueryRule jobQuery0 = new QueryRule(Job.ID, Operator.EQUALS, jobID);
			
			QueryRule subjobQuery1 = new QueryRule("nr", Operator.EQUALS, subjobNr);

//			out.println("job queryrule: " + subjobQuery0.toString());
//			out.println("subjob queryrule: " + subjobQuery1.toString());
//
//			out.println("db.find: " + db.find(Subjob.class, subjobQuery0, subjobQuery1).toString());

			Job job = db.find(Job.class, jobQuery0).get(0);
			
			QueryRule subjobQuery0 = new QueryRule(Subjob.JOB, Operator.EQUALS, job.getId());
			
			Subjob subjob = db.find(Subjob.class, subjobQuery0, subjobQuery1).get(0);

			subjob.setStatusCode(statusCode);
			subjob.setStatusText(statusText);

			try
			{
				int statusProgress = request.getInt("statusprogress");
				subjob.setStatusProgress(statusProgress);
			}
			catch (Exception e)
			{
				// no statusprogress entered, thats ok
			}

			db.update(subjob);

			// if local preparation job, and success, start the remaining
			// subjobs
			if (job.getComputeResource().equals("local") && subjobNr == 0 && statusCode == 3)
			{
				int numSubJobs = db.find(Subjob.class, subjobQuery0).size();
				ArrayList<Command> cList = new ArrayList<Command>();
				for (int x = 1; x < numSubJobs; x++)
				{
					cList.add(new Command("R CMD BATCH ./run" + jobID + "/subjob" + x + ".R", false, false, true));
				}
				ComputationResource cr = new LocalComputationResource();
				cr.executeCommands(cList);
			}

		//	db.close();

			logger.info("serving " + request.getRequest().getRequestURI());
		}
		catch (Exception e)
		{
			displayUsage(out);
			out.write("\n\n");
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}

	public void displayUsage(PrintWriter out)
	{
		String usage = "Usage:" + "\n\n" + "[insert example:\n" + "\n\n";
		out.write(usage);
	}
}
