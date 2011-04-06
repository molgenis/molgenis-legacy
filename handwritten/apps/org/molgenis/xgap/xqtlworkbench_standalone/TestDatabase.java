package org.molgenis.xgap.xqtlworkbench_standalone;

import java.io.File;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class TestDatabase {
	
	public Database getDatabase() throws DatabaseException, NamingException
	{
			BasicDataSource data_src = new BasicDataSource();
			data_src.setDriverClassName("org.hsqldb.jdbcDriver");
			data_src.setUsername("sa");
			data_src.setPassword("");
			data_src.setUrl("jdbc:hsqldb:file:hsqldb/molgenisdb;shutdown=true"); // a path within the src folder?
			data_src.setMaxIdle(10);
			data_src.setMaxWait(1000);
		
			DataSource dataSource = (DataSource)data_src;
			return new app.JDBCDatabase(dataSource, new File("attachedfiles"));
	}
}
