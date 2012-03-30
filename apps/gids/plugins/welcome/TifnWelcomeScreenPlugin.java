/* Date:        October 28, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.welcome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;

import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class TifnWelcomeScreenPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5861419875983400033L;
	
	
	public TifnWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_accessibility_AnimalDBWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/accessibility/AnimalDBWelcomeScreenPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{

	}

	@Override
	public void reload(Database db)
	{
		// Entry point when logging in, so good place to (re)set the ObservationTarget label map
	
	}
	
	
}
