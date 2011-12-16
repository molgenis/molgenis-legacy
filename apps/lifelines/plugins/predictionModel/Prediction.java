package plugins.predictionModel;


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
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

import plugins.emptydb.emptyDatabase;
import app.FillMetadata;




public class Prediction extends PluginModel<Entity>
{
	private String Status = "";
	
	private List<String> headers = null;

	private TableModel table;
	
	private File file;
	
	private String investigation = null;
	
	private boolean importingFinished = false;

	private static final long serialVersionUID = 6149846107377048848L;
	
	private List<String> spreadSheetHeanders = new ArrayList<String>();

	private List<String> chooseClassType = new ArrayList<String>();
	
	private List<String> chooseFieldName = new ArrayList<String>();

	private List<String> dataTypeOptions = new ArrayList<String>();
	
	private List<Integer> columnIndex = new ArrayList<Integer>();
	
	private HashMap<String, String> userInputToDataType = new HashMap<String, String>();
	private HashMap<Integer, String> columnIndexToClassType = new HashMap<Integer, String>();
	private HashMap<Integer, Integer> columnIndexToRelation = new HashMap<Integer, Integer>();
	private HashMap<Integer, String> columnIndexToFieldName = new HashMap<Integer, String>();;
	
	public Prediction(String name, ScreenController<?> parent)
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
		chooseClassType.add(ObservedValue.class.getSimpleName());
		
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
		return "plugins_predictionModel_Prediction";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/predictionModel/Prediction.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("UploadFile".equals(request.getAction())) 
		{
			importingFinished = false;
			setStatus("");
			System.out.println(request);
			readHeaders();
			
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
	
	public void readHeaders() throws BiffException, IOException{
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		//File file = new File(tmpDir+ "/DataShaperExcel.xls"); 

		file = new File(tmpDir+ "/LifelinesDict.xls"); 
		
		if (file.exists()) {
			
			Workbook workbook = Workbook.getWorkbook(file); 

			Sheet sheet = workbook.getSheet(0);
			
			int columns = sheet.getColumns();
			
			headers = new ArrayList<String>();
			
			columnIndex.add(-1);
			
			for(int i = 0 ; i < columns; i++)
			{
				columnIndex.add(i);
				headers.add(sheet.getCell(i, 0).getContents().toString().replaceAll(" ", "_"));
				System.out.println(sheet.getCell(i, 0).getContents().toString());
			}
			
			setSpreadSheetHeanders(headers);
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
					String classType = columnIndexToClassType.get(columnIndex);
					
					String fieldName = columnIndexToFieldName.get(columnIndex);
					
					String splitByColon[] = fieldName.toString().split(":");
					
					fieldName = fieldName.toString().split(":")[splitByColon.length - 1];
					
					Integer dependedColumn = columnIndexToRelation.get(columnIndex);
					
					if(classType.equals(ObservedValue.class.getSimpleName()))
					{
						int coHeaders[] = {columnIndex.intValue()};
						
						table.addField(classType, ObservedValue.VALUE, coHeaders, dependedColumn.intValue(), TableField.COLHEADER);
						
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
}