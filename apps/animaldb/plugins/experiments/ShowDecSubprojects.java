/* Date:        July 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.experiments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ShowDecSubprojects extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6863037294184185044L;
	private List<DecSubproject> experimentList = new ArrayList<DecSubproject>();
	private CommonService ct = CommonService.getInstance();
	private String action = "init";
	private int listId = 0;
	private List<Code> concernCodeList;
	private List<Code> goalCodeList;
	private List<Code> specialTechnCodeList;
	private List<Code> lawDefCodeList;
	private List<Code> toxResCodeList;
	private List<Code> anaesthesiaCodeList;
	private List<Code> painManagementCodeList;
	private List<Code> animalEndStatusCodeList;
	private List<ObservationTarget> decApplicationList;
	
	public ShowDecSubprojects(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_experiments_ShowDecSubprojects";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/experiments/ShowDecSubprojects.ftl";
	}
	
	public int getListId()
	{
		return listId;
	}

	public void setListId(int listId)
	{
		this.listId = listId;
	}
	
	public void setExperimentList(List<DecSubproject> experimentList) {
		this.experimentList = experimentList;
	}

	public List<DecSubproject> getExperimentList() {
		return experimentList;
	}
	
	public void setConcernCodeList(List<Code> concernCodeList)
	{
		this.concernCodeList = concernCodeList;
	}

	public List<Code> getConcernCodeList()
	{
		return concernCodeList;
	}

	public void setGoalCodeList(List<Code> goalCodeList)
	{
		this.goalCodeList = goalCodeList;
	}

	public List<Code> getGoalCodeList()
	{
		return goalCodeList;
	}

	public void setSpecialTechnCodeList(List<Code> specialTechnCodeList)
	{
		this.specialTechnCodeList = specialTechnCodeList;
	}

	public List<Code> getSpecialTechnCodeList()
	{
		return specialTechnCodeList;
	}

	public void setLawDefCodeList(List<Code> lawDefCodeList)
	{
		this.lawDefCodeList = lawDefCodeList;
	}

	public List<Code> getLawDefCodeList()
	{
		return lawDefCodeList;
	}

	public void setToxResCodeList(List<Code> toxResCodeList)
	{
		this.toxResCodeList = toxResCodeList;
	}

	public List<Code> getToxResCodeList()
	{
		return toxResCodeList;
	}

	public void setAnaesthesiaCodeList(List<Code> anaesthesiaCodeList)
	{
		this.anaesthesiaCodeList = anaesthesiaCodeList;
	}

	public List<Code> getAnaesthesiaCodeList()
	{
		return anaesthesiaCodeList;
	}

	public void setPainManagementCodeList(List<Code> painManagementCodeList)
	{
		this.painManagementCodeList = painManagementCodeList;
	}

	public List<Code> getPainManagementCodeList()
	{
		return painManagementCodeList;
	}

	public void setAnimalEndStatusCodeList(List<Code> animalEndStatusCodeList)
	{
		this.animalEndStatusCodeList = animalEndStatusCodeList;
	}

	public List<Code> getAnimalEndStatusCodeList()
	{
		return animalEndStatusCodeList;
	}

	public void setDecApplicationList(List<ObservationTarget> decApplicationList) {
		this.decApplicationList = decApplicationList;
	}

	public List<ObservationTarget> getDecApplicationList() {
		return decApplicationList;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			this.setAction(request.getAction());
		
			if (action.equals("AddEdit"))
			{
				int id = request.getInt("id");
				listId = id;
			}
			if (action.equals("Show"))
			{
				// No action here
			}
			if (action.equals("addEditDecSubproject")) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				
				// Get values from form
				
				// Name
				String name = "";
				if (request.getString("name") != null && !request.getString("name").equals("")) {
					name = request.getString("name");
				} else {
					throw(new Exception("No name given - Subproject not added"));
				}
				
				// DEC Project (Application)
				int decappId = 0;
				if (request.getString("decapp") != null) {
					String decappIdString = request.getString("decapp");
					decappIdString = decappIdString.replace(".", "");
					decappIdString = decappIdString.replace(",", "");
					decappId = Integer.parseInt(decappIdString);
				} else {
					throw(new Exception("No DEC Project (Application) given - Subproject not added"));
				}
				// DEC Subproject code
				String decnumber = "";
				if (request.getString("decnumber") != null && !request.getString("decnumber").equals("")) {
					decnumber = request.getString("decnumber");
				} else {
					throw(new Exception("No DEC Subproject Code given - Subproject not added"));
				}
				// Check if combination of Project Number + Subproject Code unique
				int featureId = ct.getMeasurementId("DecApplication");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, decappId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
				if (listId != 0) {
					// If editing existing project, don't take existing code(s) for that into account
					int projectId = ct.getObservationTargetId(getDecSubprojectByListId().getName());
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.NOT, projectId));
				}
				List<ObservedValue> valueList = q.find();
				// Iterate through list of values where other subprojects are linked to our master project
				for (ObservedValue value : valueList) {
					int subprojectId = value.getTarget_Id();
					featureId = ct.getMeasurementId("ExperimentNr");
					String otherCode = ct.getMostRecentValueAsString(subprojectId, featureId);
					if (!otherCode.equals("")) {
						if (otherCode.equals(decnumber)) {
							throw(new Exception("DEC Subproject Code not unique within DEC Project - Subproject not added"));
						}
					}
				}
				
				// DEC subproject application pdf
				String decapppdf = null;
				if (request.getString("decapppdf") != null && !request.getString("decapppdf").equals("")) {
					decapppdf = request.getString("decapppdf");
				}
				
				// Variables from lookup boxes
				String concern = request.getString("concern");
				String goal = request.getString("goal");
				String specialtechn = request.getString("specialtechn");
				String lawdef = request.getString("lawdef");
				String toxres = request.getString("toxres");
				String anaesthesia = request.getString("anaesthesia");
				String painmanagement = request.getString("painmanagement");
				String endstatus = request.getString("endstatus");
				
				// Remarks
				String remarks = null;
				if (request.getString("remarks") != null && !request.getString("remarks").equals("")) {
					remarks = request.getString("remarks");
				}
				
				// Get most recent Project start and end dates
				SimpleDateFormat sdfMolgenis = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				featureId = ct.getMeasurementId("StartDate");
				String projectStartDateString = ct.getMostRecentValueAsString(decappId, featureId);
				Date projectStartDate = sdfMolgenis.parse(projectStartDateString);
				featureId = ct.getMeasurementId("EndDate");
				Date projectEndDate = null;
				String projectEndDateString = ct.getMostRecentValueAsString(decappId, featureId);
				if (!projectEndDateString.equals("")) {
					projectEndDate = sdfMolgenis.parse(projectEndDateString);
				}
				
				// Start date-time
				String starttimeString = "";
				Date starttime = null;
				if (request.getString("starttime") != null) {
					starttimeString = request.getString("starttime");
					if (!starttimeString.equals("")) {
						starttime = sdf.parse(starttimeString);
						// Check against Project time boundaries
						if (starttime.before(projectStartDate)) {
							throw(new Exception("Start date outside DEC Project time span - Subproject not added"));
						}
						if (projectEndDate != null && starttime.after(projectEndDate)) {
							throw(new Exception("Start date outside DEC Project time span - Subproject not added"));
						}
					} else {
						throw(new Exception("No start date given - Subproject not added"));
					}
				} else {
					throw(new Exception("No start date given - Subproject not added"));
				}
				
				// End date-time
				Date endtime = null;
				String endtimeString = null;
				if (request.getString("endtime") != null) {
					endtimeString = request.getString("endtime");
					if (!endtimeString.equals("")) {
						endtime = sdf.parse(endtimeString);
						// Check against Project time boundaries
						if (endtime.before(projectStartDate) ||
							endtime.after(projectEndDate)) {
							throw(new Exception("End date outside DEC Project time span - Subproject not added"));
						}
					}
				}
				
				// Some variables we need later on
				int investigationId = ct.getOwnUserInvestigationId(this.getLogin().getUserId());
				Calendar myCal = Calendar.getInstance();
				Date now = myCal.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Check if edit or add
				int projectId;
				if (listId == 0) {
					// Make new DEC subproject (experiment)
					projectId = ct.makePanel(investigationId, name, this.getLogin().getUserId());
					int protocolId = ct.getProtocolId("SetTypeOfGroup");
					int measurementId = ct.getMeasurementId("TypeOfGroup");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationId, 
							now, null, protocolId, measurementId, projectId, "Experiment", 0));
				} else {
					// Get existing DEC subproject
					projectId = ct.getObservationTargetId(getDecSubprojectByListId().getName());
				}
				
				// Set values
				// Nice feature of pheno model: we don't have to overwrite the old values
				// We just make new ones and the most recent ones count!
				int protocolId = ct.getProtocolId("SetDecSubprojectSpecs");
				ProtocolApplication app = ct.createProtocolApplication(investigationId, protocolId);
				db.add(app);
				int protocolApplicationId = app.getId();
				int measurementId = ct.getMeasurementId("DecApplication");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, null, decappId));
				measurementId = ct.getMeasurementId("ExperimentNr");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, decnumber, 0));
				if (decapppdf != null) {
					measurementId = ct.getMeasurementId("DecSubprojectApplicationPdf");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime, measurementId, projectId, decapppdf, 0));
				}
				measurementId = ct.getMeasurementId("Concern");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, concern, 0));
				measurementId = ct.getMeasurementId("Goal");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, goal, 0));
				measurementId = ct.getMeasurementId("SpecialTechn");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, specialtechn, 0));
				measurementId = ct.getMeasurementId("LawDef");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, lawdef, 0));
				measurementId = ct.getMeasurementId("ToxRes");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, toxres, 0));
				measurementId = ct.getMeasurementId("Anaesthesia");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, anaesthesia, 0));
				measurementId = ct.getMeasurementId("PainManagement");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, painmanagement, 0));
				measurementId = ct.getMeasurementId("AnimalEndStatus");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, endstatus, 0));
				if (remarks != null) {
					measurementId = ct.getMeasurementId("OldAnimalDBRemarks");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime, measurementId, projectId, remarks, 0));
				}
				measurementId = ct.getMeasurementId("StartDate");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, starttimeString, 0));
				if (endtimeString != null) {
					measurementId = ct.getMeasurementId("EndDate");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime,measurementId, projectId, endtimeString, 0));
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				// Reload, so list is refreshed
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("DEC Subproject successfully added", true));
				this.reload(db);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public DecSubproject getDecSubprojectByListId() {
		if (listId == 0) return null;
		return experimentList.get(listId - 1);
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		// Populate experiments list
		experimentList.clear();
		try {
			List<Integer> investigationIds = ct.getWritableUserInvestigationIds(this.getLogin().getUserId());
			List<ObservationTarget> expList = ct.getAllMarkedPanels("Experiment", investigationIds);
			int pos = 1;
			for (ObservationTarget currentExp : expList) {
				String name = currentExp.getName();
				
				int featureId = ct.getMeasurementId("ExperimentNr");
				String experimentNr = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				String DecSubprojectApplicationPDF = "";
				featureId = ct.getMeasurementId("DecSubprojectApplicationPdf");
				DecSubprojectApplicationPDF = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("Concern");
				String concern = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("Goal");
				String goal = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("SpecialTechn");
				String specialTechn = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("LawDef");
				String lawDef = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("ToxRes");
				String toxRes = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("Anaesthesia");
				String anaesthesia = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("PainManagement");
				String painManagement = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("AnimalEndStatus");
				String animalEndStatus = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("OldAnimalDBRemarks");
				String oldAnimalDBRemarks = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				featureId = ct.getMeasurementId("DecApplication");
				int decApplicationId = ct.getMostRecentValueAsXref(currentExp.getId(), featureId);
				String decApplicationName = ct.getObservationTargetById(decApplicationId).getName();
				
				String startDate = null;
				featureId = ct.getMeasurementId("StartDate");
				startDate = ct.getMostRecentValueAsString(currentExp.getId(), featureId);
				
				String endDate = null;
				featureId = ct.getMeasurementId("EndDate");
				endDate = ct.getMostRecentValueAsString(currentExp.getId(), featureId);

				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				featureId = ct.getMeasurementId("Experiment");
				// Make list of ID's of all animals that are alive
				List<Integer> aliveAnimalIdList = ct.getAllObservationTargetIds("Individual", true, investigationIds);
				int nrOfAnimals = 0;
				if (aliveAnimalIdList.size() > 0) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, currentExp.getId()));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, aliveAnimalIdList));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, now));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
					nrOfAnimals = q.count();
				}
				
				DecSubproject tmpExp = new DecSubproject();
				tmpExp.setId(currentExp.getId());
				tmpExp.setDecExpListId(pos);
				tmpExp.setName(name);
				tmpExp.setExperimentNr(experimentNr);
				tmpExp.setDecSubprojectApplicationPDF(DecSubprojectApplicationPDF);
				tmpExp.setConcern(concern);
				tmpExp.setGoal(goal);
				tmpExp.setSpecialTechn(specialTechn);
				tmpExp.setLawDef(lawDef);
				tmpExp.setToxRes(toxRes);
				tmpExp.setAnaesthesia(anaesthesia);
				tmpExp.setPainManagement(painManagement);
				tmpExp.setAnimalEndStatus(animalEndStatus);
				tmpExp.setOldAnimalDBRemarks(oldAnimalDBRemarks);
				if (decApplicationId != 0) tmpExp.setDecApplicationId(decApplicationId);
				tmpExp.setDecApplication(decApplicationName);
				tmpExp.setNrOfAnimals(nrOfAnimals);
				if (startDate != null) tmpExp.setStartDate(startDate);
				if (endDate != null) tmpExp.setEndDate(endDate);
				experimentList.add(tmpExp);
				
				pos++;
			}
		} catch (Exception e) {
			this.getMessages().clear();
			String message = "Something went wrong while loading DEC subprojects";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
		
		try {
			List<Integer> investigationIds = ct.getWritableUserInvestigationIds(this.getLogin().getUserId());
			// Concern
			this.setConcernCodeList(ct.getAllCodesForFeature("Concern"));
			// Goal
			this.setGoalCodeList(ct.getAllCodesForFeature("Goal"));
			// SpecialTechn
			this.setSpecialTechnCodeList(ct.getAllCodesForFeature("SpecialTechn"));
			// LawDef
			this.setLawDefCodeList(ct.getAllCodesForFeature("LawDef"));
			// ToxRes
			this.setToxResCodeList(ct.getAllCodesForFeature("ToxRes"));
			// Anaesthesia
			this.setAnaesthesiaCodeList(ct.getAllCodesForFeature("Anaesthesia"));
			// PainManagement
			this.setPainManagementCodeList(ct.getAllCodesForFeature("PainManagement"));
			// AnimalEndStatus
			this.setAnimalEndStatusCodeList(ct.getAllCodesForFeature("AnimalEndStatus"));
			// decApplicationList
			this.setDecApplicationList(ct.getAllMarkedPanels("DecApplication", investigationIds));
		} catch (Exception e) {
			this.getMessages().clear();
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
