/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.help.general;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

public class GeneralHelpPlugin extends PluginModel {

	public GeneralHelpPlugin(String name, ScreenModel parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "plugins_help_general_GeneralHelpPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/help/general/GeneralHelpPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		System.out.println("REQUEST: \n" + request.toString());
		if (request.getString("__action") != null) {
			
		}
	}



	@Override
	public void reload(Database db) {

	}

	@Override
	public boolean isVisible() {
		return true;
	}
}
