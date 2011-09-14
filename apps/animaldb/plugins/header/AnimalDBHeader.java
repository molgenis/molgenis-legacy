/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class AnimalDBHeader extends PluginModel<Entity>
{

	private static final long serialVersionUID = 4701628601897969977L;
	private String userLogin = new String();

	public AnimalDBHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_header_AnimalDBHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/AnimalDBHeader.ftl";
	}

	@Override
	public void reload(Database db)
	{
		this.setUserLogin();
	}
	
	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {

				getLogin().logout(db);
		}
	}
	
	public void setUserLogin() {
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + " | Logout " + "</a>";
		} else {
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
		}	
	}

	public String getUserLogin() {
		
		return userLogin;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	public boolean isLoggedIn() {
		return this.getLogin().isAuthenticated();
	}
}
