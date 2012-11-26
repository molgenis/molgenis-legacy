package org.molgenis.framework.server.services;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.ServeConfig;

import Acme.Serve.FileServlet;

/// Java Servlet implementation of a HTTP file server.
//<p>
//Implements the "GET" and "HEAD" methods for files and directories.
//Handles index.html, index.htm, default.htm, default.html.
//Redirects directory URLs that lack a trailing /.
//Handles If-Modified-Since.
//</p>

public class FileService extends FileServlet implements MolgenisService
{
	public FileService(MolgenisContext mc) throws ServletException
	{
		// needed to pass MIME type mapping from webserver to servlet!
		super.init(new ServeConfig(mc.getServletContext(), null, "/"));
	}

	private static final long serialVersionUID = -2932420561105678721L;

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
		try
		{
			super.service(request.getRequest(), response.getResponse());
		}
		catch (ServletException e)
		{
			throw new IOException(e);
		}
	}
}