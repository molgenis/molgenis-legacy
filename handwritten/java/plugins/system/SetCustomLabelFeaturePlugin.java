/* Date:        January 10, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class SetCustomLabelFeaturePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -1520685442003195465L;
	
	private CommonService ct = CommonService.getInstance();
	private List<Measurement> measurementList;
	
	public SetCustomLabelFeaturePlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n";
    }

	@Override
	public String getViewName()
	{
		return "plugins_system_SetCustomLabelFeaturePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/SetCustomLabelFeaturePlugin.ftl";
	}

	public void setMeasurementList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
	}

	public List<Measurement> getMeasurementList() {
		return measurementList;
	}
	
	public String getCurrentLabel() {
		int featureId = ct.getCustomNameFeatureId();
		if (featureId == -1) {
			return "name";
		}
		try {
			Measurement feature = ct.getMeasurementById(featureId);
			return feature.getName();
		} catch (Exception e) {
			return "unknown";
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			
			if (action.equals("setLabel")) {
				String featureIdString = request.getString("feature");
				if (featureIdString != null && !featureIdString.equals("") && !featureIdString.equals("0")) {
					featureIdString.replace(".", "");
					featureIdString.replace(",", "");
					ct.setCustomNameFeatureId(Integer.parseInt(featureIdString));
					
					this.getMessages().clear();
					this.getMessages().add(new ScreenMessage("Label successfully set", true));
				}
			}
		} catch(Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage("Error - label not set", false));
			}
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		try {
			// Populate feature list
			this.setMeasurementList(ct.getAllMeasurementsSorted("name", "ASC"));
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
		//you can use this to hide this plugin, e.g. based on user rights.
		try {
			if (this.getLogin().isAuthenticated()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// Authenticated but no user name, so probably debug user
			return true;
		}
	}
}
