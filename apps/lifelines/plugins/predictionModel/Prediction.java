package plugins.predictionModel;


import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import app.FillMetadata;




public class Prediction extends PluginModel<Entity>
{
	private String Status = "";

	private TableModel table;

	private static final long serialVersionUID = 6149846107377048848L;

	public Prediction(String name, ScreenController<?> parent)
	{
		super(name, parent);
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

		if ("ImportLifelineToPheno".equals(request.getAction())) {

			loadDataFromExcel(db, request, null);

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

		//File file = new File(tmpDir+ "/DataShaperExcel.xls"); 

		File file = new File(tmpDir+ "/LifelinesDict.xls"); 
		
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
				int [] columnList = {1,2,4};
	
				table.setInvestigation("LifelinesDict");
				
				table.addField(Protocol.class.getSimpleName(), Protocol.NAME, 0, TableField.COLVALUE);
				
				table.addField(Measurement.class.getSimpleName(), Measurement.NAME, 3, TableField.COLVALUE);
				
				table.addField(ObservedValue.class.getSimpleName(), ObservedValue.VALUE, columnList, 3, TableField.COLHEADER);
				
				table.addField(Measurement.class.getSimpleName(), Measurement.DESCRIPTION, TableField.COLVALUE, 3, 5);
				
				table.addField(Protocol.class.getSimpleName(), Protocol.FEATURES_NAME, TableField.COLVALUE, 0, 3);
				
				table.convertIntoPheno(dictionaryCategory);
	
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