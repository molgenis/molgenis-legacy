package org.molgenis.framework.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.molgenis.framework.db.Database;
import org.molgenis.util.HttpServletRequestTuple;

public class MolgenisRequest extends HttpServletRequestTuple
{
	Database database;
	ServletContext sc;
	
	public MolgenisRequest(Database database, HttpServletRequest request, ServletContext sc) throws Exception
	{
		super(request);
		this.database = database;
	}
	
	public Database getDatabase()
	{
		return database;
	}

	public String getRequestURI()
	{
		return getRequest().getRequestURI();
	}

	public ServletContext getServletContext()
	{
		return sc;
	}

}
