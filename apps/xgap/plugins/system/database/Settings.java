/*
 * Date: April 8, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.database;

import java.util.ArrayList;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import regressiontest.cluster.DataLoader;

public class Settings extends PluginModel
{
	String console = "";
	
	public Settings(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_system_database_Settings";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/database/Settings.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.console = "";
		
		ArrayList<String> result = new ArrayList<String>();
		
		if ("loadExampleData".equals(request.getAction()))
		{
			result = DataLoader.load(db, false);
		} else if("resetDatabase".equals(request.getAction()))
		{
			result.add(ResetXgapDb.reset(this.getDatabase(), true));
		}
		
		if(result.size() > 0) for(String line: result)
		{
			console += line + "<br/>";
		}
		else console = null;
		
		
		

		// replace example below with yours
		// try
		// {
		// //start database transaction
		// db.beginTx();
		//
		// //get the "__action" parameter from the UI
		// String action = request.getAction();
		//		
		// if( action.equals("do_add") )
		// {
		// Experiment e = new Experiment();
		// e.set(request);
		// db.add(e);
		// }
		//
		// //commit all database actions above
		// db.commitTx();
		//
		// } catch(Exception e)
		// {
		// db.rollbackTx();
		// //e.g. show a message in your form
		// }
	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//			
		// //do something
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

	public String getConsole()
	{
		return console;
	}

	public void setConsole(String console)
	{
		this.console = console;
	}
	
}
