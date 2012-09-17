/* Date:        January 10, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.system;

import java.util.List;

import org.molgenis.animaldb.CustomLabelFeature;
import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class SetCustomLabelFeaturePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -1520685442003195465L;
	
	private CommonService ct = CommonService.getInstance();
	private List<Measurement> measurementList;
	private String currentLabel;
	
	public SetCustomLabelFeaturePlugin(String name, ScreenController<?> parent)
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
		return "org_molgenis_animaldb_plugins_system_SetCustomLabelFeaturePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/system/SetCustomLabelFeaturePlugin.ftl";
	}

	public void setMeasurementList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
	}

	public List<Measurement> getMeasurementList() {
		return measurementList;
	}
	
	private void setCurrentLabel(Database db) {
		int featureId = ct.getCustomNameFeatureId(this.getLogin().getUserName());
		if (featureId == -1) {
			currentLabel = "name";
		}
		try {
			Measurement feature = db.findById(Measurement.class, featureId);
			currentLabel = feature.getName();
		} catch (Exception e) {
			currentLabel = "unknown";
		}
	}
	
	public String getCurrentLabel() {
		return currentLabel;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			int userId = this.getLogin().getUserId();
			
			if (action.equals("setLabel")) {
				int featureId = request.getInt("feature");
				if (featureId != -1) {
					CustomLabelFeature entry;
					// check if user already has a feature set
					List<CustomLabelFeature> featList = db.query(CustomLabelFeature.class).eq(CustomLabelFeature.USERID, userId).find();
					if (featList.size() > 0) {
						entry = featList.get(0);
						entry.setFeatureId(featureId);
						db.update(entry);
					} else {
						entry = new CustomLabelFeature();
						entry.setUserId(userId);
						entry.setFeatureId(featureId);
						db.add(entry);
					}
				} else {
					List<CustomLabelFeature> featList = db.query(CustomLabelFeature.class).eq(CustomLabelFeature.USERID, userId).find();
					if (featList.size() > 0) {
						CustomLabelFeature entry = featList.get(0);
						db.remove(entry);
					}
				}
				
				ct.makeObservationTargetNameMap(this.getLogin().getUserName(), true);
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Label successfully set", true));
			}
		} catch(Exception e) {
			this.getMessages().clear();
			this.getMessages().add(new ScreenMessage("Error - label not set", false));
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		setCurrentLabel(db);
		
		try {
			// Populate feature list
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			this.setMeasurementList(ct.getAllMeasurementsSorted("name", "ASC", investigationNames));
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
