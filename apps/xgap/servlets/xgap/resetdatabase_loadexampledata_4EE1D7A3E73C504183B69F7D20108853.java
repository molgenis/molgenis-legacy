package servlets.xgap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import regressiontest.cluster.DataLoader;

public class resetdatabase_loadexampledata_4EE1D7A3E73C504183B69F7D20108853 extends app.servlet.MolgenisServlet
{

	private static final long serialVersionUID = -6004840016845633449L;

	// private static Logger logger =
	// Logger.getLogger(LoadDatamodelServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		boolean resetSuccess = false;
		Database db = null;
		boolean dbAvailable = false;

		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try
		{
			db = this.getDatabase();
			dbAvailable = true;
		}
		catch (Exception e1)
		{
			e1.printStackTrace(out);
		}

		if (dbAvailable)
		{
			try
			{
				String report = ResetXgapDb.reset(db, true);
				out.print(report);
				resetSuccess = true;
			}
			catch (Exception e)
			{
				e.printStackTrace(out);
			}
		}

		if (resetSuccess)
		{
			ArrayList<String> result = DataLoader.load(db, false);
			for (String s : result)
			{
				out.println(s);
			}
		}

		out.close();
	}
}
