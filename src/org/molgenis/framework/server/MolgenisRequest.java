package org.molgenis.framework.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.util.HttpServletRequestTuple;

public class MolgenisRequest extends HttpServletRequestTuple
{

	Database db;
	String servicePath;
	
	public MolgenisRequest(HttpServletRequest request) throws Exception
	{
		super(request);

	}

	public MolgenisRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		super(request, response);

	}

	public Database getDatabase()
	{
		return db;
	}

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public String getServicePath()
	{
		return servicePath;
	}

	public void setServicePath(String servicePath)
	{
		this.servicePath = servicePath;
	}

	
	
}
