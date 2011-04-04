/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;




public class BbmriHeader extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5516712543692105018L;
	private String userLogin = new String();
	
	public BbmriHeader(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_header_BbmriHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/BbmriHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if ("doLogout".equals(request.getAction())) {
				getLogin().logout();
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

	public void Logout() {
		if (this.getLogin().isAuthenticated()) {
			getLogin().logout();
		}	
	}
	
	public void setUserLogin() {
		if (this.getLogin().isAuthenticated()) {
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
