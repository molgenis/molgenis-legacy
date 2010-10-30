<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/model/JDBCDatabase
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
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.DataSourceWrapper;
import org.molgenis.framework.db.jdbc.SimpleDataSourceWrapper;

public class JDBCDatabase extends org.molgenis.framework.db.jdbc.JDBCDatabase
{
	public JDBCDatabase(DataSource data_src, File file_source) throws DatabaseException
	{
		this(new SimpleDataSourceWrapper(data_src), file_source);
	}

	public JDBCDatabase(DataSourceWrapper data_src, File file_src) throws DatabaseException
	{
		super(data_src, file_src, new JDBCMetaDatabase());
		this.setup();
	}

	public JDBCDatabase(Properties p)
	{
		super(p);
		this.setup();
	}
	
	public JDBCDatabase(MolgenisOptions options)
	{
		super(options);
		this.setup();
	}
	
	public JDBCDatabase()
	{
		super(new MolgenisOptions());
		this.setup();
	}

	public JDBCDatabase(String propertiesFilePath) throws FileNotFoundException, IOException, DatabaseException
	{
		super(propertiesFilePath, new JDBCMetaDatabase());
		this.setup();
	}
	
	private void setup()
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
			<#if entity.decorator?exists>
		this.putMapper(${entity.namespace}.${Name(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${Name(entity)}Mapper(this)));	
			<#else>
		this.putMapper(${entity.namespace}.${Name(entity)}.class, new ${entity.namespace}.db.${Name(entity)}Mapper(this));
			</#if>
		</#if></#list>
	}
}