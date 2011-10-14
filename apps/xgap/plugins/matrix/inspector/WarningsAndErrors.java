package plugins.matrix.inspector;

import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.core.Nameable;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationElement;

public class WarningsAndErrors {
	
	private List<String> warnings;
	private List<String> errors;
	private List<String> missingRowAnnotations;
	private List<String> missingColumnAnnotations;
	
	public WarningsAndErrors(Data data, Database db, DataMatrixInstance modelInstance) throws DatabaseException{
		
		List<String> rowNamesOfInstance = modelInstance.getRowNames();
		List<String> colNamesOfInstance = modelInstance.getColNames();
		
		List<ObservationElement> rowsInDb = db.find(ObservationElement.class, new QueryRule("name", Operator.IN, rowNamesOfInstance));
		List<ObservationElement> colsInDb = db.find(ObservationElement.class, new QueryRule("name", Operator.IN, colNamesOfInstance));
		
		this.warnings = checkForWarnings(data, db, modelInstance, rowsInDb, colsInDb);
		this.errors = checkForErrors();
		this.missingRowAnnotations = checkForMissingAnnotations(rowNamesOfInstance, rowsInDb);
		this.missingColumnAnnotations = checkForMissingAnnotations(colNamesOfInstance, colsInDb);
	}
	

	private List<String> checkForErrors(){
		List<String> errors = new ArrayList<String>();
		
		// what kind of additional checks would we need?
		
		return errors;
	}
	
	private List<String> checkForMissingAnnotations(List<String> namesOfInstance, List<ObservationElement> inDb){
		List<String> missing = new ArrayList<String>();
		
		List<String> dbNames = new ArrayList<String>();
		for(Nameable iden : inDb){
			dbNames.add(iden.getName());
		}
		
		if(namesOfInstance.size() == dbNames.size()){
			return missing;
		}
		
		for(String instanceName : namesOfInstance){
			if(!dbNames.contains(instanceName)){
				missing.add(instanceName);
			}
		}
		
		return missing;
	}
	
	private List<String> checkForWarnings(Data data, Database db, DataMatrixInstance modelInstance, List<ObservationElement> rows, List<ObservationElement> cols) throws DatabaseException{
		List<String> warnings = new ArrayList<String>();
		
		if(data.getStorage().equals("BinaryFile")){
			//BinaryMatrix instance = (BinaryMatrix) model.getBrowser().getModel().getInstance();
			BinaryDataMatrixInstance instance = (BinaryDataMatrixInstance) modelInstance;
			
			if(!data.getFeatureType().equals(instance.getData().getFeatureType())){
				warnings.add("Column type conflict: Data declares " + data.getFeatureType() + " while the original binary file was saved having " + instance.getData().getFeatureType());
			}
			if(!data.getTargetType().equals(instance.getData().getTargetType())){
				warnings.add("Row type conflict: Data declares " + data.getTargetType() + " while the original binary file was saved having " + instance.getData().getTargetType());
			}
			if(!data.getValueType().equals(instance.getData().getValueType())){
				warnings.add("Value type conflict: Data declares " + data.getValueType() + " while the original binary file was saved having " + instance.getData().getValueType());
			}
			if(!data.getInvestigation_Name().equals(instance.getData().getInvestigation_Name())){
				warnings.add("Investigation name conflict: Data declares " + data.getInvestigation_Name() + " while the original binary file was saved having " + instance.getData().getInvestigation_Name());
			}
			if(!data.getName().equals(instance.getData().getName())){
				warnings.add("Name conflict: Data declares " + data.getName() + " while the original binary file was saved having " + instance.getData().getName());
			}
			if(!data.getStorage().equals(instance.getData().getStorage())){
				warnings.add("Source type conflict: Data declares " + data.getStorage() + " while the original binary file was saved having " + instance.getData().getStorage());
			}
		}
		
		for(ObservationElement iden : rows){
			if(!iden.get__Type().equals(data.getTargetType())){
				warnings.add("Row element conflict: Identifiable " + iden.getName() + " is of type " + iden.get__Type() + " while Data declares " + data.getTargetType());
			}
		}
		for(ObservationElement iden : cols){
			if(!iden.get__Type().equals(data.getFeatureType())){
				warnings.add("Column element conflict: Identifiable " + iden.getName() + " is of type " + iden.get__Type() + " while Data declares " + data.getFeatureType());
			}
		}
		return warnings;
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	public List<String> getErrors()
	{
		return errors;
	}

	public List<String> getMissingRowAnnotations()
	{
		return missingRowAnnotations;
	}

	public List<String> getMissingColumnAnnotations()
	{
		return missingColumnAnnotations;
	}

}
