/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.breedingplugin;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.ObservationElementMatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ManageParentgroups extends PluginModel<Entity>
{
	private static final long serialVersionUID = 203412348106990472L;
	private List<Integer> selectedMotherIdList = null;
	private List<Integer> selectedFatherIdList = null;
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	private String startdate = null;
	private List<ObservationTarget> lineList;
	private int line = -1;
	private String remarks = null;
	private boolean firstTime = true;
	private List<ObservationTarget> pgList;
	ObservationElementMatrixViewer motherMatrixViewer = null;
	ObservationElementMatrixViewer fatherMatrixViewer = null;
	private static String MOTHERMATRIX = "mothermatrix";
	private static String FATHERMATRIX = "fathermatrix";
	private String action = "init";
	
	public ManageParentgroups(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	public List<Integer> getSelectedMotherIdList() {
		return selectedMotherIdList;
	}
	public void setSelectedMotherList(List<Integer> selectedMotherIdList) {
		this.selectedMotherIdList = selectedMotherIdList;
	}

	public List<Integer> getSelectedFatherIdList() {
		return selectedFatherIdList;
	}
	public void setSelectedFatherList(List<Integer> selectedFatherIdList) {
		this.selectedFatherIdList = selectedFatherIdList;
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

	public void setLine(int line) {
		this.line = line;
	}
	public int getLine() {
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

	public List<ObservationTarget> getPgList() {
		return pgList;
	}
	public void setPgList(List<ObservationTarget> pgList) {
		this.pgList = pgList;
	}
	
	public String getPgStartDate(int pgId) {
		try {
			return ct.getMostRecentValueAsString(pgId, ct.getMeasurementId("StartDate"));
		} catch (Exception e) {
			e.printStackTrace();
			return "Error when retrieving start date";
		}
	}
	
	public String getPgRemarks(int pgId) throws DatabaseException {
		List<String> remarksList = ct.getRemarks(pgId);
		String returnString = "";
		for (String remark : remarksList) {
			returnString += (remark + "<br>");
		}
		if (returnString.length() > 0) {
			returnString = returnString.substring(0, returnString.length() - 4);
		}
		return returnString;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String renderMotherMatrixViewer() {
		if (motherMatrixViewer != null) {
			return motherMatrixViewer.render();
		} else {
			return "No viewer available, matrix for selecting mother(s) cannot be rendered.";
		}
	}
	
	public String renderFatherMatrixViewer() {
		if (fatherMatrixViewer != null) {
			return fatherMatrixViewer.render();
		} else {
			return "No viewer available, matrix for selecting father(s) cannot be rendered.";
		}
	}

	@Override
	public String getViewName()
	{
		return "plugins_breedingplugin_ManageParentgroups";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/breedingplugin/ManageParentgroups.ftl";
	}
	
	private void AddParents(Database db, List<Integer> parentIdList, String protocolName, String eventName, 
			String featureName, String valueName, String valueCertainName, int parentgroupid, Date tmpDate) 
			throws DatabaseException, ParseException, IOException {
		
		int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
		int protocolId = ct.getProtocolId(protocolName);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		for (int parentId : parentIdList) {
			// Find the 'SetMother'/'SetFather' event type
			// TODO: SetMother/SetFather are now plain event types with only the Mother/Father feature
			// and no longer the Certain feature. Solve this!
			// Make the event
			ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
			db.add(app);
			int eventid = app.getId();
			// Make 'Mother'/'Father' feature-value pair and link to event
			int measurementId = ct.getMeasurementId(featureName);
			valuesToAddList.add(ct.createObservedValue(invid, eventid, tmpDate, null, measurementId, parentId, 
					null, parentgroupid));		
			// Make 'Certain' feature-value pair and link to event
			String valueString;
			if (parentIdList.size() == 1) {
				valueString = "1"; // if there's only one parent of this gender, it's certain
			} else {
				valueString = "0"; // ... otherwise, not
			}
			measurementId = ct.getMeasurementId("Certain");
			valuesToAddList.add(ct.createObservedValue(invid, eventid, tmpDate, null, measurementId, parentId, 
					valueString, 0));
		}
		// Add everything to DB
		db.add(valuesToAddList);
	}
	
	private void setUserFields(Tuple request) {
		if (request.getString("startdate") != null) {
			setStartdate(request.getString("startdate"));
		}
//		if (request.getString("groupname") != null) {
//			setGroupName(request.getString("groupname"));
//		}
		if (request.getInt("line") != null) {
			setLine(request.getInt("line"));
		}
		if (request.getString("remarks") != null) {
			setRemarks(request.getString("remarks"));
		}
	}
	
	private void resetUserFields() {
		this.selectedMotherIdList.clear();
		this.selectedFatherIdList.clear();
		this.setStartdate(dateOnlyFormat.format(new Date()));
//		this.setGroupName(null);
		this.setRemarks(null);
		if (lineList.size() > 0) {
			this.setLine(lineList.get(0).getId());
		} else {
			this.setLine(0);
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{	
		action = request.getString("__action");
		try {
			Date now = new Date();
			int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
			
			if (action.startsWith(motherMatrixViewer.getName())) {
				motherMatrixViewer.handleRequest(db, request);
				this.setAction("showAddParentgroup");
			}
			if (action.startsWith(fatherMatrixViewer.getName())) {
				fatherMatrixViewer.handleRequest(db, request);
				this.setAction("showAddParentgroup");
			}
			
			if (action.equals("init")) {
				// do nothing
			}
			
			if (action.equals("showAddParentgroup")) {
				// do nothing
			}
			
			if (action.equals("addParentgroup")) {
				// Check if at least one mother and father selected:
				if (this.selectedMotherIdList.size() == 0 || this.selectedFatherIdList.size() == 0) {
					throw new Exception("No mother(s) and/or no father(s) selected");
				}
				setUserFields(request);
				Date eventDate = dateOnlyFormat.parse(startdate);
				int userId = this.getLogin().getUserId();
				// Make group
				String groupPrefix = "PG_" + ct.getObservationTargetLabel(line) + "_";
				int groupNr = ct.getHighestNumberForPrefix(groupPrefix) + 1;
				String groupNrPart = "" + groupNr;
				groupNrPart = ct.prependZeros(groupNrPart, 6);
				int groupId = ct.makePanel(invid, groupPrefix + groupNrPart, userId);
				// Make or update name prefix entry
				ct.updatePrefix(userId, "parentgroup", groupPrefix, groupNr);
				// Mark group as parent group using a special event
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupId, "Parentgroup", 0));
				// Add parent(s)
				AddParents(db, this.selectedMotherIdList, "SetMother", "eventmother", "Mother", "valuemother", 
						"valuemothercertain", groupId, eventDate);
				AddParents(db, this.selectedFatherIdList, "SetFather", "eventfather", "Father", "valuefather", 
						"valuefathercertain", groupId, eventDate);
				// Set line
				protocolId = ct.getProtocolId("SetLine");
				measurementId = ct.getMeasurementId("Line");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupId, null, line));
				// Set start date
				protocolId = ct.getProtocolId("SetStartDate");
				measurementId = ct.getMeasurementId("StartDate");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupId, dbFormat.format(eventDate), 0));
				// Set remarks
				if (remarks != null) {
					protocolId = ct.getProtocolId("SetRemark");
					measurementId = ct.getMeasurementId("Remark");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, groupId, remarks, 0));
				}
				
				// Success: empty selected lists and show success message
				this.setAction("init");
				this.resetUserFields();
				this.getMessages().add(new ScreenMessage("Parent group " + (groupPrefix + groupNrPart) + " successfully added", true));
			}
			
			if (action.equals("remIndMother")) {
				setUserFields(request);
				int motherId = request.getInt("mother");
				this.selectedMotherIdList.remove(this.selectedMotherIdList.indexOf(motherId));
				this.setAction("showAddParentgroup");
			}
			
			if (action.equals("remIndFather")) {
				setUserFields(request);
				int fatherId = request.getInt("father");
				this.selectedFatherIdList.remove(this.selectedFatherIdList.indexOf(fatherId));
				this.setAction("showAddParentgroup");
			}
			
			if (action.equals("updateLine")) {
				setUserFields(request);
				this.setAction("showAddParentgroup");
				// reload() will take care of updating matrix viewers
			}
			
			if (action.equals("addMothersFromMatrix")) {
				setUserFields(request);
				List<? extends ObservationElement> rows = motherMatrixViewer.getSelection();
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(MOTHERMATRIX + "_selected_" + rowCnt) != null) {
						int motherId = row.getId();
						if (!this.selectedMotherIdList.contains(motherId)) {
							this.selectedMotherIdList.add(motherId);
						}
					}
					rowCnt++;
				}
				this.setAction("showAddParentgroup");
			}
			if (action.equals("addFathersFromMatrix")) {
				setUserFields(request);
				List<? extends ObservationElement> rows = fatherMatrixViewer.getSelection();
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(FATHERMATRIX + "_selected_" + rowCnt) != null) {
						int fatherId = row.getId();
						if (!this.selectedFatherIdList.contains(fatherId)) {
							this.selectedFatherIdList.add(fatherId);
						}
					}
					rowCnt++;
				}
				this.setAction("showAddParentgroup");
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
		
		// Populate lists (do this on every reload so they keep fresh)
		try {
			// Populate existing PG list
			pgList =  ct.getAllMarkedPanels("Parentgroup", investigationIds);
			// Populate line list
			lineList = ct.getAllMarkedPanels("Line", investigationIds);
			// Default selected is first line
			if (line == -1 && lineList.size() > 0) {
				line = lineList.get(0).getId();
			}
			if (selectedMotherIdList == null) {
				selectedMotherIdList = new ArrayList<Integer>();
			}
			if (selectedFatherIdList == null) {
				selectedFatherIdList = new ArrayList<Integer>();
			}
			
		} catch (Exception e) {
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
		// Some init that only needs to be done once
		if (firstTime == true) {
			firstTime = false;
			ct.setDatabase(db);
			ct.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
			this.setStartdate(dateOnlyFormat.format(new Date()));
			
			reloadMatrixViewers();
		}
	}
	
	private void reloadMatrixViewers() {
		try {
			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Species");
			measurementsToShow.add("Sex");
			measurementsToShow.add("Active");
			measurementsToShow.add("Line");
			// Mother matrix viewer
			List<MatrixQueryRule> motherFilterRules = new ArrayList<MatrixQueryRule>();
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
					Operator.IN, measurementsToShow));
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Sex"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, "Female"));
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != -1) {
				motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS,
						ct.getObservationTargetLabel(line)));
				// Setting filter on the RELATION field with value = line would be more efficient,
				// but gives a very un-userfriendly toString value when shown in the UI
			}
			motherMatrixViewer = new ObservationElementMatrixViewer(this, MOTHERMATRIX, 
					new SliceablePhenoMatrix(this.getDatabase(), Individual.class, Measurement.class), 
					false, motherFilterRules);
			// Father matrix viewer
			List<MatrixQueryRule> fatherFilterRules = new ArrayList<MatrixQueryRule>();
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
					Operator.IN, measurementsToShow));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Sex"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, "Male"));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != -1) {
				fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						ct.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS,
						ct.getObservationTargetLabel(line)));
				// Setting filter on the RELATION field with value = line would be more efficient,
				// but gives a very un-userfriendly toString value when shown in the UI
			}
			fatherMatrixViewer = new ObservationElementMatrixViewer(this, FATHERMATRIX, 
					new SliceablePhenoMatrix(this.getDatabase(), Individual.class, Measurement.class), 
					false, fatherFilterRules);
		} catch (Exception e) {
			String message = "Something went wrong while loading matrix viewers";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
