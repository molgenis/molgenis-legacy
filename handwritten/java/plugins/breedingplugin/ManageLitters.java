/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.breedingplugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ManageLitters extends PluginModel<Entity>
{
	private static final long serialVersionUID = 7608670026855241487L;
	private List<ObservationTarget> parentgroupList;
	private List<Litter> litterList = new ArrayList<Litter>();
	private int selectedParentgroup;
	private int litter;
	private String litterName = "";
	private String datetime = "";
	private String birthdatetime = "";
	private String weandatetime = "";
	private int litterSize;
	private int weanSizeFemale;
	private int weanSizeMale;
	private boolean litterSizeApproximate;
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	private String action = "ShowLitters";
	private String customName = null;
	private int customNumber = -1;

	public ManageLitters(String name, ScreenController<?> parent)
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
		return "plugins_breedingplugin_ManageLitters";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/breedingplugin/ManageLitters.ftl";
	}
	
	// Parent group list related methods:
	public List<ObservationTarget> getParentgroupList() {
		return parentgroupList;
	}
	public void setParentgroupList(List<ObservationTarget> parentgroupList) {
		this.parentgroupList = parentgroupList;
	}
	
	public void setLitterList(List<Litter> litterList) {
		this.litterList = litterList;
	}
	public List<Litter> getLitterList() {
		return litterList;
	}
	
	public String getLitterName() {
		return litterName;
	}
	public void setLitterName(String litterName) {
		this.litterName = litterName;
	}
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	public String getBirthdatetime() {
		return birthdatetime;
	}
	public void setBirthdatetime(String birthdatetime) {
		this.birthdatetime = birthdatetime;
	}
	
	public void setWeandatetime(String weandatetime) {
		this.weandatetime = weandatetime;
	}
	public String getWeandatetime() {
		return weandatetime;
	}

	public int getLitterSize() {
		return litterSize;
	}
	public void setLitterSize(int litterSize) {
		this.litterSize = litterSize;
	}
	
	public void setWeanSizeFemale(int weanSizeFemale) {
		this.weanSizeFemale = weanSizeFemale;
	}
	public int getWeanSizeFemale() {
		return weanSizeFemale;
	}
	
	public void setWeanSizeMale(int weanSizeMale) {
		this.weanSizeMale = weanSizeMale;
	}
	public int getWeanSizeMale() {
		return weanSizeMale;
	}

	public int getSelectedParentgroup() {
		return selectedParentgroup;
	}
	public void setSelectedParentgroup(int selectedParentgroup) {
		this.selectedParentgroup = selectedParentgroup;
	}
	
	public void setLitter(int litter) {
		this.litter = litter;
	}
	public int getLitter() {
		return litter;
	}

	public boolean getLitterSizeApproximate() {
		return litterSizeApproximate;
	}
	public void setLitterSizeApproximate(boolean litterSizeApproximate) {
		this.litterSizeApproximate = litterSizeApproximate;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	public String getAction()
	{
		return action;
	}
	
	public String getCustomNameFeature() throws DatabaseException, ParseException {
		int featureId = ct.getCustomNameFeatureId(this.getLogin().getUserId());
		if (featureId == -1) {
			return null;
		} else {
			return ct.getMeasurementById(featureId).getName();
		}
	}

	private void setUserFields(Tuple request, boolean wean) throws Exception {
		if (wean == true) {
			if (request.getString("weandatetime") == null || request.getString("weandatetime").equals("")) {
				throw new Exception("Wean date cannot be empty");
			}
			setWeandatetime(request.getString("weandatetime"));
			setWeanSizeFemale(request.getInt("weansizefemale"));
			setWeanSizeMale(request.getInt("weansizemale"));
			
			customName = request.getString("customname");
			if (request.getString("customname") == null) {
				customName = "";
			}
			if (request.getInt("startnumber") != null) {
				customNumber = request.getInt("startnumber");
			} else {
				customNumber = 1; // standard start at 1
			}
			
		} else {
			setLitterName(request.getString("littername"));
			String parentgroupIdString = request.getString("parentgroup");
			parentgroupIdString = parentgroupIdString.replace(".", "");
			parentgroupIdString = parentgroupIdString.replace(",", "");
			setSelectedParentgroup(Integer.parseInt(parentgroupIdString));
			if (request.getString("birthdatetime") == null || request.getString("birthdatetime").equals("")) {
				throw new Exception("Birth date cannot be empty");
			}
			setBirthdatetime(request.getString("birthdatetime"));
			setLitterSize(request.getInt("littersize"));
			if (request.getBool("sizeapp_toggle") != null) {
				setLitterSizeApproximate(true);
			} else {
				setLitterSizeApproximate(false);
			}
		}
	}


	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			
			int invid = ct.getOwnUserInvestigationId(this.getLogin().getUserId());
			
			this.action = request.getString("__action");
			
			if (action.equals("AddLitter")) {
				//
			}
			
			if (action.equals("ShowLitters")) {
				//
			}
			
			if (action.equals("ApplyAddLitter")) {
				setUserFields(request, false);
				Date eventDate = sdf.parse(birthdatetime);
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Make group
				int litterid = ct.makePanel(invid, litterName, this.getLogin().getUserId());
				// Mark group as a litter
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litterid, "Litter", 0));

				// Apply other fields using event
				protocolId = ct.getProtocolId("SetLitterSpecs");
				ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
				db.add(app);
				int eventid = app.getId();
				// Parentgroup
				measurementId = ct.getMeasurementId("Parentgroup");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, 
						litterid, null, selectedParentgroup));
				// Date of Birth
				measurementId = ct.getMeasurementId("DateOfBirth");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, 
						litterid, birthdatetime, 0));
				// Size
				measurementId = ct.getMeasurementId("Size");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, litterid, 
						Integer.toString(litterSize), 0));
				// Size approximate (certain)?
				String valueString = "0";
				if (litterSizeApproximate == true) {
					valueString = "1";
				}
				measurementId = ct.getMeasurementId("Certain");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, litterid, 
						valueString, 0));
				// Get Source via Line
				measurementId = ct.getMeasurementId("Source");
				try {
					int lineId = ct.getMostRecentValueAsXref(selectedParentgroup, ct.getMeasurementId("Line"));
					int sourceId = ct.getMostRecentValueAsXref(lineId, measurementId);
					protocolId = ct.getProtocolId("SetSource");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
						eventDate, null, protocolId, measurementId, litterid, null, sourceId));
				} catch(Exception e) {
					//
				}
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.action = "ShowLitters";
				this.reload(db);
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Litter succesfully added", true));
			}
			
			if (action.equals("ShowWean")) {
				// Find and set litter
				String litterIdString = request.getString("id");
				litterIdString = litterIdString.replace(".", "");
				litterIdString = litterIdString.replace(",", "");
				int litterId = Integer.parseInt(litterIdString);
				setLitter(litterId);
			}
			
			if (action.equals("Wean")) {
				setUserFields(request, true);
				Date weanDate = sdf.parse(weandatetime);	
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				List<ObservationTarget> animalsToAddList = new ArrayList<ObservationTarget>();
				
				int measurementId;
				// Source (take from litter)
				int sourceId;
				try {
					measurementId = ct.getMeasurementId("Source");
					sourceId = ct.getMostRecentValueAsXref(litter, measurementId);
				} catch (Exception e) {
					throw(new Exception("No source found - litter not weaned"));
				}
				// Get litter birth date
				String litterBirthDateString;
				Date litterBirthDate;
				try {
					measurementId = ct.getMeasurementId("DateOfBirth");
					litterBirthDateString = ct.getMostRecentValueAsString(litter, measurementId);
					litterBirthDate = sdf.parse(litterBirthDateString);
				} catch (Exception e) {
					throw(new Exception("No litter birth date found - litter not weaned"));
				}
				// Find Parentgroup for this litter
				int parentgroupId;
				try {
					measurementId = ct.getMeasurementId("Parentgroup");
					parentgroupId = ct.getMostRecentValueAsXref(litter, measurementId);
				} catch (Exception e) {
					throw(new Exception("No parentgroup found - litter not weaned"));
				}
				// Find first mother, plus her animal type and species
				int speciesId;
				String animalType;
				measurementId = ct.getMeasurementId("Mother");
				Query<ObservedValue> motherQuery = db.query(ObservedValue.class);
				motherQuery.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, parentgroupId));
				motherQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
				List<ObservedValue> motherValueList = motherQuery.find();
				if (motherValueList.size() > 0) {
					int motherId = motherValueList.get(0).getTarget_Id();
					measurementId = ct.getMeasurementId("Species");
					speciesId = ct.getMostRecentValueAsXref(motherId, measurementId);
					measurementId = ct.getMeasurementId("AnimalType");
					animalType = ct.getMostRecentValueAsString(motherId, measurementId);
					// Keep normal and transgene types, but set type of child from wild parents to normal
					if (animalType.equals("C. Wildvang") || animalType.equals("D. Biotoop")) {
						animalType = "A. Gewoon dier";
					}
				} else {
					throw(new Exception("No mother (properties) found - litter not weaned"));
				}
				// Set wean size
				int weanSize = weanSizeFemale + weanSizeMale;
				int protocolId = ct.getProtocolId("SetWeanSize");
				measurementId = ct.getMeasurementId("WeanSize");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litter, Integer.toString(weanSize), 0));
				// Set wean date on litter
				protocolId = ct.getProtocolId("SetWeanDate");
				measurementId = ct.getMeasurementId("WeanDate");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, litter, weandatetime, 0));
				
				db.beginTx();
				
				// Link animals to litter and set wean dates etc.
				for (int animalNumber = 0; animalNumber < weanSize; animalNumber++) {
					ObservationTarget animalToAdd = ct.createIndividual(invid, "animal_" + weanDate + "_" + animalNumber, this.getLogin().getUserId());
					animalsToAddList.add(animalToAdd);
				}
				db.add(animalsToAddList);
				
				int animalNumber = 0;
				for (ObservationTarget animal : animalsToAddList) {
					int animalId = animal.getId();
					
					// TODO: link every value to a single Wean protocol application instead of to its own one
					
					protocolId = ct.getProtocolId("SetLitter");
					measurementId = ct.getMeasurementId("Litter");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, null, litter));
					// Set sex
					int sexId = ct.getObservationTargetId("Female");
					if (animalNumber >= weanSizeFemale) {
						sexId = ct.getObservationTargetId("Male");
					}
					protocolId = ct.getProtocolId("SetSex");
					measurementId = ct.getMeasurementId("Sex");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, null, sexId));
					// Set wean date on animal
					protocolId = ct.getProtocolId("SetWeanDate");
					measurementId = ct.getMeasurementId("WeanDate");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, weandatetime, 0));
					// Set 'Active'
					protocolId = ct.getProtocolId("SetActive");
					measurementId = ct.getMeasurementId("Active");
			 		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
			 				litterBirthDate, null, protocolId, measurementId, animalId, "Alive", 0));
			 		// Set 'Date of Birth'
			 		protocolId = ct.getProtocolId("SetDateOfBirth");
					measurementId = ct.getMeasurementId("DateOfBirth");
			 		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate,
			 				null, protocolId, measurementId, animalId, litterBirthDateString, 0));
					// Set species
			 		protocolId = ct.getProtocolId("SetSpecies");
					measurementId = ct.getMeasurementId("Species");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, null, speciesId));
					// Set animal type
					protocolId = ct.getProtocolId("SetAnimalType");
					measurementId = ct.getMeasurementId("AnimalType");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, animalType, 0));
					// Set source
					protocolId = ct.getProtocolId("SetSource");
					measurementId = ct.getMeasurementId("Source");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, null, sourceId));
					// Set custom name/ID
					if (this.getCustomNameFeature() != null) {
						protocolId = ct.getProtocolId("Set" + this.getCustomNameFeature());
						measurementId = ct.getMeasurementId(this.getCustomNameFeature());
						valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
								null, protocolId, measurementId, animalId, customName + customNumber, 0));
						customNumber++;
					}
					
					animalNumber++;
				}
				
				db.add(valuesToAddList);
				
				db.commitTx();
				
				// Update custom label map now new animals have been added
				ct.makeObservationTargetNameMap(this.getLogin().getUserId(), true);
				
				this.action = "ShowLitters";
				this.reload(db);
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("All " + weanSize + " animals succesfully weaned", true));
			}

		} catch (Exception e) {
			try {
				db.rollbackTx();
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
			if (e.getMessage() != null) {
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
			this.action = "ShowLitters";
		}
	}

	@Override
	public void reload(Database db)
	{	
		ct.setDatabase(db);
		ct.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
		
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
			// Populate litter list
			litterList.clear();
			List<ObservationTarget> tmpLitterList = ct.getAllMarkedPanels("Litter", investigationIds);
			for (ObservationTarget tmpLitter : tmpLitterList) {
				// Check if no wean date set
				int featid = ct.getMeasurementId("WeanDate");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpLitter.getId()));
				if (q.count() == 0) {
					Litter litterToAdd = new Litter();
					// ID
					litterToAdd.setId(tmpLitter.getId());
					// Name
					litterToAdd.setName(tmpLitter.getName());
					// Parentgroup
					String parentgroup = null;
					featid = ct.getMeasurementId("Parentgroup");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpLitter.getId()));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() > 0) {
						int parentgroupId = valueList.get(0).getRelation_Id();
						parentgroup = ct.getObservationTargetById(parentgroupId).getName();
					}
					litterToAdd.setParentgroup(parentgroup);
					// Birthdate
					String birthDate = null;
					featid = ct.getMeasurementId("DateOfBirth");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpLitter.getId()));
					valueList = q.find();
					if (valueList.size() > 0) {
						birthDate = valueList.get(0).getValue();
					}
					litterToAdd.setBirthDate(birthDate);
					// Size
					String size = "-1";
					featid = ct.getMeasurementId("Size");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpLitter.getId()));
					valueList = q.find();
					if (valueList.size() > 0) {
						size = valueList.get(0).getValue();
					}
					litterToAdd.setSize(Integer.parseInt(size));
					// Size approximate
					String isApproximate = "";
					featid = ct.getMeasurementId("Certain");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpLitter.getId()));
					valueList = q.find();
					if (valueList.size() > 0) {
						String tmpValue = valueList.get(0).getValue();
						if (tmpValue.equals("0")) isApproximate = "No";
						if (tmpValue.equals("1")) isApproximate = "Yes";
					}
					litterToAdd.setSizeApproximate(isApproximate);
					
					litterList.add(litterToAdd);
				}
			}
			
			// Populate parent group list
			this.setParentgroupList(ct.getAllMarkedPanels("Parentgroup", investigationIds));
			
		} catch (Exception e) {
			if (e.getMessage() != null) {
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
}
