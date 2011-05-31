package org.molgenis.framework.ui;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;

public class EasyPluginModel extends SimpleScreenModel
{
	private static final long serialVersionUID = 4866399456367824712L;

	public EasyPluginModel(ScreenController<?> controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isVisible()
	{
		Login login = this.getController().getApplicationController().getLogin();
		if (login.isAuthenticated())
		{
			try
			{
				if (login.canRead(this.getController()))
				{
					return true;
				}
			}
			catch (DatabaseException e)
			{
				e.printStackTrace();
			}
		}

		return false;
	}
}
