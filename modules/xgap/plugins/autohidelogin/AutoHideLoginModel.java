/* Date:        December 3, 2008
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generate.screen.PluginScreenJavaTemplateGen 3.0.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.autohidelogin;


import org.molgenis.auth.ui.UserLoginModel;

//import commonservice.CommonService;

/**
 * This screen shows a login box, or if someone is already logged in, the user
 * information and a logout button.
 */
public class AutoHideLoginModel extends UserLoginModel
{

	private static final long serialVersionUID = 6708187240283301564L;
	
	AutoHideLogin controller;
	public AutoHideLoginModel(AutoHideLogin controller)
	{
		super(controller);
		this.controller = controller;
	}
	
	@Override
	public boolean isVisible()
	{
		return controller.ac.sessionVariables.get(AutoHideLogin.AUTOHIDE_LOGIN) != null ? ((Boolean)controller.ac.sessionVariables.get(AutoHideLogin.AUTOHIDE_LOGIN)) : false;
	}
	
}