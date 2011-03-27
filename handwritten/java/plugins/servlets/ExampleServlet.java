package plugins.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class ExampleServlet extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(ExampleServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		PrintWriter out = response.getWriter();

		try
		{
			Tuple req = new HttpServletRequestTuple(request);

			int optie = req.getInt("optie");

			if (optie == 1)
			{
				out.println("optie 1");
			}
			if (optie == 2)
			{
				out.println("optie 2");
			}

			logger.info("serving " + request.getRequestURI());
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

/**
 * in de web.xml:
 * <servlet>
		<servlet-name>ExampleServlet</servlet-name>
		<servlet-class>plugins.servlets.ExampleServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ExampleServlet</servlet-name>
		<url-pattern>/exampleservlet</url-pattern>
	</servlet-mapping>	
*/
