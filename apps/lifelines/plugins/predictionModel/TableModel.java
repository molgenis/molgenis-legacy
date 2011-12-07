package plugins.predictionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;

import jxl.Sheet;

public class TableModel {

	private Database db;
	
	public static final String MEASUREMENT_DATATYPE = "measurement_datatype";

	public static final String MEASUREMENT_DESCRIPTION = "measurement_description";

	public static final String PROTOCOL_FEATURE = "Protocol_Feature";

	public static final String MEASUREMENT_STRING = "string";
	
	public static final String MEASUREMENT_INT = "int";
	
	public static final String MEASUREMENT_DATE = "datetime";
	
	public static final String MEASUREMENT_CATEGORICAL = "categorical";
	
	public static final String MEASUREMENT_CODE = "code";

	public static final String IGNORE = "IGNORE";

	public static final String CATEGORY = "Category";

	public static final String MEASUREMENT_CATEGORY = "Measurement_Category";

	public static final String CODE_STRING = "Code_String";
	
	public static String PANEL = "Panel";
	
	public static String PROTOCOLAPPLICATION = "ProtocolApplication";
	
	public static String MEASUREMENT = "Measurement";
	
	public static String OBSERVERDVALUE = "ObservedValue";
	
	public static String PROTOCOL = "Protocol";
	
	public int columnSize = 0;
	
	public List<TableField> configuration;
	
	public Prediction prediction;
	
	public TableField field;
	
	private int observationTarget = -1;

	private int protocolIndex = -1;

	private int featureIndex = -1;

	private String MeasurementDataType = "string";
	
	private HashMap<String, String> InputToMolgenisDataType = new HashMap<String, String>();
	
	private HashMap<Integer, Integer> protocolSubprotocolIndex = new HashMap<Integer, Integer>();

	private HashMap<Integer, Integer> protocolSubProtocol = new HashMap<Integer, Integer>();

	private String protocolName = null;

	private String subProtocolName = null;

	private int unitsIndex = -1;

	private int temporalIndex = -1;

	private int measurementIndex = -1;

	private int categoryIndex = -1;

	private int missingCategoryIndex = -1;

	private List<Integer> missingCategoryList = new ArrayList<Integer>();

	private HashMap<Integer, List<Integer>> categoryAddToMeasurement = new HashMap<Integer, List<Integer>>();
	
	//OntologyTerm Parameters
	private int ontologyTermIndex = -1;

	private int ontologyNameIndex = -1;

	private int ontologyTermAccessIndex = -1;

	private int ontologyDefinitionIndex = -1;

	private int ontologyTermPathIndex = -1;

	public TableModel(int i,  Database db) {
		this.db = db;
		this.columnSize = i;
		configuration = new ArrayList<TableField>();
	}
	

	public void addField(Prediction prediction, String ClassType, int[] columnList, Boolean Vertical) {
		
		for(int i = 0; i < columnList.length; i++){
			addField(prediction, ClassType, columnList[i], Vertical);
		}
		
	}
	
	
	public void addField(Prediction prediction, String ClassType, int columnIndex, Boolean Vertical){
		
		TableField field;
		
		if(ClassType.equalsIgnoreCase("Measurement")){
			
			Measurement measurement = new Measurement();
			
			field = new TableField(measurement, ClassType, columnIndex, Vertical);
			
			configuration.add(field);
		
		}else if(ClassType.equalsIgnoreCase("Panel")){
		
			Panel panel = new Panel();
			
			field = new TableField(panel, ClassType, columnIndex, Vertical);
			
			configuration.add(field);
		
		}else if(ClassType.equalsIgnoreCase("ObservedValue")){
		
			ObservedValue observedValue = new ObservedValue();
			
			field = new TableField(observedValue, ClassType, columnIndex, Vertical);
			
			configuration.add(field);
			
		}else if(ClassType.equalsIgnoreCase("Protocol")){
		
			Protocol protocol = new Protocol();
			
			field = new TableField(protocol, ClassType, columnIndex, Vertical);
			
			configuration.add(field);
		
		}else if(ClassType.equalsIgnoreCase("Category")){
		
			Category category = new Category();
			
			field = new TableField(category, ClassType, columnIndex, Vertical);
			
			configuration.add(field);
		
		}else{
			
			field = new TableField(ClassType, columnIndex, Vertical);
			
			configuration.add(field);
		}
		
	}
	
	public TableField getField(int columnIndex){
		return configuration.get(columnIndex);
	}
	
	public List<TableField> getConfiguration(){
		return configuration;
	}
	
