package org.molgenis.animaldb.plugins.administration;

public class DecSubproject extends DecEntity {
	private int decExpListId;
	private String experimentNr;
	private String experimentTitle;
	private String decSubprojectApplicationPDF;
	private String concern;
	private String goal;
	private String specialTechn;
	private String lawDef;
	private String toxRes;
	private String anaesthesia;
	private String painManagement;
	private String animalEndStatus;
	private String remarks;
	private int decApplicationId;
	private String decApplication;
	private int nrOfAnimals;
	
	public void setDecExpListId(int decExpListId)
	{
		this.decExpListId = decExpListId;
	}
	public int getDecExpListId()
	{
		return decExpListId;
	}
	
	public void setNrOfAnimals(int nrOfAnimals) {
		this.nrOfAnimals = nrOfAnimals;
	}
	public int getNrOfAnimals() {
		return nrOfAnimals;
	}
	public void setExperimentNr(String experimentNr) {
		this.experimentNr = experimentNr;
	}
	public String getExperimentNr() {
		return experimentNr;
	}
	public void setExperimentTitle(String experimentTitle) {
		this.experimentTitle = experimentTitle;
	}
	public String getExperimentTitle() {
		return experimentTitle;
	}
	public void setDecSubprojectApplicationPDF(
			String decSubprojectApplicationPDF) {
		this.decSubprojectApplicationPDF = decSubprojectApplicationPDF;
	}
	public String getDecSubprojectApplicationPDF() {
		return decSubprojectApplicationPDF;
	}
	public void setConcern(String concern) {
		this.concern = concern;
	}
	public String getConcern() {
		return concern;
	}
	public void setGoal(String goal) {
		this.goal = goal;
	}
	public String getGoal() {
		return goal;
	}
	public void setSpecialTechn(String specialTechn) {
		this.specialTechn = specialTechn;
	}
	public String getSpecialTechn() {
		return specialTechn;
	}
	public void setLawDef(String lawDef) {
		this.lawDef = lawDef;
	}
	public String getLawDef() {
		return lawDef;
	}
	public void setToxRes(String toxRes) {
		this.toxRes = toxRes;
	}
	public String getToxRes() {
		return toxRes;
	}
	public void setAnaesthesia(String anaesthesia) {
		this.anaesthesia = anaesthesia;
	}
	public String getAnaesthesia() {
		return anaesthesia;
	}
	public void setPainManagement(String painManagement) {
		this.painManagement = painManagement;
	}
	public String getPainManagement() {
		return painManagement;
	}
	public void setAnimalEndStatus(String animalEndStatus) {
		this.animalEndStatus = animalEndStatus;
	}
	public String getAnimalEndStatus() {
		return animalEndStatus;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getRemarks() {
		if (remarks == null) {
			return "";
		}
		return remarks;
	}
	public void setDecApplicationId(int decApplicationId) {
		this.decApplicationId = decApplicationId;
	}
	public int getDecApplicationId() {
		return decApplicationId;
	}
	public void setDecApplication(String decApplication) {
		this.decApplication = decApplication;
	}
	public String getDecApplication() {
		return decApplication;
	}
}
