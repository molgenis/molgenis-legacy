package org.molgenis.framework.server;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.molgenis.framework.db.Database;

public class MolgenisContext
{
	//private Database db;
	private ServletContext sc;
	
	private DataSource ds;
	
	private String variant;
	
	// other static variables here, eg.
	// String molgenisVariantID
	// UsedMolgenisOptions
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletContext sc, DataSource ds, String variant)
	{
		//this.db = db;
		this.sc = sc;
		this.ds = ds;
		this.variant = variant;
	}
	
	

//	public Database getDatabase()
//	{
//		return db;
//	}

	public String getVariant()
	{
		return variant;
	}



	public ServletContext getServletContext()
	{
		return sc;
	}

	public DataSource getDataSource()
	{
		return ds;
	}
	
	
	
	
}
