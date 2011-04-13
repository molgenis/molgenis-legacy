package org.molgenis.auth.vo;

import org.molgenis.framework.ui.html.Container;

public class UserLoginVO
{
	private Container authenticationForm= new Container();
	private Container userAreaForm = new Container();
	private Container registrationForm = new Container();
	private Container forgotForm= new Container();

	public Container getAuthenticationForm() {
		return authenticationForm;
	}
	public void setAuthenticationForm(Container authenticationForm) {
		this.authenticationForm = authenticationForm;
	}
	public Container getUserAreaForm() {
		return userAreaForm;
	}
	public void setUserAreaForm(Container userAreaForm) {
		this.userAreaForm = userAreaForm;
	}
	public Container getRegistrationForm() {
		return registrationForm;
	}
	public void setRegistrationForm(Container registrationForm) {
		this.registrationForm = registrationForm;
	}
	public void setForgotForm(Container forgotForm) {
		this.forgotForm = forgotForm;
	}
	public Container getForgotForm() {
		return forgotForm;
	}
}
