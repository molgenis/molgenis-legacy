/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breedingplugin;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.animaldb.plugins.output.LabelGenerator;
import org.molgenis.animaldb.plugins.output.LabelGeneratorException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;



public class ManageLitters extends PluginModel<Entity>
{
	private static final long serialVersionUID = 7608670026855241487L;
	private List<ObservationTarget> parentgroupList;
	private List<Litter> litterList = new ArrayList<Litter>();
	private List<Litter> genoLitterList = new ArrayList<Litter>();
	private int selectedParentgroup = -1;
	private int litter;
//	private String litterName = "";
	private String birthdate = null;
	private String weandate = null;
	private int litterSize;
	private int weanSizeFemale;
	private int weanSizeMale;
	private int weanSizeUnknown;
	private boolean litterSizeApproximate;
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat oldDateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String action = "ShowLitters";
	private String nameBase = null;
	private int startNumber = -1;
	private String labelDownloadLink = null;
	private List<ObservationTarget> backgroundList;
	private List<ObservationTarget> sexList;
	private List<String> geneNameList;
	private List<String> geneStateList;
	private List<String> colorList;
	private List<Category> earmarkList;
	private int genoLitterId;
	private List<String> bases = null;
	private String remarks = null;
	//private String status = null;
	private Table genotypeTable = null;
	private int nrOfGenotypes = 1;
	MatrixViewer matrixViewer = null;
	MatrixViewer litterMatrixViewer = null;
	private static String MATRIX = "matrix";
	private static String LITTERMATRIX = "littermatrix";
	private int userId = -1;
	private String respres = null;
	private List<Location> locationList;
	private int locId = -1;
	
	//hack to pass database to toHtml() via toHtml(db)
	private Database toHtmlDb;
	public void setToHtmlDb(Database toHtmlDb)
	{
		this.toHtmlDb = toHtmlDb;
	}

