/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.lifelinesresearchportal.plugins.header;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class MolgenisHeader extends PluginModel<Entity>
{
	private static final long serialVersionUID = -8210616687815863944L;

	public MolgenisHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_xwbllheader_MolgenisHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/lifelinesresearchportal/plugins/header/MolgenisHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// static
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

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/xwbllcolors.css\">" + "\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/main_override.css\">";
	}

	private String userLogin;

	public String getUserLogin()
	{

		return userLogin;
	}

	public void setUserLogin()
	{
		// if the AutoHideLoginSwitchService is enabled, use this style of
		// redirecting in the output URLs
		if (this.getApplicationController().getMolgenisContext().getUsedOptions().services
				.contains("services.AutoHideLoginService@/autohideloginswitch"))
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

	public void setUserLoginAutoHideService()
	{
		// TODO: check for this.getLogin() instanceof DatabaseLogin ?
		if (this.getLogin().isAuthenticated())
		{
			this.userLogin = "<a href='autohideloginswitch'>" + "Logged in as: "
					+ ((DatabaseLogin) this.getLogin()).getUserName() + "</a>";
			this.userLogin += " | ";
			this.userLogin += "<a href='molgenis.do?__target=UserLogin&__action=Logout'>" + "Logout " + "</a>";
		}
		else
		{
			this.userLogin = "<a href='autohideloginswitch'>" + "Login" + "</a>";
		}
	}

	public void setUserLoginRegular()
	{
		if (this.getLogin().isAuthenticated())
		{
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Logged in as: "
					+ ((DatabaseLogin) this.getLogin()).getUserName() + "</a>";
			this.userLogin += " | ";
			this.userLogin += "<a href='molgenis.do?__target=UserLogin&__action=Logout'>" + "Logout " + "</a>";
		}
		else
		{
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
		}
	}
}
