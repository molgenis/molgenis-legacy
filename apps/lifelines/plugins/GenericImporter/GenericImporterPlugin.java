package plugins.GenericImporter;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

import plugins.emptydb.emptyDatabase;
import app.FillMetadata;




public class GenericImporterPlugin extends PluginModel<Entity>
{
	private String Status = "";
	
	private List<String> headers = null;

	private TableModel table;
	
	private File file;
	
	private String investigation = null;
	
	private boolean importingFinished = true;

	private static final long serialVersionUID = 6149846107377048848L;
	
	private List<String> spreadSheetHeanders = new ArrayList<String>();

	private List<String> chooseClassType = new ArrayList<String>();
	
	private List<String> chooseFieldName = new ArrayList<String>();

	private List<String> dataTypeOptions = new ArrayList<String>();
	
	private List<Integer> columnIndex = new ArrayList<Integer>();
	
	private HashMap<String, String> userInputToDataType = new HashMap<String, String>();
	private HashMap<Integer, String> columnIndexToClassType = new HashMap<Integer, String>();
	private HashMap<Integer, Integer> columnIndexToRelation = new HashMap<Integer, Integer>();
	private HashMap<Integer, String> columnIndexToFieldName = new HashMap<Integer, String>();

	private String excelDirection = "UploadFileByColumn";

	private String uploadFileName = "";

	private int StepsFlag = 0;

	private int columnCount = 0;
	
