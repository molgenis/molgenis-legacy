/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.emptydb;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

public class EmptyDbPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1511261755841429645L;

	public EmptyDbPlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_emptydb_EmptyDbPlugin ";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/emptydb/EmptyDbPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");

			if (action.equals("emptyDatabase")) {
				new emptyDatabase((JDBCDatabase)db, false);
			}
		}catch(Exception e){
			logger.error(e);
		}
	}

	@Override
	public void reload(Database db)
	{	
		
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
}
