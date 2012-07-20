package plugins.biobankimporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;

import org.hibernate.mapping.Array;
import org.molgenis.auth.Institute;
import org.molgenis.auth.Person;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

public class TableController {

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

	private HashMap<String, String> checkExistingMeasurementsInDB = new HashMap<String, String>();

	private HashMap<String, String> checkExistingEntitesInDB = new HashMap<String, String>();

	public TableController(int i,  Database db) {
		this.db = db;
		this.columnSize = i;
		configuration = new ArrayList<TableField>();
	}

	public void addField(String classType, String fieldName, String multipleValues, int[] columnList, Boolean Vertical) 
	{
		this.addField(classType, fieldName, multipleValues, columnList, Vertical, new SimpleTuple());
	}

	public void addField(String ClassType, String fieldName, String multipleValues, int[] columnList, Boolean Vertical, Tuple defaults) 
	{

		for(int i = 0; i < columnList.length; i++){
			this.addField(ClassType, fieldName, multipleValues, columnList[i], Vertical, defaults, -1);
		}
	}

	public void addField(String ClassType, String fieldName, String multipleValues, boolean Vertical, int dependedIndex, int... columnIndexes) 
	{

		List<Integer> columnList = new ArrayList<Integer>();

		for(int i = 0; i < columnIndexes.length; i++)
		{

			if(columnIndexToTableField.containsKey(columnIndexes[i]))
			{
				columnIndexToTableField.get(columnIndexes[i]).setDependentColumnIndex(dependedIndex);
				columnIndexToTableField.get(columnIndexes[i]).setRelation(fieldName);

			}else{

				this.addField(ClassType, fieldName, multipleValues, columnIndexes[i], Vertical, new SimpleTuple(), dependedIndex);
				columnIndexToTableField.get(columnIndexes[i]).setRelation(fieldName);
			}
			columnList.add(columnIndexes[i]);
		}
		relationIndex.put(dependedIndex, columnList);
	}

	public void addField(String ClassType, String fieldName, String multipleValues, int columnIndex, Boolean Vertical) 
	{
		this.addField( ClassType, fieldName, multipleValues, columnIndex, Vertical, new SimpleTuple(), -1);
	}

	public void addField(String ClassType, String fieldName, String multipleValues, int columnIndex,
			boolean Vertical, int... dependentColumnIndex) 
	{

		this.addField(ClassType, fieldName, multipleValues, columnIndex, Vertical, new SimpleTuple(), dependentColumnIndex);

	}

	public void addField(String ClassType, String fieldName, String multipleValues, int columnIndex,
			boolean Vertical, Tuple defaults) 
	{
		this.addField(ClassType, fieldName, multipleValues, columnIndex, Vertical, defaults, -1);

	}

	public void addField(String ClassType, String fieldName, String multipleValues, int[] coHeaders,
			int targetIndex, boolean Vertical) 
	{
		observationTarget = targetIndex;
		this.addField(ClassType, fieldName, multipleValues, coHeaders, Vertical, new SimpleTuple());
		observationTarget = -1;
	}

