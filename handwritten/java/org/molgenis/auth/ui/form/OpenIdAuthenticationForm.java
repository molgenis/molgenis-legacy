package org.molgenis.auth.ui.form;

import org.molgenis.framework.ui.html.ActionInput;

public class OpenIdAuthenticationForm extends DatabaseAuthenticationForm
{
	public OpenIdAuthenticationForm()
	{
		super();
		ActionInput googleInput     = new ActionInput("google");
		googleInput.setIcon("res/img/google.png");
		this.add(googleInput);
		ActionInput yahooInput      = new ActionInput("yahoo");
		yahooInput.setIcon("res/img/yahoo.png");
		this.add(yahooInput);
	}
}
