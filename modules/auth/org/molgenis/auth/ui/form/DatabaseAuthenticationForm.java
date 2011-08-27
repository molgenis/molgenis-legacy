package org.molgenis.auth.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.PasswordInput;
import org.molgenis.framework.ui.html.StringInput;

public class DatabaseAuthenticationForm extends Container
{

	private static final long serialVersionUID = 798533925465868923L;

	public DatabaseAuthenticationForm()
	{
		StringInput usernameInput   = new StringInput("username");
		usernameInput.setNillable(false);
		this.add(usernameInput);
		PasswordInput passwordInput   = new PasswordInput("password");
		passwordInput.setNillable(false);
		this.add(passwordInput);
		ActionInput loginInput        = new ActionInput("Login");
		loginInput.setTooltip("Login");
		this.add(loginInput);
		ActionInput logoutInput       = new ActionInput("Logout");
		logoutInput.setTooltip("Logout");
		this.add(logoutInput);
	}
}
