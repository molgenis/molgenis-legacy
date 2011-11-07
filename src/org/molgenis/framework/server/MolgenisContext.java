package org.molgenis.framework.server;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.molgenis.framework.db.Database;

public class MolgenisContext
{
	//private Database db;
	private ServletContext sc;
	
	private DataSource ds;
	
	// other static variables here, eg.
	// String molgenisVariantID
	// UsedMolgenisOptions
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletContext sc, DataSource ds)
	{
		//this.db = db;
		this.sc = sc;
		this.ds = ds;
	}

//	public Database getDatabase()
//	{
//		return db;
//	}

	public ServletContext getServletContext()
	{
		return sc;
	}

	public DataSource getDataSource()
	{
		return ds;
	}
	
	
	
	
}