 	public GenericImporterPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		
		setChooseClassType();
	}
	

	public boolean isImportingFinished() {
		return importingFinished;
	}

	public void setImportingFinished(boolean importingFinished) {
		this.importingFinished = importingFinished;
	}
	
	public List<String> getChooseClassType() {
		return chooseClassType;
	}

	public List<String> getSpreadSheetHeanders() {
		return spreadSheetHeanders;
	}

	public void setSpreadSheetHeanders(List<String> spreadSheetHeanders) {
		this.spreadSheetHeanders = spreadSheetHeanders;
	}
	
	public void setChooseClassType(){
		
		chooseClassType.add(Measurement.class.getSimpleName());
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.DESCRIPTION);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.DATATYPE);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.UNIT_NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.TEMPORAL);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.INVESTIGATION_NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.CATEGORIES_NAME);
		chooseClassType.add(Protocol.class.getSimpleName());
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.FEATURES_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.INVESTIGATION_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.SUBPROTOCOLS_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.DESCRIPTION);
		chooseClassType.add(Category.class.getSimpleName());
		chooseClassType.add(Category.class.getSimpleName() + ":" + Category.ISMISSING);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.NAME);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.CODE_STRING);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.LABEL);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.DESCRIPTION);
		chooseFieldName.add(Individual.class.getSimpleName() + ":" + Individual.FATHER_NAME);
		chooseFieldName.add(Individual.class.getSimpleName() + ":" + Individual.MOTHER_NAME);
		chooseFieldName.add(ObservedValue.class.getSimpleName());
		chooseFieldName.add(ObservationTarget.class.getSimpleName() + ":" + ObservationTarget.NAME);
		chooseFieldName.add(Panel.class.getSimpleName() + ":" + Panel.NAME);
		chooseFieldName.add(Panel.class.getSimpleName() + ":" + Panel.INDIVIDUALS_NAME);
		chooseClassType.add(ObservedValue.class.getSimpleName());
		chooseClassType.add(ObservationTarget.class.getSimpleName());
		chooseClassType.add(Individual.class.getSimpleName());
		chooseClassType.add(Panel.class.getSimpleName());
		
		dataTypeOptions.add("string");
		dataTypeOptions.add("int");
		dataTypeOptions.add("datetime");
		dataTypeOptions.add("categorical");
		
	}


	public List<String> getChooseFieldName() {
		return chooseFieldName;
	}

	public void setChooseFieldName(List<String> chooseFieldName) {
		this.chooseFieldName = chooseFieldName;
	}

	public List<String> getDataTypeOptions() {
		return dataTypeOptions;
	}

	public void setDataTypeOptions(List<String> dataTypeOptions) {
		this.dataTypeOptions = dataTypeOptions;
	}

	@Override
	public String getViewName()
	{
		return "plugins_GenericImporter_GenericImporterPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/GenericImporter/GenericImporterPlugin.ftl";
	}
	
	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("UploadFileByColumn".equals(request.getAction())) 
		{
			excelDirection = "UploadFileByColumn";
			System.out.println(request);
			uploadFileName  = request.getString("uploadFile");
			readHeaders(request.getAction());
			this.setStepsFlag(1);
		} else if ("UploadFileByRow".equals(request.getAction())) {
			excelDirection = "UploadFileByRow";
			System.out.println(request);
			uploadFileName  = request.getString("uploadFile");
			readHeaders(request.getAction());
			this.setStepsFlag(1);
			
		} else if ("ImportLifelineToPheno".equals(request.getAction())){
			
			int count = 0;
			
			String MolgenisDataTypeOption = null;
			
			String userInputDatType = null;
			
			int columnIndex = 0;
			
			if(headers != null)
			{
				for(String member : headers)
				{
					if(request.getList(member) != null)
					{	
						int index = 0;
						
						for(Object eachMember : request.getList(member))
						{	
							System.out.println(eachMember.toString());
							if(index == 0)
							{
								columnIndexToClassType.put(columnIndex, eachMember.toString());
								index++;
								
							}else if(index == 1){
								
								columnIndexToFieldName.put(columnIndex, eachMember.toString());
								index++;
								
							}else if(index == 2){
								System.out.println(columnIndex + "-------------------------->" + eachMember.toString());
								columnIndexToRelation.put(columnIndex, Integer.parseInt(eachMember.toString()));
							}
						}
					}
					
					columnIndex++;
					
					while(request.getString(member + "_options_" + count) != null)
					{
						String eachMember = request.getString(member + "_options_" + count);
						System.out.println(eachMember.toString() + " Molgenis option!");
						MolgenisDataTypeOption = eachMember.toString();
						
						if(request.getString(member + "_input_" + count) != null)
						{
							userInputDatType = request.getString(member + "_input_" + count);
							
							userInputToDataType.put(MolgenisDataTypeOption, userInputDatType);
						}
						count++;
					}
					
				}
				
				if(request.getString("investigation") != null)
				{
					investigation = request.getString("investigation");
				
				}
				
				loadDataFromExcel(db, request, null);
				
			}else{
				setStatus("Please do the step one first!");
			}
			
		} else if ("fillinDatabase".equals(request.getAction())) {

			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			this.setStatus("The database is empty now");
		}
		
	}
	
	public void readHeaders(String header) throws BiffException, IOException{
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		//file = new File(tmpDir+ "/DataShaperExcel.xls"); 

		//file = new File(tmpDir+ "/LifelinesDict.xls"); 
		
		file = new File(uploadFileName); 
		
		
		if (file.exists()) {
			
			importingFinished = false;
			
			setStatus("");
			
			Workbook workbook = Workbook.getWorkbook(file); 

			Sheet sheet = workbook.getSheet(0);
			
			int columns = sheet.getColumns();
			
			int rows = sheet.getRows();
			
			headers = new ArrayList<String>();
			
			columnIndex.add(0);
			
			if(header.equals("UploadFileByColumn"))
			{
				setColumnCount(columns);
				
				for(int i = 0 ; i < columns; i++)
				{
					columnIndex.add(i + 1);
					headers.add(sheet.getCell(i, 0).getContents().toString().replaceAll(" ", "_"));
					System.out.println(sheet.getCell(i, 0).getContents().toString());
					
				}
				
				setSpreadSheetHeanders(headers);
			}
			
			if(header.equals("UploadFileByRow"))
			{
				setColumnCount(rows);
				
				for(int i = 0 ; i < rows; i++)
				{
					columnIndex.add(i);
					headers.add(sheet.getCell(0, i).getContents().toString().replaceAll(" ", "_"));
					System.out.println(sheet.getCell(0, i).getContents().toString());
				}
				
				setSpreadSheetHeanders(headers);
			}
			
			
			
		}else {
			
			this.setStatus("Please upload a file first!");
		}
	}
	
	public List<Integer> getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(List<Integer> columnIndex) {
		this.columnIndex = columnIndex;
	}

	@SuppressWarnings("unchecked")
	public void loadDataFromExcel(Database db, Tuple request, Investigation inv) throws BiffException, IOException, DatabaseException{

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		//File file = new File(tmpDir+ "/DataShaperExcel.xls"); 

		if (file.exists()) {

			System.out.println("The excel file is being imported, please be patient");

			this.setStatus("The excel file is being imported, please be patient");

			Workbook workbook = Workbook.getWorkbook(file); 

			Sheet dictionaryCategory = workbook.getSheet(0);

			table = new TableModel (dictionaryCategory.getColumns(), db);
			
			//DataShaper input code!
			{			
//				int [] columnList = {0, 1, 2};
//				
//				table.setInvestigation("DataShaper");
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.NAME, columnList, TableField.COLVALUE);
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.SUBPROTOCOLS_NAME, TableField.COLVALUE, 0,1);
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.SUBPROTOCOLS_NAME, TableField.COLVALUE, 1,2);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.NAME, 3, TableField.COLVALUE);
//				
//				table.addField(Category.class.getSimpleName(), Category.NAME, 8, TableField.COLVALUE);
//				
//				Tuple defaults = new SimpleTuple();
//				
//				defaults.set(Category.ISMISSING, true);
//				
//				table.addField(Category.class.getSimpleName(), Category.NAME, 9, TableField.COLVALUE, defaults);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.DESCRIPTION, TableField.COLVALUE, 3,4);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.CATEGORIES_NAME, TableField.COLVALUE, 3,8,9);
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.FEATURES_NAME, TableField.COLVALUE, 2,3);
//				
//				int [] coHeaders = {7, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24};
//				
//				table.addField(ObservedValue.class.getSimpleName(), ObservedValue.VALUE, coHeaders, 3, TableField.COLHEADER);		
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.UNIT_NAME, TableField.COLVALUE, 3, 5);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.TEMPORAL, TableField.COLVALUE, 3, 6);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.DATATYPE, TableField.COLVALUE, 3, 19, 25);
//				
//				table.setDataType("Integer", "int");
//				
//				table.setDataType("Categorical", "categorical");
//				
//				table.setDataType("Decimal", "int");
//				
//				table.setDataType("Date", "datetime");
//				
//				table.convertIntoPheno(dictionaryCategory);
			}
			
			//Lifeline Dictionary input
			{
				List<String> referenceClass = new ArrayList<String>();
				referenceClass.add(Measurement.CATEGORIES_NAME);
				referenceClass.add(Protocol.SUBPROTOCOLS_NAME);
				referenceClass.add(Protocol.FEATURES_NAME);
				
				for(Integer columnIndex : columnIndexToClassType.keySet())
				{
					//columnIndex--;
					
					String classType = columnIndexToClassType.get(columnIndex);
					
					String fieldName = columnIndexToFieldName.get(columnIndex);
					
					String splitByColon[] = fieldName.toString().split(":");
					
					fieldName = fieldName.toString().split(":")[splitByColon.length - 1];
					
					Integer dependedColumn = columnIndexToRelation.get(columnIndex);
					
					dependedColumn--;
					
					table.setDirection(excelDirection);
					
					if(classType.equals(ObservedValue.class.getSimpleName()))
					{
						int coHeaders[] = {columnIndex.intValue()};
						System.out.println(columnIndex);
						table.addField(classType, ObservedValue.VALUE, coHeaders, dependedColumn.intValue(), TableField.COLHEADER);
						
					}else if (classType.equals(Category.class.getSimpleName() + ":" + Category.ISMISSING)){
						
						Tuple defaults = new SimpleTuple();
						defaults.set(Category.ISMISSING, true);
						table.addField(Category.class.getSimpleName(), "name", columnIndex.intValue(), TableField.COLVALUE, defaults);
						table.addField(classType, fieldName, TableField.COLVALUE, dependedColumn.intValue(), columnIndex.intValue());
						
					}else{
						
						if(dependedColumn.intValue() == -1)
						{
							table.addField(classType, fieldName, columnIndex.intValue(), TableField.COLVALUE);
							
						}else{
							
							if(referenceClass.contains(fieldName))
							{
								table.addField(classType, "name", columnIndex.intValue(), TableField.COLVALUE);
							}
							
							table.addField(classType, fieldName, TableField.COLVALUE, dependedColumn.intValue(), columnIndex.intValue());
							
							if(classType.equals(Measurement.class.getSimpleName()) && fieldName.equals(Measurement.DATATYPE))
							{
								
								for(String molgenisOption : userInputToDataType.keySet())
								{
									table.setDataType(userInputToDataType.get(molgenisOption), molgenisOption);
								}
							}
						}
					}
				}
				
				table.setInvestigation(investigation);
				
				table.convertIntoPheno(dictionaryCategory);
				
//				int [] columnList = {1,2,4};
//	
//				table.setInvestigation("LifelinesDict");
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.NAME, 0, TableField.COLVALUE);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.NAME, 3, TableField.COLVALUE);
//				
//				table.addField(ObservedValue.class.getSimpleName(), ObservedValue.VALUE, columnList, 3, TableField.COLHEADER);
//				
//				table.addField(Measurement.class.getSimpleName(), Measurement.DESCRIPTION, TableField.COLVALUE, 3, 5);
//				
//				table.addField(Protocol.class.getSimpleName(), Protocol.FEATURES_NAME, TableField.COLVALUE, 0, 3);
//				
//				table.convertIntoPheno(dictionaryCategory);
	
