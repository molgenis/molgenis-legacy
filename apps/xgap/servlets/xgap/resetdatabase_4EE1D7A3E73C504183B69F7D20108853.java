package servlets.xgap;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

public class resetdatabase_4EE1D7A3E73C504183B69F7D20108853 extends app.servlet.MolgenisServlet
{

	private static final long serialVersionUID = -6004248764368336249L;

	// private static Logger logger =
	// Logger.getLogger(LoadDatamodelServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try
		{
			String report = ResetXgapDb.reset(this.getDatabase(), true);
			out.print(report);
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}

		out.close();
	}
}
