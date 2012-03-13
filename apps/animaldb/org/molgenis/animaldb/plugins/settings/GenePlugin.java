/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.settings;

import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class GenePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6637437260773077373L;
	private String action = "init";
	private List<String> geneList;
	private CommonService ct = CommonService.getInstance();
	
	public GenePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	public List<String> getGeneList() {
		return geneList;
	}
	public void setGeneList(List<String> geneList) {
		this.geneList = geneList;
	}

	@Override
	public String getViewName()
	{
		return "plugins_settings_GenePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/settings/GenePlugin.ftl";
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
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
			
			if (action.equals("addGene")) {
				String geneName = request.getString("name");
				if (geneName != null) {
					ct.makeCategory(geneName, geneName, "GeneModification");
				}
				this.setSuccess("Gene modification successfully added");
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
		
		// Populate gene list
		try {
			this.geneList = ct.getAllCodesForFeatureAsStrings("GeneModification");
			
		} catch (Exception e) {
			String message = "Something went wrong while loading gene list";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.setError(message);
			e.printStackTrace();
		}
	}
	
}
