package plugins.GenericImporter;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;

import org.molgenis.compute.ComputeProtocol;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
//import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
//import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
//import org.openqa.jetty.util.Observed;
//import org.springframework.validation.DataBinder;

//import com.googlecode.charts4j.Data;

import app.DatabaseFactory;

public class TableModel {

	private Database db;

	public int columnSize = 0;

	public List<TableField> configuration;

	public TableField field;

	private int observationTarget = -1;

	private String MeasurementDataType = "Not Matching";

	private HashMap<String, String> InputToMolgenisDataType = new HashMap<String, String>();

	private HashMap<Integer, TableField> columnIndexToTableField = new HashMap<Integer, TableField>();

	private List<Integer> missingCategoryList = new ArrayList<Integer>();

	private HashMap<Integer, List<Integer>> relationIndex = new HashMap<Integer, List<Integer>>();

	private String investigationName = null;

	private String excelDirection = "UploadFileByColumn";

	private Measurement displayNameMeasurement = null;

	private HashMap<String, String> checkExistingMeasurementsInDB = new HashMap<String, String>();

	private HashMap<String, String> checkExistingEntitesInDB = new HashMap<String, String>();

	public TableModel(int i,  Database db) {
		this.db = db;
		this.columnSize = i;
		configuration = new ArrayList<TableField>();
	}

	public void addField(String classType, String fieldName, int[] columnList, Boolean Vertical) 
	{
		this.addField(classType, fieldName, columnList, Vertical, new SimpleTuple());
	}

	public void addField(String ClassType, String fieldName, int[] columnList, Boolean Vertical, Tuple defaults) 
	{

		for(int i = 0; i < columnList.length; i++){
			this.addField( ClassType, fieldName, columnList[i], Vertical, defaults, -1);
		}
	}

	public void addField(String ClassType, String fieldName, boolean Vertical, int dependedIndex, int... columnIndexes) 
	{

		List<Integer> columnList = new ArrayList<Integer>();

		for(int i = 0; i < columnIndexes.length; i++)
		{

			if(columnIndexToTableField.containsKey(columnIndexes[i]))
			{
				columnIndexToTableField.get(columnIndexes[i]).setDependentColumnIndex(dependedIndex);
				columnIndexToTableField.get(columnIndexes[i]).setRelation(fieldName);

			}else{

				this.addField(ClassType, fieldName, columnIndexes[i], Vertical, new SimpleTuple(), dependedIndex);
				columnIndexToTableField.get(columnIndexes[i]).setRelation(fieldName);
			}
			columnList.add(columnIndexes[i]);
		}
		relationIndex.put(dependedIndex, columnList);
	}

	public void addField(String ClassType, String fieldName, int columnIndex, Boolean Vertical) 
	{
		this.addField( ClassType, fieldName, columnIndex, Vertical, new SimpleTuple(), -1);
	}

	public void addField(String ClassType, String fieldName, int columnIndex,
			boolean Vertical, int... dependentColumnIndex) 
	{

		this.addField(ClassType, fieldName, columnIndex, Vertical, new SimpleTuple(), dependentColumnIndex);

	}

	public void addField(String ClassType, String fieldName, int columnIndex,
			boolean Vertical, Tuple defaults) 
	{
		this.addField(ClassType, fieldName, columnIndex, Vertical, defaults, -1);

	}

	public void addField(String ClassType, String fieldName, int[] coHeaders,
			int targetIndex, boolean Vertical) 
	{
		observationTarget = targetIndex;
		this.addField(ClassType, fieldName, coHeaders, Vertical, new SimpleTuple());
		observationTarget = -1;
	}

