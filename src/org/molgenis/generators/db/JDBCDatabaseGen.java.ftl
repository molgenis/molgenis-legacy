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
import org.molgenis.model.elements.Model;

public class JDBCDatabase extends org.molgenis.framework.db.jdbc.JDBCDatabase
{
	private JDBCMetaDatabase metaData = null;

	public JDBCDatabase(DataSource data_src, File file_source) throws DatabaseException
	{
		this(new SimpleDataSourceWrapper(data_src), file_source);
	}

	public JDBCDatabase(DataSourceWrapper data_src, File file_src) throws DatabaseException
	{
		super(data_src, file_src);
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
		super(propertiesFilePath);
		this.setup();
	}
	
	private void setup()
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
			<#if entity.decorator?exists>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}Mapper(this)));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}Mapper(this))));
				</#if>	
			<#else>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}Mapper(this));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}Mapper(this)));
				</#if>
			</#if>
		</#if></#list>
	}
	
	@Override
	public Model getMetaData() throws DatabaseException
	{
		//load on demand.
		//nb: the JDBCMetaDatabase must be made much faster which is done in the generator
		//because now it is still validating which it shouldn't
		if(metaData == null)
			metaData = new JDBCMetaDatabase();
		return metaData;
	}
}