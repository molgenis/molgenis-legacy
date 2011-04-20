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
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ManageParentgroups extends PluginModel<Entity>
{
	private static final long serialVersionUID = 203412348106990472L;
	private List<Integer> motherIdList = new ArrayList<Integer>();
	private List<Integer> selectedMotherIdList = new ArrayList<Integer>();
	private List<Integer> fatherIdList = new ArrayList<Integer>();
	private List<Integer> selectedFatherIdList = new ArrayList<Integer>();
	private CommonService ct = CommonService.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	private String groupName = "";
	private String datetime = "";
	private List<ObservationTarget> sourceList;
	private int source;
	
	public ManageParentgroups(String name, ScreenModel<Entity> parent)
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
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getSource() {
		return source;
	}

	public List<ObservationTarget> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<ObservationTarget> sourceList) {
		this.sourceList = sourceList;
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
		int eventCounter = 0;
		int invid = ct.getInvestigationId("AnimalDB");
		int protocolId = ct.getProtocolId(protocolName);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		for (int parentId : parentIdList) {
			eventCounter++;
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
	
	private void setUserFields(Tuple request) throws DatabaseException, ParseException {
		setDatetime(request.getString("datetime"));
		setGroupName(request.getString("groupname"));
		String sourceIdString = request.getString("source");
		sourceIdString = sourceIdString.replace(".", "");
		sourceIdString = sourceIdString.replace(",", "");
		setSource(Integer.parseInt(sourceIdString));
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			
			int invid = ct.getInvestigationId("AnimalDB");
			
			String action = request.getString("__action");
			
			if (action.equals("addParentgroup")) {
				// Check if at least one mother and father selected:
				if (this.selectedMotherIdList.size() == 0 || this.selectedFatherIdList.size() == 0) {
					throw new Exception("No mother(s) and/or no father(s) selected");
				}
				setUserFields(request);
				Date eventDate = sdf.parse(datetime);	
				// Make group
				int groupid = ct.makePanel(invid, groupName);
				// Mark group as parent group using a special event
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupid, "Parentgroup", 0));
				// Add parent(s)
				AddParents(db, this.selectedMotherIdList, "SetMother", "eventmother", "Mother", "valuemother", 
						"valuemothercertain", groupid, eventDate);
				AddParents(db, this.selectedFatherIdList, "SetFather", "eventfather", "Father", "valuefather", 
						"valuefathercertain", groupid, eventDate);
				// Set source
				protocolId = ct.getProtocolId("SetSource");
				measurementId = ct.getMeasurementId("Source");
				db.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
						protocolId, measurementId, groupid, null, source));
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Parentgroup succesfully added", true));
			}
			
			if (action.equals("addIndMother")) {
				setUserFields(request);
				String tmpString = request.getString("ind_mother").replace(".", "");
				tmpString = tmpString.replace(",", "");
				int motherId = Integer.parseInt(tmpString); 
				if (!this.selectedMotherIdList.contains(motherId)) {
					this.selectedMotherIdList.add(motherId);
				}
			}
			
			if (action.equals("remIndMother")) {
				setUserFields(request);
				String tmpString = request.getString("mother").replace(".", "");
				tmpString = tmpString.replace(",", "");
				int motherId = Integer.parseInt(tmpString);
				this.selectedMotherIdList.remove(motherId);
			}
			
			if (action.equals("addIndFather")) {
				setUserFields(request);
				String tmpString = request.getString("ind_father").replace(".", "");
				tmpString = tmpString.replace(",", "");
				int fatherId = Integer.parseInt(tmpString);
				if (!this.selectedFatherIdList.contains(fatherId)) {
					this.selectedFatherIdList.add(fatherId);
				}
			}
			
			if (action.equals("remIndFather")) {
				setUserFields(request);
				String tmpString = request.getString("father").replace(".", "");
				tmpString = tmpString.replace(",", "");
				int fatherId = Integer.parseInt(tmpString);
				this.selectedFatherIdList.remove(fatherId);
			}
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}
	
	public List<Integer> populateParentList(Database db, String sex) throws DatabaseException, ParseException {
		int featid = ct.getMeasurementId("Sex");
		int sexid = ct.getObservationTargetId(sex);
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
		q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, sexid));
		List<ObservedValue> valueList = q.find();
		List<Integer> parentIdList = new ArrayList<Integer>();
		for (ObservedValue value : valueList) {
			parentIdList.add(value.getTarget_Id());
		}
		return parentIdList;
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		try {
			// Populate mother list
			motherIdList = populateParentList(db, "Female");
			// Populate father list
			fatherIdList = populateParentList(db, "Male");
			// Populate source list
			// All source types pertaining to "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<Panel> tmpSourceList = ct.getAllMarkedPanels("Source");
			for (Panel tmpSource : tmpSourceList) {
				int featid = ct.getMeasurementId("SourceType");
				Query<ObservedValue> sourceTypeQuery = db.query(ObservedValue.class);
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpSource.getId()));
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				List<ObservedValue> sourceTypeValueList = sourceTypeQuery.find();
				if (sourceTypeValueList.size() > 0)
				{
					String sourcetype = sourceTypeValueList.get(0).getValue();
					if (sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid")) {
						sourceList.add(tmpSource);
					}
				}
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
	
	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}
}
