package org.molgenis.framework.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

public interface DataSourceWrapper
{
	public Connection getConnection() throws NamingException, SQLException;

	public String getDriverClassName();

	//public String getDriverClassName() throws NamingException;
	
	//public int getNumActive();
	
}