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
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.util.CsvFileWriter;

<#list model.entities as entity><#if !entity.abstract && entity.association==false>
${imports(model, entity, "")}
</#if></#list>

public class CsvExport
{
	static Logger logger = Logger.getLogger(CsvExport.class.getSimpleName());
		
		/**
	 * Default export all using a target directory and a database to export
	 * @param directory
	 * @param db
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db) throws Exception
	{
		exportAll(directory, db, true, new QueryRule[]{});
	}
	
	/**
	 * Export all using a set of QueryRules used for all entities if applicable to that entity
	 * @param directory
	 * @param db
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, QueryRule ... rules) throws Exception
	{
		exportAll(directory, db, true, rules);
	}
	
	/**
	 * Export all where a boolean skip autoid fields forces an ignore of the auto id field ("id")
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, boolean skipAutoId) throws Exception
	{
		exportAll(directory, db, skipAutoId, new QueryRule[]{});
	}
	
	/**
	 * Export all with both a boolean skipAutoId and a set of QueryRules to specify both the skipping of auto id, and applying of a filter
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, boolean skipAutoId, QueryRule ... rules) throws Exception
	{				
		<#list entities as entity><#if !entity.abstract && entity.association==false>
		export${Name(entity)}(db, new File(directory+"/${entity.name?lower_case}.txt"), skipAutoId ? Arrays.asList(new String[]{<#assign first = true><#list entity.allFields as f><#if !(f.type = "int" && f.auto)><#if first><#assign first=false><#else>,</#if><#if f.type="mref" || f.type="xref"><#list f.xrefLabelNames as label>"${f.name}_${label}"<#if label_has_next>,</#if></#list><#else>"${f.name}"</#if></#if></#list>}) : null, rules);		
		</#if></#list>
			
		logger.debug("done");
	}
	
	public void exportAll(File directory, List ... entityLists) throws Exception
	{				
		for(List l: entityLists) if(l.size()>0)
		{
			<#list entities as entity><#if !entity.abstract && entity.association==false>
			if(l.get(0).getClass().equals(${JavaName(entity)}.class))
				export${Name(entity)}(l, new File(directory+"/${entity.name?lower_case}.txt"));		
			</#if></#list>
		}
			
		logger.debug("done");
	}
	
		private QueryRule[] matchQueryRulesToEntity(Entity e, QueryRule ... rules) throws MolgenisModelException
	{
		ArrayList<QueryRule> tmpResult = new ArrayList<QueryRule>();
		for(QueryRule q : rules){
			if(!(e.getAllField(q.getField()) == null)){
				tmpResult.add(q); //field is okay for this entity
			}
			//special case: eg. investigation.name -> if current entity is 'investigation', use field 'name'
			String[] splitField = q.getField().split("\\.");
			if(splitField.length == 2){
				if(e.getName().equals(splitField[0])){
					QueryRule copy = new QueryRule(q);
					copy.setField(splitField[1]);
					tmpResult.add(copy);
				}
			}
		}
		QueryRule[] result = new QueryRule[tmpResult.size()];
		for(int i=0; i<result.length; i++){
			result[i] = tmpResult.get(i);
		}
		return result;
	}

<#list entities as entity><#if !entity.abstract && entity.association==false>
	/**
	 *	export ${Name(entity)} to file.
	 *  @param db the database to export from.
	 *  @param f the file to export to.
	 */
	public void export${Name(entity)}(Database db, File f, List<String> fieldsToExport, QueryRule ... rules) throws DatabaseException, IOException, ParseException, MolgenisModelException
	{
		if(db.count(${Name(entity)}.class<#if entity.hasAncestor() || entity.isRootAncestor()>, new QueryRule("${typefield()}",Operator.EQUALS, "${Name(entity)}")</#if>) > 0)
		{
			
			Query query = db.query(${Name(entity)}.class);
			<#if entity.hasAncestor() || entity.isRootAncestor()>QueryRule type = new QueryRule("${typefield()}",Operator.EQUALS, "${Name(entity)}");
			query.addRules(type);</#if>
			QueryRule[] newRules = matchQueryRulesToEntity(db.getMetaData().getEntity("${Name(entity)}"), rules);
			query.addRules(newRules);
			int count = query.count();
			if(count > 0){
				CsvFileWriter ${name(entity)}Writer = new CsvFileWriter(f);
				query.find(${name(entity)}Writer, fieldsToExport);
				${name(entity)}Writer.close();
			}
			<#--db.find(${Name(entity)}.class, ${name(entity)}Writer<#if entity.hasAncestor() || entity.isRootAncestor()>, new QueryRule(${typefield()},Operator.EQUALS, "${Name(entity)}")</#if>);-->
		}
	}
	
	public void export${Name(entity)}(List<${JavaName(entity)}> entities, File file) throws IOException
	{
		if(entities.size()>0)
		{
			//filter nulls
			List<String> fields = entities.get(0).getFields();
			List<String> notNulls = new ArrayList<String>();
			
			for(String f: fields)
			{
				for(${JavaName(entity)} e: entities)
				{
					if(e.get(f) != null) notNulls.add(f);
					break;
				}
			}			
			
			//write
			CsvFileWriter ${name(entity)}Writer = new CsvFileWriter(file, notNulls);
			${name(entity)}Writer.writeHeader();
			for(${JavaName(entity)} e: entities)
			{
				${name(entity)}Writer.writeRow(e);
			}
			${name(entity)}Writer.close();
		}
	}
</#if></#list>	
}