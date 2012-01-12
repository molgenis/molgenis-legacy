/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.lifelinesresearchportal.plugins.loader;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class LifeLinesLoaderPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public LifeLinesLoaderPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
//	public String getCustomHtmlHeaders()
//    {
//        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
//    }

	@Override
	public String getViewName()
	{
		return "org_molgenis_lifelinesresearchportal_plugins_loader_LifeLinesLoaderPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/lifelinesresearchportal/plugins/loader/LifeLinesLoaderPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
		String action = request.getAction();
		
		if( action.equals("load") )
		{
			String zipFileName = request.getString("zip");
			int study = request.getInt("study");
			try {
				ImportMapper.importData(zipFileName, study);
				this.setSuccess("LifeLines Publish data import complete!");
			} catch(Exception e) {
				e.printStackTrace();
				this.setError("LifeLines Publish data import failed");
			}
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
