package core;

import generic.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class WWWServer extends Webserver implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	private static String servletLoc = "../servlets";
	private static String servletLocJAR = "servlets";

	public WWWServer(String variant) throws IOException
	{
		Utils.console("Starting server");
		Webserver.PathTreeDictionary aliases = new Webserver.PathTreeDictionary();

		// Filesystem aliases
		aliases.put("/cgi-bin", new java.io.File("WebContent/cgi-bin"));
		aliases.put("/", new java.io.File("WebContent"));
		setMappingTable(aliases);
		GetServlets getServlets = new GetServlets();
		// Serving all servlets in handwritten/java/servlets

		System.out.println("GetServlets starting");
		
		HashMap<String, String> autoMapping = new HashMap<String, String>();
		URL servetlocation = getClass().getResource(servletLoc);
		
		if(servetlocation != null){
			System.out.println("getting servlets from location " + servletLoc);
			autoMapping = getServlets.getMapping(servletLoc, false); // 27-7 bugfix ER, please check!
		}else{
			System.out.println("servetlocation == null, getting servlets from location " + servletLocJAR);
			autoMapping = getServlets.getMapping(servletLocJAR, true); // 27-7 bugfix ER, please check!
		}
		
		for (String key : autoMapping.keySet())	{
			addServlet(variant + "/" + key, autoMapping.get(key));
		}
		
		// Serving molgenis, API's, CGI, static files, tmp files
		addServlet(variant + "/molgenis.do", "app.servlet.MolgenisServlet");
		addServlet(variant + "/api/R", "RApiServlet");
		addServlet(variant + "/api/find/", "app.servlet.MolgenisServlet");
		addServlet(variant + "/api", "app.servlet.MolgenisServlet");
		addServlet(variant + "/xref", "app.servlet.MolgenisServlet");
		addServlet(variant + "/cgi-bin", "core.servlets.CGIServlet");
		addServlet(variant + "/tmpfile", "core.servlets.tmpfileservlet");
		addServlet(variant + "/", "core.servlets.FileServlet");
		addServlet(variant + "/bot", "core.servlets.BotServlet");
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
