/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class AddLocationPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -3311258163342757278L;
	private List<ObservationTarget> locationList;
	private int selectedLocation; // not used at the moment
	private CommonService ct = CommonService.getInstance();
	
	public AddLocationPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	// Location related methods:
	public List<ObservationTarget> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<ObservationTarget> locationList) {
		this.locationList = locationList;
	}
	public int getSelectedLocation() {
		return selectedLocation;
	}
	public void setSelectedLocation(int selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	@Override
	public String getViewName()
	{
		return "plugins_location_AddLocationPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/location/AddLocationPlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			if (action.equals("addLocation")) {
				
				// Get values from form + current datetime
				int slocid = request.getInt("superlocation");
				String name = request.getString("name");
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				
				// Make and add location
				int invid = ct.getUserInvestigationId(this.getLogin().getUserId());
				int locid = ct.makeLocation(invid, name, this.getLogin().getUserId());
				if (slocid > 0) {
					int protocolId = ct.getProtocolId("SetSublocationOf");
					int measurementId = ct.getMeasurementId("Location");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, locid, null, slocid));
					// TODO: end previous subloc event
				}
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Location successfully added", true));
				this.reload(db);
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
			// Populate location list
			List<Integer> locationIdList = ct.getAllObservationTargetIds("Location", false);
			if (locationIdList.size() > 0) {
				this.locationList = ct.getObservationTargets(locationIdList);
			} else {
				this.locationList = new ArrayList<ObservationTarget>();
			}
		} catch (Exception e) {
			this.getMessages().clear();
			String message = "Something went wrong while loading location list";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
}
