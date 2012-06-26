package org.molgenis.framework.db.jdbc;

import javax.naming.NamingException;
import javax.sql.DataSource;

public class SimpleDataSourceWrapper extends AbstractDataSourceWrapper
{
	DataSource dSource;
	
	public SimpleDataSourceWrapper(DataSource dSource)
	{
		if (dSource == null || !(dSource instanceof DataSource)) throw new IllegalArgumentException(
		"DataSource cannot be null");
		this.dSource = dSource;
	}

	@Override
	protected DataSource getDataSource() throws NamingException
	{
		//logger.debug("Getting dataSource");
		return this.dSource;
	}
}
