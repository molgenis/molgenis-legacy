/* Date:        August 2, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugin.topmenu;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class DocumentationScreen extends PluginModel<Entity>
{

	private static final long serialVersionUID = 3897753142133478576L;

	public DocumentationScreen(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_topmenu_DocumentationScreen";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugin/topmenu/DocumentationScreen.ftl";
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
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
