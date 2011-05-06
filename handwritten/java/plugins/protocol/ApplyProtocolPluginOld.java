/* Date:        January 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

@Deprecated
public class ApplyProtocolPluginOld extends PluginModel<Entity> {

	private static final long serialVersionUID = 783393812835980181L;
	private List<Protocol> protocolList;
	private List<Integer> targetIdList;
	private List<Integer> selectedTargetIdList = new ArrayList<Integer>();
	private List<ObservationTarget> groupList;
	private CommonService ct = CommonService.getInstance();
	
	public ApplyProtocolPluginOld(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
        	   "<script src=\"res/scripts/custom/addevent.js\" language=\"javascript\"></script>\n" +
        	   "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	// Event type related methods:
	public List<Protocol> getProtocolList() {
		return protocolList;
	}
	public void setProtocolList(List<Protocol> protocolList) {
		this.protocolList = protocolList;
	}
	
	// Target related methods:
	public List<Integer> getTargetIdList() {
		return targetIdList;
	}
	public void setTargetIdList(List<Integer> targetList) {
		this.targetIdList = targetList;
	}
	public List<Integer> getSelectedTargetIdList() {
		return selectedTargetIdList;
	}
	public void setSelectedTargetIdList(List<Integer> selectedTargetIdList) {
		this.selectedTargetIdList = selectedTargetIdList;
	}
	
	public String getTargetName(Integer id) {
		try {
			return ct.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}
	
	// Group related methods:
	public List<ObservationTarget> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<ObservationTarget> groupList) {
		this.groupList = groupList;
	}

	@Override
	public String getViewName()
	{
		return "plugins_protocol_ApplyProtocolPluginOld";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/protocol/ApplyProtocolPluginOld.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
//		String klazzName = "Animal";
//		Class<Entity> k = Class.forName(klazzName + ".class");
//		db.find(k);
//		
		try {
			String action = request.getString("__action");
			
			if (action.equals("addEvent")) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				int eventCounter = 0;
				int animalCounter = 0;
				int protocolId = request.getInt("eventtype");
				boolean sepval = false;
				if (request.getBool("sepvaltoggle") != null) {
					sepval = true;
				}
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				for (Integer targetid : selectedTargetIdList) {
					eventCounter++;
					
					String animalSelectInterject = "0_"; 
					if (sepval) {
						animalSelectInterject = animalCounter + "_";
					}
					
					// First, make the event
					int invid = ct.getInvestigationId("AnimalDB");
					ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
					db.add(app);
					int eventid = app.getId();
					// Then, add the values
					int valueNr = 0;
					while (request.getString("value" + animalSelectInterject + valueNr) != null) {
						String startDatetimeString = request.getString("startdatetime" + animalSelectInterject + valueNr);
						Date startDatetime = sdf.parse(startDatetimeString);
						Date endDatetime = null;
						if (request.getString("enddatetime" + animalSelectInterject + valueNr) != null) {
							String endDatetimeString = request.getString("enddatetime" + animalSelectInterject + valueNr);
							endDatetime = sdf.parse(endDatetimeString);
						}
						String valueString = null;
						int targetRef = 0;
						int featureid = request.getInt("feature" + animalSelectInterject + valueNr);
						Measurement feature = null;
						if (featureid > 0) {
							feature = ct.getMeasurementById(featureid);
						}
						if (feature != null) {
							if (feature.getDataType().equals("xref")) {
								targetRef = request.getInt("value" + animalSelectInterject + valueNr);
							} else {
								valueString = request.getString("value" + animalSelectInterject + valueNr);
							}
						} else {		
							valueString = request.getString("value" + animalSelectInterject + valueNr);
						}
						valuesToAddList.add(ct.createObservedValue(invid, eventid, startDatetime, endDatetime, 
								feature.getId(), targetid, valueString, targetRef));
						valueNr++;
					}
					animalCounter++;
				} // end while loop through animals
				
				// Add everything to DB
				db.add(valuesToAddList);
				
				this.getMessages().clear();
				this.setMessages(new ScreenMessage("Event(s) succesfully applied", true));
			}
			
			if (action.equals("addIndividual")) {
				List<?> objList = request.getList("ind_animal");
                Iterator<?> objListIt = objList.iterator();
                while (objListIt.hasNext()) {
                	String tmpString = objListIt.next().toString();
                	int animalId = Integer.parseInt(tmpString);
    				if (!this.selectedTargetIdList.contains(animalId)) {
    					this.selectedTargetIdList.add(animalId);
                    }
                }
                this.getMessages().clear();
				this.setMessages(new ScreenMessage("Individual succesfully added", true));
			}
			
			if (action.equals("addGroup")) {
				int groupId = request.getInt("group_animal");
				String featname = "Group";
				// Is it a labeled targetgroup?
				int featId = ct.getMeasurementId("TypeOfGroup");
				Query<ObservedValue> q = db.query(ObservedValue.class);
                q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featId));
                q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, groupId));
                List<ObservedValue> valueList = q.find();
                if (valueList.size() > 0) {
                	// Take first label as feature (TODO: allow for more?)
                	// Ignore "Selection", as that isn't set using a SetSelection protocol
                	if (!valueList.get(0).getValue().equals("Selection")) {
                		featname = valueList.get(0).getValue();
                	}
                }
				
				featId = ct.getMeasurementId(featname);
                q = db.query(ObservedValue.class);
                q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featId));
                q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, groupId));
                valueList = q.find();
                
                Iterator<ObservedValue> iterator = valueList.iterator();
                while (iterator.hasNext()) {
                	int animalId = iterator.next().getTarget_Id();
	                if (!this.selectedTargetIdList.contains(animalId)) {
	                	this.selectedTargetIdList.add(animalId);
	                }
                }
                this.getMessages().clear();
				this.setMessages(new ScreenMessage("Group succesfully added", true));
			}
 
			if (action.equals("remIndividual")) {
               
                List<?> objList = request.getList("animal");
                List<Integer> idList = new ArrayList<Integer>();
                Iterator<?> objListIt = objList.iterator();
                while (objListIt.hasNext()) {
                	String tmpString = objListIt.next().toString();
                	idList.add(Integer.parseInt(tmpString));
                }
               
            	this.selectedTargetIdList.removeAll(idList);
            	this.getMessages().clear();
				this.setMessages(new ScreenMessage("Individual succesfully removed", true));
			}
			
			if (action.equals("remAll")) {
                this.selectedTargetIdList.clear();
                this.getMessages().clear();
				this.setMessages(new ScreenMessage("All targets succesfully removed", true));
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		try {
			// Populate protocol list
			this.setProtocolList(ct.getAllProtocolsSorted("name", "ASC"));
			// Populate target ID list
			this.setTargetIdList(ct.getAllObservationTargetIds(null, false));
			// Populate animal group list
			List<Integer> groupIdList = ct.getAllObservationTargetIds("Group", false);
			this.setGroupList(ct.getObservationTargets(groupIdList));
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
