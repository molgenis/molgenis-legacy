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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ExcelUtils;

import ${entity.namespace}.${JavaName(entity)};
import ${entity.namespace}.csv.${JavaName(entity)}CsvReader;

/**
 * Reads ${JavaName(entity)} from Excel file.
 */
public class ${JavaName(entity)}ExcelReader
{
	private static final Logger logger = Logger.getLogger(${JavaName(entity)}ExcelReader.class);
			
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
		boolean fileHasHeaders = ExcelUtils.writeSheetToFile(sheet, tmp${JavaName(entity)}, true);
		if(fileHasHeaders){
			int count = new ${JavaName(entity)}CsvReader().importCsv(db, tmp${JavaName(entity)}, defaults, dbAction, missingValue);
			tmp${JavaName(entity)}.delete();
			return count;
		}else{
			tmp${JavaName(entity)}.delete();
			return 0;
		}
	}
	
	public List<String> getNonEmptyHeaders(Sheet sheet)
	{
		List<String> headers = new ArrayList<String>();
		Row header = sheet.getRow(0); // assume headers are on first line
		if (header != null)
		{
			Iterator<Cell> it = header.cellIterator();
			while (it.hasNext())
			{
				Cell cell = it.next();
				if (cell != null)
				{
					String value = cell.getStringCellValue();
					if (StringUtils.isNotBlank(value))
					{
						headers.add(value);
					}
				}
			}
		}

		return headers;
	}
	
}