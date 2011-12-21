package org.molgenis.framework.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.molgenis.MolgenisOptions;

public class MolgenisContext
{
	private ServletConfig sc;
	private DataSource ds;
	private MolgenisOptions usedOptions;
	private String variant;
	private TokenFactory tokenFactory;
	
	// other "static" variables here, eg.
	// molgenis version
	// date/time of generation
	// revision number
	
	public MolgenisContext(ServletConfig sc, DataSource ds, MolgenisOptions usedOptions, String variant)
	{
		this.sc = sc;
		this.ds = ds;
		this.usedOptions = usedOptions;
		this.variant = variant;
		this.tokenFactory = new TokenFactory();
	}

	public TokenFactory getTokenFactory()
	{
		return tokenFactory;
	}

	public String getVariant()
	{
		return variant;
	}

	public ServletConfig getServletConfig()
	{
		return sc;
	}
	
	public ServletContext getServletContext()
	{
		return sc.getServletContext();
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
