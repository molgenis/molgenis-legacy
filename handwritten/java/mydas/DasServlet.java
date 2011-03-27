package mydas;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class DasServlet extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(DasServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		try
		{
			response.setContentType("application/x-das-features+xml");
			
			//response.setContentType("text/plain");
			
			//possible:
			//application/x-das-sources+xml
			//application/x-das-segments+xml
			//application/x-das-types+xml
			//application/x-das-features+xml
			
			
			printExample(out);
				
			//ie.
			//out.write("Hello World");
			
			//SEE:
			//http://www.biodas.org/documents/das2/das2_get.html
				
			
			logger.info("serving " + request.getRequestURI());
		} catch (Exception e)
		{
			out.println("An error has occurred. Did you use the servlet correctly?");
			out.println("Usage example: http://localhost:8080/xgap_1_3_distro/das?argument=value");
			out.println();
			e.printStackTrace(out);
		} finally
		{
			out.close();
		}
	}
	
	
	
	private void printExample(PrintWriter out){
		String example = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
		+ "<FEATURES xmlns=\"http://biodas.org/documents/das2\"" + "\n"
		+ "          xml:base=\"http://www.example.org/volvox/1/\">" + "\n"
		+ " <FEATURE uri=\"feature/cTel54X\" type=\"type/gene\" title=\"tg-3\">" + "\n"
		+ "   <LOC segment=\"segment/Chr2\" range=\"1200:2917:1\" />" + "\n"
		+ " </FEATURE>" + "\n"

		+ " <FEATURE uri=\"feature/hit12\"" + "\n"
		+ "          type=\"type/est-alignment\"" + "\n"
		+ "          created=\"2001-12-15T22:43:36\"" + "\n"
		+ "          modified=\"2004-09-26T21:10:15\" >" + "\n"

		+ "   <LOC segment=\"segment/Chr3\" range=\"1201:1400:1\" />" + "\n"
		+ "   <PART uri=\"feature/hit12.hsp1\" />" + "\n"
		+ "   <PART uri=\"feature/hit12.hsp2\" />" + "\n"
		+ "   <PROP key=\"omescore\" value=\"180\" />" + "\n"
		+ " </FEATURE>" + "\n"
		+ "</FEATURES>";
		out.write(example);
	}

}

/**
 * in de web.xml: <servlet> <servlet-name>ExampleServlet</servlet-name>
 * <servlet-class>plugins.servlets.ExampleServlet</servlet-class> </servlet>
 * <servlet-mapping> <servlet-name>ExampleServlet</servlet-name>
 * <url-pattern>/exampleservlet</url-pattern> </servlet-mapping>
 */
