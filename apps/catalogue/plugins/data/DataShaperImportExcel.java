package plugins.data;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.hamcrest.core.IsInstanceOf;
import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.FillMetadata;

import plugins.emptydb.emptyDatabase;


/*
 * This is an importer that is specific for that DataShaper Schema excel file.
 * 
 * 
 */

public class DataShaperImportExcel extends PluginModel<Entity>
{
	private String Status = "";

	private static final long serialVersionUID = 6149846107377048848L;
	
	//private ImporterModel importerModel = new ImporterModel();
	
	//private String NAME = "name";

	public DataShaperImportExcel(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_data_DataShaperImportExcel";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/DataShaperImportExcel.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("ImportDatashaperToPheno".equals(request.getAction())) {

			System.out.println("----------------->");

			System.out.println(db.query(Investigation.class).eq(Investigation.NAME, " DataShaper").count());

			Investigation inv = new Investigation();

			if(db.query(Investigation.class).eq(Investigation.NAME, "DataShaper").count() == 0){

				inv.setName("DataShaper");
				
				db.add(inv);
				
			}else{
				
				inv = db.find (Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, "DataShaper")).get(0);
			}
			
			loadDataFromExcel(db, request, inv);

		}

		if ("fillinDatabase".equals(request.getAction())) {

			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			Status = "The database is empty now";
		}

	}
	@SuppressWarnings("unchecked")
	public void loadDataFromExcel(Database db, Tuple request, Investigation inv) throws BiffException, IOException, DatabaseException{


		List<String> ProtocolFeatures = new ArrayList<String>();

		String protocolName = "";

		String themeName = "";

		String groupName = "";

		String measurementName = "";

		boolean MeasurementTemporal = false;

		List<ObservedValue> observedValues  = new ArrayList<ObservedValue>();

		HashMap<String, List> linkProtocolMeasurement = new HashMap<String, List>();

		HashMap<String, List> linkCodeMeasurement = new HashMap<String, List>();

		HashMap<String, String> linkUnitMeasurement = new HashMap<String, String>();

		HashMap<String, List> linkProtocolTheme = new HashMap<String, List>();

		HashMap<String, List> linkThemeGroup = new HashMap<String, List>();

		List<Measurement> addedMeasurements = new ArrayList<Measurement>();

		List<Protocol> addedProtocols = new ArrayList<Protocol>();

		List<Measurement> measurements = new ArrayList<Measurement>();

		List<Protocol> protocols = new ArrayList<Protocol>();

		List<Protocol> themes = new ArrayList<Protocol>();

		List<Protocol> groups = new ArrayList<Protocol>();

		List<OntologyTerm> ontologyTerms = new ArrayList<OntologyTerm>();

		List<Ontology> ontologies = new ArrayList<Ontology>();

		List<Category> codes = new ArrayList<Category>();

		Measurement mea;

		OntologyTerm ontology_Term;

		Ontology ontology;

		Protocol prot, theme, group;

		Category code;

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		File file = new File(tmpDir+ "/DataShaperExcel.xls"); 

		if (file.exists()) {

			System.out.println("The excel file is being imported, please be patient");

			this.setStatus("The excel file is being imported, please be patient");

			Workbook workbook = Workbook.getWorkbook(file); 

			Sheet sheet = workbook.getSheet(0); 

			System.out.println(sheet.getCell(0, 0).getContents());

			int row = sheet.getRows();

			int column = sheet.getColumns();

			Measurement headers [] = new Measurement[column];
			
			System.out.println(row);

			for(int i = 0; i < column; i++){
				Measurement headerMea = new Measurement();
				headerMea.setName(sheet.getCell(i, 0).getContents().replaceAll("'", ""));
				headerMea.setInvestigation(inv);
				headers[i] = headerMea;
				measurements.add(headerMea);
			}
			
			for (int i = 1; i < row - 1; i++){

				mea = new Measurement();

				ontology_Term = new OntologyTerm();

				ontology = new Ontology();

				//code = new Category();

				prot = new Protocol();

				theme = new Protocol();

				group = new Protocol();
				
				group.setInvestigation(inv);
				
				theme.setInvestigation(inv);
				
				prot.setInvestigation(inv);
				
				mea.setInvestigation(inv);
				
				boolean WhetherDoubleCheck = false;
				
				for(int j = 0; j < column; j++){
					
					if (j==0) { //group is also a protocol 

						groupName = sheet.getCell(j, i).getContents().replaceAll("'", "");

						group.setName(groupName);

						if(!linkThemeGroup.containsKey(groupName)){

							List groupNames = new ArrayList<String>();

							linkThemeGroup.put(groupName, groupNames);
						}

						if(!groups.contains(group))
							groups.add(group);

					}else if (j==1) { //theme is also a protocol 

						themeName = sheet.getCell(j, i).getContents().replaceAll("'", "");

						theme.setName(themeName);

						if(!linkProtocolTheme.containsKey(themeName)){

							List themeNames = new ArrayList<String>();

							linkProtocolTheme.put(themeName, themeNames);
						}

						if(!themes.contains(theme))
							themes.add(theme);

						List<String> gourpHolder = linkThemeGroup.get(groupName);

						gourpHolder.add(themeName);

						linkThemeGroup.put(groupName, gourpHolder);

					}else if(j == 2){

						protocolName = sheet.getCell(j, i).getContents().replaceAll("'", "");

						if(!linkProtocolMeasurement.containsKey(protocolName)){
							ProtocolFeatures = new ArrayList<String>();
							linkProtocolMeasurement.put(protocolName, ProtocolFeatures);
						}

						if(!protocols.contains(prot))
							protocols.add(prot);

						List<String> tempHolder = linkProtocolTheme.get(themeName);

						tempHolder.add(protocolName);

						linkProtocolTheme.put(themeName, tempHolder);

						prot.setName(sheet.getCell(j, i).getContents().replaceAll("'", ""));

					}else if(j == 3){

						measurementName = sheet.getCell(j, i).getContents().replaceAll("'", "");

						ontology_Term.setName(measurementName);

						mea.setName(measurementName);
						
						List<String> ontologyReference = new ArrayList<String>();
						
						ontologyReference.add(measurementName);
						
						//mea.setOntologyReference_Name(ontologyReference);
						
						mea.setInvestigation(inv);

						List<String> temporaryHolder = linkProtocolMeasurement.get(protocolName);

						if(!temporaryHolder.contains(protocolName)){

							temporaryHolder.add(measurementName);

							linkProtocolMeasurement.put(protocolName, temporaryHolder);
						}

					}else if (j == 4){

						mea.setDescription(sheet.getCell(j, i).getContents());

					}else if (j==5) {

						OntologyTerm unit = new OntologyTerm();
						String unitName = sheet.getCell(j,i).getContents().replace(" ", "_").replace("/", "_");
						unit.setName(unitName);

						if (unitName !="" && !ontologyTerms.contains(unit)) {
							ontologyTerms.add(unit);
							linkUnitMeasurement.put(measurementName, unitName);
						}

					}else  if( j == 6) { //is repeatable refers to the measurement  Erik says it's the temporal field of measurement entity in pheno model . 
						
						String tmp = sheet.getCell(j,i).getContents();

						if (tmp == "No") MeasurementTemporal = false;
						else if (tmp =="Yes") MeasurementTemporal = true;

						mea.setTemporal(MeasurementTemporal);


					}else if (j == 7) {

						String variableURIName = sheet.getCell(j,i).getContents();
						ontology_Term.setTermPath(variableURIName);
						String array[] = variableURIName.split("#");
						String ontologyName = array[0];
						ontology.setName(ontologyName); //TODO don`t konw the name yet
						ontology.setOntologyURI(ontologyName);
						ontology_Term.setOntology_Name(ontologyName);
					}

					else if(j == 8 || j == 9){
						
						if(sheet.getCell(j, i).getContents().length() > 0 && sheet.getCell(j, i).getContents() != null){

							String [] codeString = sheet.getCell(j, i).getContents().split("\\|");

							for(int index = 0; index < codeString.length; index++){
								codeString[index] = codeString[index].trim();
							}

							for(int k = 0; k < codeString.length; k++){

								code = new Category();
								
								code.setInvestigation(inv);
								
								code.setName(codeString[k].replaceAll("'", ""));
								
								code.setCode_String(codeString[k]);
								
								code.setLabel(codeString[k]);
								
								code.setDescription(codeString[k]);
								
								if(codeString[k].equalsIgnoreCase(measurementName)){
									code.setName(codeString[k].replaceAll("'", "") + "_code");
								}
								
								if(linkCodeMeasurement.containsKey(measurementName)){
									List<String> categories = linkCodeMeasurement.get(measurementName);
									linkCodeMeasurement.put(measurementName, categories);
									if(!categories.contains(codeString[k])){
										categories.add(codeString[k]);
										
									}
								}else{
									List<String> categories = new ArrayList<String>();
									categories.add(codeString[k]);
									linkCodeMeasurement.put(measurementName, categories);
								}
								
								
								if(j == 9)
									code.setIsMissing(true);
								
								if(!codes.contains(code))
									codes.add(code);
							
							}
						}

					}else if(j == 18){
						
						String format = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(format.equalsIgnoreCase("Categorical")){
							mea.setDataType("code");
						}
						if(format.equalsIgnoreCase("Open")){
							WhetherDoubleCheck = true;
						}
						
					}else if(j == 23){
						
						String Target = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(!Target.equals("") && Target != null){
							if(Target.equalsIgnoreCase("Participant")){
								mea.setTargettypeAllowedForRelation_ClassName("org.molgenis.pheno.Individual");
							}
						}
						
					}else if(j == 25){
						
						String type = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(WhetherDoubleCheck){
							
							if(type.equalsIgnoreCase("Integer")){
								mea.setDataType("int");
							}
							if(type.equalsIgnoreCase("Text")){
								mea.setDataType("string");
							}
							if(type.equalsIgnoreCase("Decimal")){
								mea.setDataType("decimal");
							}
							if(type.equalsIgnoreCase("Date")){
								mea.setDataType("datetime");
							}
						}
					}else{
						
						String cellValue = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(!cellValue.equals("") && cellValue != null){
							
							ObservedValue ob = new ObservedValue();

							ob.setTarget_Name(mea.getName());
							ob.setFeature_Name(headers[j].getName());
							ob.setValue(cellValue);
							ob.setInvestigation(inv);
							if(!observedValues.contains(ob))
								observedValues.add(ob);
						}
					}
				}
				
				if(!measurements.contains(mea))
					measurements.add(mea);

				if(!ontologyTerms.contains(ontology_Term))
					ontologyTerms.add(ontology_Term);

				if(!ontologies.contains(ontology))
					ontologies.add(ontology);
			}
			
			
			try {

				db.update(ontologies, DatabaseAction.ADD_IGNORE_EXISTING, Ontology.NAME);
				
				db.update(ontologyTerms, DatabaseAction.ADD_IGNORE_EXISTING, OntologyTerm.NAME);
				
				db.update(codes, DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME, Category.INVESTIGATION_NAME);
				
				for (Measurement m: addedMeasurements) {

					List<String> categoryNames = linkCodeMeasurement.get(m.getName());
					
					if(categoryNames != null){
						
						List<Category> measList = db.find(Category.class, new QueryRule(Category.LABEL, Operator.IN, categoryNames));
						
						List<Integer> CategoryIdList = new ArrayList<Integer>();
						
						for(Category c : measList){
							CategoryIdList.add(c.getId());
						}
						m.setCategories_Id(CategoryIdList);
					}
				}
				
				for (Measurement m: addedMeasurements) {

					String tmp = linkUnitMeasurement.get(m.getName());

					List<String> unitHolder = new ArrayList<String>();

					unitHolder.add(tmp);

					List<OntologyTerm> ontologyTermsList = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.IN, unitHolder));

					for (OntologyTerm ot: ontologyTermsList) {
						m.setUnit_Id(ot.getId());
					}
				}
				
				db.update(measurements, DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME, Measurement.INVESTIGATION_NAME);
				
				// TEMPORARY FIX FOR MREF RESOLVE FOREIGN KEYS BUG
				for (Protocol p : protocols) {

					if(linkProtocolMeasurement.containsKey(p.getName())){
						List<String> featureNames = linkProtocolMeasurement.get(p.getName());
						List<Measurement> measList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureNames));
						List<Integer> measIdList = new ArrayList<Integer>();
						for (Measurement m : measList) {
							measIdList.add(m.getId());
						}
						p.setFeatures_Id(measIdList);

					}
				}

				db.update(protocols, DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME, Protocol.INVESTIGATION_NAME);
				
				for (Protocol p : themes) {

					if(linkProtocolTheme.containsKey(p.getName())){

						List<String> subProtocolNames = linkProtocolTheme.get(p.getName());
						List<Protocol> subProtocols = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, subProtocolNames));
						List<Integer> subProtocolsId = new ArrayList<Integer>();
						for(Protocol subP : subProtocols){
							subProtocolsId.add(subP.getId());
						}
						p.setSubprotocols_Id(subProtocolsId);
					}
				}

				db.update(themes, DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME, Protocol.INVESTIGATION_NAME);
				
				for (Protocol p : groups) {

					if(linkThemeGroup.containsKey(p.getName())){

						List<String> subProtocolNames = linkThemeGroup.get(p.getName());
						List<Protocol> subProtocols = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, subProtocolNames));
						List<Integer> subProtocolsId = new ArrayList<Integer>();
						for(Protocol subP : subProtocols){
							subProtocolsId.add(subP.getId());
						}
						p.setSubprotocols_Id(subProtocolsId);
					}
				}
				
				
				db.update(groups, DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME, Protocol.INVESTIGATION_NAME);
				
				db.update(observedValues, DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.VALUE, ObservedValue.INVESTIGATION_NAME, 
						ObservedValue.TARGET_NAME, ObservedValue.FEATURE_NAME);
				
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("The file" + tmpDir + " was imported successfully");
			this.setStatus("The file" + tmpDir + " was imported successfully");
		} else {
			System.out.println("The excel file should be located here :"+ tmpDir + " and the name of the file should be DataShaperExcel.xls");
			this.setStatus("The excel file should be located here :"+ tmpDir + " and the name of the file should be DataShaperExcel.xls");

		}

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