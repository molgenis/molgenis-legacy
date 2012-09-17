/* Date:        October 28, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.accessibility;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.news.MolgenisNews;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ManualPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5861419875983400033L;
	List<MolgenisNews> news;
	
	public ManualPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_accessibility_ManualPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/accessibility/ManualPlugin.ftl";
	}

	@Override
	public void reload(Database db)
	{
		//
	}
	
}
