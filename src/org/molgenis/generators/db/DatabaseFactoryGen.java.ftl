<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${package}/DatabaseFactory
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.DataSourceWrapper;

public class DatabaseFactory
{
	public static Database create(DataSource data_src, File file_source) throws DatabaseException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase(data_src, file_source);
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase(); // ignore parameters, everything is declared in persistence.xml
<#else>
		throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(DataSourceWrapper data_src, File file_src) throws DatabaseException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase(data_src, file_src);
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase(); // ignore parameters, everything is declared in persistence.xml
<#else>
		throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(Properties p) throws DatabaseException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase(p);
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase(); // ignore parameters, everything is declared in persistence.xml
<#else>
		throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(MolgenisOptions options) throws DatabaseException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase(options);
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase(); // ignore parameters, everything is declared in persistence.xml
<#else>
		throw new UnsupportedOperationException();
</#if>
	}
	
	public static Database create() throws DatabaseException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase();
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase();
<#else>
		throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(String propertiesFilePath) throws DatabaseException, FileNotFoundException, IOException
	{
<#if databaseImp == "jdbc">
		return new ${package}.JDBCDatabase(propertiesFilePath);
<#elseif databaseImp == "jpa">
		return new ${package}.JpaDatabase(propertiesFilePath);
<#else>
		throw new UnsupportedOperationException();
</#if>
	}
}