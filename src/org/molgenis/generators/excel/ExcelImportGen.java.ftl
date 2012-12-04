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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.CsvToDatabase.ImportResult;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.Tuple;

<#list model.entities as entity>
<#--not needed?
import ${entity.namespace}.${JavaName(entity)};-->
<#if !entity.abstract>import ${entity.namespace}.excel.${JavaName(entity)}ExcelReader;
</#if></#list>

public class ExcelImport
{
	static Logger logger = Logger.getLogger(ExcelImport.class.getSimpleName());
	
	public static void importAll(File excelFile, Database db, Tuple defaults) throws Exception
	{
		importAll(excelFile, db, defaults, null, DatabaseAction.ADD, "", true);
	}
	
	public static ImportResult importAll(File excelFile, Database db, Tuple defaults, List<String> components, DatabaseAction dbAction, String missingValue) throws Exception
	{
		return importAll(excelFile, db, defaults, components, dbAction, missingValue, true);
	}
	
	public static void importAll(File excelFile, Database db, Tuple defaults, boolean useDbTransaction) throws Exception
	{
		//set default missing value to ""
		importAll(excelFile, db, defaults, null, DatabaseAction.ADD, "", useDbTransaction);
	}

	public static ImportResult importAll(File excelFile, Database db, Tuple defaults, List<String> components, DatabaseAction dbAction, String missingValue, boolean useDbTransaction) throws Exception
	{	
		Workbook workbook = WorkbookFactory.create(excelFile);

		ArrayList<String> sheetNames = new ArrayList<String>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
		{
			String sheetName = workbook.getSheetName(i);
			if (sheetName != null)
			{
				sheetNames.add(sheetName.toLowerCase());
			}
		}
		
		ImportResult result = new ImportResult();

		try
		{
			if (useDbTransaction)
			{
				if (!db.inTx())
				{
					db.beginTx();
				}
				else
				{
					throw new DatabaseException("Cannot continue ExcelImport: database already in transaction.");
				}
			}
						
			if(dbAction.toString().startsWith("REMOVE"))
			{
				//reverse xref dependency order for remove
				<#list entities?reverse as entity><#if !entity.abstract>
				if (result.getErrorItem().equals("no error found") && (components == null || components.contains("${entity.name?lower_case}")))
				{
					try {
						int count = 0;
						if(sheetNames.contains("${entity.name?lower_case}")){
							count = new ${JavaName(entity)}ExcelReader().importSheet(db, workbook.getSheetAt(sheetNames.indexOf("${entity.name?lower_case}")), defaults, dbAction, missingValue);
						}
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
						int count = 0;
						if(sheetNames.contains("${entity.name?lower_case}")){
							count = new ${JavaName(entity)}ExcelReader().importSheet(db, workbook.getSheetAt(sheetNames.indexOf("${entity.name?lower_case}")), defaults, dbAction, missingValue);
						}
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
			
			if (useDbTransaction)
			{
				logger.debug("commiting transactions...");
				if (db.inTx()){
					db.commitTx();
				}else{
					throw new DatabaseException("Cannot commit ExcelImport: database not in transaction.");
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Import failed: " + e.getMessage());
			if (useDbTransaction)
			{
				if (db.inTx()){
					logger.debug("Db in transaction, rolling back...");
					db.rollbackTx();
				}else{
					logger.debug("Db not in transaction");
				}
			}
			throw e;
		}
		
		return result;
	}
}