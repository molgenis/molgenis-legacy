/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import org.molgenis.framework.ui.ScreenController;

public class Chd7Search extends SearchPlugin
{
	private static final long serialVersionUID = 4159412082076885902L;

	public Chd7Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.GENENAME = "CHD7";
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
