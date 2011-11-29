package plugins.data;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.cxf.binding.corba.wsdl.Array;
import org.apache.poi.hssf.record.formula.Ptg;
import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.FillMetadata;
import app.servlet.RestApi.CategoryList;

import plugins.emptydb.emptyDatabase;




public class LLDataImportExcel extends PluginModel<Entity>
{
	private String Status = "";

	private static final long serialVersionUID = 6149846107377048848L;

	public LLDataImportExcel(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_data_LLDataImportExcel";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/LLDataImportExcel.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("ImportLifelineToPheno".equals(request.getAction())) {

			System.out.println("----------------->");

			System.out.println(db.query(Investigation.class).eq(Investigation.NAME, " Lifelines").count());

			Investigation inv = new Investigation();

			if(db.query(Investigation.class).eq(Investigation.NAME, "LifeLines").count() == 0){

				inv.setName("LifeLines");
				db.add(inv);

			}
			loadDataFromExcel(db, request, inv);

		}

		if ("fillinDatabase".equals(request.getAction())) {

			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			this.setStatus("The database is empty now");
		}

	}
	@SuppressWarnings("unchecked")
	public void loadDataFromExcel(Database db, Tuple request, Investigation inv) throws BiffException, IOException, DatabaseException{

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		File file = new File(tmpDir+ "/LifelinesDict.xls"); 

		if (file.exists()) {

			System.out.println("The excel file is being imported, please be patient");

			this.setStatus("The excel file is being imported, please be patient");

			Workbook workbook = Workbook.getWorkbook(file); 

			Sheet dictionary = workbook.getSheet(0); 
			
			Sheet dictionaryCategory = null;
			
			int numberOfSheet = workbook.getNumberOfSheets();
			
			for(int i = 0; i < numberOfSheet; i++){
				
				dictionaryCategory = workbook.getSheet(i); //TODO what if there are more than 2 sheets?
				
				System.out.println(dictionaryCategory);
				
				if(dictionaryCategory.getName().equals("Dictionary"))
					convertDictionary(dictionaryCategory, db, request, inv);
				else if (dictionaryCategory.getName().equals("Category"))
					insertCategory(workbook.getSheet(1), db, request, inv);
				
				this.setStatus("The file is imported.Congrats!" );
			}
		} else {
			this.setStatus("The file should be in " + file );
		}
	}
	
	public void insertCategory(Sheet excelSheet, Database db, Tuple request, Investigation inv) throws DatabaseException{
	
		int row = excelSheet.getRows();

		int column = excelSheet.getColumns();
		
		String fieldName = "";
		
		String codeLable = "";
		
		String codeString = "";
		
		HashMap<String, Measurement> nameToMesurement = new HashMap<String, Measurement>();
		
		HashMap<Measurement, List<String>> measurementToCategory = new HashMap <Measurement, List<String>>();
		
		List<String> measurementNameList = new ArrayList<String>();
		
		List<Measurement> addedMeasurement = new ArrayList<Measurement>();
		
		List<Category> addedCategory = new ArrayList<Category>();
		
		for(Measurement m : db.find(Measurement.class)){
			nameToMesurement.put(m.getName(), m);
		}
		
		
		for(int i = 1; i < row; i++){
			
			Measurement measurement = new Measurement();
			
			Category category = new Category();
			
			for(int j = 0; j < column; j++){
				
				if (j == 1){
					
					fieldName = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					measurement = nameToMesurement.get(fieldName);
					
					if(!measurementToCategory.containsKey(measurement)){
						
						List<String> temp = new ArrayList<String>();
						measurementToCategory.put(measurement, temp);
					}
				
				}else if(j == 3){
					
					codeLable = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					category.setLabel(codeLable);
					category.setName(codeLable);
					
				}else if(j == 4){
				
					codeString = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					category.setCode_String(codeString);
					category.setDescription(codeString);
				}
				
			}
			
			if(!addedCategory.contains(category))
				addedCategory.add(category);
			
			List<String> temp = measurementToCategory.get(measurement);
			
			if(!temp.contains(category.getLabel())){
				temp.add(category.getLabel());
				measurementToCategory.put(measurement, temp);
			}
			
			//System.out.println(measurement.getName() + "--------------" + category.getLabel());
			
			if(!addedMeasurement.contains(measurement))
				
				addedMeasurement.add(measurement);
		}
		
		List<Category> addedCodes = new ArrayList<Category>();
		
		for( Category cod : addedCategory){
			if(db.query(Category.class).eq(Category.CODE_STRING, cod.getCode_String()).count() == 0){
				if(!addedCodes.contains(cod)){
					addedCodes.add(cod);
				}
			}
		}
		
		db.add(addedCodes);
		
//		for(Category code : addedCodes){
//			System.out.println(code.getName());
//			db.add(code);
//		}
		
		for(Measurement m : addedMeasurement){

			List<String> categoryNames = measurementToCategory.get(m);
			
			List<Integer> categoryId = new ArrayList<Integer>();
			//System.out.print(m.getName() + "\t");
			
			List<Category> categories = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, categoryNames));
			
			for(Category c : categories){
				if(!categoryId.contains(c.getId()))
					categoryId.add(c.getId());
			}
			
			m.setCategories_Id(categoryId);
			
		}
		for(Measurement m : addedMeasurement){
			System.out.println(m.getName() + "--------" + m.getCategories_Id());
			db.update(m);
		}
		//db.update(addedMeasurement);
	}
	
	public void convertDictionary(Sheet excelSheet, Database db, Tuple request, Investigation inv) throws DatabaseException{
		
		int row = excelSheet.getRows();

		int column = excelSheet.getColumns();

		String protocolName = "";
		
		String fieldTypeName = "";
		
		String measurementName = "";
		
		String descriptionText;
		
		Pattern text = Pattern.compile("text");
		
		Pattern number = Pattern.compile("number");
		
		List<Protocol> protocolList = new ArrayList<Protocol>();
		
		List<Measurement> measurementList = new ArrayList<Measurement>();
		
		HashMap<String, List> protocolMeasurement = new HashMap<String, List>();
		
		for(int i = 1; i < row; i++){
			
			Protocol protocol = new Protocol();
			
			Measurement measurement = new Measurement();
			
			for(int j = 0; j < column; j++){
				
				if( j == 0 ){
					
					protocolName = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					
					protocol.setName(protocolName);
					
					protocol.setInvestigation(inv);
					
					if(!protocolMeasurement.containsKey(protocolName)){
						List<String> temp = new ArrayList();
						protocolMeasurement.put(protocolName,temp);
					}
				}
				
				if(j == 3){
					
					measurementName = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					
					if(measurementName.equalsIgnoreCase("ID")){
						measurementName = protocolName + "_" + measurementName;
					}
					
					measurement.setName(measurementName);
					
					measurement.setInvestigation(inv);
					
					List<String> tempMeasurements = protocolMeasurement.get(protocolName.toLowerCase());
					
					if(!tempMeasurements.contains(measurementName))
						tempMeasurements.add(measurementName);
					
					protocolMeasurement.put(protocolName, tempMeasurements);
				}
			
				if(j == 4){
					
					fieldTypeName = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase().toLowerCase();
					
					Matcher m = text.matcher(fieldTypeName);
					
					
					if(fieldTypeName.equals("date")){
					
						measurement.setDataType("datetime");
					
					}else if(m.find()){
						measurement.setDataType("string");
					}else{
						m = number.matcher(fieldTypeName);
						if(m.find()){
							measurement.setDataType("int");
						}
					}
				}
				
				if(j == 5){
				
					descriptionText = excelSheet.getCell(j, i).getContents().replaceAll("'", "").toLowerCase();
					
					measurement.setDescription(descriptionText);
				}
			}
			
			if(!protocolList.contains(protocol))
				protocolList.add(protocol);
			
			if(!measurementList.contains(measurement)){
				if(measurementName.equalsIgnoreCase("PA_ID")){
					if(protocolName.equalsIgnoreCase("PATIENT")){
						measurementList.add(measurement);
					}
				}else if(measurementName.equalsIgnoreCase("BZ_ID")){
					if(protocolName.equalsIgnoreCase("BEZOEK")){
						measurementList.add(measurement);
					}
				}else if(measurementName.equalsIgnoreCase("ELEMENT NO")){
					if(protocolName.equalsIgnoreCase("BEP_OMSCHR")){
						measurementList.add(measurement);
					}
				}else{
					measurementList.add(measurement);
				}
			}
		}
		
		List<Protocol> addedProtocols = new ArrayList<Protocol>();
		
		List<Measurement> addedMeasurements = new ArrayList<Measurement>();
		
		for( Protocol proto : protocolList){

			if(db.query(Protocol.class).eq(Protocol.NAME, proto.getName()).count() == 0){
				if(!addedProtocols.contains(proto)){

					addedProtocols.add(proto);
				}
			}
		}
		
		for(Measurement measure : measurementList){

			if(db.query(Measurement.class).eq(Measurement.NAME, measure.getName()).count() == 0){

				if(!addedMeasurements.contains(measure)){
					addedMeasurements.add(measure);
				}
			}

		}
		db.add(measurementList);
		
		for(Protocol p : addedProtocols){
			
			if(protocolMeasurement.containsKey(p.getName())){
				List<String> featureNames = protocolMeasurement.get(p.getName());
				List<Measurement> measList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureNames));
				List<Integer> measIdList = new ArrayList<Integer>();
				for (Measurement m : measList) {
					measIdList.add(m.getId());
				}
				p.setFeatures_Id(measIdList);

			}
		}
		
		db.add(protocolList);
	}
	
	@Override
	public void reload(Database db)	{
	}


	public void setStatus(String status) {
		Status = status;
	}


	public String getStatus() {
		return Status;
	}
}