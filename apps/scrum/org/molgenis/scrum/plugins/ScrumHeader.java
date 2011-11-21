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
		this.setUserLogin();

	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public void setUserLogin() {
		//System.out.println(this.getLogin().isAuthenticated());
		if (this.getLogin().isAuthenticated()) {

			//this.userLogin = "<a href='http://vm7.target.rug.nl/bbmri_gcc/molgenis.do?__target=main&select=UserLogin'>" + "Welcome " +  ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			//this.userLogin += "<a href='http://vm7.target.rug.nl/bbmri_gcc/molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + " | Exit " + "</a>";		

			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + " | Exit " + "</a>";		

		} else {
			//this.userLogin = "<a href='http://vm7.target.rug.nl/bbmri_gcc/molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
		}
		
	}

	public String getUserLogin() {
		
		return userLogin;
	}
}
