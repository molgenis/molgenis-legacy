package servlets.xgap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.cluster.Job;
import org.molgenis.cluster.Subjob;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.cluster.helper.Command;
import plugins.cluster.implementations.LocalComputationResource;
import plugins.cluster.interfaces.ComputationResource;
import app.JDBCDatabase;
import filehandling.generic.MolgenisFileHandler;

/**
 * TaskReporter servlet receives status updates from subjobs and reports them to
 * the database. In addition, it activates remaining subjobs in some cases.
 * 
 * @author joerivandervelde
 * 
 */
public class taskreporter extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(taskreporter.class);

	/**
	 * Get a resource from the jar and copy it the the response.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		// OutputStream out = response.getOutputStream();
		PrintWriter out = response.getWriter();

		try
		{
			Tuple req = new HttpServletRequestTuple(request);

			int jobID = req.getInt("job"); // unique

			int subjobNr = req.getInt("subjob"); // 1..N

			int statusCode = req.getInt("statuscode"); // code

			String statusText = req.getString("statustext"); // text

			JDBCDatabase db = (JDBCDatabase) this.getDatabase();

			QueryRule jobQuery0 = new QueryRule("id", Operator.EQUALS, jobID);
			QueryRule subjobQuery0 = new QueryRule("job", Operator.EQUALS, jobID);
			QueryRule subjobQuery1 = new QueryRule("nr", Operator.EQUALS, subjobNr);

			out.println("job queryrule: " + subjobQuery0.toString());
			out.println("subjob queryrule: " + subjobQuery1.toString());

			out.println("db.find: " + db.find(Subjob.class, subjobQuery0, subjobQuery1).toString());

			Job job = db.find(Job.class, jobQuery0).get(0);
			Subjob subjob = db.find(Subjob.class, subjobQuery0, subjobQuery1).get(0);

			subjob.setStatusCode(statusCode);
			subjob.setStatusText(statusText);

			try
			{
				int statusProgress = req.getInt("statusprogress");
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
				MolgenisFileHandler xf = new MolgenisFileHandler(db);
				ComputationResource cr = new LocalComputationResource(xf);
				cr.executeCommands(cList);
			}

			db.close();

			logger.info("serving " + request.getRequestURI());
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
