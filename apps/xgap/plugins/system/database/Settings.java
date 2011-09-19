/*
 * Date: April 8, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.MolgenisOptions;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import plugins.cluster.demo.ClusterDemo;
import regressiontest.cluster.DataLoader;
import app.servlet.UsedMolgenisOptions;

public class Settings extends PluginModel
{
	private String console = "";
	private Map<String, String> info = new LinkedHashMap<String, String>();

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
		try
		{
			this.console = "";

			ArrayList<String> result = new ArrayList<String>();

			if ("loadExampleData".equals(request.getAction()))
			{

				if (db.find(MolgenisUser.class).size() == 2)
				{
					console += "No existing users in db (except admin/anonymous), adding example users..<br>";
					ClusterDemo.addExampleUsers(db);
					console += "Giving extra needed permissions to example users..<br>";
					ClusterDemo.giveExtraNeededPermissions(db);
				}
				else
				{
					console += "BEWARE: Existing users found, skipping adding example users!<br>";
				}

				result = DataLoader.load(db, false);
			}
			else if ("resetDatabase".equals(request.getAction()))
			{
				result.add(ResetXgapDb.reset(this.getDatabase(), true));
			}

			if (result.size() > 0) for (String line : result)
			{
				console += line + "<br>";
			}
			else
				console = null;

		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	@Override
	public void reload(Database db)
	{
		MolgenisOptions mo = new UsedMolgenisOptions();
		String models = "";
		for (String modelXml : mo.getModelDatabase())
		{
			modelXml = modelXml.replace("modules/datamodel/", "");
			models += modelXml + "<br>";
		}

		info.put("db_user", mo.db_user);
		info.put("db_driver", mo.db_driver);
		info.put("db_uri", mo.db_uri);
		info.put("db_mode", mo.db_mode);
		info.put("object_relational_mapping", mo.object_relational_mapping);
		info.put("mapper_implementation", mo.mapper_implementation.toString());
		info.put("model_database", models);
		info.put("model_userinterface", mo.model_userinterface);
		info.put("auth_loginclass", mo.auth_loginclass);
		info.put("decorator_overriders", mo.decorator_overriders);
		info.put("render_decorator", mo.render_decorator);

	}

	public Map<String, String> getInfo()
	{
		return info;
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
