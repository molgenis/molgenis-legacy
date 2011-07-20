import generic.Utils;

import java.io.IOException;
import java.util.HashMap;

public class WWWServer extends Webserver implements Runnable
{
	private static final long serialVersionUID = 1L;

	public WWWServer(String variant) throws IOException
	{
		Utils.console("Starting server");
		Webserver.PathTreeDictionary aliases = new Webserver.PathTreeDictionary();

		// Filesystem aliases
		aliases.put("/cgi-bin", new java.io.File("WebContent/cgi-bin"));
		aliases.put("/", new java.io.File("WebContent"));
		setMappingTable(aliases);

		// Serving all servlets in handwritten/java/servlets
		HashMap<String, String> autoMapping = new GetServlets().getServletLocations();
		for (String key : autoMapping.keySet())	{
			addServlet(variant + "/" + key, autoMapping.get(key));
		}

		// Serving molgenis, API's, CGI, static files, tmp files
		addServlet(variant + "/molgenis.do", "app.servlet.MolgenisServlet");
		addServlet(variant + "/api/R", "RApiServlet");
		addServlet(variant + "/api/find/", "app.servlet.MolgenisServlet");
		addServlet(variant + "/api", "app.servlet.MolgenisServlet");
		addServlet(variant + "/xref", "app.servlet.MolgenisServlet");
		addServlet(variant + "/cgi-bin", "servlets.CGIServlet");
		addServlet(variant + "/tmpfile", "servlets.tmpfileservlet");
		addServlet(variant + "/", "servlets.FileServlet");
		addServlet(variant + "/bot", "servlets.BotServlet");
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
