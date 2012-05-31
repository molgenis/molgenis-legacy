/* Date:        December 3, 2008
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generate.screen.PluginScreenJavaTemplateGen 3.0.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.auth.ui;


import org.molgenis.auth.ui.form.RegistrationForm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

//import commonservice.CommonService;

/**
 * This screen shows a login box, or if someone is already logged in, the user
 * information and a logout button.
 */
public class UserLogin extends SimpleUserLogin
{
	private static final long serialVersionUID = -3084964114182861171L;

	public UserLogin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new UserLoginModel(this));
	}


	public ScreenView getView()
	{
		return new FreemarkerView("UserLogin.ftl", getModel());
	}
	
	public void Register(Database db, Tuple request)
	{
		this.getModel().setAction("Register");
	}
	
	@Override
	public void reload(Database db)
	{
		super.reload(db);
		
		this.populateRegistrationForm();
	}

	private void populateRegistrationForm()
	{
		((UserLoginModel) this.getModel()).setRegistrationForm(new RegistrationForm());
	}
}