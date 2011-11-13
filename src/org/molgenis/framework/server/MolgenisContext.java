package org.molgenis.framework.server;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.molgenis.MolgenisOptions;

public class MolgenisContext
{
	//private Database db;
	private ServletContext sc;
	
	private DataSource ds;
	
	private MolgenisOptions usedOptions;
	
	private String variant;
	
	// other static variables here, eg.
	// String molgenisVariantID
	// UsedMolgenisOptions
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletContext sc, DataSource ds, MolgenisOptions usedOptions, String variant)
	{
		//this.db = db;
		this.sc = sc;
		this.ds = ds;
		this.usedOptions = usedOptions;
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



	public MolgenisOptions getUsedOptions()
	{
		return usedOptions;
	}



	public void setUsedOptions(MolgenisOptions usedOptions)
	{
		this.usedOptions = usedOptions;
	}
	
	
	
	
}
