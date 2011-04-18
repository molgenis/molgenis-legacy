import generic.Utils;

import java.io.IOException;
import java.util.HashMap;

import app.servlet.MolgenisServlet;

public class WWWServer extends Webserver implements Runnable
{
	private static final long serialVersionUID = 1L;

	public WWWServer() throws IOException
	{
		Utils.console("Starting server");
		Webserver.PathTreeDictionary aliases = new Webserver.PathTreeDictionary();

		// Application name, which MUST be equal to the deployed URL for the app
		// ie. variantID 'animaldb' deployed at 'localhost:8080/animaldb/'
		String var = MolgenisServlet.getMolgenisVariantID();

		// Filesystem aliases
		aliases.put("/cgi-bin", new java.io.File("WebContent/cgi-bin"));
		aliases.put("/", new java.io.File("WebContent"));
		setMappingTable(aliases);

		// Serving all servlets in handwritten/java/servlets
		HashMap<String, String> autoMapping = new GetServlets().getServletLocations();
		for (String key : autoMapping.keySet())	{
			addServlet(var + "/" + key, autoMapping.get(key));
		}

		// Serving molgenis, API's, CGI, static files, tmp files
		addServlet(var + "/molgenis.do", "app.servlet.MolgenisServlet");
		addServlet(var + "/api/R", "RApiServlet");
		addServlet(var + "/api/find/", "app.servlet.MolgenisServlet");
		addServlet(var + "/api", "app.servlet.MolgenisServlet");
		addServlet(var + "/xref", "app.servlet.MolgenisServlet");
		addServlet(var + "/cgi-bin", "servlets.CGIServlet");
		addServlet(var + "/tmpfile/", "servlets.tmpfileservlet");
		addServlet(var + "/", "servlets.FileServlet");
	}

	public void run()
	{
		try
		{
			serve();
			return;
		}
		catch (Exception e)
		{
			log("ERROR [http server] ", e);
			e.printStackTrace();
			return;
		}
	}

}
