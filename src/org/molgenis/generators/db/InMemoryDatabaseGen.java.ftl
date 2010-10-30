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

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Model;

//import ${model.getName()}.JDBCMetaDatabase;
import ${package}.JDBCMetaDatabase;

public class InMemoryDatabase extends org.molgenis.framework.db.inmemory.InMemoryDatabase
{
	public InMemoryDatabase() 
	{
		//naieve impl, much todo
	}
	
	@Override
	public Model getMetaData() throws DatabaseException
	{
		return new JDBCMetaDatabase();
	}
}