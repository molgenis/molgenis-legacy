/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.location;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class LocationInfoPlugin extends PluginModel
{
	private static final long serialVersionUID = 6637437260773077373L;
	private List<ObservationTarget> locationList;
	private int selectedLocation; // not used at the moment
	private CommonService ct = CommonService.getInstance();
	
	public LocationInfoPlugin(String name, ScreenModel parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
		return "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n" +
 	   "<script src=\"res/scripts/custom/locationinfo.js\" language=\"javascript\"></script>\n" +
 	   "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
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
		return "plugins_location_LocationInfoPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/location/LocationInfoPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}

	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		// Populate location list
		try {
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
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		
		if(this.getLogin().isAuthenticated()){
			return true;
		}else
		{
			return false;
		}
	}
}
