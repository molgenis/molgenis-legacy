package org.molgenis.framework.server;

import javax.servlet.http.HttpServletRequest;

import org.molgenis.framework.db.Database;
import org.molgenis.util.HttpServletRequestTuple;

public class MolgenisRequest extends HttpServletRequestTuple
{
	Database database;
	
	public MolgenisRequest(Database database, HttpServletRequest request) throws Exception
	{
		super(request);
		this.database = database;
	}
	
	public Database getDatabase()
	{
		return database;
	}

}