	public void convertIntoPheno(Sheet sheet){
		
		int row = sheet.getRows();

		int column = sheet.getColumns();
		
		List<Panel> panelList = new ArrayList<Panel> ();
		
		List<Protocol> protocolList = new ArrayList<Protocol> ();
		
		List<Measurement> measurementList = new ArrayList<Measurement> ();
		
		List<ObservedValue> observedValueList = new ArrayList<ObservedValue>();
		
		List<Category> categoryList = new ArrayList<Category>();
		
		List<OntologyTerm> ontologyTermList = new ArrayList<OntologyTerm>();
		
		HashMap<String, List> protocolMeasurementList = new HashMap<String, List>();
		
		HashMap<String, List> measurementCategoryList = new HashMap<String, List>();
		
		HashMap<String, List> protocolSubprotocolList = new HashMap<String, List>();
		
		HashMap<String, String> protocolSubprotocolName = new HashMap<String, String>();
		
		Measurement measurement;
		
		Protocol protocol;
		
		ObservedValue observedValue;
		
		Category category;
		
		Panel panel;
		
		for(int i = 0; i < row; i++){
			
			measurement = new Measurement();
			panel = new Panel ();
			protocol = new Protocol();
			category = new Category();
			String protocolWithFeature = null;
			String featureWithProtocol = null;
			
			for(int j = 0; j < column; j++){
				
				String cellValue = sheet.getCell(j, i).getContents().replaceAll("'", "").trim();

				System.out.println("The cell value is " + cellValue);
				System.out.println("The size is =========== " + configuration.size());
				TableField field = configuration.get(j);
				
				if(field.getVertical()){
					
					if(field.ClassType.equalsIgnoreCase("IGNORE")){
						System.out.println("The row " + i + " and column " + j + " are ignored!");
					}else if(field.ClassType.equalsIgnoreCase("Measurement") && i != 0){
						
						measurement.setName(cellValue);
						
						measurementList.add(measurement);
						
						if(featureIndex == j){
							featureWithProtocol = cellValue;
						}
						
					} else if(field.ClassType.equalsIgnoreCase("Panel") && i != 0){
						
						panel.setName(cellValue);
						
						panelList.add(panel);
						
					}else if(field.ClassType.equalsIgnoreCase("Protocol") && i != 0){
						
						protocol.setName(cellValue);
						
						if(protocolSubProtocol.size() != 0 ){
							
							if(protocolSubProtocol.containsKey(j)){
								protocolName = cellValue;
								Integer subProtocolIndex = protocolSubProtocol.get(j);
								subProtocolName = sheet.getCell(subProtocolIndex.intValue(), i).getContents().replaceAll("'", "").trim();
								
								if(protocolName != null & subProtocolName != null){
									
									if(protocolSubprotocolList.containsKey(protocolName)){
										List<String> subProtocolList = protocolSubprotocolList.get(protocolName);
										if(!subProtocolList.contains(subProtocolName)){
											subProtocolList.add(subProtocolName);
											protocolSubprotocolList.put(protocolName, subProtocolList);
										}
									}else{
										List<String> subProtocolList = new ArrayList<String>();
										subProtocolList.add(subProtocolName);
										protocolSubprotocolList.put(protocolName, subProtocolList);
									}
									protocolName = null;
									subProtocolName = null;
								}
							}
						}
						
						if(!protocolList.contains(protocol))
							protocolList.add(protocol);
					
						if(j == protocolIndex){
							protocolWithFeature = cellValue;
						}
						protocol = new Protocol();
						
					}else if(field.ClassType.equalsIgnoreCase("Category") && i != 0 && cellValue.length() > 0){
						
						String [] codeString = cellValue.split("\\|");
						
						for(int index = 0; index < codeString.length; index++){
							codeString[index] = codeString[index].trim();
						}
						
						for(int k = 0; k < codeString.length; k++){

							category = new Category();
							
							category.setName(codeString[k].replaceAll("'", ""));
							
							category.setCode_String(codeString[k]);
							
							category.setLabel(codeString[k]);
							
							category.setDescription(cellValue);
							
							if(missingCategoryList.contains(j)){
								category.setIsMissing(true);
							}
							
							if(!categoryList.contains(category))
								categoryList.add(category);
							
							if(measurementIndex != -1 && categoryAddToMeasurement.get(measurementIndex).contains(j))
								addCategoryToMeasurement(category, measurement, measurementCategoryList);
						}
						
					}else if(field.ClassType.equalsIgnoreCase(MEASUREMENT_DESCRIPTION) && i != 0){
						
						measurement.setDescription(cellValue);
					
					}else if(field.ClassType.equalsIgnoreCase(MEASUREMENT_DATATYPE) && i != 0){
						
						String datatype = adjustDataTypeValue(cellValue);
						if(!datatype.equals("string"))
							measurement.setDataType(datatype);
					}else if(field.ClassType.equalsIgnoreCase(CODE_STRING) && i != 0){
						
							category.setCode_String(cellValue);
					}
					
				}else{
					
					//The header is measurement!
					if(i == 0){
						
						if(field.ClassType.equalsIgnoreCase("Measurement")){
							
							measurement = new Measurement();
							
							measurement.setName(cellValue);
							
							measurementList.add(measurement);
						}
					//The rest of the column is observedValue!
					}else{
						
						if(!cellValue.equals("") && cellValue != null && observationTarget != -1){
							
							observedValue = new ObservedValue();
							
							String headerName = sheet.getCell(j, 0).getContents().replaceAll("'", "").trim();
							
							String targetName = sheet.getCell(observationTarget, i).getContents().replaceAll("'", "").trim();
							
							observedValue.setFeature_Name(headerName);
							
							observedValue.setTarget_Name(targetName);
							
							observedValue.setValue(cellValue);
							
							observedValueList.add(observedValue);
						}
					}
				}
				
				//This is for the measurement configuration
				if(unitsIndex == j){
					if(!cellValue.equals("")){
						measurement.setUnit_Name(cellValue);
						OntologyTerm ontologyTerm = new OntologyTerm();
						ontologyTerm.setName(cellValue);
						if(!ontologyTermList.contains(ontologyTerm))
							ontologyTermList.add(ontologyTerm);
					}
				}
				if(temporalIndex == j){
					
					if(cellValue.equalsIgnoreCase("yes")){
						measurement.setTemporal(true);
					}else if(cellValue.equalsIgnoreCase("no")){
						measurement.setTemporal(false);
					}
				}
			}
			
			if(i != 0 && protocolWithFeature != null && featureWithProtocol != null){
				
				if(protocolMeasurementList.containsKey(protocolWithFeature)){
					List<String> featureList = protocolMeasurementList.get(protocolWithFeature);
					if(!featureList.contains(featureWithProtocol)){
						featureList.add(featureWithProtocol);
						protocolMeasurementList.put(protocolWithFeature, featureList);
					}
				}else{
					List<String> featureList = new ArrayList<String>();
					featureList.add(featureWithProtocol);
					protocolMeasurementList.put(protocolWithFeature, featureList);
				}
				featureWithProtocol = null;
				protocolWithFeature = null;
				
			}
		}
		
		try {
			
			db.update(categoryList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME, Category.CODE_STRING, Category.INVESTIGATION_NAME);
			
			db.update(ontologyTermList, Database.DatabaseAction.ADD_IGNORE_EXISTING, OntologyTerm.NAME, OntologyTerm.ONTOLOGY_NAME);
			
			if(measurementCategoryList.size() != 0){
				
				for(Measurement m : measurementList){
					List<String> categoryNameList = measurementCategoryList.get(m.getName());
					if(categoryNameList != null){
						List<Category> categoryEntityList = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, categoryNameList));
						List<Integer> categoryIdList = new ArrayList<Integer>();
						for(Category c : categoryEntityList){
							categoryIdList.add(c.getId());
						}
						m.setCategories(categoryIdList);
					}
				}
			}
			
