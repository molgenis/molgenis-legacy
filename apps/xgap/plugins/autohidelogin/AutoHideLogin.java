package plugins.autohidelogin;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

public class AutoHideLogin extends org.molgenis.auth.ui.UserLogin
{

	public static String AUTOHIDE_LOGIN = "autohide_login_switch_boolean";
	
	/**
	 * Special version of UserLogin. After every login or logout,
	 * the plugin tab hides itself. Must work together with
	 * AutoHideLoginSwitchService and a header that uses the services,
	 * e.g. apps/wormqtl/org/molgenis/wormqtl/header/MolgenisHeader.java
	 */
	private static final long serialVersionUID = -4799149937057039542L;

	public ApplicationController ac;
	
	public AutoHideLogin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new AutoHideLoginModel(this));
		this.ac = this.getApplicationController();
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("org/molgenis/auth/ui/UserLogin.ftl", getModel());
	}
	
	@Override
	public void Login(Database db, Tuple request) throws Exception
	{
		super.Login(db, request);
		ac.sessionVariables.put(AUTOHIDE_LOGIN, false);
	}
	
	@Override
	public void Logout(Database db, Tuple request) throws Exception
	{
		super.Logout(db, request);
		ac.sessionVariables.put(AUTOHIDE_LOGIN, false);
	}
}
