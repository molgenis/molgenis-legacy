/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
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

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
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

public class ShowAnimalsInSubprojects extends PluginModel<Entity>
{
	private static final long serialVersionUID = 5429577464896082058L;
	private List<ObservationTarget> subprojectList;
	private List<Integer> allAnimalIdList;
	private List<Integer> animalRemoveIdList = new ArrayList<Integer>();
	private List<Integer> animalIdList = new ArrayList<Integer>();
	private List<Code> expectedDiscomfortCodeList;
	private List<Code> painManagementCodeList;
	private List<Code> anaesthesiaCodeList;
	private List<Code> expectedEndstatusCodeList;
	private List<Code> actualDiscomfortCodeList;
	private List<Code> actualEndstatusCodeList;
	private CommonService ct = CommonService.getInstance();
	private String action = "init";
	private ObservationTarget animalToAddOrRemove;
	private ObservationTarget subproject;
	private List<MolgenisBatch> batchList = new ArrayList<MolgenisBatch>();
	private SimpleDateFormat oldDateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

	public ShowAnimalsInSubprojects(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_experiments_ShowAnimalsInSubprojects";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/experiments/ShowAnimalsInSubprojects.ftl";
	}
	
	public String getCurrentDate() {
		Date now = new Date();
		return oldDateOnlyFormat.format(now);
	}

	public void setActualDiscomfortCodeList(List<Code> actualDiscomfortCodeList)
	{
		this.actualDiscomfortCodeList = actualDiscomfortCodeList;
	}

	public List<Code> getActualDiscomfortCodeList()
	{
		return actualDiscomfortCodeList;
	}

	public void setActualEndstatusCodeList(List<Code> actualEndstatusCodeList)
	{
		this.actualEndstatusCodeList = actualEndstatusCodeList;
	}

	public List<Code> getActualEndstatusCodeList()
	{
		return actualEndstatusCodeList;
	}

	public void setExpectedDiscomfortCodeList(List<Code> expectedDiscomfortCodeList)
	{
		this.expectedDiscomfortCodeList = expectedDiscomfortCodeList;
	}

	public List<Code> getExpectedDiscomfortCodeList()
	{
		return expectedDiscomfortCodeList;
	}

	public void setExpectedEndstatusCodeList(List<Code> expectedEndstatusCodeList)
	{
		this.expectedEndstatusCodeList = expectedEndstatusCodeList;
	}

	public List<Code> getExpectedEndstatusCodeList()
	{
		return expectedEndstatusCodeList;
	}

	public void setPainManagementCodeList(List<Code> painManagementCodeList)
	{
		this.painManagementCodeList = painManagementCodeList;
	}

	public List<Code> getPainManagementCodeList()
	{
		return painManagementCodeList;
	}

	public void setAnaesthesiaCodeList(List<Code> anaesthesiaCodeList)
	{
		this.anaesthesiaCodeList = anaesthesiaCodeList;
	}

	public List<Code> getAnaesthesiaCodeList()
	{
		return anaesthesiaCodeList;
	}

	public List<ObservationTarget> getSubprojectList()
	{
		return subprojectList;
	}

	public void setSubprojectList(List<ObservationTarget> subprojectList)
	{
		this.subprojectList = subprojectList;
	}

	public List<Integer> getAnimalIdList()
	{
		return animalIdList;
	}

	public void setAnimalIdList(List<Integer> animalIdList)
	{
		this.animalIdList = animalIdList;
	}

	public void setAllAnimalList(List<Integer> allAnimalIdList)
	{
		this.allAnimalIdList = allAnimalIdList;
	}

	public List<Integer> getAllAnimalIdList()
	{
		return allAnimalIdList;
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

	public void setSubproject(ObservationTarget subproject)
	{
		this.subproject = subproject;
	}

	public ObservationTarget getSubproject()
	{
		return subproject;
	}
	
	public void setBatchList(List<MolgenisBatch> batchList) {
		this.batchList = batchList;
	}

	public List<MolgenisBatch> getBatchList() {
		return batchList;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			this.action = request.getString("__action");

			if (action.equals("ShowAnimalsInSubproject"))
			{
				animalIdList.clear();
				
				// Get chosen subproject and set in this class
				int subprojectId = request.getInt("id");
				setSubproject(ct.getObservationTargetById(subprojectId));
				
				// Find all the animals currently in this DEC subproject
				java.sql.Date nowDb = new java.sql.Date(new Date().getTime());
				int featureId = ct.getMeasurementId("Experiment");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, subprojectId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
				q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, nowDb));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				List<ObservedValue> valueList = q.find();
				for (ObservedValue v : valueList) {
					animalIdList.add(v.getTarget_Id());
				}
				this.reload(db);
			}
			
