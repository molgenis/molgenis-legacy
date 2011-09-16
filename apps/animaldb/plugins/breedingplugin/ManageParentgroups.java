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

public class ManageParentgroups extends PluginModel<Entity>
{
	private static final long serialVersionUID = 203412348106990472L;
	private List<Integer> motherIdList = new ArrayList<Integer>();
	private List<Integer> motherIdListFromLine = new ArrayList<Integer>();
	private List<Integer> selectedMotherIdList = new ArrayList<Integer>();
	private List<Integer> fatherIdList = new ArrayList<Integer>();
	private List<Integer> fatherIdListFromLine = new ArrayList<Integer>();
	private List<Integer> selectedFatherIdList = new ArrayList<Integer>();
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
//	private String groupName = null;
	private String startdate = null;
	private List<ObservationTarget> lineList;
	private int line = 0;
	private String remarks = null;
	private boolean firstTime = true;
	
	public ManageParentgroups(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}
	
	// Mother related methods:
	public List<Integer> getMotherIdList() {
		return motherIdList;
	}

	public void setMotherList(List<Integer> motherIdList) {
		this.motherIdList = motherIdList;
	}

	public List<Integer> getSelectedMotherIdList() {
		return selectedMotherIdList;
	}

	public void setSelectedMotherList(List<Integer> selectedMotherIdList) {
		this.selectedMotherIdList = selectedMotherIdList;
	}
	
	// Father related methods:
	public List<Integer> getFatherIdList() {
		return fatherIdList;
	}

	public void setFatherIdList(List<Integer> fatherIdList) {
		this.fatherIdList = fatherIdList;
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
	
//	public String getGroupName() {
//		return groupName;
//	}
//	public void setGroupName(String groupName) {
//		this.groupName = groupName;
//	}
	
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

	public List<Integer> getMotherIdListFromLine() {
		return motherIdListFromLine;
	}

	public void setMotherIdListFromLine(List<Integer> motherIdListFromLine) {
		this.motherIdListFromLine = motherIdListFromLine;
	}

	public List<Integer> getFatherIdListFromLine() {
		return fatherIdListFromLine;
	}

	public void setFatherIdListFromLine(List<Integer> fatherIdListFromLine) {
		this.fatherIdListFromLine = fatherIdListFromLine;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
		try {
			Date now = new Date();
			
			int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
			
			String action = request.getString("__action");
			
			if (action.equals("addParentgroup")) {
				// Check if at least one mother and father selected:
				if (this.selectedMotherIdList.size() == 0 || this.selectedFatherIdList.size() == 0) {
					throw new Exception("No mother(s) and/or no father(s) selected");
				}
				setUserFields(request);
				Date eventDate = dateOnlyFormat.parse(startdate);	
				// Make group
				String groupName = "PG_" + ct.getObservationTargetLabel(line) + "_";
				groupName += (ct.getHighestNumberForNameBase(groupName) + 1);
				int groupId = ct.makePanel(invid, groupName, this.getLogin().getUserId());
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
				// Set remarks
				if (remarks != null) {
					protocolId = ct.getProtocolId("SetRemark");
					measurementId = ct.getMeasurementId("Remark");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, groupId, remarks, 0));
				}
				
				// Success: empty selected lists and show success message
				this.resetUserFields();
				this.getMessages().add(new ScreenMessage("Parent group " + groupName + " successfully added", true));
			}
			
			if (action.equals("addIndMother")) {
				setUserFields(request);
				int motherId = request.getInt("ind_mother");
				if (!this.selectedMotherIdList.contains(motherId)) {
					this.selectedMotherIdList.add(motherId);
				}
			}
			if (action.equals("addIndMotherFromLine")) {
				setUserFields(request);
				int motherId = request.getInt("ind_mother_line");
				if (!this.selectedMotherIdList.contains(motherId)) {
					this.selectedMotherIdList.add(motherId);
				}
			}
			
			if (action.equals("remIndMother")) {
				setUserFields(request);
				int motherId = request.getInt("mother");
				this.selectedMotherIdList.remove(this.selectedMotherIdList.indexOf(motherId));
			}
			
			if (action.equals("addIndFather")) {
				setUserFields(request);
				int fatherId = request.getInt("ind_father");
				if (!this.selectedFatherIdList.contains(fatherId)) {
					this.selectedFatherIdList.add(fatherId);
				}
			}
			if (action.equals("addIndFatherFromLine")) {
				setUserFields(request);
				int fatherId = request.getInt("ind_father_line");
				if (!this.selectedFatherIdList.contains(fatherId)) {
					this.selectedFatherIdList.add(fatherId);
				}
			}
			
			if (action.equals("remIndFather")) {
				setUserFields(request);
				int fatherId = request.getInt("father");
				this.selectedFatherIdList.remove(this.selectedFatherIdList.indexOf(fatherId));
			}
			
			if (action.equals("updateLine")) {
				setUserFields(request);
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
	public List<Integer> populateParentList(Database db, String sexName, List<Integer> investigationIds) 
		throws DatabaseException, ParseException {
		
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Sex"));
		q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, sexName));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
		q.addRules(new QueryRule(Operator.SORTASC, ObservedValue.TARGET_NAME));
		List<ObservedValue> valueList = q.find();
		List<Integer> parentIdList = new ArrayList<Integer>();
		for (ObservedValue value : valueList) {
			// TODO: filter out the dead ones!
			parentIdList.add(value.getTarget_Id());
		}
		return parentIdList;
	}
	
	public List<Integer> restrictParentListByLine(Database db, List<Integer> allParentIds, List<Integer> investigationIds) throws DatabaseException, ParseException {
		
		if (line == 0) {
			return allParentIds;
		}
		
		List<Integer> returnList = new ArrayList<Integer>();
		
		String lineName = ct.getObservationTargetLabel(line);
		
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Line"));
		q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, lineName));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
		q.addRules(new QueryRule(Operator.SORTASC, ObservedValue.TARGET_NAME));
		List<ObservedValue> valueList = q.find();
		for (ObservedValue value : valueList) {
			if (allParentIds.contains(value.getTarget_Id())) {
				returnList.add(value.getTarget_Id());
			}
		}
		
		return returnList;
	}

	@Override
	public void reload(Database db)
	{
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
		
		if (firstTime == true) {
			firstTime = false;
			ct.setDatabase(db);
			ct.makeObservationTargetNameMap(this.getLogin().getUserId(), false);
			this.resetUserFields();
			
			try {
				lineList = ct.getAllMarkedPanels("Line", investigationIds);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Default selected is first line
			if (lineList.size() > 0) {
				line = lineList.get(0).getId();
			}
		}
		
		try {
			// Populate line list
			lineList = ct.getAllMarkedPanels("Line", investigationIds);
			// Populate mother list
			motherIdList = populateParentList(db, "Female", investigationIds);
			motherIdListFromLine = restrictParentListByLine(db, motherIdList, investigationIds);
			// Populate father list
			fatherIdList = populateParentList(db, "Male", investigationIds);
			fatherIdListFromLine = restrictParentListByLine(db, fatherIdList, investigationIds);
			
		} catch (Exception e) {
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