	public void addField(String ClassType, String fieldName, String multipleValues, int columnIndex, Boolean Vertical, Tuple defaults, int... dependentColumnIndex)
	{

		try {					
			//create a tableField that will take care of loading columnIndex into 'name' property
			field = new TableField(ClassType, fieldName, multipleValues, columnIndex, Vertical, defaults, dependentColumnIndex);
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

	@SuppressWarnings("unchecked")
	public void convertIntoPheno(Sheet[] sheets, int startingRowIndex, boolean multipleSheets, boolean sheetImportProtocol) throws DatabaseException
	{
		List<Measurement> headerMeasurements = new ArrayList<Measurement>();
		List<InvestigationElement> measurementList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> categoryList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> protocolList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> observationTargetList = new ArrayList<InvestigationElement>();
		List<InvestigationElement> panelList = new ArrayList<InvestigationElement>();
		List<ObservedValue> observedValueList = new ArrayList<ObservedValue>();

		HashMap<String, OntologyTerm> ontologyTermOfList = new HashMap<String, OntologyTerm>();
		HashMap<String, Institute> listOfInstitute = new HashMap<String, Institute>();
		HashMap<String, Person> listOfPerson = new HashMap<String, Person>();

		List<String> nonInvestigationElement = new ArrayList<String>();

		nonInvestigationElement.add(Person.class.getSimpleName());
		nonInvestigationElement.add(Institute.class.getSimpleName());
		nonInvestigationElement.add(OntologyTerm.class.getSimpleName());

		List<String> referenceFields = new ArrayList<String>();

		referenceFields.add(Protocol.NAME);
		referenceFields.add(Protocol.FEATURES_NAME);
		referenceFields.add(Protocol.SUBPROTOCOLS_NAME);
		referenceFields.add(Measurement.CATEGORIES_NAME);
		referenceFields.add(Measurement.UNIT_NAME);

		//listOfInstitue.get(0).getLabelValue();

		int sheetSize = sheets.length;

		if(multipleSheets == true){
			sheetSize = 1;
		}

		try{
			for(int sheetIndex = 0; sheetIndex < sheetSize; sheetIndex++){

				Sheet sheet = sheets[sheetIndex];

				int row = sheet.getRows();

				int column = sheet.getColumns();

				if(excelDirection.equals("UploadFileByRow"))
				{
					row = sheet.getColumns();
					column = sheet.getRows();
				}

				//three dimensional matrix of<colIndex, rowIndex, valueIndex>
				//third dimension of valueIndex is to deal with multiple values in one cell
				//we made colIndex key because not all colIndexes are used
				Map<Integer,List<List<InvestigationElement>>> colValues = new LinkedHashMap<Integer,List<List<InvestigationElement>>>();
				Map<String, Map<String, List<InvestigationElement>>> existingValuesForClassType = new LinkedHashMap<String, Map<String,List<InvestigationElement>>>();


				for(int rowIndex = 0; (rowIndex + startingRowIndex) < row; rowIndex++){

					for(int colIndex = 0; colIndex < column; colIndex++){

						String cellValue;

						if(excelDirection.equals("UploadFileByRow"))
							cellValue = sheet.getCell(rowIndex, colIndex).getContents();
						else
							cellValue = sheet.getCell(colIndex, rowIndex + startingRowIndex).getContents();

						TableField field = columnIndexToTableField.get(colIndex);

						if(columnIndexToTableField.get(colIndex) != null && !columnIndexToTableField.get(colIndex).getClassType().equals("NULL")){

							if(columnIndexToTableField.get(colIndex).getVertical() && rowIndex != 0){

								if(!existingValuesForClassType.containsKey(field.getClassType() + field.getFieldName())){
									Map<String, List<InvestigationElement>> tempHolder = new LinkedHashMap<String, List<InvestigationElement>>();
									existingValuesForClassType.put(field.getClassType() + field.getFieldName(), tempHolder);
								}

								if(existingValuesForClassType.get(field.getClassType() + field.getFieldName()).containsKey(cellValue)){

									//check colIndex: if there is already a list for colIndex
									if(colValues.get(colIndex) == null)
									{
										colValues.put(colIndex, new ArrayList<List<InvestigationElement>>());
									}

									colValues.get(colIndex).add(new ArrayList<InvestigationElement>());

									colValues.get(colIndex).get(rowIndex - 1).addAll(existingValuesForClassType.get(field.getClassType() + field.getFieldName()).get(cellValue));

								}else{
									//we split on multivalue
									String[] multiValue = cellValue.split(field.getValueSplitter());

									List<String> splitValue = new ArrayList<String>();
									
									if(field.getMultipleValues().equals("true")){
										
										for(int valueIndex = 0; valueIndex < multiValue.length; valueIndex++){
											
											splitValue.addAll(Arrays.asList(multiValue[valueIndex].split(",")));
											
										}
									}else{
										splitValue.addAll(Arrays.asList(multiValue));
									}
									
									int index = 0;
									
									for(String eachValue : splitValue)
									{
										
										String value = eachValue.replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();
										
										//If the fieldName is 'name', added as a new entity
										if(field.getFieldName().equalsIgnoreCase("NAME")){

											if(nonInvestigationElement.contains(field.getClassType())){
												
												if(field.getClassType().equals(Person.class.getSimpleName())){
													
													Person person = new Person();
													person.setName(value);
													if(!listOfPerson.containsKey(value)){
														listOfPerson.put(value, person);
													}

												}else if(field.getClassType().equals(Institute.class.getSimpleName())){
													
													Institute ins = new Institute();
													ins.setName(value);
													if(!listOfInstitute.containsKey(value)){
														listOfInstitute.put(value, ins);
													}
													
												}else if(field.getClassType().equals(OntologyTerm.class.getSimpleName())){
													
													OntologyTerm ot = new OntologyTerm();
													ot.setName(value);
													if(!ontologyTermOfList.containsKey(value)){
														ontologyTermOfList.put(value, ot);
													}
												}
												
											}else{
												//For the name of eneities, only letters, digits, underscore, space are allowed
												//The others are removed from the string
												

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

														//For the category, description and code_string could have other characters than
														//the ones that are allowed in the name
														String dsecription = eachValue;

														entity.set(Category.NAME, value);

														if(dsecription.split("=").length > 1){
															entity.set(Category.CODE_STRING, dsecription.split("=")[0].trim());
															entity.set(Category.DESCRIPTION, dsecription.split("=")[1].trim());
														}else{
															entity.set(Category.DESCRIPTION, dsecription.split("=")[0].trim());
															entity.set(Category.CODE_STRING, index);
														}

														//													entity.set(Category.LABEL, value);

														if(field.getDefaults().getString(Category.ISMISSING) != null)
															entity.set(Category.ISMISSING, field.getDefaults().getString(Category.ISMISSING));

													}else{
														//set name for other entities other than category.
														entity.set(field.getFieldName(), value);
													}

													if(investigationName != null)
														entity.set("Investigation_name", investigationName);

													colValues.get(colIndex).get(rowIndex - 1).add(entity);

													field.addCellValue(value);
												}
											}
										}
										
										index++;
									}
								}

								if(field.getDependentColumnIndex()[0] != -1 && !cellValue.equals(""))
								{

									for(int index = 0; index < field.getDependentColumnIndex().length; index++)
									{
										int dependentColumn = field.getDependentColumnIndex()[index];

										//										TableField dependendField = columnIndexToTableField.get(dependentColumn);

										//InvestigationElement addingPropertyToEntity = dependendField.getEntity();

										int existingRow = rowIndex;

										InvestigationElement addingPropertyToEntity = null;

										while(colValues.get(dependentColumn).get(existingRow - 1).size() == 0){

											existingRow--;
										}

										addingPropertyToEntity = colValues.get(dependentColumn).get(existingRow - 1).get(0);

										String multipleValues[] = cellValue.split(field.getValueSplitter());

										List<Object> values = new ArrayList<Object>();

										for(int i = 0; i < multipleValues.length; i++){

											if(referenceFields.contains(field.getFieldName())){

												values.add(multipleValues[i].replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim());

											}else{
												values.add(multipleValues[i].trim());
											}
										}

										//Due to using generic method get() property of the Pheno Entity, so we don`t know which Object data
										//the field would be. We need to check the field type first. It could be list, boolean, string
										if(addingPropertyToEntity.get(field.getRelationString()) != null)
										{
											if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(ArrayList.class))
											{
												@SuppressWarnings("unchecked")
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

											}else if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(Boolean.class)){

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

											}else if(addingPropertyToEntity.get(field.getRelationString()).getClass().equals(String.class)){

												values.clear();
												values.add(cellValue);
											}

											if(field.getRelationString().equals(Measurement.DATATYPE))
											{

												String dataType = adjustDataTypeValue(cellValue);

												if(!dataType.equals(MeasurementDataType))
												{
													values.clear();
													values.add(dataType);
												}else{
													values.clear();
													values.add("string");
												}
											}
										}

										if(field.getRelationString().equals(Measurement.UNIT_NAME))
										{

											for(int i = 0; i < multipleValues.length; i++)
											{

												if(!multipleValues[i].equals(""))
												{

													String ontologyTermName = multipleValues[i].replaceAll("[^(a-zA-Z0-9_\\s)]", " ");

													List<OntologyTerm> existingOntologyTermList = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.EQUALS, ontologyTermName));

													if(existingOntologyTermList.size() == 0)
													{
														OntologyTerm unitOntologyTerm = new OntologyTerm();

														unitOntologyTerm.setName(ontologyTermName);
														unitOntologyTerm.setDefinition(multipleValues[i]);

														if(!ontologyTermOfList.keySet().contains(unitOntologyTerm.getName()))
														{
															ontologyTermOfList.put(unitOntologyTerm.getName(), unitOntologyTerm);
														}
													}
												}
											}
										}

										List<Object> removeDuplicate = new ArrayList<Object>();

										for(Object o : values){
											if(!removeDuplicate.contains(o)){
												removeDuplicate.add(o);
											}
										}
										values = removeDuplicate;

										if(values.size() == 1)
										{	
											if(!values.get(0).equals(""))
												addingPropertyToEntity.set(field.getRelationString(), values.get(0));
										}else{
											addingPropertyToEntity.set(field.getRelationString(), values);
										}
									}
								}

								if(!existingValuesForClassType.get(field.getClassType() + field.getFieldName()).containsKey(cellValue) && colValues.containsKey(colIndex)){
									existingValuesForClassType.get(field.getClassType() + field.getFieldName()).put(cellValue, colValues.get(colIndex).get(rowIndex - 1));
								}

							}else{

								//The header is measurement!
								if(rowIndex == 0){

									if(field.getClassType().equalsIgnoreCase(ObservedValue.class.getSimpleName())){

										//										"".replaceAll("[^(a-zA-Z0-9_\\s)]", " ");

										String measurementName = cellValue.replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

										Measurement measurement = new Measurement();

										measurement.setName(measurementName);

										measurement.setInvestigation_Name(investigationName);

										if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).size() != 0){

											Measurement measure = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).get(0);

											if(!measure.getInvestigation_Name().equals(investigationName)){
												measurement.setName(measurementName + "_" +investigationName);
												measurement.setLabel(measurementName);
												checkExistingMeasurementsInDB.put(measure.getName(), measurementName + "_" +investigationName);
												headerMeasurements.add(measurement);
											}

										}else{
											headerMeasurements.add(measurement);
										}
									}
									//The rest of the column is observedValue!
								}else{

									if(!cellValue.equals("") && cellValue != null && field.getObservationTarget() != -1){

										List<String> multipleValuesInCells = new ArrayList<String>();

										if(field.getMultipleValues().equals("true")){

											multipleValuesInCells = Arrays.asList(cellValue.split(","));

										}else{
											multipleValuesInCells.add(cellValue);
										}

										for(String eachValue : multipleValuesInCells){

											ObservedValue observedValue = new ObservedValue();

											String headerName = sheet.getCell(colIndex, startingRowIndex).getContents().replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

											String targetName = sheet.getCell(field.getObservationTarget(), rowIndex + startingRowIndex).getContents().replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

											//TODO: import measurements then import individual data. The measurement has to be consistent.
											if(checkExistingMeasurementsInDB.keySet().contains(headerName)){
												headerName = checkExistingMeasurementsInDB.get(headerName);
											}

											observedValue.setFeature_Name(headerName);

											observedValue.setTarget_Name(targetName);

											TableField targetField = columnIndexToTableField.get(field.getObservationTarget());

											if(targetField.getClassType().equalsIgnoreCase(Measurement.class.getSimpleName())){

												if(!checkExistingMeasurementsInDB.containsKey(targetName)){

													Query<Measurement> q = db.query(Measurement.class);
													q.addRules(new QueryRule(Measurement.NAME, Operator.EQUALS, targetName));
													q.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.NOT, investigationName));

													if(q.find().size() > 0){
														checkExistingMeasurementsInDB.put(targetName, targetName + "_" + investigationName);
														observedValue.setTarget_Name(checkExistingMeasurementsInDB.get(targetName));
													}
												}else{
													observedValue.setTarget_Name(checkExistingMeasurementsInDB.get(targetName));
												}
											}

											observedValue.setValue(eachValue);

											observedValueList.add(observedValue);

											if(investigationName != null)
												observedValue.set("Investigation_name", investigationName);

										}
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

