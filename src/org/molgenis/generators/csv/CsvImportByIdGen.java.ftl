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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvFileReader;

<#list entities as entity>
	<#if !entity.abstract>
	import ${entity.namespace}.${JavaName(entity)};
	</#if>
</#list>


public class CsvImportById
{
	static Logger logger = Logger.getLogger(CsvImport.class.getSimpleName());
	//mappings between imported and internally assigned ids
	//only necessary for automatic ids
<#list entities as entity>
	<#if pkeyField(entity)?exists>
	static Map<${pkeyJavaType(entity)},${pkeyJavaType(entity)}> ${name(entity)}IdMap = new TreeMap<${pkeyJavaType(entity)},${pkeyJavaType(entity)}>(); 
</#if></#list>		

	/**
	 * Csv import of whole database.
	 * TODO: add filter parameters...
	 */
	public static void importData(File directory, Database db) throws Exception
	{
		try
		{
			db.beginTx();
						
			<#list entities as entity><#if !entity.abstract>
			import${JavaName(entity)}(db, new File(directory + "/${entity.name?lower_case}.txt"));
			</#if></#list>			
			
			// insert back again...
			logger.debug("commiting transactions...");
			
			db.commitTx();
		}
		catch (Exception e)
		{
			logger.error("import failed: " + e.getMessage());
			logger.debug("rolling back transactions...");
			db.rollbackTx();

			throw e;
		}

		logger.debug("done");
	}
	
<#list entities as entity><#if !entity.abstract >	
	/**
	 * Imports ${JavaName(entity)} from tab/comma delimited File.
	 * @param ${entity.name}File a tab delimited file with ${JavaName(entity)} data.
	 */
	private static void import${JavaName(entity)}(Database db, File ${entity.name}File)	throws DatabaseException, IOException, Exception 
	{
		logger.debug("trying to import "+${entity.name}File);
		if(	!${entity.name}File.exists() )
		{
			logger.warn("${entity.name}.txt file is missing, skipped import");
		}
		else
		{
			//read ${entity.name} from file
			CsvReader reader = new CsvFileReader(${entity.name}File);
			List<${JavaName(entity)}> ${name(entity)}List = db.toList(${JavaName(entity)}.class, reader, Integer.MAX_VALUE); //should have no limit 
			logger.debug("loaded "+${name(entity)}List.size()+" ${entity.name} objects");
			
			//redirect incoming and outgoing fkeys
			List<${pkeyJavaType(entity)}> ${name(entity)}Ids = new ArrayList<${pkeyJavaType(entity)}>(); //also doesn't scale
			for(int i = 0; i < ${name(entity)}List.size(); i++ ) //sorry, not a real list so need to put back!!
			{
				${JavaName(entity)} object = ${name(entity)}List.get(i);
				
				//remember index of this id for incoming fkeys
				${name(entity)}Ids.add(object.get${JavaName(PkeyName(entity))}()); 
				
				//redirect outgoing fkeys
				<#list allFields(entity) as f><#if f.type = "xref">
				if(object.get${JavaName(f)}() != null) object.set${JavaName(f)}_${JavaName(f.getXrefField())}(${name(f.xrefEntity)}IdMap.get(object.get${JavaName(f)}_${JavaName(f.xrefField)}()));
				<#elseif f.type="mref"> 
				List<${type(f.xrefField)} > ${name(f)}Ids = new ArrayList<${type(f.xrefField)}>();
				for(${type(f.xrefField)} id: object.get${JavaName(f)}_${JavaName(f.xrefField)}())
				{
					${name(f)}Ids.add(${name(f.xrefEntity)}IdMap.get(id));
				}
				</#if></#list>
				
				//add assay back to list
				${name(entity)}List.set(i, object);
			}
			//add to database
			db.add(${name(entity)}List);
			for(int i = 0; i < ${name(entity)}List.size(); i++)
			{
				${name(entity)}IdMap.put(${name(entity)}Ids.get(i), ${name(entity)}List.get(i).get${JavaName(PkeyName(entity))}());
				<#list entity.getAllAncestors() as ancestor>
				${name(ancestor)}IdMap.put(${name(entity)}Ids.get(i), ${name(entity)}List.get(i).get${JavaName(PkeyName(entity))}());
				</#list>
			}
		}	 
	}
</#if></#list>	
}