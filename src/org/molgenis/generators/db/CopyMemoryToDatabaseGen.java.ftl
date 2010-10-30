<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.InMemoryDatabase;
<#list model.entities as entity><#if !entity.isAbstract()>
import ${entity.namespace}.${Name(entity)};	
</#if></#list>

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class CopyMemoryToDatabase
{
	static Logger logger = Logger.getLogger(CopyMemoryToDatabase.class.getSimpleName());
	
<#list entities as e><#if !e.abstract>	
	Map<${pkeyJavaType(e)},${pkeyJavaType(e)}> ${name(e)}Ids = new LinkedHashMap<${pkeyJavaType(e)},${pkeyJavaType(e)}>();
</#if></#list>
	
	public void copyMemoryToDatabase(InMemoryDatabase imdb, Database db) throws DatabaseException, IOException 
	{
		
<#list entities as e><#if !e.abstract && !e.association>	
		copy${Name(e)}(imdb,db);
</#if></#list>		
	}
	
<#list entities as e><#if !e.abstract && !e.association>		
	private void copy${Name(e)}(InMemoryDatabase imdb, Database db) throws DatabaseException, IOException
	{
		List<${Name(e)}> entities = imdb.find(${Name(e)}.class);
		
		//remember the original ids
		for(int i = 0; i < entities.size(); i++) ${name(e)}Ids.put(i, entities.get(i).get${PkeyName(e)}());
		
		//remap all foreign keys (because those have been imported before)
		for(${Name(e)} e: entities)
		{
<#list e.allFields as f>
<#if f.type = "xref">		
			//xref
			e.set${Name(f)}( ${name(f.XRefEntity)}Ids.get(e.get${Name(f)}()));
</#if>
<#if f.type = "mref">
			//mref
			{
				List<${pkeyJavaType(model.getEntity(f.XRefEntity))}> newIds = new ArrayList<${pkeyJavaType(model.getEntity(f.XRefEntity))}>();
				for(${pkeyJavaType(model.getEntity(f.XRefEntity))} key: e.get${Name(f)}())
				{
					newIds.add(${name(f.XRefEntity)}Ids.get(key));
				}
				e.set${Name(f)}(newIds);
			}
</#if>
</#list>
		}
		
		db.add(entities);
		
		//map the old ids to the new ids
		//so fkeys to this entity can be remapped.
		Map<${pkeyJavaType(e)},${pkeyJavaType(e)}> copy = new LinkedHashMap<${pkeyJavaType(e)},${pkeyJavaType(e)}>();
		for(int i = 0; i < entities.size(); i++) 
		{
			copy.put(${name(e)}Ids.get(i), entities.get(i).get${PkeyName(e)}());
		}	
		${name(e)}Ids = copy;
	}
</#if></#list>	
	
	
}