				List<InvestigationElement> measurementListForEachSheet = new ArrayList<InvestigationElement>();

				for(Integer colIndex: colValues.keySet())
				{
					for(List<InvestigationElement> list: colValues.get(colIndex))
					{
						if(columnIndexToTableField.get(colIndex).getClassType().equals("Measurement"))
						{
							measurementList.addAll(list);
							measurementListForEachSheet.addAll(list);
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
						if(columnIndexToTableField.get(colIndex).getClassType().equals("Panel"))
						{
							panelList.addAll(list);
						}
					}
				}

				String sheetName = sheet.getName().replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

				if(sheetImportProtocol == false){
					Protocol sheetNameProtocol = new Protocol();
					sheetNameProtocol.setName(sheetName);
					sheetNameProtocol.setInvestigation_Name(investigationName);
					List<String> nameOfMeasurements = new ArrayList<String>();
					for(InvestigationElement e : measurementListForEachSheet){
						nameOfMeasurements.add(e.getName());
					}
					sheetNameProtocol.setFeatures_Name(nameOfMeasurements);
					protocolList.add(sheetNameProtocol);
				}
			}

			db.beginTx();

			setInvestigation();
			
			db.update(new ArrayList<OntologyTerm>(ontologyTermOfList.values()), Database.DatabaseAction.ADD_IGNORE_EXISTING, OntologyTerm.NAME);