			if (action.equals("RemoveAnimalsFromSubproject"))
			{
				animalRemoveIdList.clear();
				for (int animalCounter = 0; animalCounter < animalIdList.size(); animalCounter++) {
					if (request.getBool("rem" + animalCounter) != null) {
						animalRemoveIdList.add(animalIdList.get(animalCounter));
					}
				}
				this.reload(db);
			}
			
			if (action.equals("AddAnimalToSubproject"))
			{
				// no action required here
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
					subProjectRemovalDate = oldDateOnlyFormat.parse(subProjectRemovalDateString);
				} else {
					throw(new Exception("No removal date given - animal(s) not removed"));
				}
				
				// Date-time of death (if applicable)
				Date deathDate = null;
				if (request.getString("deathdate") != null) {
					String deathDateString = request.getString("deathdate");
					if (!deathDateString.equals("")) {
						deathDate = oldDateOnlyFormat.parse(deathDateString);
					}
				}
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				for (int animalId : animalRemoveIdList) {
					// Get DEC subproject
					int featureId = ct.getMeasurementId("Experiment");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
					q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, subproject.getId()));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() == 1) // if not, no or multiple experiments found, so something terribly wrong
					{
						ObservedValue value = valueList.get(0);
						// Set end date-time
						value.setEndtime(subProjectRemovalDate);
						db.update(value);
						
						int investigationId = ct.getOwnUserInvestigationId(this.getLogin().getUserId());
						
						// If applicable, end status Active and set Death date
						if (endstatus.equals("A. Dood in het kader van de proef") || endstatus.equals("B. Gedood na beeindiging van de proef")) {
							featureId = ct.getMeasurementId("Active");
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
							List<ObservedValue> activeValueList = activeQuery.find();
							if (activeValueList.size() > 0) {
								// Take most recent one and update
								ObservedValue activeValue = activeValueList.get(activeValueList.size() - 1);
								activeValue.setEndtime(deathDate);
								activeValue.setValue("Dead");
								db.update(activeValue);
							}
							
							int protocolId = ct.getProtocolId("SetDeathDate");
							int measurementId = ct.getMeasurementId("DeathDate");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationId, 
									deathDate, null, protocolId, measurementId, animalId, 
									newDateOnlyFormat.format(deathDate), 0));
						}
						
						// Set subproject end values
						Date endstatusDate = null;
						if (subProjectRemovalDate != null) {
							endstatusDate = subProjectRemovalDate;
						}
						int protocolId = ct.getProtocolId("AnimalFromSubproject");
						ProtocolApplication app = ct.createProtocolApplication(investigationId, protocolId);
						db.add(app);
						int protocolApplicationId = app.getId();
						int measurementId = ct.getMeasurementId("FromExperiment");
						valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
								endstatusDate, null, measurementId, animalId, null, subproject.getId()));
						measurementId = ct.getMeasurementId("ActualDiscomfort");
						valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
								endstatusDate, null, measurementId, animalId, discomfort, 0));
						measurementId = ct.getMeasurementId("ActualAnimalEndStatus");
						valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
								endstatusDate, null, measurementId, animalId, endstatus, 0));
					} else {
						throw(new Exception("No or multiple open DEC subprojects found - animal(s) not removed"));
					}
				} // end of loop through animal remove list
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Animal(s) successfully removed", true));
				this.reload(db);
			}
			
			if (action.equals("ApplyAddAnimalToSubproject"))
			{
				SimpleDateFormat oldDateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
				SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				
				// Get Subproject start and end dates
				String subprojectStartDateString = ct.getMostRecentValueAsString(subproject.getId(), ct.getMeasurementId("StartDate"));
				Date subprojectStartDate = dbFormat.parse(subprojectStartDateString);
				Date subprojectEndDate = null;
				String subprojectEndDateString = ct.getMostRecentValueAsString(subproject.getId(), ct.getMeasurementId("EndDate"));
				if (!subprojectEndDateString.equals("")) {
					subprojectEndDate = dbFormat.parse(subprojectEndDateString);
				}
				
				// Get values from form for one or more animals
				// Firstly, common values for all animals (TODO: maybe change so you can have separate values for each animal)
				
				// Date of entry
				Date subProjectAdditionDate = null;
				if (request.getString("subprojectadditiondate") != null) {
					String subProjectRemovalDateString = request.getString("subprojectadditiondate");
					subProjectAdditionDate = oldDateOnlyFormat.parse(subProjectRemovalDateString);
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
				
				// Make list of all the animal id's, both individuals ones and those from groups
				List<Integer> animalIdList = new ArrayList<Integer>();
				if (request.getList("animal") != null) {
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
				}
				// Remove animals from id list that are already in an experiment currently
				int featureId = ct.getMeasurementId("Experiment");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
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
					// Calculate sourceTypeSubproject based on animal's SourceType and DEC Subproject history
					String sourceTypeSubproject = null;
					featureId = ct.getMeasurementId("Source");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					valueList = q.find();
					if (valueList.size() > 0)
					{
						int sourceId = valueList.get(0).getRelation_Id();
						featureId = ct.getMeasurementId("SourceType");
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, sourceId));
						q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
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
							featureId = ct.getMeasurementId("Experiment");
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
							q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
							q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.GREATER_EQUAL, startOfYearString));
							int nrOfSubprojects = q.count();
							if (nrOfSubprojects == 1) sourceTypeSubproject = "Hergebruik eerste maal in het registratiejaar";
							if (nrOfSubprojects > 1) sourceTypeSubproject = "Hergebruik tweede, derde enz. maal in het registratiejaar";
						}
					}
					
					// Make 'AnimalInSubproject' protocol application and add values
					int investigationId = ct.getOwnUserInvestigationId(this.getLogin().getUserId());
					int protocolId = ct.getProtocolId("AnimalInSubproject");
					ProtocolApplication app = ct.createProtocolApplication(investigationId, protocolId);
					db.add(app);
					int protocolApplicationId = app.getId();
					int measurementId = ct.getMeasurementId("Experiment");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
							subProjectAdditionDate, null, measurementId, animalId, null, subproject.getId()));
					if (sourceTypeSubproject != null) {
						measurementId = ct.getMeasurementId("SourceTypeSubproject");
						valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
								subProjectAdditionDate, null, measurementId, animalId, sourceTypeSubproject, 0));
					}
					measurementId = ct.getMeasurementId("PainManagement");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
							subProjectAdditionDate, null, measurementId, animalId, painManagement, 0));
					measurementId = ct.getMeasurementId("Anaesthesia");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
							subProjectAdditionDate, null, measurementId, animalId, anaesthesia, 0));
					measurementId = ct.getMeasurementId("ExpectedDiscomfort");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
							subProjectAdditionDate, null, measurementId, animalId, actualDiscomfort, 0));
					measurementId = ct.getMeasurementId("ExpectedAnimalEndStatus");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, 
							subProjectAdditionDate, null, measurementId, animalId, actualEndstatus, 0));
				} // end of for-loop through animals
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().add(new ScreenMessage("Animal(s) successfully added", true));
				this.reload(db);
			}
		}
		catch (Exception e)
		{
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
		ct.makeObservationTargetNameMap(this.getLogin().getUserId(), false);

		try
		{
			List<Integer> investigationIds = ct.getWritableUserInvestigationIds(this.getLogin().getUserId());
			
			// Populate DEC subproject list
			this.setSubprojectList(ct.getAllMarkedPanels("Experiment", investigationIds));
			
			// Populate batch list
			setBatchList(ct.getAllBatches());
			
			// Populate list of all animals
			allAnimalIdList = ct.getAllObservationTargetIds("Individual", true, investigationIds);			
			
			// Populate pain management code list
			this.setPainManagementCodeList(ct.getAllCodesForFeature("PainManagement"));
			// Populate anaesthesia code list
			this.setAnaesthesiaCodeList(ct.getAllCodesForFeature("Anaesthesia"));
			// Populate expected discomfort code list
			this.setExpectedDiscomfortCodeList(ct.getAllCodesForFeature("ExpectedDiscomfort"));
			// Populate expected endstatus code list
			this.setExpectedEndstatusCodeList(ct.getAllCodesForFeature("ExpectedAnimalEndStatus"));
			// Populate actual discomfort code list
			this.setActualDiscomfortCodeList(ct.getAllCodesForFeature("ActualDiscomfort"));
			// Populate actual endstatus code list
			this.setActualEndstatusCodeList(ct.getAllCodesForFeature("ActualAnimalEndStatus"));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

}
