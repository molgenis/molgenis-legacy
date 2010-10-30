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

import javax.persistence.EntityManager;

import javax.sql.DataSource;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.datasource.DataSourceWrapper;
import org.molgenis.framework.db.jdbc.datasource.SimpleDataSourceWrapper;

public class JpaDatabase extends org.molgenis.framework.db.jpa.JpaDatabase
{
	//@EntityManager
	//EntityManager em;

	public JpaDatabase(EntityManager em)
	{
		super(em);
		this.setup();
	}

/*
	public JpaDatabase(Properties p)
	{
		super(p);
		this.setup();
	}

	public JpaDatabase(String propertiesFilePath) throws FileNotFoundException, IOException
	{
		super(propertiesFilePath);
		this.setup();
	}
*/	
	private void setup()
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
			<#if entity.decorator?exists>
		this.putMapper(${entity.namespace}.data.types.${Name(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.data.mappers.${Name(entity)}Mapper()));	
			<#else>
		this.putMapper(${entity.namespace}.data.types.${Name(entity)}.class, new ${entity.namespace}.data.mappers.${Name(entity)}Mapper());
			</#if>
		</#if></#list>
	}
}