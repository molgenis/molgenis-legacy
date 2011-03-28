/* Date:        October 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.scriptmanager;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ScriptManager extends PluginModel<Entity>
{
	private static final long serialVersionUID = 1502796634143996989L;
	private ScriptManagerModel model = new ScriptManagerModel();

	public ScriptManagerModel getModel()
	{
		return this.model;
	}

	public ScriptManager(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_scriptmanager_ScriptManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/scriptmanager/ScriptManager.ftl";
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// replace example below with yours
		try
		{

			request.getString("__action");

		
			

			this.setMessages();
		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{

		try
		{
			// XgapLocalFileHandler xf = new XgapLocalFileHandler(db);
			// File dir = xf.getStorageFor("rscript");
		//	List<RScript> customScripts = db.find(RScript.class);
		//	this.model.setCustomScripts(customScripts);

		
		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			e.printStackTrace();
		}

	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
}
