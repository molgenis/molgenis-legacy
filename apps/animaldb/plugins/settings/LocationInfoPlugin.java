/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class LocationInfoPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6637437260773077373L;
	private List<ObservationTarget> locationList;
	private String action = "init";
	private Map<Integer, String> superLocMap;
	private CommonService ct = CommonService.getInstance();
	
	public LocationInfoPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	public List<ObservationTarget> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<ObservationTarget> locationList) {
		this.locationList = locationList;
	}

	@Override
	public String getViewName()
	{
		return "plugins_settings_LocationInfoPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/settings/LocationInfoPlugin.ftl";
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}
	
	public String getSuperLocName(int locId) {
		return superLocMap.get(locId);
	}
	
	private String getSuperLoc(Database db, int locId) throws DatabaseException {
		
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Location"));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, locId));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null)); // only active one!
		if (q.find().size() == 1) {
			ObservedValue currentValue = q.find().get(0);
			if (currentValue.getRelation_Id() != null) {
				return currentValue.getRelation_Name();
			}
		}
		return "";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		try {
			action = request.getString("__action");
			
			if (action.equals("Add")) {
				//
			}
			
			if (action.equals("Import")) {
				//
			}
			
			if (action.equals("Manage")) {
				//
			}
			
			if (action.equals("addLocation")) {
				
				// Get values from form + current datetime
				int slocid = request.getInt("superlocation");
				String name = request.getString("name");
				Date now = new Date();
				
				// Make and add location
				int invid = ct.getOwnUserInvestigationId(this.getLogin().getUserId());
				int locid = ct.makeLocation(invid, name, this.getLogin().getUserId());
				if (slocid > 0) {
					int protocolId = ct.getProtocolId("SetSublocationOf");
					int measurementId = ct.getMeasurementId("Location");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, locid, null, slocid));
				}
				this.setSuccess("Location successfully added");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setError(e.getMessage());
			}
		}
	}

	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		// Populate location list and superloc map
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
			List<Integer> locationIdList = ct.getAllObservationTargetIds("Location", false, investigationIds);
			if (locationIdList.size() > 0) {
				this.locationList = ct.getObservationTargets(locationIdList);
			} else {
				this.locationList = new ArrayList<ObservationTarget>();
			}
			
			superLocMap = new HashMap<Integer, String>();
			for (ObservationTarget loc : locationList) {
				superLocMap.put(loc.getId(), this.getSuperLoc(db, loc.getId()));
			}
			
		} catch (Exception e) {
			String message = "Something went wrong while loading location list";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.setError(message);
			e.printStackTrace();
		}
	}
	
}
