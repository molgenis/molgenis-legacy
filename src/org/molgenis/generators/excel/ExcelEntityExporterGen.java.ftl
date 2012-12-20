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

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.io.csv.CsvReader;
import org.molgenis.util.tuple.Tuple;

public class ExcelEntityExporter
{
	static Logger logger = Logger.getLogger(ExcelEntityExporter.class);
	
	protected int sheetIndex = 0;
	
	/**
	 * Default export all using a target file and a database to export
	 * @param directory
	 * @param db
	 * @throws Exception
	 */
	public void exportAll(File excelFile, Database db) throws Exception
	{
		exportAll(excelFile, db, false, new QueryRule[]{});
	}
	
	/**
	 * Export all using a set of QueryRules used for all entities if applicable to that entity
	 * @param directory
	 * @param db
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File excelFile, Database db, QueryRule ... rules) throws Exception
	{
		exportAll(excelFile, db, false, rules);
	}
	
	/**
	 * Export all where a boolean skipAutoId forces an ignore of the auto id field ("id")
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @throws Exception
	 */
	public void exportAll(File excelFile, Database db, boolean skipAutoId) throws Exception
	{
		exportAll(excelFile, db, skipAutoId, new QueryRule[]{});
	}
	
	/**
	 * Export all with both a boolean skipAutoId and a set of QueryRules to specify both the skipping of auto id, and applying of a filter
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File excelFile, Database db, boolean skipAutoId, QueryRule ... rules) throws Exception
	{
		// Do checks on target file
		if(excelFile.exists()){
			throw new Exception("Target file " + excelFile.getAbsolutePath() + " already exists, will not proceed.");
		}
		boolean createSuccess = excelFile.createNewFile();
		if(!createSuccess){
			throw new Exception("Creation of target file " + excelFile.getAbsolutePath() + " failed, cannot proceed.");
		}
		
		// Create temporary directory
		File directory = new File(System.getProperty("java.io.tmpdir") + File.separator + "molgenis_export"+System.currentTimeMillis());
		directory.mkdir();
		
		// Export CSV to this directory
		CsvEntityExporter entityExporter = new CsvEntityExporter();
		entityExporter.exportAll(directory, db, skipAutoId, rules);
			
		// Create new Excel workbook
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(excelFile,
				ws);

		// Format the fonts
	    WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 
	      10, WritableFont.BOLD);
	    WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
	    headerFormat.setWrap(false);
	    WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 
	  	      10, WritableFont.NO_BOLD);
	  	   WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	  	    cellFormat.setWrap(false);
		
	  	// Variable: copy file contents to the workbook sheets
	  	<#list entities as entity><#if !entity.abstract && entity.system==false>
		copyCsvToWorkbook("${entity.name}", new File(directory+"/${entity.name?lower_case}.txt"), workbook, headerFormat, cellFormat);		
		</#if></#list>

	  	// Close workbook
		workbook.write();
	    workbook.close();
	    
	    // Remove temporary directory
		FileUtils.deleteDirectory(directory);
	}

	/**
	 * Convert a CSV to an Excel sheet inside a workbook
	 * @throws Exception 
	 */
	public void copyCsvToWorkbook(String sheetName, File file, WritableWorkbook workbook, WritableCellFormat headerFormat, WritableCellFormat cellFormat) throws Exception
	{
		if(file.exists())
		{
			// Create sheet
			WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
			
			// Parse CSV file to tuples TODO: batch this
			final List<Tuple> tuples = new ArrayList<Tuple>();
			CsvReader csvReader = new CsvReader(file);
			try
			{
				for (Tuple tuple : csvReader)
				{
					tuples.add(tuple);
				}
			}
			finally
			{
				csvReader.close();
			}
			
			// Add and store headers
			int j = 0;
			List<String> tupleFields = new ArrayList<String>();
			for(String colName : tuples.get(0).getColNames()) {
				tupleFields.add(colName);
				Label l = new Label(j, 0, colName, headerFormat);
				sheet.addCell(l);
			}
			
			// Add cells
			int rowIndex = 1;
			for(Tuple t : tuples){
				for(int i = 0; i < tupleFields.size(); i++){
					if(!(t.get(tupleFields.get(i)) == null)){
						Label l = new Label(i, rowIndex, t.get(tupleFields.get(i)).toString(), cellFormat);
						sheet.addCell(l);
					}else{
						sheet.addCell(new Label(i, rowIndex, "", cellFormat));
					}
					
				}
				rowIndex++;
			}
		    sheetIndex++;
		}
	}
}