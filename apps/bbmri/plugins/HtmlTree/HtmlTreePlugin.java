/* Date:        March 23, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.HtmlTree;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class HtmlTreePlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6143910771849972946L;

	public HtmlTreePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_HtmlTree_HtmlTreePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/HtmlTree/HtmlTreePlugin.ftl";
	}


	public String getCustomHtmlHeaders()
	{
		
		return  "<script type=\"text/javascript\" src=\"res/HTMLTree/mktree.js\"></script>\n"
				+ "<link rel=\"stylesheet\" href=\"res/HTMLTree/mktree.css\" type=\"text/css\"> \n" 
				+ "<script src=\"res/jquery-plugins/Treeview/jquery.treeview.js\" language=\"javascript\"></script>"
				+";\n";
	}
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//			//start database transaction
//			db.beginTx();
//
//			//get the "__action" parameter from the UI
//			String action = request.getAction();
//		
//			if( action.equals("do_add") )
//			{
//				Experiment e = new Experiment();
//				e.set(request);
//				db.add(e);
//			}
//
//			//commit all database actions above
//			db.commitTx();
//
//		} catch(Exception e)
//		{
//			db.rollbackTx();
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
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}
}