	public ManageLitters(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
				"<script src=\"res/scripts/custom/litters.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
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
	
	public String getBirthdate() {
		if (birthdate != null) {
			return birthdate;
		}
		return newDateOnlyFormat.format(new Date());
		
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	
	public void setWeandate(String weandate) {
		this.weandate = weandate;
	}
	public String getWeandate() {
		if (weandate != null) {
			return weandate;
		}
		return newDateOnlyFormat.format(new Date());
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
	
	public void setWeanSizeUnknown(int weanSizeUnknown) {
		this.weanSizeUnknown = weanSizeUnknown;
	}
	public int getWeanSizeUnknown() {
		return weanSizeUnknown;
	}

	public int getSelectedParentgroup() {
		return selectedParentgroup;
	}
	public String getSelectedParentgroupName() {
		try {
			return ct.getObservationTargetLabel(selectedParentgroup);
		} catch (Exception e) {
			return "None";
		}
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
	
	public String getLitterName() {
		try {
			return ct.getObservationTargetLabel(litter);
		} catch (Exception e) {
			return "No litter selected";
		}
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

	public List<Category> getEarmarkList() {
		return earmarkList;
	}

	public void setEarmarkList(List<Category> earmarkList) {
		this.earmarkList = earmarkList;
	}
	
	public String renderDoneLitterMatrix() {
		if (litterMatrixViewer != null) {
			litterMatrixViewer.setDatabase(toHtmlDb);
			return litterMatrixViewer.render();
		} else {
			return "No viewer available, matrix for displaying weaned and genotyped litters cannot be rendered.";
		}
	}
	
	public String renderMatrixViewer() {
		if (matrixViewer != null) {
			matrixViewer.setDatabase(toHtmlDb);
			return matrixViewer.render();
		} else {
			return "No viewer available, matrix for selecting a parentgroup cannot be rendered.";
		}
	}

	private void setUserFields(Tuple request, boolean wean) throws Exception {
		if (wean == true) {
			locId = request.getInt("location");
			respres = request.getString("respres");
			if (request.getString("weandate") == null || request.getString("weandate").equals("")) {
				throw new Exception("Wean date cannot be empty");
			}
			weandate = request.getString("weandate"); // in old date format!
			setWeanSizeFemale(request.getInt("weansizefemale"));
			setWeanSizeMale(request.getInt("weansizemale"));
			setWeanSizeUnknown(request.getInt("weansizeunknown"));
			this.setRemarks(request.getString("remarks"));
			
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
			if (request.getString("birthdate") == null || request.getString("birthdate").equals("")) {
				throw new Exception("Birth date cannot be empty");
			}
			birthdate = request.getString("birthdate"); // in old date format!
			setLitterSize(request.getInt("littersize"));
			if (request.getBool("sizeapp_toggle") != null) {
				setLitterSizeApproximate(true);
			} else {
				setLitterSizeApproximate(false);
			}
			this.setRemarks(request.getString("remarks"));
		}
	}
	
	public String getParentInfo() {
		
		try {
			String returnString = "";
			int parentgroupId = ct.getMostRecentValueAsXref(this.getGenoLitterId(), "Parentgroup");
			String parentgroupName = ct.getObservationTargetById(parentgroupId).getName();
			returnString += ("Parentgroup: " + parentgroupName + "<br />");
			returnString += ("Line: " + getLineInfo(parentgroupId) + "<br />");
			int motherId = findParentForParentgroup(parentgroupName, "Mother", toHtmlDb);
			returnString += ("Mother: " + getGenoInfo(motherId, toHtmlDb) + "<br />");
			int fatherId = findParentForParentgroup(parentgroupName, "Father", toHtmlDb);
			returnString += ("Father: " + getGenoInfo(fatherId, toHtmlDb) + "<br />");
			
			return returnString;
			
		} catch (Exception e) {
			return "No (complete) parent info available";
		}
	}
	
	public List<Individual> getAnimalsInLitter(Database db) {
		try {
			return getAnimalsInLitter(this.getGenoLitterId(), db);
		} catch (Exception e) {
			// On fail, return empty list to UI
			return new ArrayList<Individual>();
		}
	}
	
	public List<Individual> getAnimalsInLitter(int litterId, Database db) {
		List<Individual> returnList = new ArrayList<Individual>();
		try {
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, litterId));
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
			List<ObservedValue> valueList = q.find();
			for (ObservedValue value : valueList) {
				int animalId = value.getTarget_Id();
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
			return ct.getMostRecentValueAsXref(animalId, "Sex");
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getAnimalColor(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, "Color");
		} catch (Exception e) {
			return "unknown";
		}
	}
	
	public Date getAnimalBirthDate(int animalId) {
		try {
			String birthDateString = ct.getMostRecentValueAsString(animalId, "DateOfBirth");
			return newDateOnlyFormat.parse(birthDateString);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getAnimalBirthDateAsString(int animalId) {
		try {
			String birthDateString = ct.getMostRecentValueAsString(animalId, "DateOfBirth");
			Date tmpBirthDate = newDateOnlyFormat.parse(birthDateString);
			return oldDateOnlyFormat.format(tmpBirthDate);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getAnimalEarmark(int animalId) {
		try {
			return ct.getMostRecentValueAsString(animalId, "Earmark");
		} catch (Exception e) {
			return "";
		}
	}
	
	public int getAnimalBackground(int animalId) {
		try {
			return ct.getMostRecentValueAsXref(animalId, "Background");
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getAnimalGeneInfo(String measurementName, int animalId, int genoNr, Database db) {
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, measurementName));
		List<ObservedValue> valueList;
		try {
			valueList = q.find();
		} catch (DatabaseException e) {
			return "";
		}
		if (valueList.size() > genoNr) {
			return valueList.get(genoNr).getValue();
		} else {
			return "";
		}
	}
	
	private int findParentForParentgroup(String parentgroupName, String parentSex, Database db) throws DatabaseException, ParseException {
		ct.setDatabase(db);
		Query<ObservedValue> parentQuery = db.query(ObservedValue.class);
		parentQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, parentgroupName));
		parentQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Parentgroup" + parentSex));
		List<ObservedValue> parentValueList = parentQuery.find();
		if (parentValueList.size() > 0) {
			return parentValueList.get(0).getRelation_Id();
		} else {
			throw new DatabaseException("Fatal error: no " + parentSex + " found for parentgroup " + parentgroupName);
		}
	}
	
	private String getGenoInfo(int animalId, Database db) throws DatabaseException, ParseException {
		String returnString = "";
		int animalBackgroundId = ct.getMostRecentValueAsXref(animalId, "Background");
		String animalBackgroundName = "unknown";
		if (animalBackgroundId != -1) {
			animalBackgroundName = ct.getObservationTargetById(animalBackgroundId).getName();
		}
		returnString += ("background: " + animalBackgroundName + "; ");
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GeneModification"));
		List<ObservedValue> valueList = q.find();
		if (valueList != null) {
			for (ObservedValue value : valueList) {
				String geneName = value.getValue();
				String geneState = "";
				Query<ObservedValue> geneStateQuery = db.query(ObservedValue.class);
				geneStateQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				geneStateQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GeneState"));
				geneStateQuery.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, value.getProtocolApplication_Id()));
				List<ObservedValue> geneStateValueList = geneStateQuery.find();
				if (geneStateValueList != null && geneStateValueList.size() > 0) {
					geneState = geneStateValueList.get(0).getValue();
				}
				if (geneName == null || geneName.equals("null") || geneName.equals("")) {
					geneName = "unknown";
				}
				if (geneState == null || geneState.equals("null") || geneState.equals("")) {
					geneState = "unknown";
				}
				returnString += ("gene: " + geneName + ": " + geneState + "; ");
			}
		}
		if (returnString.length() > 0) {
			returnString = returnString.substring(0, returnString.length() - 2);
		}
		return returnString;
	}
	
	private String getLineInfo(int parentgroupId) throws DatabaseException, ParseException {
		int lineId = ct.getMostRecentValueAsXref(parentgroupId, ct.getMeasurementId("Line"));
		String lineName = ct.getObservationTargetById(lineId).getName();
		return lineName;
	}

	public List<String> getBases() {
		return bases;
	}

	public void setBases(List<String> bases) {
		this.bases = bases;
	}
	
	public String getGenodate() {
		return oldDateOnlyFormat.format(new Date());
	}
	
	public String getStartNumberHelperContent() {
		try {
			String helperContents = "";
			helperContents += (ct.getHighestNumberForPrefix("") + 1);
			helperContents += ";1";
			for (String base : this.bases) {
				if (!base.equals("")) {
					helperContents += (";" + (ct.getHighestNumberForPrefix(base) + 1));
				}
			}
			return helperContents;
		} catch (Exception e) {
			return "";
		}
	}
	
	public int getStartNumberForEmptyBase() {
		try {
			return ct.getHighestNumberForPrefix("") + 1;
		} catch (DatabaseException e) {
			return 1;
		}
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
//	public String getStatus(int litterId) throws DatabaseException {
//		try {
//			return ct.getMostRecentValueAsString(litterId, ct.getMeasurementId("Active"));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error while retrieving status";
//		}
//	}
//	
//	public void setStatus(String status) {
//		this.status = status;
//	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		try {
			this.action = request.getString("__action");
			
			if (action.startsWith(matrixViewer.getName())) {
				matrixViewer.handleRequest(db, request);
				this.action = "AddLitter";
				return;
			}
			
			if (litterMatrixViewer != null && action.startsWith(litterMatrixViewer.getName())) {
				litterMatrixViewer.handleRequest(db, request);
				this.action = "ShowLitters";
				return;
			}
			
			if (action.equals("MakeTmpLabels")) {
				setLitter(request.getInt("id"));
				makeTempCageLabels(db);
			}
			
			if (action.equals("MakeDefLabels")) {
				List<?> rows = litterMatrixViewer.getSelection(db);
				try { 
					int row = request.getInt(LITTERMATRIX + "_selected");
					this.litter = ((ObservationElement) rows.get(row)).getId();
					makeDefCageLabels(db);
					this.setSuccess("Definitive cage labels created successfully");
				} catch (Exception e) {	
					this.setAction("ShowDoneLitters");
					this.setError("Something went wrong while making definitive cage labels: " + e.getMessage());
					return;
				}
			}
			
			if (action.equals("AddLitter")) {
				//
			}
			
			if (action.equals("ShowLitters")) {
				//
			}
			
			if (action.equals("ApplyAddLitter")) {
				String litterName = ApplyAddLitter(db, request);
				this.litterSize = 0;
				this.remarks = null;
				this.birthdate = null;
				this.selectedParentgroup = -1;
				this.action = "ShowLitters";
				reloadLitterLists(db);
				reload(db);
				this.setSuccess("Litter " + litterName + " successfully added");
			}
			
			if (action.equals("ShowWean")) {
				setLitter(request.getInt("id"));
			}
			
			if (action.equals("Wean")) {
				int weanSize = Wean(db, request);
				
				// Update custom label map now new animals have been added
				ct.makeObservationTargetNameMap(this.getLogin().getUserName(), true);
				
				this.weandate = null;
				this.weanSizeFemale = 0;
				this.weanSizeMale = 0;
				this.weanSizeUnknown = 0;
				this.remarks = null;
				this.respres = null;
				this.selectedParentgroup = -1;
				this.locId = -1;
				this.action = "ShowLitters";
				reload(db);
				reloadLitterLists(db);
				this.getMessages().add(new ScreenMessage("All " + weanSize + " animals successfully weaned", true));
			}
			
			if (action.equals("ShowGenotype")) {
				ShowGenotype(db, request);
			}
			
			if (action.equals("AddGenoCol")) {
				storeGenotypeTable(db, request);
				AddGenoCol(db, request);
				this.getMessages().add(new ScreenMessage("Gene modification + state pair successfully added", true));
			}
			
			if (action.equals("RemGenoCol")) {
				if (nrOfGenotypes > 1) {
					int currCol = 5 + ((nrOfGenotypes - 1) * 2);
					genotypeTable.removeColumn(currCol); // NB: nr. of cols is now 1 lower!
					genotypeTable.removeColumn(currCol);
					nrOfGenotypes--;
					this.getMessages().add(new ScreenMessage("Gene modification + state pair successfully removed", true));
				} else {
					this.getMessages().add(new ScreenMessage("Cannot remove - at least one Gene modification + state pair has to remain", false));
				}
				storeGenotypeTable(db, request);
			}
			
			if (action.equals("Genotype")) {
				int animalCount = Genotype(db, request);
				
				this.action = "ShowLitters";
				this.selectedParentgroup = -1;
				reload(db);
				reloadLitterLists(db);
				reloadLitterMatrixViewer();
				this.getMessages().add(new ScreenMessage("All " + animalCount + " animals successfully genotyped", true));
			}
			
		} catch (Exception e) {
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
			this.action = "ShowLitters";
		}
	}

	private int Genotype(Database db, Tuple request) throws Exception
	{
		Date now = new Date();
		int invid = ct.getObservationTargetById(this.genoLitterId).getInvestigation_Id();
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserName());
		
		// Set genotype date on litter -> this is how we mark a litter as genotyped
		int protocolId = ct.getProtocolId("SetGenotypeDate");
		int measurementId = ct.getMeasurementId("GenotypeDate");
		if (request.getString("genodate") == null) {
			throw new Exception("Genotype date not filled in - litter not genotyped");
		}
		Date genoDate = oldDateOnlyFormat.parse(request.getString("genodate"));
		String genodate = newDateOnlyFormat.format(genoDate);
		db.add(ct.createObservedValueWithProtocolApplication(invid, now, 
				null, protocolId, measurementId, this.genoLitterId, genodate, 0));
		
		// Set genotyping remarks on litter
		if (request.getString("remarks") != null) {
			protocolId = ct.getProtocolId("SetRemark");
			measurementId = ct.getMeasurementId("Remark");
			db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
					protocolId, measurementId, this.genoLitterId, request.getString("remarks"), 0));
		}
		
		int animalCount = 0;
		for (Individual animal : this.getAnimalsInLitter(db)) {
			
			// Here we (re)set the values from the genotyping
			
			// Set sex
			int sexId = request.getInt("1_" + animalCount);
			ObservedValue value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
					ct.getMeasurementByName("Sex"), investigationIds, invid).get(0);
			value.setRelation_Id(sexId);
			value.setValue(null);
			if (value.getProtocolApplication_Id() == null) {
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetSex"));
				value.setProtocolApplication_Id(paId);
				db.add(value);
			} else {
				db.update(value);
			}
			// Set birth date
			String dob = request.getString("0_" + animalCount); // already in new format
			value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
						ct.getMeasurementByName("DateOfBirth"), investigationIds, invid).get(0);
			value.setValue(dob);
			if (value.getProtocolApplication_Id() == null) {
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetDateOfBirth"));
				value.setProtocolApplication_Id(paId);
				db.add(value);
			} else {
				db.update(value);
			}
			// Set color
			String color = request.getString("2_" + animalCount);
			value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
					ct.getMeasurementByName("Color"), investigationIds, invid).get(0);
			value.setValue(color);
			if (value.getProtocolApplication_Id() == null) {
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetColor"));
				value.setProtocolApplication_Id(paId);
				db.add(value);
			} else {
				db.update(value);
			}
			// Set earmark
			String earmark = request.getString("3_" + animalCount);
			value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
					ct.getMeasurementByName("Earmark"), investigationIds, invid).get(0);
			value.setValue(earmark);
			if (value.getProtocolApplication_Id() == null) {
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetEarmark"));
				value.setProtocolApplication_Id(paId);
				db.add(value);
			} else {
				db.update(value);
			}
			// Set background
			int backgroundId = request.getInt("4_" + animalCount);
			value = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
					ct.getMeasurementByName("Background"), investigationIds, invid).get(0);
			value.setRelation_Id(backgroundId);
			value.setValue(null);
			if (value.getProtocolApplication_Id() == null) {
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetBackground"));
				value.setProtocolApplication_Id(paId);
				db.add(value);
			} else {
				db.update(value);
			}
			// Set genotype(s)
			for (int genoNr = 0; genoNr < nrOfGenotypes; genoNr++) {
				int currCol = 5 + (genoNr * 2);
				int paId = ct.makeProtocolApplication(invid, ct.getProtocolId("SetGenotype"));
				String geneName = request.getString(currCol + "_" + animalCount);
				List<ObservedValue> valueList = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
						ct.getMeasurementByName("GeneModification"), investigationIds, invid);
				if (genoNr < valueList.size()) {
					value = valueList.get(genoNr);
				} else {
					value = new ObservedValue();
					value.setFeature_Id(ct.getMeasurementId("GeneModification"));
					value.setTarget_Id(animal.getId());
					value.setInvestigation_Id(invid);
				}
				value.setValue(geneName);
				if (value.getProtocolApplication_Id() == null) {
					value.setProtocolApplication_Id(paId);
					db.add(value);
				} else {
					db.update(value);
				}
				String geneState = request.getString((currCol + 1) + "_" + animalCount);
				valueList = ct.getObservedValuesByTargetAndFeature(animal.getId(), 
						ct.getMeasurementByName("GeneState"), investigationIds, invid);
				if (genoNr < valueList.size()) {
					value = valueList.get(genoNr);
				} else {
					value = new ObservedValue();
					value.setFeature_Id(ct.getMeasurementId("GeneState"));
					value.setTarget_Id(animal.getId());
					value.setInvestigation_Id(invid);
				}
				value.setValue(geneState);
				if (value.getProtocolApplication_Id() == null) {
					value.setProtocolApplication_Id(paId);
					db.add(value);
				} else {
					db.update(value);
				}
			}
			
			animalCount++;
		}
		return animalCount;
	}

	private void AddGenoCol(Database db, Tuple request)
	{
		nrOfGenotypes++;
		genotypeTable.addColumn("Gene modification");
		genotypeTable.addColumn("Gene state");
		int row = 0;
		for (Individual animal : getAnimalsInLitter(db)) {
			int animalId = animal.getId();
			// Check for already selected genes for this animal
			List<String> selectedGenes = new ArrayList<String>();
			for (int genoNr = 0; genoNr < nrOfGenotypes - 1; genoNr++) {
				int currCol = 5 + (genoNr * 2);
				if (request.getString(currCol + "_" + row) != null) {
					selectedGenes.add(request.getString(currCol + "_" + row));
				}
			}
			// Make new gene mod name box
			int newCol = 5 + ((nrOfGenotypes - 1) * 2);
			SelectInput geneNameInput = new SelectInput(newCol + "_" + row);
			for (String geneName : this.geneNameList) {
				if (!selectedGenes.contains(geneName)) {
					geneNameInput.addOption(geneName, geneName);
				}
			}
			geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalId, nrOfGenotypes, db));
			geneNameInput.setWidth(-1);
			genotypeTable.setCell(newCol, row, geneNameInput);
			// Make new gene state box
			SelectInput geneStateInput = new SelectInput((newCol + 1) + "_" + row);
			for (String geneState : this.geneStateList) {
				geneStateInput.addOption(geneState, geneState);
			}
			geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalId, nrOfGenotypes, db));
			geneStateInput.setWidth(-1);
			genotypeTable.setCell(newCol + 1, row, geneStateInput);
			row++;
		}
	}

	private void ShowGenotype(Database db, Tuple request)
	{
		nrOfGenotypes = 1;
		this.setGenoLitterId(request.getInt("id"));
		// Prepare table
		genotypeTable = new Table("GenoTable", "");
		genotypeTable.addColumn("Birth date");
		genotypeTable.addColumn("Sex");
		genotypeTable.addColumn("Color");
		genotypeTable.addColumn("Earmark");
		genotypeTable.addColumn("Background");
		genotypeTable.addColumn("Gene modification");
		genotypeTable.addColumn("Gene state");
		int row = 0;
		for (Individual animal : getAnimalsInLitter(db)) {
			int animalId = animal.getId();
			genotypeTable.addRow(animal.getName());
			// Birth date
			DateInput dateInput = new DateInput("0_" + row);
			dateInput.setValue(getAnimalBirthDate(animalId));
			genotypeTable.setCell(0, row, dateInput);
			// Sex
			SelectInput sexInput = new SelectInput("1_" + row);
			for (ObservationTarget sex : this.sexList) {
				sexInput.addOption(sex.getId(), sex.getName());
			}
			sexInput.setValue(getAnimalSex(animalId));
			sexInput.setWidth(-1);
			genotypeTable.setCell(1, row, sexInput);
			// Color
			SelectInput colorInput = new SelectInput("2_" + row);
			for (String color : this.colorList) {
				colorInput.addOption(color, color);
			}
			colorInput.setValue(getAnimalColor(animalId));
			colorInput.setWidth(-1);
			genotypeTable.setCell(2, row, colorInput);
			// Earmark
			SelectInput earmarkInput = new SelectInput("3_" + row);
			for (Category earmark : this.earmarkList) {
				earmarkInput.addOption(earmark.getCode_String(), earmark.getCode_String());
			}
			earmarkInput.setValue(getAnimalEarmark(animalId));
			earmarkInput.setWidth(-1);
			genotypeTable.setCell(3, row, earmarkInput);
			// Background
			SelectInput backgroundInput = new SelectInput("4_" + row);
			for (ObservationTarget background : this.backgroundList) {
				backgroundInput.addOption(background.getId(), background.getName());
			}
			backgroundInput.setValue(getAnimalBackground(animalId));
			backgroundInput.setWidth(-1);
			genotypeTable.setCell(4, row, backgroundInput);
			
			// TODO: show columns and selectboxes for ALL set geno mods
			
			// Gene mod name (1)
			SelectInput geneNameInput = new SelectInput("5_" + row);
			for (String geneName : this.geneNameList) {
				geneNameInput.addOption(geneName, geneName);
			}
			geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalId, 0, db));
			geneNameInput.setWidth(-1);
			genotypeTable.setCell(5, row, geneNameInput);
			// Gene state (1)
			SelectInput geneStateInput = new SelectInput("6_" + row);
			for (String geneState : this.geneStateList) {
				geneStateInput.addOption(geneState, geneState);
			}
			geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalId, 0, db));
			geneStateInput.setWidth(-1);
			genotypeTable.setCell(6, row, geneStateInput);
			row++;
		}
	}

	private int Wean(Database db, Tuple request) throws Exception
	{
		Date now = new Date();
		int invid = ct.getObservationTargetById(litter).getInvestigation_Id();
		setUserFields(request, true);
		Date weanDate = newDateOnlyFormat.parse(weandate);
		int userId = this.getLogin().getUserId();
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		List<ObservationTarget> animalsToAddList = new ArrayList<ObservationTarget>();
		
		// Source (take from litter)
		int sourceId = ct.getMostRecentValueAsXref(litter, "Source");
		// Get litter birth date
		String litterBirthDateString = ct.getMostRecentValueAsString(litter, "DateOfBirth");
		Date litterBirthDate = newDateOnlyFormat.parse(litterBirthDateString);
		// Find Parentgroup for this litter
		int parentgroupId = ct.getMostRecentValueAsXref(litter, "Parentgroup");
		String parentgroupName = db.findById(Panel.class, parentgroupId).getName();
		// Find Line for this Parentgroup
		int lineId = ct.getMostRecentValueAsXref(parentgroupId, "Line");
		// Find first mother, plus her animal type, species, color, background, gene modification and gene state
		// TODO: find ALL gene info
		int motherId = findParentForParentgroup(parentgroupName, "Mother", db);
		int speciesId = ct.getMostRecentValueAsXref(motherId, "Species");
		String animalType = ct.getMostRecentValueAsString(motherId, "AnimalType");
		String color = ct.getMostRecentValueAsString(motherId, "Color");
		int motherBackgroundId = ct.getMostRecentValueAsXref(motherId, "Background");
		String geneName = ct.getMostRecentValueAsString(motherId, "GeneModification");
		String geneState = ct.getMostRecentValueAsString(motherId, "GeneState");
		// Find father and his background
		int fatherId = findParentForParentgroup(parentgroupName, "Father", db);
		int fatherBackgroundId = ct.getMostRecentValueAsXref(fatherId, "Background");
		// Keep normal and transgene types, but set type of child from wild parents to normal
		// TODO: animalType should be based on BOTH mother and father
		if (animalType.equals("C. Wildvang") || animalType.equals("D. Biotoop")) {
			animalType = "A. Gewoon dier";
		}
		// Set wean sizes
		int weanSize = weanSizeFemale + weanSizeMale + weanSizeUnknown;
		int protocolId = ct.getProtocolId("SetWeanSize");
		int measurementId = ct.getMeasurementId("WeanSize");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, litter, Integer.toString(weanSize), 0));
		protocolId = ct.getProtocolId("SetWeanSizeFemale");
		measurementId = ct.getMeasurementId("WeanSizeFemale");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, litter, Integer.toString(weanSizeFemale), 0));
		protocolId = ct.getProtocolId("SetWeanSizeMale");
		measurementId = ct.getMeasurementId("WeanSizeMale");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, litter, Integer.toString(weanSizeMale), 0));
		protocolId = ct.getProtocolId("SetWeanSizeUnknown");
		measurementId = ct.getMeasurementId("WeanSizeUnknown");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, litter, Integer.toString(weanSizeUnknown), 0));
		// Set wean date on litter -> this is how we mark a litter as weaned (but not genotyped)
		protocolId = ct.getProtocolId("SetWeanDate");
		measurementId = ct.getMeasurementId("WeanDate");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
				null, protocolId, measurementId, litter, newDateOnlyFormat.format(weanDate), 0));
		// Set weaning remarks on litter
		if (remarks != null) {
			protocolId = ct.getProtocolId("SetRemark");
			measurementId = ct.getMeasurementId("Remark");
			db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
					protocolId, measurementId, litter, remarks, 0));
		}
		
		// change litter status from active to inactive
		protocolId = ct.getProtocolId("SetActive");
		measurementId = ct.getMeasurementId("Active");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, null, 
				protocolId, measurementId, litter, "Inactive", 0));
			
		// Make animal, link to litter, parents and set wean dates etc.
		for (int animalNumber = 0; animalNumber < weanSize; animalNumber++) {
			String nrPart = "" + (startNumber + animalNumber);
			nrPart = ct.prependZeros(nrPart, 6);
			ObservationTarget animalToAdd = ct.createIndividual(invid, nameBase + nrPart, 
					userId);
			animalsToAddList.add(animalToAdd);
		}
		db.add(animalsToAddList);
		
		// Make or update name prefix entry
		ct.updatePrefix("animal", nameBase, startNumber + weanSize - 1);
		
		int animalNumber = 0;
		for (ObservationTarget animal : animalsToAddList) {
			int animalId = animal.getId();
			
			// TODO: link every value to a single Wean protocol application instead of to its own one
			
			// Link to litter
			protocolId = ct.getProtocolId("SetLitter");
			measurementId = ct.getMeasurementId("Litter");
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
					null, protocolId, measurementId, animalId, null, litter));
			// Link to parents using the Mother and Father measurements
			if (motherId != -1) {
				protocolId = ct.getProtocolId("SetMother");
				measurementId = ct.getMeasurementId("Mother");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, null, motherId));
			}
			if (fatherId != -1) {
				protocolId = ct.getProtocolId("SetFather");
				measurementId = ct.getMeasurementId("Father");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, null, fatherId));
			}
			// Set line also on animal itself
			if (lineId != -1) {
				protocolId = ct.getProtocolId("SetLine");
				measurementId = ct.getMeasurementId("Line");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, null, lineId));
			}
			// Set responsible researcher
			if (respres != null) {
				protocolId = ct.getProtocolId("SetResponsibleResearcher");
				measurementId = ct.getMeasurementId("ResponsibleResearcher");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
						null, protocolId, measurementId, animalId, respres, 0));
			}
			// Set location
			if (locId != -1) {
				protocolId = ct.getProtocolId("SetLocation");
				measurementId = ct.getMeasurementId("Location");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
						null, protocolId, measurementId, animalId, null, locId));
			}
			// Set sex
			int sexId = ct.getObservationTargetId("Female");
			if (animalNumber >= weanSizeFemale) {
				if (animalNumber < weanSizeFemale + weanSizeMale) {
					sexId = ct.getObservationTargetId("Male");
				} else {
					sexId = ct.getObservationTargetId("UnknownSex");
				}
			}
			protocolId = ct.getProtocolId("SetSex");
			measurementId = ct.getMeasurementId("Sex");
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
					null, protocolId, measurementId, animalId, null, sexId));
			// Set wean date on animal
			protocolId = ct.getProtocolId("SetWeanDate");
			measurementId = ct.getMeasurementId("WeanDate");
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
					null, protocolId, measurementId, animalId, newDateOnlyFormat.format(weanDate), 0));
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
	 		if (speciesId != -1) {
		 		protocolId = ct.getProtocolId("SetSpecies");
				measurementId = ct.getMeasurementId("Species");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, null, speciesId));
	 		}
			// Set animal type
			protocolId = ct.getProtocolId("SetAnimalType");
			measurementId = ct.getMeasurementId("AnimalType");
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
					null, protocolId, measurementId, animalId, animalType, 0));
			// Set source
			if (sourceId != -1) {
				protocolId = ct.getProtocolId("SetSource");
				measurementId = ct.getMeasurementId("Source");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, null, sourceId));
			}
			// Set color based on mother's (can be changed during genotyping)
			if (color != null && !color.equals("")) {
				protocolId = ct.getProtocolId("SetColor");
				measurementId = ct.getMeasurementId("Color");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
						null, protocolId, measurementId, animalId, color, 0));
			}
			// Set background based on mother's and father's (can be changed during genotyping)
			int backgroundId = -1;
			if (motherBackgroundId != -1 && fatherBackgroundId == -1) {
				backgroundId = motherBackgroundId;
			} else if (motherBackgroundId == -1 && fatherBackgroundId != -1) {
				backgroundId = fatherBackgroundId;
			} else if (motherBackgroundId != -1 && fatherBackgroundId != -1) {
				// Make new or use existing cross background
				String motherBackgroundName = ct.getObservationTargetLabel(motherBackgroundId);
				String fatherBackgroundName = ct.getObservationTargetLabel(fatherBackgroundId);
				if (motherBackgroundId == fatherBackgroundId) {
					backgroundId = ct.getObservationTargetId(fatherBackgroundName);
				} else {
					backgroundId = ct.getObservationTargetId(fatherBackgroundName + " X " + motherBackgroundName);
				}
				if (backgroundId == -1) {
					backgroundId = ct.makePanel(invid, fatherBackgroundName + " X " + motherBackgroundName, userId);
					protocolId = ct.getProtocolId("SetTypeOfGroup");
					measurementId = ct.getMeasurementId("TypeOfGroup");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, null, 
							protocolId, measurementId, backgroundId, "Background", 0));
				}
			}
			if (backgroundId != -1) {
				protocolId = ct.getProtocolId("SetBackground");
				measurementId = ct.getMeasurementId("Background");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, weanDate, 
							null, protocolId, measurementId, animalId, null, backgroundId));
			}
			// Set genotype
			// TODO: Set based on mother's X father's and ONLY if you can know the outcome
			if (geneName != null && !geneName.equals("") && geneState != null && !geneState.equals("")) {
				protocolId = ct.getProtocolId("SetGenotype");
				int paId = ct.makeProtocolApplication(invid, protocolId);
				// Set gene mod name based on mother's (can be changed during genotyping)
				measurementId = ct.getMeasurementId("GeneModification");
				valuesToAddList.add(ct.createObservedValue(invid, paId, weanDate, 
						null, measurementId, animalId, geneName, 0));
				// Set gene state based on mother's (can be changed during genotyping)
				measurementId = ct.getMeasurementId("GeneState");
				valuesToAddList.add(ct.createObservedValue(invid, paId, weanDate, 
						null, measurementId, animalId, geneState, 0));
			}
			
			animalNumber++;
		}
		
		db.add(valuesToAddList);
		
		return weanSize;
	}

	private String ApplyAddLitter(Database db, Tuple request) throws Exception
	{
		Date now = new Date();
		
		//setUserFields(request, false);
		List<?> rows = matrixViewer.getSelection(db);
		try { 
			int row = request.getInt(MATRIX + "_selected");
			this.selectedParentgroup = ((ObservationElement) rows.get(row)).getId();
		} catch (Exception e) {	
			this.setAction("AddLitter");
			throw new Exception("No parentgroup selected - litter not added");
		}
		
		int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
		setUserFields(request, false);
		Date eventDate = newDateOnlyFormat.parse(birthdate);
		int userId = this.getLogin().getUserId();
		int lineId = ct.getMostRecentValueAsXref(selectedParentgroup, "Line");
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		// Make group
		String litterPrefix = "LT_" + ct.getObservationTargetLabel(lineId) + "_";
		int litterNr = ct.getHighestNumberForPrefix(litterPrefix) + 1;
		String litterNrPart = "" + litterNr;
		litterNrPart = ct.prependZeros(litterNrPart, 6);
		int litterid = ct.makePanel(invid, litterPrefix + litterNrPart, userId);
		// Make or update name prefix entry
		ct.updatePrefix("litter", litterPrefix, litterNr);
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
		// Set Line also on Litter
		if (lineId != -1) {
			measurementId = ct.getMeasurementId("Line");
			valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, 
				litterid, null, lineId));
		}
		// Date of Birth
		measurementId = ct.getMeasurementId("DateOfBirth");
		valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, 
				litterid, newDateOnlyFormat.format(eventDate), 0));
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
		// Remarks
		if (remarks != null) {
			protocolId = ct.getProtocolId("SetRemark");
			measurementId = ct.getMeasurementId("Remark");
			db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
					protocolId, measurementId, litterid, remarks, 0));
		}
		// Try to get Source via Line
		int sourceId = ct.getMostRecentValueAsXref(lineId, "Source");
		if (sourceId != -1) {
			measurementId = ct.getMeasurementId("Source");
			protocolId = ct.getProtocolId("SetSource");
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
					eventDate, null, protocolId, measurementId, litterid, null, sourceId));
		}
		// Active
		measurementId = ct.getMeasurementId("Active");
		valuesToAddList.add(ct.createObservedValue(invid, eventid, eventDate, null, measurementId, litterid, 
				"Active", 0));
		
		// Add everything to DB
		db.add(valuesToAddList);
		
		return litterPrefix + litterNrPart;
	}

	private void makeDefCageLabels(Database db) throws LabelGeneratorException, DatabaseException, ParseException {
		
		// PDF file stuff
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "deflabels.pdf");
		String filename = pdfFile.getName();
		LabelGenerator labelgenerator = new LabelGenerator(2);
		labelgenerator.startDocument(pdfFile);
		
		// Litter stuff
		int parentgroupId = ct.getMostRecentValueAsXref(litter, "Parentgroup");
		String parentgroupName = db.findById(Panel.class, parentgroupId).getName();
		String line = this.getLineInfo(parentgroupId);
		int motherId = findParentForParentgroup(parentgroupName, "Mother", db);
		String motherInfo = this.getGenoInfo(motherId, db);
		int fatherId = findParentForParentgroup(parentgroupName, "Father", db);
		String fatherInfo = this.getGenoInfo(fatherId, db);
		
		List<String> elementLabelList;	
		List<String> elementList;
		
		for (Individual animal : this.getAnimalsInLitter(litter, db)) {
			int animalId = animal.getId();
			elementList = new ArrayList<String>();
			elementLabelList = new ArrayList<String>();
			
			// Name / custom label
			elementLabelList.add("Name:");
			elementList.add(ct.getObservationTargetLabel(animalId));
			// Earmark
			elementLabelList.add("Earmark:");
			elementList.add(ct.getMostRecentValueAsString(animalId, "Earmark"));
			// Line
			elementLabelList.add("Line:");
			elementList.add(line);
			// Background + GeneModification + GeneState
			elementLabelList.add("Genotype:");
			elementList.add(this.getGenoInfo(animalId, db));
			// Color + Sex
			elementLabelList.add("Color and Sex:");
			String color = ct.getMostRecentValueAsString(animalId, "Color");
			if (color == null || color.equals("null") || color.equals("")) {
				color = "unknown";
			}
			int sexId = ct.getMostRecentValueAsXref(animalId, "Sex");
			String sex = ct.getObservationTargetById(sexId).getName();
			elementList.add(color + "\t\t" + sex);
			//Birthdate
			elementLabelList.add("Birthdate:");
			elementList.add(ct.getMostRecentValueAsString(animalId, "DateOfBirth"));
			// Geno mother
			elementLabelList.add("Genotype mother:");
			elementList.add(motherInfo);
			// Geno father
			elementLabelList.add("Genotype father:");
			elementList.add(fatherInfo);
			// Add DEC nr, if present, or empty if not
			elementLabelList.add("DEC:");
			String DecNr = ct.getMostRecentValueAsString(animalId, "DecNr") + " " + ct.getMostRecentValueAsString(animalId, "ExperimentNr");
			elementList.add(DecNr);
			// Not needed at this time, maybe later:
			// Birthdate
			//elementList.add("Birthdate: " + ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("DateOfBirth")));
			// OldUliDbExperimentator -> TODO: add responsible researcher
			//elementList.add("Experimenter: " + ct.getMostRecentValueAsString(animalId, ct.getMeasurementId("OldUliDbExperimentator")));
			
			labelgenerator.addLabelToDocument(elementLabelList, elementList);
		}
		
		// In case of an odd number of animals, add extra label to make row full
		if (this.getAnimalsInLitter(litter, db).size() %2 != 0) {
			elementLabelList = new ArrayList<String>();
			elementList = new ArrayList<String>();
			labelgenerator.addLabelToDocument(elementLabelList, elementList);
		}
		
		labelgenerator.finishDocument();
		this.setLabelDownloadLink("<a href=\"tmpfile/" + filename + "\" target=\"blank\">Download definitive cage labels as pdf</a>");
	}

	private void makeTempCageLabels(Database db) throws Exception {
		
		// PDF file stuff
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "weanlabels.pdf");
		String filename = pdfFile.getName();
		LabelGenerator labelgenerator = new LabelGenerator(2);
		labelgenerator.startDocument(pdfFile);
		List<String> elementList;
		
		// Selected litter stuff
		int parentgroupId = ct.getMostRecentValueAsXref(litter, "Parentgroup");
		String parentgroupName = db.findById(Panel.class, parentgroupId).getName();
		int lineId = ct.getMostRecentValueAsXref(parentgroupId, ct.getMeasurementId("Line"));
		String lineName = ct.getObservationTargetById(lineId).getName();
		int motherId = findParentForParentgroup(parentgroupName, "Mother", db);
		String motherName = ct.getObservationTargetById(motherId).getName();
		int fatherId = findParentForParentgroup(parentgroupName, "Father", db);
		String fatherName = ct.getObservationTargetById(fatherId).getName();
		String litterBirthDateString = ct.getMostRecentValueAsString(litter, "DateOfBirth");
		int nrOfFemales = Integer.parseInt(ct.getMostRecentValueAsString(litter, "WeanSizeFemale"));
		int nrOfMales = Integer.parseInt(ct.getMostRecentValueAsString(litter, "WeanSizeMale"));
		int nrOfUnknowns = Integer.parseInt(ct.getMostRecentValueAsString(litter, "WeanSizeUnknown"));
		List<ObservedValue> litterValList = db.query(ObservedValue.class).eq(ObservedValue.FEATURE_NAME, "Litter").
				eq(ObservedValue.RELATION, litter).find();
		int femaleId = ct.getObservationTargetId("Female");
		int maleId = ct.getObservationTargetId("Male");
		List<String> females = new ArrayList<String>();
		List<String> males = new ArrayList<String>();
		List<String> unknowns = new ArrayList<String>();
		for (ObservedValue litterVal : litterValList) {
			int animalId = litterVal.getTarget_Id();
			String animalName = litterVal.getTarget_Name();
			if (ct.getMostRecentValueAsXref(animalId, "Sex") == femaleId) {
				females.add(animalName);
			} else if (ct.getMostRecentValueAsXref(animalId, "Sex") == maleId) {
				males.add(animalName);
			} else {
				unknowns.add(animalName);
			}
		}
		
		// Labels for females
		int nrOfCages = 0;
		int femaleNr = 0;
		while (nrOfFemales > 0) {
			elementList = new ArrayList<String>();
			// Line name + Nr. of females in cage
			String firstLine = lineName + "\t\t"; 
			// Females can be 2 or 3 in a cage, if possible not 1
			int cageSize;
			if (nrOfFemales > 4) {
				cageSize = 3;
			} else {
				if (nrOfFemales == 4) {
					cageSize = 2;
				} else {
					cageSize = nrOfFemales;
				}
			}
			firstLine += (cageSize + " female");
			if (cageSize > 1) firstLine += "s";
			elementList.add(firstLine);
			// Parents
			elementList.add(motherName + " x " + fatherName);
			// Litter birth date
			elementList.add(litterBirthDateString);
			// Nrs. for writing extra information behind
			for (int i = 1; i <= cageSize; i++) {
				elementList.add(females.get(femaleNr++) + ".");
			}
			
			labelgenerator.addLabelToDocument(elementList);
			nrOfFemales -= cageSize;
			nrOfCages++;
		}
		
		// Labels for males
		int maleNr = 0;
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
			elementList.add(motherName + " x " + fatherName);
			// Litter birth date
			elementList.add(litterBirthDateString);
			// Nrs. for writing extra information behind
			for (int i = 1; i <= Math.min(nrOfMales, 2); i++) {
				elementList.add(males.get(maleNr++) + ".");
			}
			
			labelgenerator.addLabelToDocument(elementList);
			nrOfMales -= 2;
			nrOfCages++;
		}
		
		// Labels for unknowns
		// TODO: keep or group together with (fe)males?
		int unknownNr = 0;
		while (nrOfUnknowns > 0) {
			elementList = new ArrayList<String>();
			// Line name + Nr. of unknowns in cage
			String firstLine = lineName; 
			if (nrOfUnknowns >= 2) {
				firstLine += "\t\t2 unknowns";
			} else {
				firstLine += "\t\t1 unknown";
			}
			elementList.add(firstLine);
			// Parents
			elementList.add(motherName + " x " + fatherName);
			// Litter birth date
			elementList.add(litterBirthDateString);
			// Nrs. for writing extra information behind
			for (int i = 1; i <= Math.min(nrOfUnknowns, 2); i++) {
				elementList.add(unknowns.get(unknownNr++) + ".");
			}
			
			labelgenerator.addLabelToDocument(elementList);
			nrOfUnknowns -= 2;
			nrOfCages++;
		}
		
		// In case of an odd number of cages, add extra label to make row full
		if (nrOfCages %2 != 0) {
			elementList = new ArrayList<String>();
			labelgenerator.addLabelToDocument(elementList);
		}
		
		labelgenerator.finishDocument();
		this.setLabelDownloadLink("<a href=\"tmpfile/" + filename + "\" target=\"blank\">Download temporary wean labels as pdf</a>");
	}

	@Override
	public void reload(Database db)
	{	
		ct.setDatabase(db);
		this.toHtmlDb = db;
		if (this.getLogin().getUserId().intValue() != userId) {
			userId = this.getLogin().getUserId().intValue();
			reloadLitterLists(db);
			reloadMatrixViewer();
			reloadLitterMatrixViewer();
		}
		
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserName());
			
			// Populate parent group list
			this.setParentgroupList(ct.getAllMarkedPanels("Parentgroup", investigationIds));
			// Populate backgrounds list
			this.setBackgroundList(ct.getAllMarkedPanels("Background", investigationIds));
			// Populate sexes list
			this.setSexList(ct.getAllMarkedPanels("Sex", investigationIds));
			// Populate gene name list
			this.setGeneNameList(ct.getAllCodesForFeatureAsStrings("GeneModification"));
			// Populate gene state list
			this.setGeneStateList(ct.getAllCodesForFeatureAsStrings("GeneState"));
			// Populate color list
			this.setColorList(ct.getAllCodesForFeatureAsStrings("Color"));
			// Populate earmark list
			this.setEarmarkList(ct.getAllCodesForFeature("Earmark"));
			// Populate name prefixes list for the animals
			this.bases = new ArrayList<String>();
			List<String> tmpPrefixes = ct.getPrefixes("animal");
			for (String tmpPrefix : tmpPrefixes) {
				if (!tmpPrefix.equals("")) {
					this.bases.add(tmpPrefix);
				}
			}
			// Populate location list
			this.setLocationList(ct.getAllLocations());
		} catch (Exception e) {
			if (e.getMessage() != null) {
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
	private void reloadLitterLists(Database db) {
		ct.setDatabase(db);
		ct.makeObservationTargetNameMap(this.getLogin().getUserName(), false);
		
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserName());
			
			// Populate litter lists
			litterList.clear();
			genoLitterList.clear();
			
			// Make list of ID's of weaned litters
			List<Integer> weanedLitterIdList = new ArrayList<Integer>();
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "WeanDate"));
			List<ObservedValue> valueList = q.find();
			for (ObservedValue value : valueList) {
				int litterId = value.getTarget_Id();
				if (!weanedLitterIdList.contains(litterId)) {
					weanedLitterIdList.add(litterId);
				}
			}
			// Make list of ID's of genotyped litters
			List<Integer> genotypedLitterIdList = new ArrayList<Integer>();
			q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GenotypeDate"));
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
				
				if (genotypedLitterIdList.contains(litterId)) {
					continue;
				}
				
				// Make a temporary litter and set all relevant values
				Litter litterToAdd = new Litter();
				// ID
				litterToAdd.setId(litterId);
				// Name
				litterToAdd.setName(litter.getName());
				// Parentgroup
				int parentgroupId = ct.getMostRecentValueAsXref(litterId, "Parentgroup");
				if (parentgroupId != -1) {
					String parentgroup = ct.getObservationTargetLabel(parentgroupId);
					litterToAdd.setParentgroup(parentgroup);
				}
				// Line
				int lineId = ct.getMostRecentValueAsXref(litterId, "Line");
				if (lineId != -1) {
					String line = ct.getObservationTargetLabel(lineId);
					litterToAdd.setLine(line);
				}
				// Birth date
				String birthDate = ct.getMostRecentValueAsString(litterId, "DateOfBirth");
				if (birthDate != null && !birthDate.equals("")) {
					litterToAdd.setBirthDate(birthDate);
				}
				// Wean date
				String weanDate = ct.getMostRecentValueAsString(litterId,"WeanDate");
				if (weanDate != null && !weanDate.equals("")) {
					litterToAdd.setWeanDate(weanDate);
				}
				// Size
				String size = ct.getMostRecentValueAsString(litterId, "Size");
				if (size.equals("")) {
					litterToAdd.setSize(-1);
				} else {
					litterToAdd.setSize(Integer.parseInt(size));
				}
				// Wean size
				String weanSize = ct.getMostRecentValueAsString(litterId, "WeanSize");
				if (weanSize.equals("")) {
					litterToAdd.setWeanSize(-1);
				} else {
					litterToAdd.setWeanSize(Integer.parseInt(weanSize));
				}
				// Size approximate
				String isApproximate = "";
				String tmpValue = ct.getMostRecentValueAsString(litterId, "Certain");
				if (tmpValue.equals("0")) {
					isApproximate = "No";
				}
				if (tmpValue.equals("1")) {
					isApproximate = "Yes";
				}
				litterToAdd.setSizeApproximate(isApproximate);
				// Remarks
				List<String> remarksList = ct.getRemarks(litterId);
				String remarks = "";
				for (String remark : remarksList) {
					remarks += (remark + "<br>");
				}
				if (remarks.length() > 0) {
					remarks = remarks.substring(0, remarks.length() - 4);
				}
				litterToAdd.setRemarks(remarks);
				// Status
				String status = ct.getMostRecentValueAsString(litterId, "Active");
				litterToAdd.setStatus(status);
				
				// Add to the right list
				if (!weanedLitterIdList.contains(litterId) && !genotypedLitterIdList.contains(litterId)) {
					litterList.add(litterToAdd);
				} else {
					if (!genotypedLitterIdList.contains(litterId)) {
						genoLitterList.add(litterToAdd);
					}
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

	public String getGenotypeTable() {
		return genotypeTable.render();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void storeGenotypeTable(Database db, Tuple request) {
		HtmlInput input;
		for (int animalCount = 0; animalCount < this.getAnimalsInLitter(db).size(); animalCount++) {
			
			if (request.getString("0_" + animalCount) != null) {
				String dob = request.getString("0_" + animalCount); // already in new format
				input = (HtmlInput) genotypeTable.getCell(0, animalCount);
				input.setValue(dob);
				genotypeTable.setCell(0, animalCount, input);
			}
			
			if (request.getString("1_" + animalCount) != null) {
				int sexId = request.getInt("1_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(1, animalCount);
				input.setValue(sexId);
				genotypeTable.setCell(1, animalCount, input);
			}
			
			if (request.getString("2_" + animalCount) != null) {
				String color = request.getString("2_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(2, animalCount);
				input.setValue(color);
				genotypeTable.setCell(2, animalCount, input);
			}
			
			if (request.getString("3_" + animalCount) != null) {
				String earmark = request.getString("3_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(3, animalCount);
				input.setValue(earmark);
				genotypeTable.setCell(3, animalCount, input);
			}
			
			if (request.getString("4_" + animalCount) != null) {
				int backgroundId = request.getInt("4_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(4, animalCount);
				input.setValue(backgroundId);
				genotypeTable.setCell(4, animalCount, input);
			}
			
			for (int genoNr = 0; genoNr < nrOfGenotypes; genoNr++) {
				int currCol = 5 + (genoNr * 2);
				
				if (request.getString(currCol + "_" + animalCount) != null) {
					String geneName = request.getString(currCol + "_" + animalCount);
					input = (HtmlInput) genotypeTable.getCell(currCol, animalCount);
					input.setValue(geneName);
					genotypeTable.setCell(currCol, animalCount, input);
				}
				
				if (request.getString((currCol + 1) + "_" + animalCount) != null) {
					String geneState = request.getString((currCol + 1) + "_" + animalCount);
					input = (HtmlInput) genotypeTable.getCell(currCol + 1, animalCount);
					input.setValue(geneState);
					genotypeTable.setCell(currCol + 1, animalCount, input);
				}
			}
			
			animalCount++;
		}
	}
	
	private void reloadMatrixViewer() {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Line");
			measurementsToShow.add("StartDate");
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("TypeOfGroup"),
					ObservedValue.VALUE, Operator.EQUALS, "Parentgroup"));
			matrixViewer = new MatrixViewer(this, MATRIX, 
					new SliceablePhenoMatrix<Panel, Measurement>(Panel.class, Measurement.class), 
					true, 1, false, false, filterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading matrix viewer";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	private void reloadLitterMatrixViewer() {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Parentgroup");
			measurementsToShow.add("WeanDate");
			measurementsToShow.add("GenotypeDate");
			measurementsToShow.add("Remark");
			measurementsToShow.add("Line");
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Panel.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("TypeOfGroup"),
					ObservedValue.VALUE, Operator.EQUALS, "Litter"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("WeanDate"),
					ObservedValue.VALUE, Operator.NOT, null));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("GenotypeDate"),
					ObservedValue.VALUE, Operator.NOT, null));
			litterMatrixViewer = new MatrixViewer(this, LITTERMATRIX, 
					new SliceablePhenoMatrix<Panel, Measurement>(Panel.class, Measurement.class), 
					true, 1, false, false, filterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading matrix viewer";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