			db.update(new ArrayList<Institute>(listOfInstitute.values()), Database.DatabaseAction.ADD_IGNORE_EXISTING, Institute.NAME);
			
			db.update(new ArrayList<Person>(listOfPerson.values()), Database.DatabaseAction.ADD_IGNORE_EXISTING, Person.NAME);
			
			HashMap<String, InvestigationElement> removedPanelList = removeDuplicates(panelList); 

			checkExistenceInDB(removedPanelList, Panel.class.getSimpleName());

			panelList = new ArrayList<InvestigationElement>(removedPanelList.values());

			//			for(InvestigationElement e : panelList){
			//				System.out.println(e.getName());
			//			}
			db.update(panelList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Panel.NAME);

			HashMap<String, InvestigationElement> hashMapObservationTarget = removeDuplicates(observationTargetList);

			checkExistenceInDB(hashMapObservationTarget, ObservationTarget.class.getSimpleName());

			observationTargetList = new ArrayList<InvestigationElement>(hashMapObservationTarget.values());

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

			HashMap<String, InvestigationElement> hashMapCategory = removeDuplicates(categoryList);

			checkExistenceInDB(hashMapCategory, Category.class.getSimpleName());

			categoryList = new ArrayList<InvestigationElement> (hashMapCategory.values());

