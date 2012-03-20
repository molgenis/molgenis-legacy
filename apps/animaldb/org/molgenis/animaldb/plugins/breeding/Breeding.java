/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class Breeding extends PluginModel<Entity>
{
	private static final long serialVersionUID = 203412348106990472L;
	private List<String> selectedMotherNameList = null;
	private List<String> selectedFatherNameList = null;
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String startdate = null;
	private List<ObservationTarget> lineList;
	private String line = null;
	private String litterRemarks = null;
	//private String pgStatus = null;
	MatrixViewer motherMatrixViewer = null;
	MatrixViewer fatherMatrixViewer = null;
	MatrixViewer pgMatrixViewer = null;
	MatrixViewer litterMatrixViewer = null;
	private static String MOTHERMATRIX = "mothermatrix";
	private static String FATHERMATRIX = "fathermatrix";
	private static String PGMATRIX = "pgmatrix";
	private static String LITTERMATRIX = "littermatrix";
	private String action = "init";
	private String userName = null;
	private String motherMatrixViewerString;
	private String fatherMatrixViewerString;
	private String pgMatrixViewerString;
	private String litterMatrixViewerString;
	private String entity = "Parentgroups";
	private String birthdate = null;
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String remarks = null;
	private String selectedParentgroup = null;
	private int litterSize;
	private boolean litterSizeApproximate;
	private String locName = null;
	private String respres = null;
	private int weanSizeFemale;
	private int weanSizeMale;
	private int weanSizeUnknown;
	private String weandate = null;
	private String nameBase = null;
	private int startNumber = -1;
	
	public Breeding(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">";
	}

	public List<String> getSelectedMotherNameList() {
		return selectedMotherNameList;
	}
	public void setSelectedMotherList(List<String> selectedMotherNameList) {
		this.selectedMotherNameList = selectedMotherNameList;
	}

	public List<String> getSelectedFatherNameList() {
		return selectedFatherNameList;
	}
	public void setSelectedFatherList(List<String> selectedFatherNameList) {
		this.selectedFatherNameList = selectedFatherNameList;
	}
	
	public String getAnimalName(Integer id) {
		try {
			return ct.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}
	
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public void setLine(String line) {
		this.line = line;
	}
	
	public String getLine() {
		return line;
	}

	public List<ObservationTarget> getLineList() {
		return lineList;
	}
	public void setLineList(List<ObservationTarget> lineList) {
		this.lineList = lineList;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getEntity()
	{
		return entity;
	}

	public void setEntity(String entity)
	{
		this.entity = entity;
	}

	public String getMotherMatrixViewer()
	{
		if (motherMatrixViewerString != null) {
			return motherMatrixViewerString;
		}
		return "Mother matrix not loaded";
	}
	
	public void loadMotherMatrixViewer(Database db) {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Sex");
			measurementsToShow.add("Active");
			measurementsToShow.add("Line");
			measurementsToShow.add("Background");
			measurementsToShow.add("GeneModification");
			measurementsToShow.add("GeneState");
			measurementsToShow.add("Species");
			// Mother matrix viewer
			List<MatrixQueryRule> motherFilterRules = new ArrayList<MatrixQueryRule>();
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Sex"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, "Female"));
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != null) {
				motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS, line));
				// Setting filter on the RELATION field with value = line would be more efficient,
				// but gives a very un-userfriendly toString value when shown in the MatrixViewer UI
				String speciesName = ct.getMostRecentValueAsXrefName(line, "Species");
				motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Species"), ObservedValue.RELATION_NAME, Operator.EQUALS, 
						speciesName));
			}
			
			SliceablePhenoMatrix<Individual, Measurement> SPMM = new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class);
			SPMM.setDatabase(db);
			motherMatrixViewer = new MatrixViewer(this, MOTHERMATRIX, SPMM, 
					true, 2, false, false, motherFilterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading mother matrix viewer";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	public String getFatherMatrixViewer()
	{
		if (fatherMatrixViewerString != null) {
			return fatherMatrixViewerString;
		}
		return "Father matrix not loaded";
	}
	
	public void loadFatherMatrixViewer(Database db) {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Sex");
			measurementsToShow.add("Active");
			measurementsToShow.add("Line");
			measurementsToShow.add("Background");
			measurementsToShow.add("GeneModification");
			measurementsToShow.add("GeneState");
			measurementsToShow.add("Species");
			// Father matrix viewer
			List<MatrixQueryRule> fatherFilterRules = new ArrayList<MatrixQueryRule>();
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Sex"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, "Male"));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != null) {
				fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS, line));
				// Setting filter on the RELATION field with value = line would be more efficient,
				// but gives a very un-userfriendly toString value when shown in the MatrixViewer UI
				String speciesName = ct.getMostRecentValueAsXrefName(line, "Species");
				fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Species"), ObservedValue.RELATION_NAME, Operator.EQUALS, 
						speciesName));
			}
			SliceablePhenoMatrix<Individual, Measurement> SPMF = new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class);
			SPMF.setDatabase(db);
			fatherMatrixViewer = new MatrixViewer(this, FATHERMATRIX, SPMF, 
					true, 2, false, false, fatherFilterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading father matrix viewer";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	public String getPgMatrixViewer()
	{
		return pgMatrixViewerString;
	}
	
	public void loadPgMatrixViewer(Database db) {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Active");
			measurementsToShow.add("StartDate");
			measurementsToShow.add("Remark");
			measurementsToShow.add("Line");
			measurementsToShow.add("ParentgroupMother");
			measurementsToShow.add("ParentgroupFather");
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Panel.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("TypeOfGroup"),
					ObservedValue.VALUE, Operator.EQUALS, "Parentgroup"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Active"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Line"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, this.line));
			pgMatrixViewer = new MatrixViewer(this, PGMATRIX, 
					new SliceablePhenoMatrix<Panel, Measurement>(Panel.class, Measurement.class), 
					true, 1, false, false, filterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading parentgroup matrix";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	public String getLitterMatrixViewer()
	{
		return litterMatrixViewerString;
	}
	
	public void loadLitterMatrixViewer(Database db) {
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Active");
			measurementsToShow.add("Parentgroup");
			measurementsToShow.add("Line");
			measurementsToShow.add("DateOfBirth");
			measurementsToShow.add("WeanDate");
			measurementsToShow.add("Size");
			measurementsToShow.add("WeanSize");
			measurementsToShow.add("GenotypeDate");
			measurementsToShow.add("Remark");
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Panel.INVESTIGATION_NAME, 
					Operator.IN, investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("TypeOfGroup"),
					ObservedValue.VALUE, Operator.EQUALS, "Litter"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Active"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Line"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, this.line));
			litterMatrixViewer = new MatrixViewer(this, LITTERMATRIX, 
					new SliceablePhenoMatrix<Panel, Measurement>(Panel.class, Measurement.class), 
					true, 1, false, false, filterRules, 
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		} catch (Exception e) {
			String message = "Something went wrong while loading litter matrix";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_breeding_Breeding";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/breeding/Breeding.ftl";
	}
	
	private void AddParents(Database db, List<String> parentNameList, String protocolName, String featureName, 
			String parentgroupName, Date eventDate) 
			throws DatabaseException, ParseException, IOException {
		
		String invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName()).get(0);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		for (String parentName : parentNameList) {
			// Find the 'SetParentgroupMother'/'SetParentgroupFather' event type
			// TODO: SetParentgroupMother/SetParentgroupFather are now plain event types with only the ParentgroupMother/ParentgroupFather feature
			// and no longer the Certain feature. Solve this!
			// Make the event
			ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
			db.add(app);
			// Make 'ParentgroupMother'/'ParentgroupFather' feature-value pair and link to event
			valuesToAddList.add(ct.createObservedValue(invName, app.getName(), eventDate, null, featureName, parentgroupName, 
					null, parentName));		
			// Make 'Certain' feature-value pair and link to event
			String valueString;
			if (parentNameList.size() == 1) {
				valueString = "1"; // if there's only one parent of this gender, it's certain
			} else {
				valueString = "0"; // ... otherwise, not
			}
			valuesToAddList.add(ct.createObservedValue(invName, app.getName(), eventDate, null, "Certain", parentName, 
					valueString, null));
		}
		// Add everything to DB
		db.add(valuesToAddList);
	}
	
	private void resetUserFields() {
		this.selectedMotherNameList.clear();
		this.selectedFatherNameList.clear();
		this.setStartdate(dateOnlyFormat.format(new Date()));
		this.setRemarks(null);
		if (lineList.size() > 0) {
			this.setLine(lineList.get(0).getName());
		} else {
			this.setLine(null);
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		action = request.getString("__action");
		try {
			if (motherMatrixViewer != null && action.startsWith(motherMatrixViewer.getName())) {
				motherMatrixViewer.setDatabase(db);
				motherMatrixViewer.handleRequest(db, request);
				motherMatrixViewerString = motherMatrixViewer.render();
				this.action = "addParentgroupScreen2"; // return to mother selection screen
				return;
			}
			
			if (fatherMatrixViewer != null && action.startsWith(fatherMatrixViewer.getName())) {
				fatherMatrixViewer.setDatabase(db);
				fatherMatrixViewer.handleRequest(db, request);
				fatherMatrixViewerString = fatherMatrixViewer.render();
				this.action = "addParentgroupScreen3"; // return to father selection screen
				return;
			}
			
			if (pgMatrixViewer != null && action.startsWith(pgMatrixViewer.getName())) {
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewer.handleRequest(db, request);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.action = "init"; // return to start screen
				this.entity = "Parentgroups";
				return;
			}
			
			if (litterMatrixViewer != null && action.startsWith(litterMatrixViewer.getName())) {
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer.handleRequest(db, request);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init"; // return to start screen
				this.entity = "Litters";
				return;
			}
			
			if (action.equals("init")) {
				// do nothing here
			}
			
			if (action.equals("addParentgroupScreen3")) {
				String motherNames = "";
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) motherMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(MOTHERMATRIX + "_selected_" + rowCnt) != null) {
						String motherName = row.getName();
						if (!this.selectedMotherNameList.contains(motherName)) {
							this.selectedMotherNameList.add(motherName);
							motherNames += motherName + " ";
						}
					}
					rowCnt++;
				}
				// Check if at least one mother selected:
				if (this.selectedMotherNameList.size() == 0) {
					action = "addParentgroupScreen2"; // stay in current screen
					throw new Exception("No mother(s) selected");
				}
				this.setSuccess("Mother(s) " + motherNames + "successfully added");
				loadFatherMatrixViewer(db);
				fatherMatrixViewer.setDatabase(db);
				fatherMatrixViewerString = fatherMatrixViewer.render();
			}
			
			if (action.equals("addParentgroupScreen4")) {
				String fatherNames = "";
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) fatherMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(FATHERMATRIX + "_selected_" + rowCnt) != null) {
						String fatherName = row.getName();
						if (!this.selectedFatherNameList.contains(fatherName)) {
							this.selectedFatherNameList.add(fatherName);
							fatherNames += fatherName + " ";
						}
					}
					rowCnt++;
				}
				// Check if at least one father selected:
				if (this.selectedFatherNameList.size() == 0) {
					action = "addParentgroupScreen3"; // stay in current screen
					throw new Exception("No father(s) selected");
				}
				this.setSuccess("Father(s) " + fatherNames + "successfully added");
			}
			
			if (action.equals("addParentgroup")) {
				String newPgName = AddParentgroup(db, request);
				// Reset matrix and add filter on name of newly added PG:
				loadPgMatrixViewer(db);
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewer.getMatrix().getRules().add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, 
						Individual.NAME, Operator.EQUALS, newPgName));
				pgMatrixViewer.reloadMatrix(db, null);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.setAction("init");
				this.resetUserFields();
				fatherMatrixViewer = null;
				motherMatrixViewer = null;
				this.setSuccess("Parentgroup " + newPgName + " successfully added; adding filter to matrix: name = " + newPgName);
			}
			
			if (action.equals("createParentgroup")) {
				loadMotherMatrixViewer(db);
				motherMatrixViewer.setDatabase(db);
				motherMatrixViewerString = motherMatrixViewer.render();
			}
			
			if (action.equals("changeLine")) {
				this.line = request.getString("line");
				this.setSuccess("Breeding line changed to " + this.line);
				// Reset matrix and return to main screen
				loadPgMatrixViewer(db);
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.action = "init";
			}
			
			if (action.equals("switchParentgroups")) {
				this.setEntity("Parentgroups");
				this.action = "init";
			}
			
			if (action.equals("switchLitters")) {
				this.setEntity("Litters");
				this.action = "init";
			}
			
			if (action.equals("deActivate")) {
				List<?> rows = pgMatrixViewer.getSelection(db);
				String pgName;
				try { 
					int row = request.getInt(PGMATRIX + "_selected");
					pgName = ((ObservationElement) rows.get(row)).getName();
				} catch (Exception e) {	
					this.setAction("init");
					throw new Exception("No parentgroup selected");
				}
				ObservedValue activeVal = db.query(ObservedValue.class).
						eq(ObservedValue.TARGET_NAME, pgName).
						eq(ObservedValue.FEATURE_NAME, "Active").
						find().get(0);
				if (activeVal.getValue().equals("Active")) {
					activeVal.setValue("Inactive");
				} else {
					activeVal.setValue("Active");
				}
				db.update(activeVal);
				// Reset matrix and return to main screen
				loadPgMatrixViewer(db);
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.action = "init";
				this.entity = "Parentgroups";
			}
			
			if (action.equals("deActivateLitter")) {
				List<?> rows = litterMatrixViewer.getSelection(db);
				String litterName;
				try { 
					int row = request.getInt(LITTERMATRIX + "_selected");
					litterName = ((ObservationElement) rows.get(row)).getName();
				} catch (Exception e) {	
					this.setAction("init");
					throw new Exception("No litter selected");
				}
				ObservedValue activeVal = db.query(ObservedValue.class).
						eq(ObservedValue.TARGET_NAME, litterName).
						eq(ObservedValue.FEATURE_NAME, "Active").
						find().get(0);
				if (activeVal.getValue().equals("Active")) {
					activeVal.setValue("Inactive");
				} else {
					activeVal.setValue("Active");
				}
				db.update(activeVal);
				// Reset matrix and return to main screen
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init";
				this.entity = "Litters";
			}
			
			if (action.equals("createLitter")) {
				// Get selected parentgroup from PARENTGROUP matrix
				List<?> rows = pgMatrixViewer.getSelection(db);
				try { 
					int row = request.getInt(PGMATRIX + "_selected");
					this.selectedParentgroup = ((ObservationElement) rows.get(row)).getName();
				} catch (Exception e) {	
					this.setAction("init");
					throw new Exception("No parentgroup selected");
				} 
			}
			
			if (action.equals("addLitter")) {
				String newLitterName = ApplyAddLitter(db, request);
				// return to start screen for litters
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer.getMatrix().getRules().add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, 
						Individual.NAME, Operator.EQUALS, newLitterName));
				litterMatrixViewer.reloadMatrix(db, null);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.setAction("init");
				this.resetUserFields();
				this.entity = "Litters";
				this.setSuccess("Litter " + newLitterName + " successfully added; adding filter to matrix: name = " + newLitterName);
			}
			
			if (action.equals("weanLitter")) {
				
			}

			if (action.equals("genotypeLitter")) {
	
			}

			if (action.equals("makeLabels")) {
	
			}
			
		} catch (Exception e) {
			String message = "Something went wrong";
			if (e.getMessage() != null) {
				message += ": " + e.getMessage();
			}
			this.setError(message);
			e.printStackTrace();
		}
	}

	private String AddParentgroup(Database db, Tuple request) throws Exception {
		Date now = new Date();
		String invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName()).get(0);
		// Save start date and remarks that were set in screen 4
		if (request.getString("startdate") != null) {
			setStartdate(request.getString("startdate"));
		}
		if (request.getString("remarks") != null) {
			setRemarks(request.getString("remarks"));
		}
		Date eventDate = dateOnlyFormat.parse(startdate);
		// Make parentgroup
		String groupPrefix = "PG_" + line + "_";
		int groupNr = ct.getHighestNumberForPrefix(groupPrefix) + 1;
		String groupNrPart = "" + groupNr;
		groupNrPart = ct.prependZeros(groupNrPart, 6);
		String groupName = groupPrefix + groupNrPart;
		ct.makePanel(invName, groupName, userName);
		// Make or update name prefix entry
		ct.updatePrefix("parentgroup", groupPrefix, groupNr);
		// Mark group as parent group using a special event
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
				"SetTypeOfGroup", "TypeOfGroup", groupName, "Parentgroup", null));
		// Add parent(s)
		AddParents(db, this.selectedMotherNameList, "SetParentgroupMother", "ParentgroupMother", groupName, eventDate);
		AddParents(db, this.selectedFatherNameList, "SetParentgroupFather", "ParentgroupFather", groupName, eventDate);
		// Set line
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
				"SetLine", "Line", groupName, null, line));
		// Set start date
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
				"SetStartDate", "StartDate", groupName, dbFormat.format(eventDate), null));
		// Set remarks
		if (remarks != null) {
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
					"SetRemark", "Remark", groupName, remarks, null));
		}
		
		//Add Set Active, with (start)time = entrydate and endtime = null
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetActive", "Active", 
				groupName, "Active", null));
		
		return groupName;
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		// Populate lists (do this on every reload so they keep fresh, and do it here
		// because we need the lineList in the init part that comes after)
		try {
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			// Populate line list
			lineList = ct.getAllMarkedPanels("Line", investigationNames);
			// Default selected is first line
			if (line == null && lineList.size() > 0) {
				line = lineList.get(0).getName();
			}
			if (selectedMotherNameList == null) {
				selectedMotherNameList = new ArrayList<String>();
			}
			if (selectedFatherNameList == null) {
				selectedFatherNameList = new ArrayList<String>();
			}
			
		} catch (Exception e) {
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
		// Some init that only needs to be done once after login
		if (userName != this.getLogin().getUserName()) {
			userName = this.getLogin().getUserName();
			ct.makeObservationTargetNameMap(userName, false);
			this.setStartdate(dateOnlyFormat.format(new Date()));
			// Prepare pg matrix
			if (pgMatrixViewer == null) {
				loadPgMatrixViewer(db);
			}
			pgMatrixViewer.setDatabase(db);
			pgMatrixViewerString = pgMatrixViewer.render();
			// Prepare litter matrix
			if (litterMatrixViewer == null) {
				loadLitterMatrixViewer(db);
			}
			litterMatrixViewer.setDatabase(db);
			litterMatrixViewerString = litterMatrixViewer.render();
		}
	}
	
	public String getBirthdate() {
		if (birthdate != null) {
			return birthdate;
		}
		return newDateOnlyFormat.format(new Date());
	}
	
	public int getLitterSize() {
		return litterSize;
	}
	
	public String getLitterRemarks() {
		return litterRemarks;
	}
	
	public String getSelectedParentgroup() {
		if (selectedParentgroup != null) {
			return selectedParentgroup;
		}
		return "Error: no parentgoup selected";
	}
	
	private String ApplyAddLitter(Database db, Tuple request) throws Exception
	{
		Date now = new Date();
		
		setUserFields(request, false);
		Date eventDate = newDateOnlyFormat.parse(birthdate);
		String userName = this.getLogin().getUserName();
		String invName = ct.getOwnUserInvestigationNames(userName).get(0);
		String lineName = ct.getMostRecentValueAsXrefName(selectedParentgroup, "Line");
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		// Make group
		String litterPrefix = "LT_" + lineName + "_";
		int litterNr = ct.getHighestNumberForPrefix(litterPrefix) + 1;
		String litterNrPart = "" + litterNr;
		litterNrPart = ct.prependZeros(litterNrPart, 6);
		String litterName = litterPrefix + litterNrPart;
		ct.makePanel(invName, litterName, userName);
		// Make or update name prefix entry
		ct.updatePrefix("litter", litterPrefix, litterNr);
		// Mark group as a litter
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
				"SetTypeOfGroup", "TypeOfGroup", litterName, "Litter", null));

		// Apply other fields using event
		ProtocolApplication app = ct.createProtocolApplication(invName, "SetLitterSpecs");
		db.add(app);
		String paName = app.getName();
		// Parentgroup
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Parentgroup", 
				litterName, null, selectedParentgroup));
		// Set Line also on Litter
		if (lineName != null) {
			valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Line", 
				litterName, null, lineName));
		}
		// Date of Birth
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "DateOfBirth", 
				litterName, newDateOnlyFormat.format(eventDate), null));
		// Size
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Size", litterName, 
				Integer.toString(litterSize), null));
		// Size approximate (certain)?
		String valueString = "0";
		if (litterSizeApproximate == true) {
			valueString = "1";
		}
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Certain", litterName, 
				valueString, null));
		// Remarks
		if (remarks != null) {
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
					"SetRemark", "Remark", litterName, remarks, null));
		}
		// Try to get Source via Line
		String sourceName = ct.getMostRecentValueAsXrefName(lineName, "Source");
		if (sourceName != null) {
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
					eventDate, null, "SetSource", "Source", litterName, null, sourceName));
		}
		// Active
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Active", litterName, 
				"Active", null));
		// Add everything to DB
		db.add(valuesToAddList);
		
		return litterName;
	}
	
	private void setUserFields(Tuple request, boolean wean) throws Exception {
		if (wean == true) {
			locName = request.getString("location");
			if (locName != null && locName.equals("")) {
				locName = null;
			}
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

	private void setWeanSizeUnknown(int weanSizeUnknown)
	{
		this.weanSizeUnknown = weanSizeUnknown;
	}

	private void setWeanSizeMale(int weanSizeMale)
	{
		this.weanSizeMale = weanSizeMale;
	}

	private void setWeanSizeFemale(int weanSizeFemale)
	{
		this.weanSizeFemale = weanSizeFemale;
	}

	private void setLitterSizeApproximate(boolean litterSizeApproximate)
	{
		this.litterSizeApproximate = litterSizeApproximate;
	}

	private void setLitterSize(int litterSize)
	{
		this.litterSize = litterSize;
	}
}
