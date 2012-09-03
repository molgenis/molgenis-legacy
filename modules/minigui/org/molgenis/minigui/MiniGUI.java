/* Date:        April 22, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.minigui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class MiniGUI<E extends Entity> extends PluginModel<E>
{
	/**
	 * Experimental minimal GUI for MOLGENIS
	 */
	private static final long serialVersionUID = 5163895580240779726L;

	public MiniGUI(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private MiniGUIModel model = new MiniGUIModel();

	public MiniGUIModel getVO()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_minigui_MiniGUI";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/minigui/MiniGUI.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");

			try
			{
				// if (action.equals("goto"))
				// {
				// String screen = request.getString("__selectScreen");
				// System.out.println("SCREEN SELECT: " + screen);
				// this.getParent().setSelected(screen);
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		this.model.setUiTree(this.getParent().getChildren());
		// System.out.println("selected: " +
		// this.getParent().getSelected().getName());
		// for (ScreenController sc : this.getParent().getAllChildren())
		// {
		// if (sc instanceof FormController)
		// {
		// System.out.println("FormController: " + sc.getName());
		// }
		// else if (sc instanceof MenuController)
		// {
		// System.out.println("MenuController: " + sc.getName());
		// }
		// else if (sc instanceof PluginModel)
		// {
		// System.out.println("PluginModel: " + sc.getName());
		// }
		// else if (sc instanceof EasyPluginController)
		// {
		// System.out.println("EasyPluginController: " + sc.getName());
		// }
		// else
		// {
		// System.out.println("UNKNOWN: " + sc.getName());
		// }
		// }
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String style = "\n";
		style += "<link rel=\"stylesheet\" type=\"text/css\" href=\"jquery/superfish/superfish.css\" media=\"screen\">\n";
		style += "<script type=\"text/javascript\" src=\"jquery/superfish/superfish.js\"></script>\n";
		style += "<script type=\"text/javascript\">\n";
		style += "	jQuery(function(){\n";
		style += "	jQuery('ul.sf-menu').superfish();\n";
		style += "});\n";
		style += "</script>\n";

		// search box
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/qtlfinder.css\">\n";
		style += "<script type=\"text/javascript\" src=\"etc/js/clear-default-text.js\"></script>\n";

		// plain style
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"jquery/superfish/colors.css\">\n";
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"jquery/superfish/main_override.css\">";
		return style;
	}
}