/* Date:        July 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.administration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class ShowDecSubprojects extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6863037294184185044L;
	private CommonService ct = CommonService.getInstance();
	private String action = "init";
	private int listId = -1;
	private List<Category> experimentNrCodeList;
	private List<Category> concernCodeList;
	private List<Category> goalCodeList;
	private List<Category> specialTechnCodeList;
	private List<Category> lawDefCodeList;
	private List<Category> toxResCodeList;
	private List<Category> anaesthesiaCodeList;
	private List<Category> painManagementCodeList;
	private List<Category> animalEndStatusCodeList;
	private List<Category> expectedDiscomfortCodeList;
	private List<Category> expectedEndstatusCodeList;
	private List<Category> actualDiscomfortCodeList;
	private List<Category> actualEndstatusCodeList;
	private List<ObservationTarget> decApplicationList;
	private List<String> allAnimalNameList;
	private List<Integer> animalRemoveIdList = new ArrayList<Integer>();
	private List<Integer> animalIdList = new ArrayList<Integer>();
	private ObservationTarget animalToAddOrRemove;
	private List<DecSubproject> experimentList = new ArrayList<DecSubproject>();
	//private List<MolgenisBatch> batchList = new ArrayList<MolgenisBatch>();
	MatrixViewer addAnimalsMatrixViewer = null;
	static String ADDANIMALSMATRIX = "addanimalsmatrix";
	MatrixViewer remAnimalsMatrixViewer = null;
	static String REMANIMALSMATRIX = "remanimalsmatrix";
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private boolean refresh = true;
	private int userId = -1;
	private String projectStartDateString = null;
	private String projectEndDateString = null;
	
	//hack to pass database to toHtml() via toHtml(db)
	private Database toHtmlDb;
	public void setToHtmlDb(Database toHtmlDb)
	{
		this.toHtmlDb = toHtmlDb;
	}

	public ShowDecSubprojects(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
		public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<script src=\"res/scripts/custom/animalterm.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}
	
	
	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_administration_ShowDecSubprojects";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/administration/ShowDecSubprojects.ftl";
	}
	
	public String getCurrentDate() {
		Date now = new Date();
		return newDateOnlyFormat.format(now);
	}
	
	public int getListId()
	{
		return listId;
	}
	
	public void setListId(int subprojectId)
	{
		this.listId = subprojectId;
	}
	
	public void setActualDiscomfortCodeList(List<Category> actualDiscomfortCodeList)
	{
		this.actualDiscomfortCodeList = actualDiscomfortCodeList;
	}

	public List<Category> getActualDiscomfortCodeList()
	{
		return actualDiscomfortCodeList;
	}

	public void setActualEndstatusCodeList(List<Category> actualEndstatusCodeList)
	{
		this.actualEndstatusCodeList = actualEndstatusCodeList;
	}

	public List<Category> getActualEndstatusCodeList()
	{
		return actualEndstatusCodeList;
	}

	public void setExpectedDiscomfortCodeList(List<Category> expectedDiscomfortCodeList)
	{
		this.expectedDiscomfortCodeList = expectedDiscomfortCodeList;
	}

	public List<Category> getExpectedDiscomfortCodeList()
	{
		return expectedDiscomfortCodeList;
	}

	public void setExpectedEndstatusCodeList(List<Category> expectedEndstatusCodeList)
	{
		this.expectedEndstatusCodeList = expectedEndstatusCodeList;
	}

	public List<Category> getExpectedEndstatusCodeList()
	{
		return expectedEndstatusCodeList;
	}
	
	public List<Category> getExperimentNrCodeList() {
		return experimentNrCodeList;
	}

	public void setExperimentNrCodeList(List<Category> experimentNrCodeList) {
		this.experimentNrCodeList = experimentNrCodeList;
	}
	
	public void setExperimentList(List<DecSubproject> experimentList) {
		this.experimentList = experimentList;
	}

	public List<DecSubproject> getExperimentList() {
		return experimentList;
	}
	
	public void setConcernCodeList(List<Category> concernCodeList)
	{
		this.concernCodeList = concernCodeList;
	}

	public List<Category> getConcernCodeList()
	{
		return concernCodeList;
	}

	public void setGoalCodeList(List<Category> goalCodeList)
	{
		this.goalCodeList = goalCodeList;
	}

	public List<Category> getGoalCodeList()
	{
		return goalCodeList;
	}

	public void setSpecialTechnCodeList(List<Category> specialTechnCodeList)
	{
		this.specialTechnCodeList = specialTechnCodeList;
	}

	public List<Category> getSpecialTechnCodeList()
	{
		return specialTechnCodeList;
	}

	public void setLawDefCodeList(List<Category> lawDefCodeList)
	{
		this.lawDefCodeList = lawDefCodeList;
	}

	public List<Category> getLawDefCodeList()
	{
		return lawDefCodeList;
	}

	public void setToxResCodeList(List<Category> toxResCodeList)
	{
		this.toxResCodeList = toxResCodeList;
	}

	public List<Category> getToxResCodeList()
	{
		return toxResCodeList;
	}

	public void setAnaesthesiaCodeList(List<Category> anaesthesiaCodeList)
	{
		this.anaesthesiaCodeList = anaesthesiaCodeList;
	}

	public List<Category> getAnaesthesiaCodeList()
	{
		return anaesthesiaCodeList;
	}

	public void setPainManagementCodeList(List<Category> painManagementCodeList)
	{
		this.painManagementCodeList = painManagementCodeList;
	}

	public List<Category> getPainManagementCodeList()
	{
		return painManagementCodeList;
	}

	public String getProjectStartDateString()
	{
		return projectStartDateString;
	}

	public void setProjectStartDateString(String projectStartDateString)
	{
		this.projectStartDateString = projectStartDateString;
	}

	public String getProjectEndDateString()
	{
		return projectEndDateString;
	}

	public void setProjectEndDateString(String projectEndDateString)
	{
		this.projectEndDateString = projectEndDateString;
	}

	public void setAnimalEndStatusCodeList(List<Category> animalEndStatusCodeList)
	{
		this.animalEndStatusCodeList = animalEndStatusCodeList;
	}

	public List<Category> getAnimalEndStatusCodeList()
	{
		return animalEndStatusCodeList;
	}

	public void setDecApplicationList(List<ObservationTarget> decApplicationList) {
		this.decApplicationList = decApplicationList;
	}

	public List<ObservationTarget> getDecApplicationList() {
		return decApplicationList;
	}
	
	public List<Integer> getAnimalIdList()
	{
		return animalIdList;
	}

	public void setAnimalIdList(List<Integer> animalIdList)
	{
		this.animalIdList = animalIdList;
	}

	public void setAllAnimalList(List<String> allAnimalNameList)
	{
		this.allAnimalNameList = allAnimalNameList;
	}

	public List<String> getAllAnimalNameList()
	{
		return allAnimalNameList;
	}
	
	public String getAnimalName(Integer id) {
		try {
			return ct.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}

	public void setAnimalToAddOrRemove(ObservationTarget animalToAddOrRemove)
	{
		this.animalToAddOrRemove = animalToAddOrRemove;
	}

	public ObservationTarget getAnimalToAddOrRemove()
	{
		return animalToAddOrRemove;
	}

	public void setAnimalRemoveIdList(List<Integer> animalRemoveIdList) {
		this.animalRemoveIdList = animalRemoveIdList;
	}

	public List<Integer> getAnimalRemoveIdList() {
		return animalRemoveIdList;
	}
	
	/*public void setBatchList(List<MolgenisBatch> batchList) {
		this.batchList = batchList;
	}

	public List<MolgenisBatch> getBatchList() {
		return batchList;
	}*/

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
	
	public DecSubproject getSelectedDecSubproject() {
		if (listId == -1) return null;
		return experimentList.get(listId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		if (addAnimalsMatrixViewer != null) {
			addAnimalsMatrixViewer.setDatabase(db);
		}
		if (remAnimalsMatrixViewer != null) {
			remAnimalsMatrixViewer.setDatabase(db);
		}
		
		try {
			this.setAction(request.getAction());
			
			if (addAnimalsMatrixViewer != null && action.startsWith(addAnimalsMatrixViewer.getName())) {
				addAnimalsMatrixViewer.handleRequest(db, request);
				action = "AddAnimalToSubproject";
				return; // avoid matrix reinitialization
			}
			if (remAnimalsMatrixViewer != null && action.startsWith(remAnimalsMatrixViewer.getName())) {
				remAnimalsMatrixViewer.handleRequest(db, request);
				action = "EditAnimals";
				return; // avoid matrix reinitialization
			}
		
			if (action.equals("AddEdit") || action.equals("EditAnimals"))
			{
				listId = request.getInt("id");
			}
			
			if (action.equals("Show"))
			{
				// No action here
			}
			
			if (action.equals("addEditDecSubproject")) {
				SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				
				// Get values from form
				
				//TODO auto fill name by combination of "DEC"+ DECNr + subproject code
				// Name
				String name = "DEC ";
				if (request.getString("name") != null && !request.getString("name").equals("")) {
					name = request.getString("name");
					
				}
				
				// Title
				String title = "";
				if (request.getString("experimenttitle") != null && !request.getString("experimenttitle").equals("")) {
					title = request.getString("experimenttitle");
				} else {
					throw(new Exception("No title given - Subproject not added"));
				}
				
				// DEC project (Application)
				String decappName;
				if (request.getString("decapp") != null) {
					Integer decappid = request.getInt("decapp");
					decappName = ct.getObservationTargetLabel(decappid);
					name = decappName;
					
				} else {
					throw(new Exception("No DEC application given - Subproject not added"));
				}
				
								
				// DEC subproject code
				String expnumber = "";
				if (request.getString("expnumber") != null && !request.getString("expnumber").equals("")) {
					expnumber = request.getString("expnumber");
				} else {
					throw(new Exception("No DEC subproject code given - Subproject not added"));
				}
				// Check if combination of Project number + Subproject code unique
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, name));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "DecApplication"));
				if (listId != -1) {
					// If editing existing subproject, don't take existing code(s) for that into account
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.NOT, getSelectedDecSubproject().getId()));
				}
				List<ObservedValue> valueList = q.find();
				// Iterate through list of values where other subprojects are linked to our master project
				for (ObservedValue value : valueList) {
					String subprojectName = value.getTarget_Name();
					String otherCode = ct.getMostRecentValueAsString(subprojectName, "ExperimentNr");
					if (!otherCode.equals("")) {
						if (otherCode.equals(expnumber)) {
							throw(new Exception("DEC subproject code not unique within DEC project - Subproject not added"));
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
				//String projectStartDateString = ct.getMostRecentValueAsString(name, "StartDate");
				setProjectStartDateString(ct.getMostRecentValueAsString(name, "StartDate"));
				Date projectStartDate = newDateOnlyFormat.parse(this.projectStartDateString);
				Date projectEndDate = null;
				//String projectEndDateString = ct.getMostRecentValueAsString(name, "EndDate");
				setProjectEndDateString(ct.getMostRecentValueAsString(name, "EndDate"));
				if (!projectEndDateString.equals("")) {
					projectEndDate = newDateOnlyFormat.parse(this.projectEndDateString);
				}
				
				// Start date
				Date startdate = null;
				if (request.getString("startdate") != null) {
					String startdateString = request.getString("startdate");
					if (!startdateString.equals("")) {
						startdate = newDateOnlyFormat.parse(startdateString);
						// Check against Project time boundaries
						if (startdate.before(projectStartDate)) {
							throw(new Exception("Start date outside DEC project time span - Subproject not added"));
						}
						if (projectEndDate != null && startdate.after(projectEndDate)) {
							throw(new Exception("Start date outside DEC project time span - Subproject not added"));
						}
					} else {
						throw(new Exception("No start date given - Subproject not added"));
					}
				} else {
					throw(new Exception("No start date given - Subproject not added"));
				}
				
				// End date
				Date enddate = null;
				if (request.getString("enddate") != null) {
					String enddateString = request.getString("enddate");
					if (!enddateString.equals("")) {
						enddate = newDateOnlyFormat.parse(enddateString);
						// Check against Project time boundaries
						if (enddate.before(projectStartDate) ||
								enddate.after(projectEndDate)) {
							throw(new Exception("End date outside DEC project time span - Subproject not added"));
						}
					}
				}
				
				// Dec Subproject budget
				String decsubprojectbudget = "";
				if (request.getString("decsubprojectbudget") != null && !request.getString("decsubprojectbudget").equals("")) {
					Integer decsubprojectbudgetint = request.getInt("decsubprojectbudget");
					decsubprojectbudget = Integer.toString(decsubprojectbudgetint);
				} else {
					throw(new Exception("No budget given - Subproject not added"));
				}
				
				// Some variables we need later on
				String investigationName = ct.getOwnUserInvestigationName(this.getLogin().getUserName());
				Calendar myCal = Calendar.getInstance();
				Date now = myCal.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Check if edit or add
				if (listId == -1) {
					// autogenerate subprojectname to be "DEC " + decnr + subprojectnr
					name = name + expnumber;
					// Make new DEC subproject (experiment)
					ct.makePanel(investigationName, name, this.getLogin().getUserName());
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationName, 
							now, null, "SetTypeOfGroup", "TypeOfGroup", name, "Experiment", null));
				} else {
					String selName = getSelectedDecSubproject().getName();
					// check if the subprojectr changed and modify name of the observationtarget accordingly:
					String previousexpnumber = ct.getMostRecentValueAsString(selName, "ExperimentNr");
					if (expnumber != previousexpnumber) {
						name = decappName + expnumber; 
						
						ObservationTarget subproject = ct.getObservationTargetByName(selName);
						subproject.setName(name);
						db.update(subproject);
					}
				}
				
				// Set values
				// Nice feature of pheno model: we don't have to overwrite the old values
				// We just make new ones and the most recent ones count!
				// TODO: this is not entirely true anymore, now that value dates
				// are date-only, without time info, so values from the same day
				// cannot be distinguished anymore!
				// So what we want to do is edit the existing values instead of making new ones!
				ProtocolApplication app = ct.createProtocolApplication(investigationName, "SetDecSubprojectSpecs");
				db.add(app);
				String protocolApplicationName = app.getName();
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "DecApplication", name, null, decappName));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "ExperimentNr", name, expnumber, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "ExperimentTitle", name, title, null));
				if (decapppdf != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "DecSubprojectApplicationPdf", name, decapppdf, null));
				}
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "Concern", name, concern, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "Goal", name, goal, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "SpecialTechn", name, specialtechn, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "LawDef", name, lawdef, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "ToxRes", name, toxres, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "Anaesthesia", name, anaesthesia, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "PainManagement", name, painmanagement, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "AnimalEndStatus", name, endstatus, null));
				if (remarks != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "Remark", name, remarks, null));
				}
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "StartDate", name, newDateOnlyFormat.format(startdate), null));
				if (enddate != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "EndDate", name, newDateOnlyFormat.format(enddate), null));
				}
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "DecSubprojectBudget", name, decsubprojectbudget, null));
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().add(new ScreenMessage("DEC subproject successfully added", true));
				refresh = true;
			}
			
			if (action.equals("EditAnimals"))
			{
				listId = request.getInt("id");
				
				animalIdList.clear();
				
				// Find all the animals currently in this DEC subproject
				java.sql.Date nowDb = new java.sql.Date(new Date().getTime());
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, getSelectedDecSubproject().getName()));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
				q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, nowDb));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				List<ObservedValue> valueList = q.find();
				for (ObservedValue v : valueList) {
					animalIdList.add(v.getTarget_Id());
				}
				
				// (Re)initialize remove animals matrix viewer
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Experiment");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
						ObservedValue.VALUE, Operator.EQUALS, "Alive"));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Experiment"),
						ObservedValue.RELATION, Operator.EQUALS, getSelectedDecSubproject().getId()));
				// TODO: find a way to filter out only the animals that are CURRENTLY in this DEC subproject
				remAnimalsMatrixViewer = new MatrixViewer(this, REMANIMALSMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 2, false, false, filterRules, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				remAnimalsMatrixViewer.setDatabase(db);
			}
			
			if (action.equals("RemoveAnimalsFromSubproject"))
			{
				/*animalRemoveIdList.clear();
				for (int animalCounter = 0; animalCounter < animalIdList.size(); animalCounter++) {
					if (request.getBool("rem" + animalCounter) != null) {
						animalRemoveIdList.add(animalIdList.get(animalCounter));
					}
				}*/
				
				animalRemoveIdList.clear();
				List<ObservationElement> rows = (List<ObservationElement>) remAnimalsMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(REMANIMALSMATRIX + "_selected_" + rowCnt) != null) {
						int animalId = row.getId();
						if (!animalRemoveIdList.contains(animalId)) {
							animalRemoveIdList.add(animalId);
						}
					}
					rowCnt++;
				}
			}
			
			if (action.equals("AddAnimalToSubproject"))
			{
				// (Re)initialize add animals matrix viewer
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Species");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Experiment");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
						ObservedValue.VALUE, Operator.EQUALS, "Alive"));
				//filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Experiment"),
				//		ObservedValue.RELATION, Operator.NOT, getSelectedDecSubproject().getId()));
				//filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Experiment"),
				//		ObservedValue.RELATION, Operator.EQUALS, null));
				// TODO: find a way to filter out only the animals that are CURRENTLY NOT in THIS DEC subproject
				addAnimalsMatrixViewer = new MatrixViewer(this, ADDANIMALSMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 2, false, false, filterRules, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				addAnimalsMatrixViewer.setDatabase(db);
			}
			
			if (action.equals("ApplyRemoveAnimalsFromSubproject"))
			{	
				// Get values from form
				
				// Discomfort
				String discomfort = request.getString("discomfort");
				
				// End status
				String endstatus = request.getString("endstatus");
				
				// Date-time of removal
				Date subProjectRemovalDate = null;
				if (request.getString("subprojectremovaldate") != null) {
					String subProjectRemovalDateString = request.getString("subprojectremovaldate");
					subProjectRemovalDate = newDateOnlyFormat.parse(subProjectRemovalDateString);
				} else {
					throw(new Exception("No removal date given - animal(s) not removed"));
				}
				
				// Date-time of death (if applicable)
				Date deathDate = null;
				if (request.getString("deathdate") != null) {
					String deathDateString = request.getString("deathdate");
					if (!deathDateString.equals("")) {
						deathDate = newDateOnlyFormat.parse(deathDateString);
					}
				}
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				for (int animalId : animalRemoveIdList) {
					String animalName = ct.getObservationTargetLabel(animalId);
					// Get DEC subproject
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
					q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, getSelectedDecSubproject().getName()));
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() == 1) // if not, no or multiple experiments found, so something terribly wrong
					{
						ObservedValue value = valueList.get(0);
						// Set end date-time
						value.setEndtime(subProjectRemovalDate);
						db.update(value);
						
						String investigationName = ct.getOwnUserInvestigationName(this.getLogin().getUserName());
						
						// If applicable, end status Active and set Death date
						if (endstatus.equals("A. Dood in het kader van de proef") || endstatus.equals("B. Gedood na beeindiging van de proef")) {
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active"));
							List<ObservedValue> activeValueList = activeQuery.find();
							if (activeValueList.size() > 0) {
								// Take most recent one and update
								ObservedValue activeValue = activeValueList.get(activeValueList.size() - 1);
								activeValue.setEndtime(deathDate);
								activeValue.setValue("Dead");
								db.update(activeValue);
							}
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationName, 
									deathDate, null, "SetDeathDate", "DeathDate", animalName, 
									newDateOnlyFormat.format(deathDate), null));
						}
						
						// Set subproject end values
						Date endstatusDate = null;
						if (subProjectRemovalDate != null) {
							endstatusDate = subProjectRemovalDate;
						}
						ProtocolApplication app = ct.createProtocolApplication(investigationName, "AnimalFromSubproject");
						db.add(app);
						String protocolApplicationName = app.getName();
						valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
								endstatusDate, null, "FromExperiment", animalName, null, getSelectedDecSubproject().getName()));
						valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
								endstatusDate, null, "ActualDiscomfort", animalName, discomfort, null));
						valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
								endstatusDate, null, "ActualAnimalEndStatus", animalName, endstatus, null));
					} else {
						throw(new Exception("No or multiple open DEC subprojects found - animal(s) not removed"));
					}
				} // end of loop through animal remove list
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().add(new ScreenMessage("Animal(s) successfully removed", true));
				refresh = true;
			}
			
			if (action.equals("ApplyAddAnimalToSubproject"))
			{	
				String subprojectName = getSelectedDecSubproject().getName();
				
				// Get Subproject start and end dates
				String subprojectStartDateString = ct.getMostRecentValueAsString(subprojectName, "StartDate");
				Date subprojectStartDate = newDateOnlyFormat.parse(subprojectStartDateString);
				Date subprojectEndDate = null;
				String subprojectEndDateString = ct.getMostRecentValueAsString(subprojectName, "EndDate");
				if (!subprojectEndDateString.equals("")) {
					subprojectEndDate = newDateOnlyFormat.parse(subprojectEndDateString);
				}
				
				// Get values from form for one or more animals
				// Firstly, common values for all animals (TODO: maybe change so you can have separate values for each animal)
				
				// Date of entry
				Date subProjectAdditionDate = null;
				if (request.getString("subprojectadditiondate") != null) {
					String subProjectRemovalDateString = request.getString("subprojectadditiondate");
					subProjectAdditionDate = newDateOnlyFormat.parse(subProjectRemovalDateString);
					// Check against Subproject time boundaries
					if (subProjectAdditionDate.before(subprojectStartDate) ||
						(subprojectEndDate != null && subProjectAdditionDate.after(subprojectEndDate))) {
						throw(new Exception("Entry date outside DEC Subproject time span - animal(s) not added"));
					}
				} else {
					throw(new Exception("No entry date given - animal(s) not added"));
				}
				
				String painManagement = request.getString("painmanagement");
				String anaesthesia = request.getString("anaesthesia");
				String actualDiscomfort = request.getString("discomfort");
				String actualEndstatus = request.getString("endstatus");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Make list of all the animal id's (, both individuals ones and those from groups)
				List<Integer> animalIdList = new ArrayList<Integer>();
				List<ObservationElement> rows = (List<ObservationElement>) addAnimalsMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(ADDANIMALSMATRIX + "_selected_" + rowCnt) != null) {
						int animalId = row.getId();
						if (!animalIdList.contains(animalId)) {
							animalIdList.add(animalId);
						}
					}
					rowCnt++;
				}
				
				/*if (request.getList("animal") != null) {
					List<?> animalIdsAsObjectsList = request.getList("animal");
					for (Object animalIdAsObject : animalIdsAsObjectsList) {
						String animalIdString = (String)animalIdAsObject;
						animalIdList.add(Integer.parseInt(animalIdString));
					}
				}
				if (request.getList("groupname") != null) {
					List<?> batchIdsAsObjectsList = request.getList("groupname");
					for (Object batchIdAsObject : batchIdsAsObjectsList) {
						String batchIdString = (String)batchIdAsObject;
						int batchId = Integer.parseInt(batchIdString);
						Query<MolgenisBatchEntity> batchQuery = db.query(MolgenisBatchEntity.class);
						batchQuery.addRules(new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, batchId));
						List<MolgenisBatchEntity> entities = batchQuery.find();
						for (MolgenisBatchEntity e : entities) {
							animalIdList.add(e.getObjectId());
						}
					}
				}*/
				
				// Remove animals from id list that are already in an experiment currently
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, animalIdList));
				List<ObservedValue> valueList = q.find();
				if (valueList.size() > 0) {
					String message = "The following animal id's: ";
					for (ObservedValue value : valueList) {
						int animalInExperimentId = value.getTarget_Id();
						message += animalInExperimentId;
						message += " ";
						animalIdList.remove(animalIdList.indexOf(animalInExperimentId));
					}
					message += "were removed because they are already in a DEC subproject";
					this.getMessages().add(new ScreenMessage(message, false));
				}
				
				// Secondly, set animal-specific values
				for (int animalId : animalIdList) {
					String animalName = ct.getObservationTargetLabel(animalId);
					// Calculate sourceTypeSubproject based on animal's SourceType and DEC Subproject history
					String sourceTypeSubproject = null;
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Source"));
					valueList = q.find();
					if (valueList.size() > 0)
					{
						String sourceName = valueList.get(0).getRelation_Name();
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, sourceName));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "SourceType"));
						valueList = q.find();
						if (valueList.size() > 0)
						{
							String sourceType = valueList.get(0).getValue();
							// In most of the cases, SourceTypeSubproject = SourceType
							sourceTypeSubproject = sourceType;
							// SourceTypes 1-1, 1-2 and 1-3 aggregate into SourceTypeSubproject 1
							if (sourceType.equals("Eigen fok binnen uw organisatorische werkeenheid") ||
								sourceType.equals("Andere organisatorische werkeenheid vd instelling")) {
								sourceTypeSubproject = "Geregistreerde fok/aflevering in Nederland";
							}
							// SourceTypeSubproject 6 is for first reuse, 7 for second etc.
							String startOfYearString = Calendar.getInstance().get(Calendar.YEAR) + "-01-01 00:00:00";
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
							q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
							q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.GREATER_EQUAL, startOfYearString));
							int nrOfSubprojects = q.count();
							if (nrOfSubprojects == 1) sourceTypeSubproject = "Hergebruik eerste maal in het registratiejaar";
							if (nrOfSubprojects > 1) sourceTypeSubproject = "Hergebruik tweede, derde enz. maal in het registratiejaar";
						}
					}
					
					// Make 'AnimalInSubproject' protocol application and add values
					String investigationName = ct.getOwnUserInvestigationName(this.getLogin().getUserName());
					ProtocolApplication app = ct.createProtocolApplication(investigationName, "AnimalInSubproject");
					db.add(app);
					String protocolApplicationName = app.getName();
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
							subProjectAdditionDate, null, "Experiment", animalName, null, subprojectName));
					if (sourceTypeSubproject != null) {
						valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
								subProjectAdditionDate, null, "SourceTypeSubproject", animalName, sourceTypeSubproject, null));
					}
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
							subProjectAdditionDate, null, "PainManagement", animalName, painManagement, null));
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
							subProjectAdditionDate, null, "Anaesthesia", animalName, anaesthesia, null));
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
							subProjectAdditionDate, null, "ExpectedDiscomfort", animalName, actualDiscomfort, null));
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, 
							subProjectAdditionDate, null, "ExpectedAnimalEndStatus", animalName, actualEndstatus, null));
				} // end of for-loop through animals
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().add(new ScreenMessage("Animal(s) successfully added", true));
				refresh = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		this.toHtmlDb = db;
		
		if (refresh == true || this.getLogin().getUserId().intValue() != userId) {
			userId = this.getLogin().getUserId().intValue();
			refresh = false;
			try {
				List<String> investigationNames = ct.getWritableUserInvestigationNames(this.getLogin().getUserName());
				
				// Populate batch list
				//setBatchList(ct.getAllBatches());
				
				// Populate list of all animals
				allAnimalNameList = ct.getAllObservationTargetNames("Individual", true, investigationNames);			
				
				// Populate expected discomfort code list
				this.setExpectedDiscomfortCodeList(ct.getAllCodesForFeature("ExpectedDiscomfort"));
				// Populate expected endstatus code list
				this.setExpectedEndstatusCodeList(ct.getAllCodesForFeature("ExpectedAnimalEndStatus"));
				// Populate actual discomfort code list
				this.setActualDiscomfortCodeList(ct.getAllCodesForFeature("ActualDiscomfort"));
				// Populate actual endstatus code list
				this.setActualEndstatusCodeList(ct.getAllCodesForFeature("ActualAnimalEndStatus"));
				// Experimentnrs
				this.setExperimentNrCodeList(ct.getAllCodesForFeature("ExperimentNr"));
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
				this.setDecApplicationList(ct.getAllMarkedPanels("DecApplication", investigationNames));
				
				// Populate subprojects list
				experimentList.clear();
				List<ObservationTarget> expList = ct.getAllMarkedPanels("Experiment", investigationNames);
				int pos = 0;
				for (ObservationTarget currentExp : expList) {
					String expName = currentExp.getName();			
					String experimentNr = ct.getMostRecentValueAsString(expName, "ExperimentNr");
					String experimentTitle = ct.getMostRecentValueAsString(expName, "ExperimentTitle");
					String decSubprojectApplicationPDF = ct.getMostRecentValueAsString(expName, "DecSubprojectApplicationPdf");
					String concern = ct.getMostRecentValueAsString(expName, "Concern");
					String goal = ct.getMostRecentValueAsString(expName, "Goal");
					String specialTechn = ct.getMostRecentValueAsString(expName, "SpecialTechn");
					String lawDef = ct.getMostRecentValueAsString(expName, "LawDef");
					String toxRes = ct.getMostRecentValueAsString(expName, "ToxRes");
					String anaesthesia = ct.getMostRecentValueAsString(expName, "Anaesthesia");
					String painManagement = ct.getMostRecentValueAsString(expName, "PainManagement");
					String animalEndStatus = ct.getMostRecentValueAsString(expName, "AnimalEndStatus");
					String remarks = ct.getMostRecentValueAsString(expName, "Remark");
					String decApplicationName = ct.getMostRecentValueAsXrefName(expName, "DecApplication");
					String startDate = null;
					startDate = ct.getMostRecentValueAsString(expName, "StartDate");
					String endDate = null;
					endDate = ct.getMostRecentValueAsString(expName, "EndDate");
					String decSubprojectBudget = null;
					decSubprojectBudget = ct.getMostRecentValueAsString(expName, "DecSubprojectBudget");
					java.sql.Date nowDb = new java.sql.Date(new Date().getTime());
					int nrOfAnimals = 0;
					if (allAnimalNameList.size() > 0) {
						Query<ObservedValue> q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, expName));
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, allAnimalNameList));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
						q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, nowDb));
						q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
						nrOfAnimals = q.count();
					}
					
					DecSubproject tmpExp = new DecSubproject();
					//tmpExp.setD
					tmpExp.setId(currentExp.getId());
					tmpExp.setDecExpListId(pos);
					tmpExp.setName(expName);
					tmpExp.setExperimentTitle(experimentTitle);
					tmpExp.setExperimentNr(experimentNr);
					tmpExp.setDecSubprojectApplicationPDF(decSubprojectApplicationPDF);
					tmpExp.setConcern(concern);
					tmpExp.setGoal(goal);
					tmpExp.setSpecialTechn(specialTechn);
					tmpExp.setLawDef(lawDef);
					tmpExp.setToxRes(toxRes);
					tmpExp.setAnaesthesia(anaesthesia);
					tmpExp.setPainManagement(painManagement);
					tmpExp.setAnimalEndStatus(animalEndStatus);
					tmpExp.setRemarks(remarks);
					tmpExp.setDecApplication(decApplicationName);
					tmpExp.setNrOfAnimals(nrOfAnimals);
					if (startDate != null) tmpExp.setStartDate(startDate);
					if (endDate != null) tmpExp.setEndDate(endDate);
					tmpExp.setDecSubprojectBudget(decSubprojectBudget);
					experimentList.add(tmpExp);
					
					pos++;
				}
				
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
	
	public String renderAddAnimalsMatrixViewer() {
		if (addAnimalsMatrixViewer != null) {
			addAnimalsMatrixViewer.setDatabase(toHtmlDb);
			return addAnimalsMatrixViewer.render();
		} else {
			return "No viewer available, matrix for adding animals cannot be rendered";
		}
	}
	
	public String renderRemAnimalsMatrixViewer() {
		if (remAnimalsMatrixViewer != null) {
			remAnimalsMatrixViewer.setDatabase(toHtmlDb);
			return remAnimalsMatrixViewer.render();
		} else {
			return "No viewer available, matrix for removing animals cannot be rendered";
		}
	}
	
}
