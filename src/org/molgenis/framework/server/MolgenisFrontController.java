package org.molgenis.framework.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;

public abstract class MolgenisFrontController extends HttpServlet implements
		MolgenisService
{
	// helper vars
	private static final long serialVersionUID = -2141508157810793106L;
	Logger logger = Logger.getLogger(MolgenisFrontController.class);
	
	// map of all services for this app
	protected Map<String, MolgenisService> services;
	
	// the used molgenisoptions, set by generated MolgenisServlet
	protected MolgenisOptions usedOptions = null;
	
	// generated FrontController implements thid
	public abstract Database getDatabase();
	
	// get login from session and set it to database, or create new login
	// TODO: DISCUSS: needs to be NOT in context, but in request? or session?
	public abstract Login createLogin(Database db, HttpServletRequest request) throws Exception;

	// the one and only service() used in the molgenis app
	public void service(HttpServletRequest request, HttpServletResponse response)
	{
		
		try
		{
			MolgenisRequest req = new MolgenisRequest(request, response); //TODO: Bad, but needed for redirection. DISCUSS.
			MolgenisResponse res = new MolgenisResponse(response);
			this.createLogin(this.getDatabase(), req.getRequest());
			this.handleRequest(req, res);
		}
		catch (Exception e)
		{
            //TODO: send generic error page with details
			logger.error(e.getMessage());
		}
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response)
			throws ParseException, DatabaseException, IOException
	{
		HttpServletRequest req = request.getRequest();
		String path = req.getRequestURI();
		
		//System.out.println("** handleRequest for path " + path);

		// TODO get the most specific path.
		for (String p : services.keySet())
		{
			if (path.contains(p))
			{
				System.out.println("** using path: " + path);
				services.get(p).handleRequest(request, response);
				return;
			}
		}
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
