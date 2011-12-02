package plugins.predictionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;

import jxl.Sheet;

public class TableModel {

	private Database db;
	
	public static final String MEASUREMENT_DATATYPE = "datatype";

	public static final String MEASUREMENT_DESCRIPTION = "description";

	public static final String PROTOCOL_FEATURE = "Protocol_Feature";

	public int columnSize = 0;
	
	public List<TableField> configuration;
	
	public Prediction prediction;
	
	public TableField field;
	
	static String PANEL = "Panel";
	
	static String PROTOCOLAPPLICATION = "ProtocolApplication";
	
	static String MEASUREMENT = "Measurement";
	
	static String OBSERVERDVALUE = "ObservedValue";
	
	static String PROTOCOL = "Protocol";
	
	private int observationTarget = -1;

	private int protocolIndex = -1;

	private int featureIndex = -1;
	
	public TableModel(int i,  Database db) {
		this.db = db;
		this.columnSize = i;
		configuration = new ArrayList<TableField>();
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
		
		HashMap<String, List> protocolMeasurementList = new HashMap<String, List>();
		
		Measurement measurement;
		
		Protocol protocol;
		
		ObservedValue observedValue;
		
		Panel panel;
		
		for(int i = 0; i < row; i++){
			
			measurement = new Measurement();
			panel = new Panel ();
			protocol = new Protocol();
			
			for(int j = 0; j < column; j++){
				
				String cellValue = sheet.getCell(j, i).getContents().replaceAll("'", "").trim();

				System.out.println("The cell value is " + cellValue);
				System.out.println("The size is =========== " + configuration.size());
				TableField field = configuration.get(j);
				
				if(field.getVertical()){
					
					if(field.ClassType.equalsIgnoreCase("Measurement") && i != 0){
						
						measurement.setName(cellValue);
						
						measurementList.add(measurement);
						
					} else if(field.ClassType.equalsIgnoreCase("Panel") && i != 0){
						
						panel.setName(cellValue);
						
						panelList.add(panel);
						
					}else if(field.ClassType.equalsIgnoreCase("Protocol") && i != 0){
						
						protocol.setName(cellValue);
						
						if(!protocolList.contains(protocol))
							protocolList.add(protocol);
					
					}else if(field.ClassType.equalsIgnoreCase("description") && i != 0){
						
						measurement.setDescription(cellValue);
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
			}
			
			if(protocolIndex != -1 && featureIndex != 1){
				
				String protocolName = protocol.getName();
				String featureName = measurement.getName();
				
				if(protocolName != null && featureName != null){
					if(protocolMeasurementList.containsKey(protocolName)){
						List<String> featureList = protocolMeasurementList.get(protocolName);
						featureList.add(featureName);
						protocolMeasurementList.put(protocolName, featureList);
					}else{
						List<String> featureList = new ArrayList<String>();
						protocolMeasurementList.put(protocolName, featureList);
					}
				}
			}
		}
		
		try {
			
			db.update(measurementList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME);
			
			if(protocolMeasurementList.size() != 0){
				
				for(Protocol p : protocolList){
					List<String> featureList = protocolMeasurementList.get(p.getName());
					List<Measurement> featureEntityList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureList));
					List<Integer> featureIdList = new ArrayList<Integer>();
					for(Measurement m : featureEntityList){
						featureIdList.add(m.getId());
					}
					p.setFeatures_Id(featureIdList);
				}
			}
			
			db.update(protocolList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);
			db.update(panelList, Database.DatabaseAction.ADD_IGNORE_EXISTING, Panel.NAME);
			db.update(observedValueList, Database.DatabaseAction.ADD_IGNORE_EXISTING, ObservedValue.VALUE, 
					ObservedValue.TARGET_NAME, ObservedValue.FEATURE_NAME);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
	}
	
}
