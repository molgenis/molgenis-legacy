/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.demo;

import java.util.List;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;



public class ClusterDemo extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5307970595544892186L;

	public ClusterDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_ClusterDemo";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/ClusterDemo.ftl";
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
		
		
		try
		{
			//fails when there is no table 'MolgenisUser', or no MolgenisUser named 'admin'
			//assume database has not been setup yet
			db.find(MolgenisUser.class, new QueryRule("name", Operator.EQUALS, "admin")).get(0);
			
		}
		catch(Exception e)
		{
			//setup database and report back
			String report = ResetXgapDb.reset(this.getDatabase(), true);
			if(report.endsWith("SUCCESS")){
				this.setMessages(new ScreenMessage("Database creation success!", true));
			}else{
				this.setMessages(new ScreenMessage("Database creation fail! Review report: "+report, false));
			}
			
		}
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