	public void addField(String ClassType, String fieldName, int columnIndex, Boolean Vertical, Tuple defaults, int... dependentColumnIndex)
	{

		try {					
			//create a tableField that will take care of loading columnIndex into 'name' property
			field = new TableField(ClassType, fieldName, columnIndex, Vertical, defaults, dependentColumnIndex);
			//add to the parser configuration
			configuration.add(field);

			columnIndexToTableField.put(columnIndex, field);

			if(observationTarget != -1){
				field.setObservationTarget(observationTarget);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public TableField getField(int columnIndex)
	{
		return configuration.get(columnIndex);
	}

	public List<TableField> getConfiguration()
	{
		return configuration;
	}

	public void convertIntoPheno(Sheet[] sheets, int startingRowIndex, boolean multipleSheets) throws DatabaseException
	{
		List<Measurement> headerMeasurements = new ArrayList<Measurement>();
		List<InvestigationElement> measurementList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> categoryList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> protocolList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> observationTargetList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> computeProtocolList = new ArrayList<InvestigationElement>();
		List<ObservedValue> observedValueList = new ArrayList<ObservedValue>();
		List<OntologyTerm> ontologyTermList = new ArrayList<OntologyTerm>();

		int sheetSize = sheets.length;

		if(multipleSheets == true){
			sheetSize = 1;
		}

		try{
			for(int sheetIndex = 0; sheetIndex < sheetSize; sheetIndex++){


				Sheet sheet = sheets[sheetIndex];

				int row = sheet.getRows();

				int column = sheet.getColumns();

				HashMap<String, OntologyTerm> ontologyTermOfList = new HashMap<String, OntologyTerm>();

				if(excelDirection.equals("UploadFileByRow"))
				{
					row = sheet.getColumns();
					column = sheet.getRows();
				}



				//three dimensional matrix of<colIndex, rowIndex, valueIndex>
				//third dimension of valueIndex is to deal with multiple values in one cell
				//we made colIndex key because not all colIndexes are used
				Map<Integer,List<List<InvestigationElement>>> colValues = new LinkedHashMap<Integer,List<List<InvestigationElement>>>();
				Map<Integer,Map<String, List<InvestigationElement>>> existingValues = new LinkedHashMap<Integer, Map<String, List<InvestigationElement>>>();
				Map<String, Map<String, List<InvestigationElement>>> existingValuesForClassType = new LinkedHashMap<String, Map<String,List<InvestigationElement>>>();


				for(int rowIndex = 0; (rowIndex + startingRowIndex) < row; rowIndex++){

					for(int colIndex = 0; colIndex < column; colIndex++){

						String cellValue;

						if(excelDirection.equals("UploadFileByRow"))
							cellValue = sheet.getCell(rowIndex, colIndex).getContents().replaceAll("[%#$^&л@аде']", "").trim();
						else
							cellValue = sheet.getCell(colIndex, rowIndex + startingRowIndex).getContents().replaceAll("[%#$^&л@аде']", "").trim();
						if(cellValue.equalsIgnoreCase("CHFYETIOL")){
							System.out.println();
						}
						//					System.out.println("The cell value is " + cellValue);
						//					System.out.println("The size is =========== " + configuration.size());



						TableField field = columnIndexToTableField.get(colIndex);

						if(columnIndexToTableField.get(colIndex) != null && !columnIndexToTableField.get(colIndex).getClassType().equals("NULL")){

							if(columnIndexToTableField.get(colIndex).getVertical() && rowIndex != 0){

								if(!existingValuesForClassType.containsKey(field.getClassType() + field.getFieldName())){
									Map<String, List<InvestigationElement>> tempHolder = new LinkedHashMap<String, List<InvestigationElement>>();
									existingValuesForClassType.put(field.getClassType() + field.getFieldName(), tempHolder);
								}

								//								//Keep track of the entities
								//								if(!existingValues.containsKey(colIndex))
								//								{
								//									Map<String, List<InvestigationElement>> tempHolder = new LinkedHashMap<String, List<InvestigationElement>>();
								//									existingValues.put(colIndex, tempHolder);
								//								}

								if(existingValuesForClassType.get(field.getClassType() + field.getFieldName()).containsKey(cellValue)){

									//check colIndex: if there is already a list for colIndex
									if(colValues.get(colIndex) == null)
									{
										colValues.put(colIndex, new ArrayList<List<InvestigationElement>>());
									}

									colValues.get(colIndex).add(new ArrayList<InvestigationElement>());

									colValues.get(colIndex).get(rowIndex - 1).addAll(existingValuesForClassType.get(field.getClassType() + field.getFieldName()).get(cellValue));

								}

								//								if(existingValues.get(colIndex).containsKey(cellValue))
								//								{
								//									if(colValues.get(colIndex).size() != rowIndex)
								//									{
								//										colValues.get(colIndex).add(new ArrayList<InvestigationElement>());
								//									}
								//
								//									colValues.get(colIndex).get(rowIndex - 1).addAll(existingValues.get(colIndex).get(cellValue));
								//
								//
								//								}
								else{
									//we split on multivalue
									String[] multiValue = cellValue.split(field.getValueSplitter());

									for(int valueIndex = 0; valueIndex < multiValue.length; valueIndex++)
									{
										//If the fieldName is 'name', added as a new entity
										if(field.getFieldName().equalsIgnoreCase("NAME")){

											String value = multiValue[valueIndex];

											InvestigationElement entity = null;

											//check colIndex: if there is already a list for colIndex
											if(colValues.get(colIndex) == null)
											{
												colValues.put(colIndex, new ArrayList<List<InvestigationElement>>());
											}
											//check rowIndex: if there is already a list values
											if(colValues.get(colIndex).size() != rowIndex)
											{
												//create a list for our values (to deal with multivalue)
												colValues.get(colIndex).add(new ArrayList<InvestigationElement>());

											}

											//check valueIndex: if there is already a value 
											//TODO Chao`s comment: should be multiValue.length instead of rowIndex
											if(colValues.get(colIndex).get(rowIndex - 1).size() != multiValue.length)
											{
												//create the entity
												entity = (InvestigationElement) DatabaseFactory.create().getClassForName(field.getClassType()).newInstance();
											}


											if(!value.equalsIgnoreCase(""))
											{

												if(field.getClassType().equals(Category.class.getSimpleName()))
												{

													//Category entity couldn`t have empty property in name, description, code_string, label
													//therefore it`s separated from other entites.
													String categoryName = value;

													if(value.split("=").length > 1)
													{
														categoryName = value.split("=")[1].trim();
													}

													entity.set(Category.NAME, categoryName);
													entity.set(Category.DESCRIPTION, value);
													entity.set(Category.CODE_STRING, value);
													entity.set(Category.LABEL, value);
													if(field.getDefaults().getString(Category.ISMISSING) != null)
														entity.set(Category.ISMISSING, field.getDefaults().getString(Category.ISMISSING));

												}else{
													//set the field as specified in getFieldName() = 'name' or 'missing' or 'dataType', etc
													entity.set(field.getFieldName(), value);
												}

												if(investigationName != null)
													entity.set("Investigation_name", investigationName);

												colValues.get(colIndex).get(rowIndex - 1).add(entity);

												//field.setEntity(entity);
											}
										}
									}
								}

								if(field.getDependentColumnIndex()[0] != -1 && !cellValue.equals(""))
								{

									for(int index = 0; index < field.getDependentColumnIndex().length; index++)
									{

										int dependentColumn = field.getDependentColumnIndex()[index];

										TableField dependendField = columnIndexToTableField.get(dependentColumn);

										//InvestigationElement addingPropertyToEntity = dependendField.getEntity();

										int existingRow = rowIndex;

										InvestigationElement addingPropertyToEntity = null;

										while(colValues.get(dependentColumn).get(existingRow - 1).size() == 0){

											existingRow--;
										}

										addingPropertyToEntity = colValues.get(dependentColumn).get(existingRow - 1).get(0);

										//									InvestigationElement addingPropertyToEntity = colValues.get(dependentColumn).get(rowIndex - 1).get(0);

										cellValue = cellValue.replaceAll("[%#$^&л@аде]", "");

										String multipleValues[] = cellValue.split(dependendField.getValueSplitter());

										List<Object> values = new ArrayList<Object>();

										if(field.getClassType().equals(Category.class.getSimpleName()))
										{

											for(int i = 0; i < multipleValues.length; i++)
											{

												String categoryCodeString = multipleValues[i];

												if(categoryCodeString.split("=").length > 1)
												{	
													multipleValues[i] = categoryCodeString.split("=")[1];
												}

												values.add(multipleValues[i].trim());

											}
										}else{

											for(int i = 0; i < multipleValues.length; i++){
												values.add(multipleValues[i].trim());
											}
										}

										//Due to using generic method get() property of the Pheno Entity, so we don`t know which Object data
										//the field would be. We need to check the field type first. It could be list, boolean, string
										if(addingPropertyToEntity.get(field.getRelationString()) != null)
										{
											if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(ArrayList.class))
											{
												List<String> previousProperties = (List<String>) addingPropertyToEntity.get(field.getRelationString());

												if(previousProperties != null && previousProperties.size() > 0)
												{
													for(String newValue : previousProperties)
													{
														if(!values.contains(newValue))
														{
															values.add(newValue);
														}
													}
												}

											}else if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(Boolean.class))
											{

												values.clear();

												if(field.getRelationString().equalsIgnoreCase(Measurement.TEMPORAL))
												{
													if(cellValue.equalsIgnoreCase("yes"))
													{
														values.add(true);
													}else{
														values.add(false);
													}

												}else{
													if(cellValue.equalsIgnoreCase("yes"))
														values.add(true);
												}

											}else if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(String.class))
											{

												values.clear();
												values.add(addingPropertyToEntity.get(field.getRelationString()));
											}

											if(field.getRelationString().equals(Measurement.DATATYPE))
											{

												String dataType = adjustDataTypeValue(cellValue);

												if(!dataType.equals(MeasurementDataType))
												{
													values.clear();
													values.add(dataType);
												}
											}
										}

										if(field.getRelationString().equals(Measurement.UNIT_NAME))
										{

											for(int i = 0; i < multipleValues.length; i++)
											{

												if(!multipleValues[i].equals(""))
												{

													List<String> eachValues = new ArrayList<String>();

													eachValues.add(multipleValues[i]);

													List<OntologyTerm> existingOntologyTermList = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.IN, eachValues));

													if(existingOntologyTermList.size() == 0)
													{

														OntologyTerm unitOntologyTerm = new OntologyTerm();
														unitOntologyTerm.set(OntologyTerm.NAME, multipleValues[i]);

														if(!ontologyTermOfList.keySet().contains(unitOntologyTerm.getName()))
														{
															ontologyTermOfList.put(unitOntologyTerm.getName(), unitOntologyTerm);
														}
													}
												}
											}
										}

										if(values.size() == 1)
										{	
											if(!values.get(0).equals(""))
												addingPropertyToEntity.set(field.getRelationString(), values.get(0));
										}else{
											addingPropertyToEntity.set(field.getRelationString(), values);
										}
									}
								}


								//								if(!existingValues.get(colIndex).containsKey(cellValue) && colValues.containsKey(colIndex))
								//								{
								//									existingValues.get(colIndex).put(cellValue, colValues.get(colIndex).get(rowIndex - 1));
								//								}

								if(!existingValuesForClassType.get(field.getClassType() + field.getFieldName()).containsKey(cellValue) && colValues.containsKey(colIndex)){
									existingValuesForClassType.get(field.getClassType() + field.getFieldName()).put(cellValue, colValues.get(colIndex).get(rowIndex - 1));
								}

							}else{

								//The header is measurement!
								if(rowIndex == 0){

									if(field.getClassType().equalsIgnoreCase(ObservedValue.class.getSimpleName())){

										Measurement measurement = new Measurement();

										if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, cellValue)).size() != 0){

											Measurement measure = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, cellValue)).get(0);

											//TODO this needs to be re-written!
											//The measurement already exists but not "display name. 
											if(!cellValue.equals("display name")){

												if(!measure.getInvestigation_Name().equals(investigationName)){

													if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, "display name")).size() == 0){

														displayNameMeasurement = new Measurement();

														displayNameMeasurement.setName("display name");

														db.add(displayNameMeasurement);

													}

													ObservedValue ov = new ObservedValue();
													ov.setTarget_Name(cellValue + "_" +investigationName);
													ov.setFeature_Name("display name");
													ov.setValue(cellValue);
													ov.setInvestigation_Name(investigationName);
													cellValue += "_" + investigationName;
													checkExistingMeasurementsInDB.put(measure.getName().toLowerCase(), cellValue);
													observedValueList.add(ov);
												}
											}

										}
										if(cellValue.equals("display name")){

											if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, "display name")).size() == 0){

												displayNameMeasurement = new Measurement();

												displayNameMeasurement.setName("display name");

												db.add(displayNameMeasurement);

											}
										}else{
											measurement.setName(cellValue);
											headerMeasurements.add(measurement);

											if(investigationName != null)
												measurement.set("Investigation_name", investigationName);
										}
									}
									//The rest of the column is observedValue!
								}else{

									if(!cellValue.equals("") && cellValue != null && field.getObservationTarget() != -1){

										ObservedValue observedValue = new ObservedValue();

										String headerName = sheet.getCell(colIndex, startingRowIndex).getContents().replaceAll("[%#$^&л@аде']", "").trim();

										String targetName = sheet.getCell(field.getObservationTarget(), rowIndex + startingRowIndex).getContents().replaceAll("[%#$^&л@аде']", "").trim();

										//TODO: import measurements then import individual data. The measurement has to be consistent.

										if(checkExistingMeasurementsInDB.keySet().contains(headerName.toLowerCase())){
											headerName = checkExistingMeasurementsInDB.get(headerName.toLowerCase());
										}

										observedValue.setFeature_Name(headerName);

										TableField targetField = columnIndexToTableField.get(field.getObservationTarget());

										if(targetField.getClassType().equalsIgnoreCase(Measurement.class.getSimpleName())){

											if(!checkExistingMeasurementsInDB.containsKey(targetName)){

												if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, targetName)).size() > 0){
													checkExistingMeasurementsInDB.put(targetName, targetName + "_" + investigationName);

												}else{
													checkExistingMeasurementsInDB.put(targetName, targetName);

												}
											}

											observedValue.setTarget_Name(checkExistingMeasurementsInDB.get(targetName));

										}else{
											observedValue.setTarget_Name(targetName);
										}

										observedValue.setValue(cellValue);

										observedValueList.add(observedValue);

										if(investigationName != null)
											observedValue.set("Investigation_name", investigationName);
									}
								}
							}
						}
					}
				}


				//			List<InvestigationElement> measurementList = new ArrayList<InvestigationElement>();
				//			List<InvestigationElement> categoryList = new ArrayList<InvestigationElement>();
				//			List<InvestigationElement> protocolList = new ArrayList<InvestigationElement>();
				//			List<InvestigationElement> observationTargetList = new ArrayList<InvestigationElement>();
				//			List<InvestigationElement> computeProtocolList = new ArrayList<InvestigationElement>();
				//			

				for(Integer colIndex: colValues.keySet())
				{
					for(List<InvestigationElement> list: colValues.get(colIndex))
					{
						if(columnIndexToTableField.get(colIndex).getClassType().equals("Measurement"))
						{
							measurementList.addAll(list);
						}
						if(columnIndexToTableField.get(colIndex).getClassType().equals("Category"))
						{
							categoryList.addAll(list);
						}
						if(columnIndexToTableField.get(colIndex).getClassType().equals("Protocol"))
						{
							protocolList.addAll(list);
						}
						if(columnIndexToTableField.get(colIndex).getClassType().equals("ObservationTarget"))
						{
							observationTargetList.addAll(list);
						}
						if(columnIndexToTableField.get(colIndex).getClassType().equals("ComputeProtocol"))
						{
							computeProtocolList.addAll(list);
						}
					}

				}

				for(String ontologyTermName : ontologyTermOfList.keySet()){
					ontologyTermList.add(ontologyTermOfList.get(ontologyTermName));
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		try
		{

			db.beginTx();

			setInvestigation();

			HashMap<String, InvestigationElement> hashMapObservationTarget = removeDuplicates(observationTargetList);

			observationTargetList = new ArrayList<InvestigationElement>(hashMapObservationTarget.values());

			checkExistenceInDB(hashMapObservationTarget, ObservationTarget.class.getSimpleName());

			int iterationForObservationTarget = 1;

			while(observationTargetList.size() > iterationForObservationTarget * 5000){

				List<InvestigationElement> subListForObservationTarget = observationTargetList.subList((iterationForObservationTarget-1) * 5000, 
						iterationForObservationTarget * 5000);

				db.update(subListForObservationTarget, Database.DatabaseAction.ADD_IGNORE_EXISTING, 
						ObservationTarget.NAME, ObservationTarget.INVESTIGATION_NAME);
				
				iterationForObservationTarget++;
			}
			
			List<InvestigationElement> subListForObservationTarget = observationTargetList.subList((iterationForObservationTarget - 1)*5000, 
					observationTargetList.size()); 
			
			db.update(subListForObservationTarget, Database.DatabaseAction.ADD_IGNORE_EXISTING, 
					ObservationTarget.NAME, ObservationTarget.INVESTIGATION_NAME);
			
//			db.update(observationTargetList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservationTarget.NAME, ObservationTarget.INVESTIGATION_NAME);


			db.update(ontologyTermList, Database.DatabaseAction.ADD_IGNORE_EXISTING, OntologyTerm.NAME);


			HashMap<String, InvestigationElement> hashMapCategory = removeDuplicates(categoryList);

			categoryList = new ArrayList<InvestigationElement> (hashMapCategory.values());

			checkExistenceInDB(hashMapCategory, Category.class.getSimpleName());

			db.update(categoryList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME, Category.INVESTIGATION_NAME);


			HashMap<String, InvestigationElement> displayNameToMeasurement = new HashMap<String, InvestigationElement>();

			for(InvestigationElement m : measurementList){


				String measurementName = m.getName();

				//prevent the measurement with the same name from importing twice. Therefore check the database whether the 
				//measurement already existed, if there is, make a unique measurement name by combining it with investigation
				//name. such as weight_study_KORA. In order to display the measurement with its original name such as "weight"
				//a meta-measurement "display name" is created to describe the measurements as a label (measurement becomes observationElement
				//in Molgenis) such as weight_study_KORA (ObservationElement) --------->"weight" (value) <-----------diaplay name (measurement)
				if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).size() != 0){

					//if the existing measurement comes from the same investigation, that means they are the same measurement, don`t import
					Measurement existingMeasurement = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).get(0);

					if(!existingMeasurement.getInvestigation_Name().equals(investigationName)){

						//Resolving importing the measurements with the same name. In the different studies, measurements with the same name could
						//have different definitions, so we need to distinguish this kind of variables. Therefore a display name meta-measurement is created
						//to describe these measurements! For example, measurement weight-study-1 and weight-study-2 have the same value for the display name, "weight"
						if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, "display name")).size() == 0){

							displayNameMeasurement = new Measurement();

							displayNameMeasurement.setName("display name");

							db.add(displayNameMeasurement);

						}else{
							displayNameMeasurement = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, "display name")).get(0);
						}

						//else the measurement comes from different investigation, that means there are duplicated measurements, import with investigation name
						ObservedValue ob = new ObservedValue();
						ob.setValue(measurementName);
						measurementName += "_" + m.get(Measurement.INVESTIGATION_NAME);
						m.setName(measurementName);
						m.setInvestigation_Name(investigationName);
						ob.setTarget_Name(measurementName);
						ob.setFeature_Name(displayNameMeasurement.getName());
						ob.setInvestigation_Name(m.getInvestigation_Name());
						observedValueList.add(ob);
						displayNameToMeasurement.put(ob.getValue(), m);
					}
				}

				List<String> categories_name = (List<String>) m.get(Measurement.CATEGORIES_NAME);

				List<String> categories_new = new ArrayList<String>();

				for(String eachCategory : categories_name){

					if(checkExistingEntitesInDB.containsKey(eachCategory.toLowerCase())){
						categories_new.add(checkExistingEntitesInDB.get(eachCategory.toLowerCase()));
					}else{
						categories_new.add(eachCategory);
					}	
				}

				if(categories_new.size() > 0)
				{
					List<Category> categories = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, categories_new));
					List<Integer> categoryId = new ArrayList<Integer>();
					for(Category c : categories){

						if(m.get(Measurement.NAME).equals(c.getName())){
							c.setName(c.getName() + "_code");
							db.update(c);
						}
						categoryId.add(c.getId());
					}
					m.set(Measurement.CATEGORIES, categoryId);
				}
			}

			HashMap<String, InvestigationElement> hashMapMeasurement = removeDuplicates(measurementList);

			measurementList = new ArrayList<InvestigationElement> (hashMapMeasurement.values());

			checkExistenceInDB(hashMapMeasurement, Measurement.class.getSimpleName());

			db.update(measurementList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME, Measurement.INVESTIGATION_NAME);

			//Try to update measurements

			HashMap<String, InvestigationElement> hashMapProtocol = removeDuplicates(protocolList);

			protocolList = new ArrayList<InvestigationElement>(hashMapProtocol.values());

			HashMap<String, List<String>> subProtocolAndProtocol = new HashMap<String, List<String>>();

			//mref is not working for the name. We can`t do protocol.setFeatures_name(""). Therefore we need to 
			//add features in db first, afterwards we could use protocol.setFeatures_ID(). Mref for ID is working fine
			for(InvestigationElement p : protocolList)
			{
				List<String> feature_names = (List<String>) p.get(Protocol.FEATURES_NAME);

				List<String> features_new = new ArrayList<String>();

				for(String eachFeature : feature_names){

					if(checkExistingEntitesInDB.containsKey(eachFeature.toLowerCase())){
						features_new.add(checkExistingEntitesInDB.get(eachFeature.toLowerCase()));
					}else{
						features_new.add(eachFeature);
					}	
				}

				if(features_new.size() > 0)
				{
					List<Measurement> features = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, features_new));

					if(features.size() > 0)
					{
						List<Integer> featuresId = new ArrayList<Integer>();
						for(Measurement m : features){

							if(displayNameToMeasurement.keySet().contains(m.getName())){

								if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, m.getName())).size() > 0){

									m = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, m.getName())).get(0);

									if(!featuresId.contains(m.getId()))
										featuresId.add(m.getId());
								}

							}else{
								if(!featuresId.contains(m.getId()))
									featuresId.add(m.getId());
							}
						}
						p.set(Protocol.FEATURES, featuresId);
					}
				}

				if(p.get(Protocol.SUBPROTOCOLS_NAME) != null){

					if(!subProtocolAndProtocol.containsKey(p.getName())){
						subProtocolAndProtocol.put(p.getName(), (List<String>) p.get(Protocol.SUBPROTOCOLS_NAME));
					}
					List<String> newList = new ArrayList<String>();
					p.set(Protocol.SUBPROTOCOLS_NAME, newList);
				}
			}

			db.update(protocolList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME, Protocol.INVESTIGATION_NAME);

			List<InvestigationElement> subProtocols = new ArrayList<InvestigationElement>();

			for(InvestigationElement p : protocolList)
			{

				if(!subProtocols.contains(p)){

					List<String> subProtocol_names = subProtocolAndProtocol.get(p.getName());

					List<String> subProtocols_new = new ArrayList<String>();

					for(String subProtocol : subProtocol_names){

						if(checkExistingEntitesInDB.containsKey(subProtocol.toLowerCase())){
							subProtocols_new.add(checkExistingEntitesInDB.get(subProtocol.toLowerCase()));
						}else{
							subProtocols_new.add(subProtocol);
						}	
					}

					if(subProtocols_new.size() > 0)
					{
						List<Protocol> subProtocolList = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, subProtocols_new));

						if(subProtocolList.size() > 0)
						{
							List<Integer> subProtocolId = new ArrayList<Integer>();

							for(Protocol subPro : subProtocolList){
								if(!subProtocolId.contains(subPro.getId())){
									subProtocolId.add(subPro.getId());
								}
							}
							p.set(Protocol.SUBPROTOCOLS, subProtocolId);

						}
					}
					if(p.getId() != null)
						db.update(p);
				}
			}

			HashMap<String, InvestigationElement> hashMapComputeProtocol = removeDuplicates(computeProtocolList);

			computeProtocolList = new ArrayList<InvestigationElement>(hashMapComputeProtocol.values());

			for(InvestigationElement p :computeProtocolList){

				List<String> feature_names = (List<String>) p.get(ComputeProtocol.FEATURES_NAME);

				String templateScript = (String) p.get(ComputeProtocol.SCRIPTTEMPLATE);

				if(templateScript == null){
					p.set(ComputeProtocol.SCRIPTTEMPLATE, "N/A");
				}
				//TODO 
				if(p.get(ComputeProtocol.MEM) == null)
					p.set(ComputeProtocol.MEM, "512");
				if(p.get(ComputeProtocol.CORES) == null)
					p.set(ComputeProtocol.CORES, "2");

				if(feature_names.size() > 0)
				{
					List<Measurement> features = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, feature_names));

					if(features.size() > 0)
					{
						List<Integer> featuresId = new ArrayList<Integer>();
						for(Measurement m : features){

							if(displayNameToMeasurement.keySet().contains(m.getName())){

								if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, m.getName())).size() > 0){

									m = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, m.getName())).get(0);

									if(!featuresId.contains(m.getId()))
										featuresId.add(m.getId());
								}

							}else{
								if(!featuresId.contains(m.getId()))
									featuresId.add(m.getId());
							}
						}
						p.set(ComputeProtocol.FEATURES, featuresId);
					}
				}
			}

			db.update(computeProtocolList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ComputeProtocol.NAME, ComputeProtocol.INVESTIGATION_NAME);

			db.update(headerMeasurements, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME, Measurement.INVESTIGATION_NAME);

			int iteration = 1;

			while(observedValueList.size() > iteration * 5000){

				List<ObservedValue> subList = observedValueList.subList((iteration-1) * 5000, iteration * 5000);

				db.update(subList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.INVESTIGATION_NAME, 
						ObservedValue.VALUE, ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME);
				iteration++;
			}
			
			List<ObservedValue> subList = observedValueList.subList((iteration - 1)*5000, observedValueList.size()); 
			
			db.update(subList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.INVESTIGATION_NAME, 
					ObservedValue.VALUE, ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME);

			//			db.update(observedValueList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.INVESTIGATION_NAME, 
			//						ObservedValue.VALUE, ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME);
			//			

			db.commitTx();

			observationTargetList.clear();

			measurementList.clear();

			protocolList.clear();

			computeProtocolList.clear();

			categoryList.clear();

			observationTargetList.clear();

			ontologyTermList.clear();


		} catch (Exception e) {

			db.rollbackTx();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void checkExistenceInDB(HashMap<String, InvestigationElement> hashMap, String ClassType) throws DatabaseException {

		List<String> names = new ArrayList<String>(hashMap.keySet());

		checkExistingEntitesInDB.clear();

		if(names.size() > 0){

			if(ClassType.equals(Category.class.getSimpleName()) || ClassType.equals(Measurement.class.getSimpleName()) 
					|| ClassType.equals(ObservationTarget.class.getSimpleName())){

				for(Category c : db.find(Category.class, new QueryRule("name", Operator.IN, names))){
					InvestigationElement categoryToAdd =  hashMap.get(c.getName().toLowerCase());
					categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
					checkExistingEntitesInDB.put(c.getName().toLowerCase(), categoryToAdd.getName());
				}

				for(Measurement m : db.find(Measurement.class, new QueryRule("name", Operator.IN, names))){
					InvestigationElement categoryToAdd =  hashMap.get(m.getName().toLowerCase());
					categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
					checkExistingEntitesInDB.put(m.getName().toLowerCase(), categoryToAdd.getName());
				}

				for(ObservationTarget ot : db.find(ObservationTarget.class, new QueryRule("name", Operator.IN, names))){
					InvestigationElement categoryToAdd =  hashMap.get(ot.getName().toLowerCase());
					categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
					checkExistingEntitesInDB.put(ot.getName().toLowerCase(), categoryToAdd.getName());
				}
			}

			if(ClassType.equals(ComputeProtocol.class.getSimpleName()) || ClassType.equals(Protocol.class.getSimpleName())){

				for(Protocol p : db.find(Protocol.class, new QueryRule("name", Operator.IN, names))){

					InvestigationElement categoryToAdd =  hashMap.get(p.getName().toLowerCase());
					categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
					checkExistingEntitesInDB.put(p.getName().toLowerCase(), categoryToAdd.getName());

				}
				for(ComputeProtocol p : db.find(ComputeProtocol.class, new QueryRule("name", Operator.IN, names))){

					InvestigationElement categoryToAdd =  hashMap.get(p.getName().toLowerCase());
					categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
					checkExistingEntitesInDB.put(p.getName().toLowerCase(), categoryToAdd.getName());

				}
			}
		}
	}

	private HashMap<String, OntologyTerm> removeOntologyTermDuplicates(List<OntologyTerm> ontologyTermList) {

		HashMap<String, OntologyTerm> addedName = new HashMap<String, OntologyTerm>();

		for(OntologyTerm eachElement : ontologyTermList){

			if(!addedName.containsKey(eachElement.getName().toLowerCase())){
				addedName.put(eachElement.getName().toLowerCase(), eachElement);
			}
		}
		return addedName;
	}

	private HashMap<String, InvestigationElement> removeDuplicates(List<InvestigationElement> listOfObjectsToAdd) throws DatabaseException {

		HashMap<String, InvestigationElement> addedName = new HashMap<String, InvestigationElement>();

		for(InvestigationElement eachElement : listOfObjectsToAdd){

			if(!addedName.containsKey(eachElement.getName().toLowerCase())){
				addedName.put(eachElement.getName().toLowerCase(), eachElement);
			}
		}
		return addedName;
	}

	private String adjustDataTypeValue(String cellValue) {

		for(String keySet : InputToMolgenisDataType.keySet())
		{
			Pattern p = Pattern.compile(keySet);

			Matcher m = p.matcher(cellValue);

			if(m.find()){
				return InputToMolgenisDataType.get(keySet);
			}
		}
		return MeasurementDataType;
	}


	public void setDataType(String dataTypeInput, String molgenisDataType) {

		InputToMolgenisDataType.put(dataTypeInput.toLowerCase(), molgenisDataType);
	}

	public void setMissingCategoryIndex(int missingCategoryIndex) {
		missingCategoryList.add(missingCategoryIndex);
	}

	public void setInvestigationName(String investigationName){

		if(investigationName != null)
		{
			this.investigationName = investigationName;
		}
	}

	public void setInvestigation() throws DatabaseException {

		Investigation investigation = new Investigation();

		if(investigationName != null && db.query(Investigation.class).eq(Investigation.NAME, investigationName).count() == 0){

			investigation.setName(investigationName);

			db.add(investigation);
		}
	}

	public void setDirection(String excelDirection) {

		this.excelDirection  = excelDirection;

	}
}
