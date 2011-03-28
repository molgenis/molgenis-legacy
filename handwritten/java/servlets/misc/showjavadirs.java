package servlets.misc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class showjavadirs extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	
	//TODO: Danny: unused, but i guess we do want to use it
	//private static Logger logger = Logger.getLogger(showjavadirs.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		PrintWriter out = response.getWriter();

		try
		{
			out.println("java.io.tmpdir = "+System.getProperty("java.io.tmpdir"));
			out.println("user.dir = " + System.getProperty("user.dir"));
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}
}

