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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;
<#list model.entities as entity><#if !entity.abstract>
import ${entity.namespace}.${JavaName(entity)};
import ${entity.namespace}.excel.${JavaName(entity)}ExcelReader;
</#if></#list>

public class ImportWizardExcelPrognosis {

	// map of all sheets, and whether they are importable (recognized) or not
	private Map<String, Boolean> sheetsImportable = new HashMap<String, Boolean>();

	// map of importable sheets and their importable fields
	private Map<String, List<String>> fieldsImportable = new HashMap<String, List<String>>();

	// map of importable sheets and their unknown fields
	private Map<String, List<String>> fieldsUnknown = new HashMap<String, List<String>>();

	// import order of the sheets
	private List<String> importOrder = new ArrayList<String>();

	public ImportWizardExcelPrognosis(File excelFile) throws Exception {

		Workbook workbook = Workbook.getWorkbook(excelFile);
		ArrayList<String> lowercasedSheetNames = new ArrayList<String>();
		Map<String, String> lowerToOriginalName = new HashMap<String, String>();

		try {

			for (String sheetName : workbook.getSheetNames()) {
				lowercasedSheetNames.add(sheetName.toLowerCase());
				lowerToOriginalName.put(sheetName.toLowerCase(), sheetName);
			}

			int sheetIndex = 0;
			<#list entities as entity><#if !entity.abstract>
			if (lowercasedSheetNames.contains("${entity.name?lower_case}")) {
				String originalSheetname = lowerToOriginalName.get("${entity.name?lower_case}");
				Sheet sheet = workbook.getSheet(originalSheetname);
				${JavaName(entity)}ExcelReader excelReader = new ${JavaName(entity)}ExcelReader();
				List<String> allHeaders = excelReader.getNonEmptyHeaders(sheet);
				${JavaName(entity)} entity = new ${JavaName(entity)}();
				headersToMaps(originalSheetname, allHeaders, entity.getFields());
				sheetIndex++;
			}
			</#if></#list>
			
			for(String sheetName : lowerToOriginalName.values()){
				if(importOrder.contains(sheetName)){
					sheetsImportable.put(sheetName, true);
				}else{
					sheetsImportable.put(sheetName, false);
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			workbook.close();
		}
	}
	
	public void headersToMaps(String originalSheetname, List<String> allHeaders, Vector<String> fields){
		List<String> importableHeaders = new ArrayList<String>();
		List<String> unknownHeaders = new ArrayList<String>();
		for (String header : allHeaders) {
			boolean headerIsKnown = false;
			for (String field : fields) {
				if (field.toLowerCase().equals(header.toLowerCase())) {
					headerIsKnown = true;
					break;
				}
			}
			if(headerIsKnown){
				importableHeaders.add(header);
			}else{
				unknownHeaders.add(header);
			}
		}
		importOrder.add(originalSheetname);
		fieldsImportable.put(originalSheetname, importableHeaders);
		fieldsUnknown.put(originalSheetname, unknownHeaders);
	}

	public Map<String, Boolean> getSheetsImportable() {
		return sheetsImportable;
	}

	public Map<String, List<String>> getFieldsImportable() {
		return fieldsImportable;
	}

	public Map<String, List<String>> getFieldsUnknown() {
		return fieldsUnknown;
	}

	public List<String> getImportOrder() {
		return importOrder;
	}
}