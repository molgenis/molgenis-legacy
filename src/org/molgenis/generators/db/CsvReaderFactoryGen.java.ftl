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

import ${model.getName()}.data.types.*;
import ${model.getName()}.data.csv.*;

public class CsvReaderFactory extends org.molgenis.framework.db.csv.EntityReaderFactory
{
	public CsvReaderFactory()
	{
		<#list model.entities as entity><#if !entity.isAbstract() && !entity.isSystem()>
		this.putReader(${Name(entity)}.class, new ${Name(entity)}CsvReader());
		</#if></#list>
	}
}