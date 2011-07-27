<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/model/${entity.getName()}.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.CsvPrintWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import ${entity.namespace}.${JavaName(entity)};
import ${entity.namespace}.csv.${JavaName(entity)}CsvReader;

/**
 * Reads ${JavaName(entity)} from Excel file.
 */
public class ${JavaName(entity)}ExcelReader
{
	public static final transient Logger logger = Logger.getLogger(${JavaName(entity)}ExcelReader.class);
			
	/**
	 * Imports ${JavaName(entity)} from a workbook sheet.
	 */
	public int importSheet(final Database db, Sheet sheet, final Tuple defaults, final DatabaseAction dbAction, final String missingValue) throws DatabaseException, IOException, Exception 
	{
		File tmp${JavaName(entity)} = new File(System.getProperty("java.io.tmpdir") + File.separator + "tmp${JavaName(entity)}.txt");
		if(tmp${JavaName(entity)}.exists()){
			boolean deleteSuccess = tmp${JavaName(entity)}.delete();
			if(!deleteSuccess){
				throw new Exception("Deletion of tmp file 'tmp${JavaName(entity)}.txt' failed, cannot proceed.");
			}
		}
		boolean createSuccess = tmp${JavaName(entity)}.createNewFile();
		if(!createSuccess){
			throw new Exception("Creation of tmp file 'tmp${JavaName(entity)}.txt' failed, cannot proceed.");
		}
		boolean fileHasHeaders = writeSheetToFile(sheet, tmp${JavaName(entity)});
		if(fileHasHeaders){
			int count = new ${JavaName(entity)}CsvReader().importCsv(db, tmp${JavaName(entity)}, defaults, dbAction, missingValue);
			tmp${JavaName(entity)}.delete();
			return count;
		}else{
			tmp${JavaName(entity)}.delete();
			return 0;
		}
	}
	
	public List<String> getNonEmptyHeaders(Sheet sheet){
		List<String> headers = new ArrayList<String>();
		Cell[] headerCells = sheet.getRow(0); //assume headers are on first line
		for(int i = 0; i < headerCells.length; i++){
			if(!headerCells[i].getContents().equals("")){
				headers.add(headerCells[i].getContents());
			}
		}
		return headers;
	}
	
	private boolean writeSheetToFile(Sheet sheet, File file) throws FileNotFoundException{
		List<String> headers = new ArrayList<String>();
		Cell[] headerCells = sheet.getRow(0); //assume headers are on first line
		if(headerCells.length == 0){
			return false;
		}
		ArrayList<Integer> namelessHeaderLocations = new ArrayList<Integer>(); //allow for empty columns, also column order does not matter
		for(int i = 0; i < headerCells.length; i++){
			if(!headerCells[i].getContents().equals("")){
				headers.add(headerCells[i].getContents());
			}else{
				headers.add("nameless"+i);
				namelessHeaderLocations.add(i);
			}
		}
		PrintWriter pw = new PrintWriter(file);
		CsvPrintWriter cw = new CsvPrintWriter(pw, headers);
		cw.setMissingValue("");
		cw.writeHeader();
		for(int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++){
			Tuple t = new SimpleTuple();
			int colIndex = 0;
			for(Cell c : sheet.getRow(rowIndex)){
				if(!namelessHeaderLocations.contains(colIndex) && colIndex < headers.size() && c.getContents() != null){
					t.set(headers.get(colIndex), c.getContents());
				}
				colIndex++;
			}
			cw.writeRow(t);
		}
		cw.close();
		return true;
	}
}