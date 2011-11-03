package org.molgenis.framework.server;

import javax.servlet.ServletContext;

import org.molgenis.framework.db.Database;

public class MolgenisContext
{
	private Database db;
	private ServletContext sc;
	
	public MolgenisContext(Database db, ServletContext sc)
	{
		this.db = db;
		this.sc = sc;
	}

	public Database getDatabase()
	{
		return db;
	}

	public ServletContext getServletContext()
	{
		return sc;
	}
	
	
	
	
}
