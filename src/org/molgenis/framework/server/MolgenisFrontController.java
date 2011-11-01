package org.molgenis.framework.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class MolgenisFrontController extends HttpServlet implements
		MolgenisService
{
	private Database db;
	private Map<String, MolgenisService> services = new LinkedHashMap<String,MolgenisService>();
	Logger logger = Logger.getLogger(MolgenisFrontController.class);

	public MolgenisFrontController()
	{
		services.put("/api/find", new MolgenisDownloadService());
		services.put("/api/R", new MolgenisRapiService());
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			MolgenisRequest req = new MolgenisRequest(getDatabase(), request);
			MolgenisResponse res = new MolgenisResponse(response);
			this.handleRequest(req, res);
		}
		catch (Exception e)
		{

			logger.error(e.getMessage());
		}
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response)
			throws ParseException, DatabaseException, IOException
	{
		HttpServletRequest req = request.getRequest();
		String path = req.getRequestURI();

		// TODO get the most specific path.
		for (String p : services.keySet())
		{
			if (path.contains(p))
			{
				services.get(p).handleRequest(request, response);
				return;
			}
		}
	}

	public Database getDatabase() throws Exception
	{
		return this.db;
	}

	// if (path != null && path.contains("/api/find"))
	// {
	// this.handleDownload(request, response);
	// }
	// else if (path != null && path.contains("/api/add"))
	// {
	// this.handleUpload(request, response);
	// }
	// else if (path != null && path.contains("/api/R"))
	// {
	// this.handleRAPIrequest(request, response);
	// }
	// else if (path != null && (path.contains("/api/soap")))
	// {
	// this.handleSOAPrequest(request, response);
	// }
	// else if (path != null && path.contains("/xref/find"))
	// {
	// this.handleXREFrequest(request, response);
	// }
	// else if (path != null && path.contains("/download/"))
	// {
	// this.handleDownloadFile(request, response);
	// }

}
