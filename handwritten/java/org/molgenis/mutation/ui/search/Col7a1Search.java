/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;

public class Col7a1Search extends SearchPlugin
{
	private static final long serialVersionUID = 1162846311691838788L;

	public Col7a1Search(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
		this.GENENAME = "COL7A1";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_mutation_ui_search_SearchPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/mutation/ui/search/SearchPlugin.ftl";
	}
}
