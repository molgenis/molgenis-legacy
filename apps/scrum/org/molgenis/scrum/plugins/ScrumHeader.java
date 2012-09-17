/* Date:        March 25, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.scrum.plugins;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ScrumHeader extends PluginModel<Entity>
{
	private String userLogin = new String();
	
	public ScrumHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_scrum_plugins_ScrumHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/scrum/plugins/ScrumHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
		String action = request.getString("__action");
		if ("doLogout".equals(request.getAction())) {
				try {
					getLogin().logout(db);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}

	@Override
	public void reload(Database db)
	{
		//
	}
	
	@Override
	public boolean isVisible()
	{
		// always visible
		return true;
	}
	
	public String getUserLogin() {
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=ScrumLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			this.userLogin += " | ";
			this.userLogin += "<a href='molgenis.do?__target=ScrumHeader&__action=doLogout'>" + "Exit " + "</a>";		
		} else {
			this.userLogin = "<a href='molgenis.do?__target=main&select=ScrumLogin'>" + "Login" + "</a>";
		}
		return userLogin;
		
	}
}
