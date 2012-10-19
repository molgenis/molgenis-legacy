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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
<#list model.entities as entity><#if !entity.abstract>
import ${entity.namespace}.${JavaName(entity)};
import ${entity.namespace}.excel.${JavaName(entity)}ExcelReader;
</#if></#list>

public class ImportWizardExcelPrognosis {

	// map of all sheets, and whether they are importable (recognized) or not
	private Map<String, Boolean> sheetsImportable = new LinkedHashMap<String, Boolean>();

	// map of importable sheets and their importable fields
	private Map<String, List<String>> fieldsImportable = new LinkedHashMap<String, List<String>>();

	// map of importable sheets and their unknown fields
	private Map<String, List<String>> fieldsUnknown = new LinkedHashMap<String, List<String>>();

	// map of importable sheets and their missing fields
	private Map<String, List<String>> fieldsMissing = new LinkedHashMap<String, List<String>>();
	
	// map of importable sheets and their optional fields
	private Map<String, List<String>> fieldsOptional = new LinkedHashMap<String, List<String>>();
	
	// import order of the sheets
	private List<String> importOrder = new ArrayList<String>();

	public ImportWizardExcelPrognosis(Database db, File excelFile) throws Exception {

		Workbook workbook = Workbook.getWorkbook(excelFile);
		ArrayList<String> lowercasedSheetNames = new ArrayList<String>();
		Map<String, String> lowerToOriginalName = new LinkedHashMap<String, String>();

		try {

			for (String sheetName : workbook.getSheetNames()) {
				lowercasedSheetNames.add(sheetName.toLowerCase());
				lowerToOriginalName.put(sheetName.toLowerCase(), sheetName);
			}

			<#list entities as entity><#if !entity.abstract>
			if (lowercasedSheetNames.contains("${entity.name?lower_case}")) {
				String originalSheetname = lowerToOriginalName.get("${entity.name?lower_case}");
				Sheet sheet = workbook.getSheet(originalSheetname);
				${JavaName(entity)}ExcelReader excelReader = new ${JavaName(entity)}ExcelReader();
				List<String> allHeaders = excelReader.getNonEmptyHeaders(sheet);
				List<Field> entityFields = db.getMetaData().getEntity(${JavaName(entity)}.class.getSimpleName()).getAllFields();
				headersToMaps(originalSheetname, allHeaders, entityFields);
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
	
	public void headersToMaps(String originalSheetname, List<String> allHeaders, List<Field> entityFields){
		List<String> requiredFields = new ArrayList<String>();
		List<String> optionalFields = new ArrayList<String>();
		for(Field field : entityFields) {
			if(!field.isSystem() && !field.isAuto()) {
				String fieldName = field.getName().toLowerCase();
				if(!field.isNillable()) {
					if(field.getDefaultValue() == null)
						requiredFields.add(fieldName);
					else
						optionalFields.add(fieldName);
				} else {
					optionalFields.add(fieldName);
				}
			}
		}
		
		List<String> importableHeaders = new ArrayList<String>();
		List<String> unknownHeaders = new ArrayList<String>();
		for (String header : allHeaders) {
			boolean headerIsKnown = false;
			for (Field field : entityFields) {
				String fieldName = field.getName().toLowerCase();
				if (fieldName.equals(header.toLowerCase())) {
					requiredFields.remove(fieldName);
					optionalFields.remove(fieldName);
					headerIsKnown = true;
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
		fieldsMissing.put(originalSheetname, requiredFields);
		fieldsOptional.put(originalSheetname, optionalFields);
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

	public Map<String, List<String>> getFieldsMissing() {
		return fieldsMissing;
	}

	public Map<String, List<String>> getFieldsOptional() {
		return fieldsOptional;
	}
	
	public List<String> getImportOrder() {
		return importOrder;
	}
}