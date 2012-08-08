package plugins.findingProxy;

import java.util.ArrayList;
import java.util.HashMap;

public class testModel {
	
	String testString = "#this is self-generated script!";
	
	String header = "for(rowIndex in 1:nrow(dataSet)){\n" 
				  + "\tobservationTarget = rownames(dataSet)[rowIndex];\n";
	
	String investigationName = "";

	
	String endOfScript = "}";
	
	public  HashMap<String, String> listOfVariable = new HashMap<String, String>();
	
	public HashMap<String, String> getListOfVariable()
	{	
		return listOfVariable;
	}

	public String getHeader()
	{
		return header;
	}
	
	public String getEndOfScript()
	{
		return endOfScript;
	}

	
	public void setListOfVariable(HashMap<String, String> listOfVariable)
	{
		this.listOfVariable = listOfVariable;
	}
	
	public String getTestString(){
		return testString;
	}
	
	public String getInvestigationName()
	{
		return investigationName;
	}

	public void setInvestigationName(String investigationName)
	{
		this.investigationName = investigationName;
	}
	
	public String getAddingObservedValue(){
		
		String observedValue = "observedValue <- c(";
		String featureName = "featureName <- c(";
		
		for(String eachVariable : listOfVariable.keySet()){
			observedValue += eachVariable + ",";
			featureName += "\"" + eachVariable + "\",";
		}
		observedValue = observedValue.substring(0, observedValue.length() - 1) + ");\n";
		featureName = featureName.substring(0, featureName.length() - 1) +");\n";
		
		String addObservedValue = "add.observedvalue(investigation_name = \"" + investigationName 
								+ "\", target_name=observationTarget, feature_name = featureName, value = observedValue);\n";
		addObservedValue = observedValue + featureName + addObservedValue;
		
		return addObservedValue;
	}
	
}
