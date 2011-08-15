/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.breedingplugin;

import java.io.File;
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
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.output.LabelGenerator;
import plugins.output.LabelGeneratorException;

import commonservice.CommonService;

public class ManageLitters extends PluginModel<Entity>
{
	private static final long serialVersionUID = 7608670026855241487L;
	private List<ObservationTarget> parentgroupList;
	private List<Litter> litterList = new ArrayList<Litter>();
	private List<Litter> genoLitterList = new ArrayList<Litter>();
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
	private String nameBase = null;
	private int startNumber = -1;
	private String labelDownloadLink = null;
	private List<ObservationTarget> backgroundList;
	private List<ObservationTarget> sexList;
	private List<String> geneNameList;
	private List<String> geneStateList;
	private List<String> colorList;
	private List<Code> earmarkList;
	private int genoLitterId;
	private Database db;
	private boolean firstTime = true;
	private List<String> bases = null;

	public ManageLitters(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
				"<script src=\"res/scripts/custom/litters.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
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
	
	public void setGenoLitterList(List<Litter> genoLitterList) {
		this.genoLitterList = genoLitterList;
	}
	public List<Litter> getGenoLitterList() {
		return genoLitterList;
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

	public void setLabelDownloadLink(String labelDownloadLink) {
		this.labelDownloadLink = labelDownloadLink;
	}

	public String getLabelDownloadLink() {
		return labelDownloadLink;
	}

	public List<ObservationTarget> getBackgroundList() {
		return backgroundList;
	}

	public void setBackgroundList(List<ObservationTarget> backgroundList) {
		this.backgroundList = backgroundList;
	}

	public List<String> getGeneNameList() {
		return geneNameList;
	}

	public void setGeneNameList(List<String> geneNameList) {
		this.geneNameList = geneNameList;
	}

	public List<String> getGeneStateList() {
		return geneStateList;
	}

	public void setGeneStateList(List<String> geneStateList) {
		this.geneStateList = geneStateList;
	}
	
	public int getGenoLitterId() {
		return genoLitterId;
	}

	public void setGenoLitterId(int genoLitterId) {
		this.genoLitterId = genoLitterId;
	}

	public List<ObservationTarget> getSexList() {
		return sexList;
	}

	public void setSexList(List<ObservationTarget> sexList) {
		this.sexList = sexList;
	}

	public List<String> getColorList() {
		return colorList;
	}

	public void setColorList(List<String> colorList) {
		this.colorList = colorList;
	}

	public List<Code> getEarmarkList() {
		return earmarkList;
	}

	public void setEarmarkList(List<Code> earmarkList) {
		this.earmarkList = earmarkList;
	}

	private void setUserFields(Tuple request, boolean wean) throws Exception {
		if (wean == true) {
			if (request.getString("weandatetime") == null || request.getString("weandatetime").equals("")) {
				throw new Exception("Wean date cannot be empty");
			}
			setWeandatetime(request.getString("weandatetime"));
			setWeanSizeFemale(request.getInt("weansizefemale"));
			setWeanSizeMale(request.getInt("weansizemale"));
			
			if (request.getString("namebase") != null) {
				nameBase = request.getString("namebase");
				if (nameBase.equals("New")) {
					if (request.getString("newnamebase") != null) {
						nameBase = request.getString("newnamebase");
					} else {
						nameBase = "";
					}
				}
			} else {
				nameBase = "";
			}
			if (request.getInt("startnumber") != null) {
				startNumber = request.getInt("startnumber");
			} else {
				startNumber = 1; // standard start at 1
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
	
	public String getParentInfo() {
		
		try {
			String returnString = "";
			
			int parentgroupId = ct.getMostRecentValueAsXref(this.getGenoLitterId(), ct.getMeasurementId("Parentgroup"));
			String parentgroupName = ct.getObservationTargetById(parentgroupId).getName();
			
			returnString += ("Parentgroup: " + parentgroupName + "<br />");
			returnString += (getLineInfo(parentgroupId) + "<br />");
			
			int motherId = findParentForParentgroup(parentgroupId, "Mother");
			returnString += ("Mother: " + getGenoInfo(motherId) + "<br />");
			int fatherId = findParentForParentgroup(parentgroupId, "Father");
			returnString += ("Father: " + getGenoInfo(fatherId) + "<br />");
			
			return returnString;
			
		} catch (Exception e) {
			return "No (complete) parent info available";
		}
	}
	
	public List<Individual> getAnimalsInLitter() {
		List<Individual> returnList = new ArrayList<Individual>();
		try {
			Query<ObservedValue> q = this.db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, this.getGenoLitterId()));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, ct.getMeasurementId("Litter")));
			List<ObservedValue> valueList = q.find();
			int animalId;
			for (ObservedValue value : valueList) {
				animalId = value.getTarget_Id();
				returnList.add(ct.getIndividualById(animalId));
			}
			return returnList;
		} catch (Exception e) {
			// On fail, return empty list to UI
			return new ArrayList<Individual>();
		}
	}
	
	public int getAnimalSex(int animalId) {
		try {
			return ct.getMostRecentValueAsXref(animalId, ct.getMeasurementId("Sex"));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getAnimalColor(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("Color"));
		} catch (Exception e) {
			return "unknown";
		}
	}
	
	public String getAnimalEarmark(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("Earmark"));
		} catch (Exception e) {
			return "";
		}
	}
	
	public int getAnimalBackground(int animalId) {
		try {
			return ct.getMostRecentValueAsXref(animalId, ct.getMeasurementId("Background"));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getAnimalGeneName(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("GeneName"));
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getAnimalGeneState(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("GeneState"));
		} catch (Exception e) {
			return "";
		}
	}
	
	private int findParentForParentgroup(int parentgroupId, String parentSex) throws DatabaseException, ParseException {
		int measurementId = ct.getMeasurementId(parentSex);
		Query<ObservedValue> parentQuery = db.query(ObservedValue.class);
		parentQuery.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, parentgroupId));
		parentQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
		List<ObservedValue> parentValueList = parentQuery.find();
		if (parentValueList.size() > 0) {
			return parentValueList.get(0).getTarget_Id();
		} else {
			throw new DatabaseException("No " + parentSex + " found for parentgroup with ID " + parentgroupId);
		}
	}
	
	private String getGenoInfo(int animalId) throws DatabaseException, ParseException {
		String returnString = "";
		int measurementId = ct.getMeasurementId("Background");
		int animalBackgroundId = ct.getMostRecentValueAsXref(animalId, measurementId);
		String animalBackgroundName = "unknown";
		if (animalBackgroundId != -1) {
			animalBackgroundName = ct.getObservationTargetById(animalBackgroundId).getName();
		}
		returnString += ("background: " + animalBackgroundName);
		Query<ObservedValue> q = this.db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, ct.getMeasurementId("GeneName")));
		List<ObservedValue> valueList = q.find();
		if (valueList != null) {
			int protocolApplicationId;
			for (ObservedValue value : valueList) {
				String geneName = value.getValue();
				String geneState = "";
				protocolApplicationId = value.getProtocolApplication_Id();
				q = this.db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, ct.getMeasurementId("GeneState")));
				q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
				List<ObservedValue> geneStateValueList = q.find();
				if (geneStateValueList != null) {
					if (geneStateValueList.size() > 0) {
						geneState = geneStateValueList.get(0).getValue();
					}
				}
				returnString += (", gene: " + geneName + ", ");
				returnString += ("state: " + geneState);
			}
		}
		
		return returnString;
	}
	
	private String getLineInfo(int parentgroupId) throws DatabaseException, ParseException {
		int lineId = ct.getMostRecentValueAsXref(parentgroupId, ct.getMeasurementId("Line"));
		String lineName = ct.getObservationTargetById(lineId).getName();
		return ("Line: " + lineName);
	}

	public List<String> getBases() {
		return bases;
	}

	public void setBases(List<String> bases) {
		this.bases = bases;
	}
	
	public String getStartNumberHelperContent() {
		try {
			String helperContents = "";
			helperContents += (ct.getHighestNumberForNameBase("") + 1);
			helperContents += ";1";
			for (String base : this.bases) {
				if (!base.equals("")) {
					helperContents += (";" + (ct.getHighestNumberForNameBase(base) + 1));
				}
			}
			return helperContents;
		} catch (Exception e) {
			return "";
		}
	}
	
	public int getStartNumberForEmptyBase() {
		try {
			return ct.getHighestNumberForNameBase("") + 1;
		} catch (DatabaseException e) {
			return 1;
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			
			this.action = request.getString("__action");
			
			if (action.equals("AddLitter")) {
				//
			}
			
			if (action.equals("ShowLitters")) {
				//
			}
			
			if (action.equals("ApplyAddLitter")) {
				int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
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
				this.reloadLitterLists(db);
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
				int invid = ct.getObservationTargetById(litter).getInvestigation_Id();
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
				// Find Line for this Parentgroup
				int lineId = ct.getMostRecentValueAsXref(parentgroupId, ct.getMeasurementId("Line"));
				String lineName = ct.getObservationTargetById(lineId).getName();
				// Find first mother, plus her animal type and species
				int speciesId;
				String animalType;
				String motherLabel;
				try {
					int motherId = findParentForParentgroup(parentgroupId, "Mother");
					measurementId = ct.getMeasurementId("Species");
					speciesId = ct.getMostRecentValueAsXref(motherId, measurementId);
					measurementId = ct.getMeasurementId("AnimalType");
					animalType = ct.getMostRecentValueAsString(motherId, measurementId);
					motherLabel = ct.getObservationTargetLabel(motherId);
				} catch (Exception e) {
					throw(new Exception("No mother (properties) found - litter not weaned"));
				}
				// Keep normal and transgene types, but set type of child from wild parents to normal
				if (animalType.equals("C. Wildvang") || animalType.equals("D. Biotoop")) {
					animalType = "A. Gewoon dier";
				}
				// Find father and his name or custom label
				String fatherLabel = "";
				try {
					int fatherId = findParentForParentgroup(parentgroupId, "Father");
					fatherLabel = ct.getObservationTargetLabel(fatherId);
				} catch (Exception e) {
					throw(new Exception("No father (properties) found - litter not weaned"));
				}
				// Set wean size
				int weanSize = weanSizeFemale + weanSizeMale;
				int protocolId = ct.getProtocolId("SetWeanSize");
				measurementId = ct.getMeasurementId("WeanSize");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litter, Integer.toString(weanSize), 0));
				// Set wean date on litter -> this is how we mark a litter as weaned (but not genotyped)
				protocolId = ct.getProtocolId("SetWeanDate");
				measurementId = ct.getMeasurementId("WeanDate");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, litter, weandatetime, 0));
				
				db.beginTx();
				
				// Link animals to litter and set wean dates etc.
				for (int animalNumber = 0; animalNumber < weanSize; animalNumber++) {
					ObservationTarget animalToAdd = ct.createIndividual(invid, nameBase + (startNumber + animalNumber), 
							this.getLogin().getUserId());
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
					
					animalNumber++;
				}
				
				db.add(valuesToAddList);
				
				db.commitTx();
				
				// Update custom label map now new animals have been added
				ct.makeObservationTargetNameMap(this.getLogin().getUserId(), true);
				
				// Make temporary cage labels
				makeTempCageLabels(lineName, motherLabel, fatherLabel, litterBirthDateString);
				
				this.action = "ShowLitters";
				this.reload(db);
				this.reloadLitterLists(db);
				this.getMessages().add(new ScreenMessage("All " + weanSize + " animals succesfully weaned", true));
			}
			
			if (action.equals("ShowGenotype")) {
				this.setGenoLitterId(request.getInt("id"));
			}
			
			if (action.equals("Genotype")) {
				
				int invid = ct.getObservationTargetById(this.genoLitterId).getInvestigation_Id();
				List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Set genotype date on litter -> this is how we mark a litter as genotyped
				int protocolId = ct.getProtocolId("SetGenotypeDate");
				int measurementId = ct.getMeasurementId("GenotypeDate");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
						null, protocolId, measurementId, this.genoLitterId, weandatetime, 0));
				
				int animalCount = 0;
				for (Individual animal : this.getAnimalsInLitter()) {
					
					// Here we set the values from the genotyping.
					
					// Set sex
					int sexId = request.getInt("sex_" + animalCount);
					ObservedValue value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("Sex"), investigationIds, invid).get(0);
					value.setRelation_Id(sexId);
					value.setValue(null);
					if (value.getProtocolApplication_Id() == null) {
						int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetSex"));
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					// Set color
					String color = request.getString("color_" + animalCount);
					value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("Color"), investigationIds, invid).get(0);
					value.setValue(color);
					if (value.getProtocolApplication_Id() == null) {
						int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetColor"));
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					// Set earmark
					String earmark = request.getString("earmark_" + animalCount);
					value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("Earmark"), investigationIds, invid).get(0);
					value.setValue(earmark);
					if (value.getProtocolApplication_Id() == null) {
						int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetEarmark"));
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					// Set background
					int backgroundId = request.getInt("background_" + animalCount);
					value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("Background"), investigationIds, invid).get(0);
					value.setRelation_Id(backgroundId);
					value.setValue(null);
					if (value.getProtocolApplication_Id() == null) {
						int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetBackground"));
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					// Set genotype
					int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetGenotype"));
					String geneName = request.getString("geneName_" + animalCount);
					value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("GeneName"), investigationIds, invid).get(0);
					value.setValue(geneName);
					if (value.getProtocolApplication_Id() == null) {
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					String geneState = request.getString("geneState_" + animalCount);
					value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
							ct.getMeasurementByName("GeneState"), investigationIds, invid).get(0);
					value.setValue(geneState);
					if (value.getProtocolApplication_Id() == null) {
						value.setProtocolApplication_Id(paId);
					}
					valuesToAddList.add(value);
					
					animalCount++;
				}
				
				db.update(valuesToAddList);
				
				// Make definitive cage labels
				makeDefCageLabels();
				
				this.action = "ShowLitters";
				this.reload(db);
				this.reloadLitterLists(db);
				this.getMessages().add(new ScreenMessage("All " + animalCount + " animals succesfully genotyped", true));
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

	private void makeDefCageLabels() throws LabelGeneratorException, DatabaseException, ParseException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "deflabels.pdf");
		String filename = pdfFile.getName();
		LabelGenerator labelgenerator = new LabelGenerator(2);
		labelgenerator.startDocument(pdfFile);
		
		int parentgroupId = ct.getMostRecentValueAsXref(this.getGenoLitterId(), ct.getMeasurementId("Parentgroup"));
		String line = this.getLineInfo(parentgroupId);
		int motherId = findParentForParentgroup(parentgroupId, "Mother");
		String motherInfo = this.getGenoInfo(motherId);
		int fatherId = findParentForParentgroup(parentgroupId, "Father");
		String fatherInfo = this.getGenoInfo(fatherId);
		
		List<String> elementList;
		
		for (Individual animal : this.getAnimalsInLitter()) {
			int animalId = animal.getId();
			elementList = new ArrayList<String>();
			// Earmark
			elementList.add("Earmark: " + ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("Earmark")));
			// Name / custom label
			elementList.add("Name: " + ct.getObservationTargetLabel(animalId));
			// Line
			elementList.add(line);
			// Background + GeneName + GeneState
			elementList.add(this.getGenoInfo(animalId));
			// Color + Sex
			String colorSex = "Color/sex: ";
			colorSex += ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("Color"));
			colorSex += "\t\t";
			int sexId = ct.getMostRecentValueAsXref(animalId, ct.getMeasurementId("Sex"));
			colorSex += ct.getObservationTargetById(sexId).getName();
			elementList.add(colorSex);
			// Birthdate
			elementList.add("Birthdate: " + ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("DateOfBirth")));
			// Geno mother
			elementList.add("Mother: " + motherInfo);
			// Geno father
			elementList.add("Father: " + fatherInfo);
			// OldUliDbExperimentator
			elementList.add("Experimentator: " + ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("OldUliDbExperimentator")));
			
			labelgenerator.addLabelToDocument(elementList);
		}
		
		// In case of an odd number of animals, add extra label to make row full
		if (this.getAnimalsInLitter().size() %2 != 0) {
			elementList = new ArrayList<String>();
			labelgenerator.addLabelToDocument(elementList);
		}
		
		labelgenerator.finishDocument();
		this.setLabelDownloadLink("<a href=\"tmpfile/" + filename + "\">Download definitive cage labels as pdf</a>");
	}

	private void makeTempCageLabels(String lineName, String motherLabel, String fatherLabel, String litterBirthDateString) throws LabelGeneratorException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "weanlabels.pdf");
		String filename = pdfFile.getName();
		LabelGenerator labelgenerator = new LabelGenerator(2);
		labelgenerator.startDocument(pdfFile);
		List<String> elementList;
		int nrOfCages = 0;
		int nrOfFemales = weanSizeFemale;
		while (nrOfFemales > 0) {
			elementList = new ArrayList<String>();
			// Line name + Nr. of females in cage
			String firstLine = lineName; 
			if (nrOfFemales >= 3) {
				firstLine += "\t\t3 females";
			} else {
				if (nrOfFemales == 1) {
					firstLine += "\t\t1 female";
				} else {
					firstLine += "\t\t2 females";
				}
			}
			elementList.add(firstLine);
			// Parents
			elementList.add(motherLabel + " x " + fatherLabel);
			// Litter birth date
			elementList.add(litterBirthDateString);
			// Nrs. for writing extra information behind
			for (int i = 1; i <= Math.min(nrOfFemales, 3); i++) {
				elementList.add(i + ".");
			}
			
			labelgenerator.addLabelToDocument(elementList);
			nrOfFemales -= 3;
			nrOfCages++;
		}
		int nrOfMales = weanSizeMale;
		while (nrOfMales > 0) {
			elementList = new ArrayList<String>();
			// Line name + Nr. of males in cage
			String firstLine = lineName; 
			if (nrOfMales >= 2) {
				firstLine += "\t\t2 males";
			} else {
				firstLine += "\t\t1 male";
			}
			elementList.add(firstLine);
			// Parents
			elementList.add(motherLabel + " x " + fatherLabel);
			// Litter birth date
			elementList.add(litterBirthDateString);
			// Nrs. for writing extra information behind
			for (int i = 1; i <= Math.min(nrOfMales, 2); i++) {
				elementList.add(i + ".");
			}
			
			labelgenerator.addLabelToDocument(elementList);
			nrOfMales -= 2;
			nrOfCages++;
		}
		// In case of an odd number of cages, add extra label to make row full
		if (nrOfCages %2 != 0) {
			elementList = new ArrayList<String>();
			labelgenerator.addLabelToDocument(elementList);
		}
		labelgenerator.finishDocument();
		this.setLabelDownloadLink("<a href=\"tmpfile/" + filename + "\">Download temporary wean labels as pdf</a>");
	}

	@Override
	public void reload(Database db)
	{	
		if (firstTime == true) {
			firstTime = false;
			reloadLitterLists(db);
		}
		
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
			
			// Populate parent group list
			this.setParentgroupList(ct.getAllMarkedPanels("Parentgroup", investigationIds));
			// Populate backgrounds list
			this.setBackgroundList(ct.getAllMarkedPanels("Background", investigationIds));
			// Populate sexes list
			this.setSexList(ct.getAllMarkedPanels("Sex", investigationIds));
			// Populate gene name list
			this.setGeneNameList(ct.getAllCodesForFeatureAsStrings("GeneName"));
			// Populate gene state list
			this.setGeneStateList(ct.getAllCodesForFeatureAsStrings("GeneState"));
			// Populate color list
			this.setColorList(ct.getAllCodesForFeatureAsStrings("Color"));
			// Populate earmark list
			this.setEarmarkList(ct.getAllCodesForFeature("Earmark"));
			// Populate name bases list
			this.setBases(ct.getNameBases());
		} catch (Exception e) {
			if (e.getMessage() != null) {
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
	private void reloadLitterLists(Database db) {
		this.db = db;
		
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
		
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
			
			// Populate unweaned and ungenotyped litter lists
			litterList.clear();
			genoLitterList.clear();
			
			// Make list of ID's of weaned litters
			List<Integer> weanedLitterIdList = new ArrayList<Integer>();
			int featid = ct.getMeasurementId("WeanDate");
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
			List<ObservedValue> valueList = q.find();
			for (ObservedValue value : valueList) {
				int litterId = value.getTarget_Id();
				if (!weanedLitterIdList.contains(litterId)) {
					weanedLitterIdList.add(litterId);
				}
			}
			// Make list of ID's of genotyped litters
			List<Integer> genotypedLitterIdList = new ArrayList<Integer>();
			featid = ct.getMeasurementId("GenotypeDate");
			q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
			valueList = q.find();
			for (ObservedValue value : valueList) {
				int litterId = value.getTarget_Id();
				if (!genotypedLitterIdList.contains(litterId)) {
					genotypedLitterIdList.add(litterId);
				}
			}
			// Get all litters that the current user has rights on
			List<ObservationTarget> allLitterList = ct.getAllMarkedPanels("Litter", investigationIds);
			for (ObservationTarget litter : allLitterList) {
				int litterId = litter.getId();
				if (weanedLitterIdList.contains(litterId) && genotypedLitterIdList.contains(litterId)) {
					// Skip litters that have both been weaned and genotyped
					continue;
				}
				// Make a temporary litter and set all relevant values
				Litter litterToAdd = new Litter();
				// ID
				litterToAdd.setId(litterId);
				// Name
				litterToAdd.setName(litter.getName());
				// Parentgroup
				featid = ct.getMeasurementId("Parentgroup");
				int parentgroupId = ct.getMostRecentValueAsXref(litterId, featid);
				String parentgroup = ct.getObservationTargetById(parentgroupId).getName();
				litterToAdd.setParentgroup(parentgroup);
				// Birth date
				featid = ct.getMeasurementId("DateOfBirth");
				String birthDate = ct.getMostRecentValueAsString(litterId, featid);
				if (!birthDate.equals("")) {
					litterToAdd.setBirthDate(birthDate);
				}
				// Wean date
				featid = ct.getMeasurementId("WeanDate");
				String weanDate = ct.getMostRecentValueAsString(litterId, featid);
				if (weanDate != null && !weanDate.equals("")) {
					litterToAdd.setWeanDate(weanDate);
				}
				// Size
				featid = ct.getMeasurementId("Size");
				String size = ct.getMostRecentValueAsString(litterId, featid);
				if (size.equals("")) {
					litterToAdd.setSize(-1);
				} else {
					litterToAdd.setSize(Integer.parseInt(size));
				}
				// Size approximate
				String isApproximate = "";
				featid = ct.getMeasurementId("Certain");
				String tmpValue = ct.getMostRecentValueAsString(litterId, featid);
				if (tmpValue.equals("0")) {
					isApproximate = "No";
				}
				if (tmpValue.equals("1")) {
					isApproximate = "Yes";
				}
				litterToAdd.setSizeApproximate(isApproximate);
				// Add to the right list
				if (!weanedLitterIdList.contains(litterId)) {
					litterList.add(litterToAdd);
				} else {
					genoLitterList.add(litterToAdd);
				}
			}
			
		} catch (Exception e) {
			if (e.getMessage() != null) {
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
}
