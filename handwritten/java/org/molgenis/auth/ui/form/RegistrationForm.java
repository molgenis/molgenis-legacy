package org.molgenis.auth.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.PasswordInput;
import org.molgenis.framework.ui.html.TextLineInput;

public class RegistrationForm extends Container
{
	public RegistrationForm()
	{
		TextLineInput usernameInput    = new TextLineInput("username");
		usernameInput.setLabel("Username");
		this.add(usernameInput);
		PasswordInput passwordInput    = new PasswordInput("password");
		this.add(passwordInput);
		PasswordInput passwordInput2   = new PasswordInput("password2");
		this.add(passwordInput2);
		TextLineInput emailInput       = new TextLineInput("email");
		this.add(emailInput);
		TextLineInput titleInput       = new TextLineInput("title");
		this.add(titleInput);
		TextLineInput lastnameInput    = new TextLineInput("lastname");
		this.add(lastnameInput);
		TextLineInput firstnameInput   = new TextLineInput("firstname");
		this.add(firstnameInput);
		TextLineInput positionInput    = new TextLineInput("position");
		this.add(positionInput);
		TextLineInput instituteInput   = new TextLineInput("institute");
		this.add(instituteInput);
		TextLineInput departmentInput  = new TextLineInput("department");
		this.add(departmentInput);
		TextLineInput cityInput        = new TextLineInput("city");
		this.add(cityInput);
		TextLineInput countryInput     = new TextLineInput("country");
		this.add(countryInput);
		ActionInput addUserInput       = new ActionInput("AddUser", "Add");
		addUserInput.setTooltip("Add");
		this.add(addUserInput);
		ActionInput cancelInput        = new ActionInput("Cancel");
		cancelInput.setTooltip("Cancel");
		this.add(cancelInput);
	}
}