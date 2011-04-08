package servlets.xgap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.DatabaseException;

import plugins.emptydb.emptyDatabase;
import regressiontest.cluster.DataLoader;
import app.JDBCDatabase;

public class resetdatabase_loadexampledata_4EE1D7A3E73C504183B69F7D20108853 extends app.servlet.MolgenisServlet
{

	private static final long serialVersionUID = -6004840016845633449L;

	// private static Logger logger =
	// Logger.getLogger(LoadDatamodelServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		boolean databaseIsAvailable = false;
		boolean resetSuccess = false;
		JDBCDatabase db = null;

		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try
		{
			db = (JDBCDatabase) this.getDatabase();
			databaseIsAvailable = true;
		}
		catch (Exception e)
		{
			out.print("Database unavailable.");
			out.print("\n\n");
			e.printStackTrace(out);
		}

		if (databaseIsAvailable)
		{
			try
			{
				out.print("First deleting all database-associated files if applicable.\n");
				try
				{
					List<MolgenisFile> mfList = db.find(MolgenisFile.class);
					out.print("Number of files: " + mfList.size() + "\n");
					db.remove(mfList);
					mfList = db.find(MolgenisFile.class);
					out.print("Number of after delete: " + mfList.size() + "\n");
				}
				catch (DatabaseException dbe)
				{
					// database scheme does not contain MolgenisFile.class
					// ignore this and continue to load SQL
				}
				out.print("Now resetting datamodel/database.\n");
				new emptyDatabase((JDBCDatabase) db, true);
				out.print("Reset datamodel success. Continuing to load example data.\n");
				resetSuccess = true;
			}
			catch (Exception e)
			{
				out.print("Error while trying to overwrite datamodel.");
				out.print("\n\n");
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
