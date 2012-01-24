/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.ibdportal.plugins.importer;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class ImportPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public ImportPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }

	@Override
	public String getViewName()
	{
		return "org_molgenis_ibdportal_plugins_importer_ImportPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/ibdportal/plugins/importer/ImportPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			
			if (action.equals("load")) {
				String filename = request.getString("zip");
				String legacy = request.getString("source");
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.setError("Something went wrong while importing: " + e.getMessage());
		}
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
}