			for(InvestigationElement c : categoryList){
				System.out.println(c.getName());
				if(c.getName().equals("")){
					System.out.println();
				}
			}

			db.update(categoryList, Database.DatabaseAction.ADD_UPDATE_EXISTING, Category.NAME, Category.INVESTIGATION_NAME);

			for(InvestigationElement m : measurementList){

				String measurementName = m.getName();

				//prevent the measurement with the same name from importing twice. Therefore check the database whether the 
				//measurement already existed, if there is, make a unique measurement name by combining it with investigation
				//name. such as weight_study_KORA. In order to display the measurement with its original name such as "weight"
				//a meta-measurement "display name" is created to describe the measurements as a label (measurement becomes observationElement
				//in Molgenis) such as weight_study_KORA (ObservationElement) --------->"weight" (value) <-----------diaplay name (measurement)
				//				if(db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).size() != 0){
				//
				//					//if the existing measurement comes from the same investigation, that means they are the same measurement, don`t import
				//					Measurement existingMeasurement = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).get(0);
				//
				//					if(!existingMeasurement.getInvestigation_Name().equals(investigationName)){
				if(checkExistingMeasurementsInDB.containsKey(m.getName())){
					m.set(Measurement.LABEL, measurementName);
					measurementName = checkExistingMeasurementsInDB.get(m.getName());
					m.setName(measurementName);
				}
				//Resolving importing the measurements with the same name. In the different studies, measurements with the same name could
				//have different definitions, so we need to distinguish this kind of variables. Therefore a display name meta-measurement is created
				//to describe these measurements! For example, measurement weight-study-1 and weight-study-2 have the same value for the display name, "weight"

				//					}
				//				}

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

			checkExistenceInDB(hashMapMeasurement, Measurement.class.getSimpleName());

			measurementList = new ArrayList<InvestigationElement> (hashMapMeasurement.values());

			db.update(measurementList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME, Measurement.INVESTIGATION_NAME);

			//Try to update measurements
			HashMap<String, InvestigationElement> hashMapProtocol = removeDuplicates(protocolList);

			checkExistenceInDB(hashMapProtocol, Protocol.class.getSimpleName());

			protocolList = new ArrayList<InvestigationElement>(hashMapProtocol.values());

			HashMap<String, List<String>> subProtocolAndProtocol = new HashMap<String, List<String>>();

			//mref is not working for the name. We can`t do protocol.setFeatures_name(""). Therefore we need to 
			//add features in db first, afterwards we could use protocol.setFeatures_ID(). Mref for ID is working fine
			for(InvestigationElement p : protocolList)
			{	
				@SuppressWarnings("unchecked")
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

							if(!featuresId.contains(m.getId()))
								featuresId.add(m.getId());
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

				if(p.getName().equals("Blood pressure")){
					System.out.println();
				}

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
						Query<Protocol> q = db.query(Protocol.class);

						q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, investigationName));
						q.addRules(new QueryRule(Protocol.NAME, Operator.IN, subProtocols_new));

