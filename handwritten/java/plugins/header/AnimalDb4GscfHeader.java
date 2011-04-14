/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.header;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class AnimalDb4GscfHeader extends PluginModel<Entity>
{

	private static final long serialVersionUID = 4701628601897969977L;

	public AnimalDb4GscfHeader(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_header_AnimalDb4GscfHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/AnimalDb4GscfHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//static
	}

	@Override
	public void reload(Database db)
	{
		//static
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	public boolean isLoggedIn() {
		return this.getLogin().isAuthenticated();
	}
}
