/* Date:        February 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.header;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class Col7a1Header extends PluginModel<Entity>
{
	private static final long serialVersionUID = 5933871906981851063L;

	public Col7a1Header(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_mutation_ui_header_Col7a1Header";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/mutation/ui/header/Col7a1Header.ftl";
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String cssFormat = "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\"%s\">\n";
		String jsFormat = "<script src=\"%s\" type=\"text/javascript\" language=\"javascript\"></script>";
		String headers = "";
		
//		cp res/css/colors.css generated-res/css
//		cp res/css/data.css generated-res/css
//		cp res/css/main.css generated-res/css
//		cp res/css/menu.css generated-res/css
//		cp res/scripts/all.js generated-res/scripts
//		cp res/img/*.jpg generated-res/img
		
		headers += String.format(cssFormat, "res/css/col7a1/colors.css");
		headers += String.format(cssFormat, "res/css/col7a1/data.css");
		//headers += String.format(cssFormat, "res/css/main.css");
		//headers += String.format(cssFormat, "res/css/menu.css");
		headers += String.format(jsFormat, "res/scripts/all.js");
		
		return headers;
	}
	
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//nothing to do here
	}

	@Override
	public void reload(Database db)
	{
		//nothing to do here
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
