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
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.Tuple;
//import org.molgenis.util.*;
//import org.molgenis.util.CsvFileReader;
//import org.molgenis.util.CsvReaderListener;
//import org.molgenis.util.SimpleTuple;

import org.molgenis.framework.db.CsvToDatabase.ImportResult;

<#list model.entities as entity>
<#--not needed?
import ${entity.namespace}.${JavaName(entity)};-->
<#if !entity.abstract>import ${entity.namespace}.csv.${JavaName(entity)}CsvReader;
</#if></#list>

public class CsvImport
{
	static int BATCH_SIZE = 10000;
	static int SMALL_BATCH_SIZE = 2500;
	static Logger logger = Logger.getLogger(CsvImport.class.getSimpleName());
	
	/**wrapper to use int inside anonymous classes (requires final, so cannot update directly)*/
	//FIXME move to value type elsewhere?
	public static class IntegerWrapper
	{
		private int value;
		
		public IntegerWrapper(int value)
		{
			this.value = value;
		}
		public void set(int value)
		{
			this.value = value;
		}
		public int get()
		{
			return this.value;
		}
	}
	
	public static ImportResult importAll(File directory, Database db, Tuple defaults) throws Exception
	{
		return importAll(directory, db, defaults, true);
	}
	
	public static ImportResult importAll(File directory, Database db, Tuple defaults, List<String> components, DatabaseAction dbAction, String missingValue) throws Exception
	{
		return importAll(directory, db, defaults, components, dbAction, missingValue, true);
	}
	
	public static ImportResult importAll(File directory, Database db, Tuple defaults, boolean useDbTransaction) throws Exception
	{
		//set default missing value to ""
		return importAll(directory, db, defaults, null, DatabaseAction.ADD, "", useDbTransaction);
	}

	/**
	 * Csv import of whole database.
	 * TODO: add filter parameters...
	 */
	public static ImportResult importAll(File directory, Database db, Tuple defaults, List<String> components, DatabaseAction dbAction, String missingValue, boolean useDbTransaction) throws Exception
	{
		ImportResult result = new ImportResult();
		boolean alreadyInTx = false;
		try
		{
			if (useDbTransaction)
			{
				if (!db.inTx())
				{
					db.beginTx();
				}else{
					alreadyInTx = true; 
					//throw new DatabaseException("Cannot continue CsvImport: database already in transaction.");
				}
			}
						
			if(dbAction.toString().startsWith("REMOVE"))
			{
				//reverse xref dependency order for remove
				<#list entities?reverse as entity><#if !entity.abstract>
				if (result.getErrorItem().equals("no error found") && (components == null || components.contains("${entity.name?lower_case}")))
				{
					try {
						int count = new ${JavaName(entity)}CsvReader().importCsv(db, new File(directory+"/${entity.name?lower_case}.txt"), defaults, dbAction, missingValue);
						result.getProgressLog().add("${entity.name?lower_case}");
						if(count > 0)
							result.getMessages().put("${entity.name?lower_case}", "evaluated "+count+" ${entity.name?lower_case} elements");
					} catch (Exception e) {
						result.setErrorItem("${entity.name?lower_case}");
						result.getMessages().put("${entity.name?lower_case}", e.getMessage() != null ? e.getMessage() : "null");
						throw e;
					}
				}
				</#if></#list>			
			}
			else
			{
				//follow xref dependency order
				<#list entities as entity><#if !entity.abstract>
				if (result.getErrorItem().equals("no error found") && (components == null || components.contains("${entity.name?lower_case}")))
				{
					try {
						int count = new ${JavaName(entity)}CsvReader().importCsv(db, new File(directory+"/${entity.name?lower_case}.txt"), defaults, dbAction, missingValue);
						result.getProgressLog().add("${entity.name?lower_case}");
						if(count > 0)
							result.getMessages().put("${entity.name?lower_case}",  "evaluated "+count+" ${entity.name?lower_case} elements");
					} catch (Exception e) {
						result.setErrorItem("${entity.name?lower_case}");
						result.getMessages().put("${entity.name?lower_case}", e.getMessage() != null ? e.getMessage() : "null");
						throw e;
					}					
				}
				</#if></#list>
			}			
			
			if (useDbTransaction &! alreadyInTx)
			{
				logger.debug("commiting transactions...");
				if (db.inTx()){
					db.commitTx();
				}else{
					throw new DatabaseException("Cannot commit CsvImport: database not in transaction.");
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Import failed: " + e.getMessage());
			if (useDbTransaction &! alreadyInTx)
			{
				if (db.inTx()){
					logger.debug("Db in transaction, rolling back...");
					db.rollbackTx();
				}else{
					logger.debug("Db not in transaction");
				}
			}
			e.printStackTrace();
			
			//Don't throw to avoid 'try-catch' on usage. No harm done.
			//throw e;
		}
		return result;
	}
}