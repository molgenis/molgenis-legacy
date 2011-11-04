package org.molgenis.framework.server;

import javax.servlet.ServletContext;

import org.molgenis.framework.db.Database;

public class MolgenisContext
{
	//private Database db;
	private ServletContext sc;
	
	// other static variables here, eg.
	// String molgenisVariantID
	// UsedMolgenisOptions
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletContext sc)
	{
		//this.db = db;
		this.sc = sc;
	}

//	public Database getDatabase()
//	{
//		return db;
//	}

	public ServletContext getServletContext()
	{
		return sc;
	}
	
	
	
	
}
