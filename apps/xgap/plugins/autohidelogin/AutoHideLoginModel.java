package plugins.autohidelogin;

import org.molgenis.auth.ui.UserLogin;
import org.molgenis.auth.ui.UserLoginModel;

public class AutoHideLoginModel extends UserLoginModel
{
	/**
	 * Model for auto-hiding login tab. Has switchable visibility.
	 */
	private static final long serialVersionUID = -6513451810987465855L;

	public AutoHideLoginModel(UserLogin controller)
	{
		super(controller);
	}
	
	public static Boolean isVisible;

	@Override
	public boolean isVisible()
	{
		return isVisible != null ? isVisible : false;
	}
}