//				Sheet categoryInput = workbook.getSheet(1);
//	
//				table = new TableModel (categoryInput.getColumns(), db);
//	
//				table.addField(TableModel.IGNORE, 0, TableField.COLVALUE);
//	
//				table.addField(TableModel.IGNORE, 1, TableField.COLVALUE);
//	
//				table.addField(TableModel.IGNORE, 2, TableField.COLVALUE);
//	
//				table.addField(TableModel.CATEGORY, 3, TableField.COLVALUE);
//	
//				table.addField(TableModel.CODE_STRING, 4, TableField.COLVALUE);
//	
//				table.setMeasurementCategoryRelation(1,3);
//	
//				table.convertIntoPheno(categoryInput);
			}
			
			//This is for the prediction model input
			{
//				int measurementList[] = {2,3,4,5,6,7};
//				table.addField(TableModel.PANEL, 0, TableField.COLVALUE);
//				table.addField(TableModel.MEASUREMENT, measurementList, TableField.COLHEADER);
//				table.addField(TableModel.PROTOCOL, 1, TableField.COLVALUE);
//				table.setTarget(0);
				
				//how should each ObservedValue be constructed: you need target, feature and protocol (or protocolApplication).
//				table.setTarget(TableField.COLVALUE, 0);
//				table.setFeature(TableField.COLHEADER, measurementList);
//				table.setProtocol(TableField.COLVALUE, 1);

//				table.convertIntoPheno(dictionaryCategory);
				
				
//				addField(entityType, row-coordinates, col-coordinates)
//				table.addColumn(TableModel.PANEL, 0);
//				table.addHeaders(TableModel.MEASUREMENT, measurementList);
//				
//				table.addField(TableModel.PANEL, TableField.COLUMN, 0);
//				table.addField(TableModel.MEASUREMENT, TableField.COLHEADER, measurementList);
//				table.addField(TableModel.PROTOCOL, 1, TableField.COLUMN);
//				table.setTarget(0);
//				table.convertIntoPheno(dictionaryCategory);
				
	
			}
			
			this.setStatus("finished!");

			importingFinished = true;
			
		} else {

			this.setStatus("The file should be in " + file );

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


	public void setStepsFlag(int stepsFlag) {
		StepsFlag = stepsFlag;
	}


	public int getStepsFlag() {
		return StepsFlag;
	}


	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}


	public boolean getColumnCount() {
		if (this.columnCount > 5) return true;
		else return false;
	}
}