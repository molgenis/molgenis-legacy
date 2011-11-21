package org.molgenis.framework.server;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.molgenis.MolgenisOptions;

public class MolgenisContext
{
	private ServletContext sc;
	private DataSource ds;
	private MolgenisOptions usedOptions;
	private String variant;
	private TokenManager tokenManager;
	
	// other "static" variables here, eg.
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletContext sc, DataSource ds, MolgenisOptions usedOptions, String variant)
	{
		this.sc = sc;
		this.ds = ds;
		this.usedOptions = usedOptions;
		this.variant = variant;
		this.tokenManager = new TokenManager();
	}

	
	
	public TokenManager getTokenManager()
	{
		return tokenManager;
	}

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