						List<Protocol> subProtocolList = q.find();

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

			db.update(headerMeasurements, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME, Measurement.INVESTIGATION_NAME);

			observedValueList = removeDuplicatesObservedValue(observedValueList);

			int iteration = 1;

			while(observedValueList.size() > iteration * 5000){

				List<ObservedValue> subList = observedValueList.subList((iteration-1) * 5000, iteration * 5000);

				db.update(subList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.INVESTIGATION_NAME, 
						ObservedValue.VALUE, ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME);
				iteration++;
			}

			List<ObservedValue> subList = observedValueList.subList((iteration - 1)*5000, observedValueList.size()); 

//			for(ObservedValue ov : subList){
//				System.out.println(ov);
//				db.add(ov);
//			}
			
			db.update(subList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.INVESTIGATION_NAME, 
					ObservedValue.VALUE, ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME);

			//			for(ObservedValue ov : observedValueList){
			//				System.out.println(ov);
			//				db.add(ov);
			//			}
			
			db.commitTx();

			observationTargetList.clear();

			measurementList.clear();

			protocolList.clear();

			categoryList.clear();

			observationTargetList.clear();

			ontologyTermOfList.clear();


		} catch (Exception e) {

			e.printStackTrace();
			db.rollbackTx();
			// TODO Auto-generated catch block
		}


	}

	private void checkExistenceInDB(HashMap<String, InvestigationElement> hashMap, String ClassType) throws DatabaseException {

		List<String> names = new ArrayList<String>(hashMap.keySet());

		//		checkExistingEntitesInDB.clear();

		if(names.size() > 0){

			if(ClassType.equals(Category.class.getSimpleName()) || ClassType.equals(Measurement.class.getSimpleName()) 
					|| ClassType.equals(ObservationTarget.class.getSimpleName()) || ClassType.equals(Panel.class.getSimpleName()) ){

				for(Category c : db.find(Category.class, new QueryRule("name", Operator.IN, names))){
					if(!c.getInvestigation_Name().equals(investigationName)){
						InvestigationElement categoryToAdd =  hashMap.get(c.getName().toLowerCase());
						categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
						checkExistingEntitesInDB.put(c.getName().toLowerCase(), categoryToAdd.getName());
					}else{
						hashMap.remove(c.getName().toLowerCase());
					}
				}

				for(Measurement m : db.find(Measurement.class, new QueryRule("name", Operator.IN, names))){
					if(!m.getInvestigation_Name().equals(investigationName)){
						InvestigationElement categoryToAdd =  hashMap.get(m.getName().toLowerCase());
						categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
						checkExistingEntitesInDB.put(m.getName().toLowerCase(), categoryToAdd.getName());
					}else{
						hashMap.remove(m.getName().toLowerCase());
					}
				}

				for(ObservationTarget ot : db.find(ObservationTarget.class, new QueryRule("name", Operator.IN, names))){
					if(!ot.getInvestigation_Name().equals(investigationName)){
						InvestigationElement categoryToAdd =  hashMap.get(ot.getName().toLowerCase());
						categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
						checkExistingEntitesInDB.put(ot.getName().toLowerCase(), categoryToAdd.getName());
					}else{
						hashMap.remove(ot.getName().toLowerCase());
					}
				}
			}

			if(ClassType.equals(ComputeProtocol.class.getSimpleName()) || ClassType.equals(Protocol.class.getSimpleName())){

				for(Protocol p : db.find(Protocol.class, new QueryRule("name", Operator.IN, names))){
					if(!p.getInvestigation_Name().equals(investigationName)){
						InvestigationElement categoryToAdd =  hashMap.get(p.getName().toLowerCase());
						categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
						checkExistingEntitesInDB.put(p.getName().toLowerCase(), categoryToAdd.getName());
					}else{
						hashMap.remove(p.getName().toLowerCase());
					}
				}
				for(ComputeProtocol p : db.find(ComputeProtocol.class, new QueryRule("name", Operator.IN, names))){
					if(!p.getInvestigation_Name().equals(investigationName)){
						InvestigationElement categoryToAdd =  hashMap.get(p.getName().toLowerCase());
						categoryToAdd.setName(categoryToAdd.getName() + "_" + ClassType + "_" + investigationName);
						checkExistingEntitesInDB.put(p.getName().toLowerCase(), categoryToAdd.getName());
					}else{
						hashMap.remove(p.getName().toLowerCase());
					}
				}
			}
		}
	}

	private List<ObservedValue> removeDuplicatesObservedValue(List<ObservedValue> observedValueList) throws DatabaseException{

		List<ObservedValue> uniqueValues = new ArrayList<ObservedValue>();

		List<String> uniqueCombination = new ArrayList<String>();

		for(ObservedValue ov : observedValueList){

			String combination = ov.getTarget_Name() + ov.getFeature_Name() + ov.getValue();

			if(!uniqueCombination.contains(combination)){
				uniqueCombination.add(combination);

				Query<ObservedValue> q = db.query(ObservedValue.class);

				q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, ov.getValue()));

				q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, ov.getTarget_Name()));

				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, ov.getFeature_Name()));

				q.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, ov.getInvestigation_Name()));

				if(q.find().size() == 0){
					uniqueValues.add(ov);
				}
			}
		}

		return uniqueValues;
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
			Pattern p = Pattern.compile(keySet, Pattern.CASE_INSENSITIVE);

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
