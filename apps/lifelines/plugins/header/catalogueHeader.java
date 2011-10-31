/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;




public class catalogueHeader extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4576352827620517694L;
	/**
	 * 
	 */
	private String userLogin = new String();
	
	public catalogueHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"bbmri/css/bbmri_colors.css\">" + "\n"  ;
//	}
	
	
	@Override
	public String getViewName()
	{
		return "plugins_header_catalogueHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/catalogueHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {
				getLogin().logout(db);
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
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=SimpleUserLogin&__action=doLogout'>" + " | Logout " + "</a>";
		} else {
			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Login" + "</a>";
		}
		
	}

	public String getUserLogin() {
		
		return userLogin;
	}
}
