/* Date:        March 17, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.arvc.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;

public class Header extends org.molgenis.mutation.ui.header.Header
{
	private static final long serialVersionUID = 6224612078995632056L;

	public Header(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setLogo("generated-res/arvc/img/umcg_logo.gif");
		this.getModel().setTitle("Department of Genetics - Cardiogenetics Research Group<br>ARVD/C Genetic Variants Database");
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

		headers += String.format(cssFormat, "generated-res/displaytag/css/displaytag.css");
		//headers += String.format(cssFormat, "res/displaytag/css/screen.css");
		//headers += String.format(cssFormat, "res/displaytag/css/site.css");
		headers += String.format(cssFormat, "generated-res/arvc/css/colors.css");
		headers += String.format(cssFormat, "generated-res/arvc/css/data.css");
		//headers += String.format(cssFormat, "res/css/main.css");
		//headers += String.format(cssFormat, "res/css/menu.css");
		headers += String.format(jsFormat, "generated-res/arvc/res/scripts/all.js");
		
		return headers;
	}

	@Override
	public void reload(Database db)
	{
		//nothing to do here
	}
}
