package plugins.experiments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DecSubproject {
	private int id;
	private int decExpListId;
	private String Name;
	private String ExperimentNr;
	private String DecSubprojectApplicationPDF;
	private String Concern;
	private String Goal;
	private String SpecialTechn;
	private String LawDef;
	private String ToxRes;
	private String Anaesthesia;
	private String PainManagement;
	private String AnimalEndStatus;
	private String OldAnimalDBRemarks;
	private int DecApplicationId;
	private String decApplication;
	private int NrOfAnimals;
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	
	public void setId(int id)
	{
		this.id = id;
	}
	public int getId()
	{
		return id;
	}
	public void setDecExpListId(int decExpListId)
	{
		this.decExpListId = decExpListId;
	}
	public int getDecExpListId()
	{
		return decExpListId;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public String getName() {
		return Name;
	}
	public void setNrOfAnimals(int nrOfAnimals) {
		NrOfAnimals = nrOfAnimals;
	}
	public int getNrOfAnimals() {
		return NrOfAnimals;
	}
	public void setExperimentNr(String experimentNr) {
		ExperimentNr = experimentNr;
	}
	public String getExperimentNr() {
		return ExperimentNr;
	}
	public void setDecSubprojectApplicationPDF(
			String decSubprojectApplicationPDF) {
		DecSubprojectApplicationPDF = decSubprojectApplicationPDF;
	}
	public String getDecSubprojectApplicationPDF() {
		return DecSubprojectApplicationPDF;
	}
	public void setConcern(String concern) {
		Concern = concern;
	}
	public String getConcern() {
		return Concern;
	}
	public void setGoal(String goal) {
		Goal = goal;
	}
	public String getGoal() {
		return Goal;
	}
	public void setSpecialTechn(String specialTechn) {
		SpecialTechn = specialTechn;
	}
	public String getSpecialTechn() {
		return SpecialTechn;
	}
	public void setLawDef(String lawDef) {
		LawDef = lawDef;
	}
	public String getLawDef() {
		return LawDef;
	}
	public void setToxRes(String toxRes) {
		ToxRes = toxRes;
	}
	public String getToxRes() {
		return ToxRes;
	}
	public void setAnaesthesia(String anaesthesia) {
		Anaesthesia = anaesthesia;
	}
	public String getAnaesthesia() {
		return Anaesthesia;
	}
	public void setPainManagement(String painManagement) {
		PainManagement = painManagement;
	}
	public String getPainManagement() {
		return PainManagement;
	}
	public void setAnimalEndStatus(String animalEndStatus) {
		AnimalEndStatus = animalEndStatus;
	}
	public String getAnimalEndStatus() {
		return AnimalEndStatus;
	}
	public void setOldAnimalDBRemarks(String oldAnimalDBRemarks) {
		OldAnimalDBRemarks = oldAnimalDBRemarks;
	}
	public String getOldAnimalDBRemarks() {
		if (OldAnimalDBRemarks == null) {
			return "";
		}
		return OldAnimalDBRemarks;
	}
	public void setDecApplicationId(int decApplicationId) {
		DecApplicationId = decApplicationId;
	}
	public int getDecApplicationId() {
		return DecApplicationId;
	}
	public void setDecApplication(String decApplication) {
		this.decApplication = decApplication;
	}
	public String getDecApplication() {
		return decApplication;
	}
	public void setStartDate(String startDate) throws ParseException
	{
		this.startDate = sdf.parse(startDate);
	}
	public String getStartDate()
	{
		if (startDate == null) return "";
		return sdf.format(startDate);
	}
	public void setEndDate(String endDate) throws ParseException
	{
		if (endDate.equals("")){
			this.endDate = null;
		}else {
			this.endDate = sdf.parse(endDate);
		}
	}
	public String getEndDate()
	{
		if (endDate == null) return "";
		return sdf.format(endDate);
	}
}
