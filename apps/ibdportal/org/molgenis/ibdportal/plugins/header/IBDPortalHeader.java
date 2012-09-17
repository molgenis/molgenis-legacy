/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.ibdportal.plugins.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class IBDPortalHeader extends PluginModel<Entity>
{
	private static final long serialVersionUID = 4701628601897969977L;
	
	public IBDPortalHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return  "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.rounded.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.roundedBr.css\">";		
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_ibdportal_plugins_header_IBDPortalHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/ibdportal/plugins/header/IBDPortalHeader.ftl";
	}

	@Override
	public void reload(Database db)
	{
		//
	}
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			if ("doLogout".equals(request.getAction())) {
				getLogin().logout(db);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			//this.setError(e.getMessage());
		}
	}

	public String getFullUserName() {
		
		if (this.getLogin().isAuthenticated()) {
			return ((DatabaseLogin)this.getLogin()).getFullUserName();
		}
		return null;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
}