			db.update(measurementList, Database.DatabaseAction.ADD_UPDATE_EXISTING, Measurement.NAME, 
					Measurement.CATEGORIES_NAME, Measurement.DATATYPE, Measurement.INVESTIGATION_NAME);
			
			
			if(protocolMeasurementList.size() != 0){
				
				for(Protocol p : protocolList){
					List<String> featureList = protocolMeasurementList.get(p.getName());
					
					if(featureList != null){
						List<Measurement> featureEntityList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureList));
						List<Integer> featureIdList = new ArrayList<Integer>();
						for(Measurement m : featureEntityList){
							featureIdList.add(m.getId());
						}
						p.setFeatures_Id(featureIdList);
					}
				}
			}
			
			db.update(protocolList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME, Protocol.FEATURES_NAME, Protocol.SUBPROTOCOLS_NAME, Protocol.INVESTIGATION_NAME);
			
			List<Protocol> topProtocol = new ArrayList<Protocol>();
			
			if(protocolSubprotocolList.size() != 0){
				
				for(Protocol p : protocolList){
					
					List<String> subProtocolList = protocolSubprotocolList.get(p.getName());
					if(subProtocolList != null){
						List<Protocol> subProtocolEntityList = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, subProtocolList));
						List<Integer> subProtocolIdList = new ArrayList<Integer>();
						for(Protocol pro : subProtocolEntityList){
							subProtocolIdList.add(pro.getId());
						}
						
						p.setSubprotocols(subProtocolIdList);
						db.update(p);
					}
				}
			}
			db.update(panelList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Panel.NAME, Panel.INDIVIDUALS_NAME, Panel.INVESTIGATION_NAME);
			db.update(observedValueList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.VALUE, 
					ObservedValue.TARGET_NAME, ObservedValue.FEATURE_NAME, ObservedValue.INVESTIGATION_NAME);
			
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addCategoryToMeasurement(Category category, Measurement measurement, 
			HashMap<String, List> measurementCategoryList){


		String categoryName = category.getName();
		String measurementName = measurement.getName();

		if(categoryName.equalsIgnoreCase(measurementName)){
			category.setName(category.getName() + "_code");
		}

		if(categoryName != null && measurementName != null){
			if(measurementCategoryList.containsKey(measurementName)){
				List<String> categoryNameList = measurementCategoryList.get(measurementName);
				if(!categoryNameList.contains(categoryName)){
					categoryNameList.add(categoryName);
					measurementCategoryList.put(measurementName, categoryNameList);	
				}
			}else{
				List<String> categoryNameList = new ArrayList<String>();
				categoryNameList.add(categoryName);
				measurementCategoryList.put(measurementName, categoryNameList);
			}
		}

	}
	
	private String adjustDataTypeValue(String cellValue) {
		
		Set dataTypeSet = InputToMolgenisDataType.keySet();
		
		Iterator iterator = dataTypeSet.iterator();
		
		while(iterator.hasNext()){
			
			String keySet = (String) iterator.next();
			
			Pattern p = Pattern.compile(keySet);
			
			Matcher m = p.matcher(cellValue);
			
			if(m.find()){
				return InputToMolgenisDataType.get(keySet);
			}
		}
		return MeasurementDataType;
	}


	public void setDataType(String dataTypeInput, String molgenisDataType) {
		
		InputToMolgenisDataType.put(dataTypeInput, molgenisDataType);
	}


	public Entity nameToClass(String className, Database db) throws Exception{
		
		//String className = "Measurement";
		Class<? extends Entity> c = db.getClassForName(className);
		Entity e = c.newInstance();
		e.set(ObservationElement.NAME,"myname");
		e.set("dataType","int");
		return e;
	}

	public void setObservedValue (int Target, int Feature){
		
		TableField Subject = configuration.get(Target);
		
		TableField Object = configuration.get(Feature);
		
		Subject.setRelation(Subject, Object);
	}

	public void setTarget(int i) {
		this.observationTarget = i;
	}

	public void setProtocolFeatureRelation(int protocolIndex, int featureIndex, String protocolFeature) {
		
		this.protocolIndex = protocolIndex;
		this.featureIndex = featureIndex;
		protocolSubprotocolIndex.put(protocolIndex, featureIndex);
			
	}


	public void setMeasurementCategoryRelation(int measurementIndex, Integer categoryIndex) {
		
		this.measurementIndex = measurementIndex;
		this.categoryIndex = categoryIndex;
		
		if(categoryAddToMeasurement.containsKey(measurementIndex)){
			List<Integer> categoryIndexList = categoryAddToMeasurement.get(measurementIndex);
			categoryIndexList.add(categoryIndex);
			categoryAddToMeasurement.put(measurementIndex, categoryIndexList);
		}else{
			List<Integer> categoryIndexList = new ArrayList<Integer>();
			categoryIndexList.add(categoryIndex);
			categoryAddToMeasurement.put(measurementIndex, categoryIndexList);
		}
		
	}


	public void setSubProtocolRelation(int protocolIndex, int subProtocolIndex) {
		protocolSubProtocol.put(protocolIndex, subProtocolIndex);
	}


	public void measurementSetting(int measurementIndex, int unitsIndex, int temporalIndex, int[] categoryList) {
		this.measurementIndex = measurementIndex;
		this.unitsIndex = unitsIndex;
		this.temporalIndex = temporalIndex;
		
		for(int i = 0; i < categoryList.length; i++){
			setMeasurementCategoryRelation(measurementIndex, categoryList[i]);
		}
	}


	public void setMissingCategoryIndex(int missingCategoryIndex) {
		missingCategoryList.add(missingCategoryIndex);
	}

	//TODO need to be more flexible. What if there are more columns for OntologyTerm
	//have to make ontologyTerm a list
	public void setOntologyTerm(int ontologyTermIndex, int ontologyNameIndex, int ontologyTermAccessIndex
			, int ontologyDefinitionIndex, int ontologyTermPathIndex) {
		this.ontologyTermIndex = ontologyTermIndex;
		this.ontologyNameIndex = ontologyNameIndex;
		this.ontologyTermAccessIndex = ontologyTermAccessIndex;
		this.ontologyDefinitionIndex = ontologyDefinitionIndex;
		this.ontologyTermPathIndex = ontologyTermPathIndex;
	}
}
