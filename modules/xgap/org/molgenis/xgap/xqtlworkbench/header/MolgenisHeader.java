/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.xqtlworkbench.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.autohidelogin.AutoHideLogin;

/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class MolgenisHeader extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6155556950170399575L;

	public MolgenisHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_xgap_xqtlworkbench_header_MolgenisHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/xgap/xqtlworkbench/header/MolgenisHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {

			getLogin().logout(db);
			
			//only for AutoHideLoginSwitchService, but harmless otherwise
			this.getApplicationController().sessionVariables.put(AutoHideLogin.AUTOHIDE_LOGIN, false);
		}
	}

	@Override
	public void reload(Database db)
	{
		setUserLogin();
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	private String userLogin;
	
	public String getUserLogin() {
		
		return userLogin;
	}
	
	public void setUserLogin()
	{
		//if the AutoHideLoginSwitchService is enabled, use this style of redirecting in the output URLs
		if(this.getApplicationController().getMolgenisContext().getUsedOptions().services.contains("services.AutoHideLoginService@/autohideloginswitch"))
		{
			setUserLoginAutoHideService();
		}
		// else just use the regular one
		// AS A FALLBACK ONLY: this header is specific for WormQTL
		// and we want to use the auto-hide for this app
		// but just keeping the code around doesn't hurt
		else
		{
			setUserLoginRegular();
		}
	}
	
//  previous:
//	public void setUserLogin() {
//		if(this.getLogin() instanceof DatabaseLogin)
//		{
//			if (this.getLogin().isAuthenticated()) {
//				this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getUserName() + "</a>";
//				this.userLogin += " | ";
//				this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
//			} else {
//				this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
//			}
//		}else{
//			//simplelogin
//			this.userLogin = "";
//		}
//	}
	
	public void setUserLoginAutoHideService() {
		//TODO: check for this.getLogin() instanceof DatabaseLogin ?
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='autohideloginswitch'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getUserName() + "</a>";
			this.userLogin += " | ";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
		} else {
			this.userLogin = "<a href='autohideloginswitch'>" + "Login" + "</a>";
		}	
	}
	
	public void setUserLoginRegular() {
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getUserName() + "</a>";
			this.userLogin += " | ";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
		} else {
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
		}	
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/colors.css\">" + "\n" +
			   "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/xgap_main_override.css\">" ;
	}
}
