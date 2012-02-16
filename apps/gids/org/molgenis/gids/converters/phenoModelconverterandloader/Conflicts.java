package org.molgenis.gids.converters.phenoModelconverterandloader;

public class Conflicts {

	private int id;
	private String target;
	private String featureName;
	private String oldValue;
	private String newValue;
	
	public void setVariables(String target, String featureName, int id, String oldValue, String newValue){
		this.target = target;
		this.featureName = featureName;
		this.id = id;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getId() {
		return id;
	}

	public String getTarget() {
		return target;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}
	
}
