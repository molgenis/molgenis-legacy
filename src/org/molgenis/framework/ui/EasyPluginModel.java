package org.molgenis.framework.ui;

import org.molgenis.framework.db.DatabaseException;

public class EasyPluginModel extends SimpleScreenModel
{
	private static final long serialVersionUID = 4866399456367824712L;

	public EasyPluginModel(ScreenController<?> controller)
	{
		super(controller);
	}

	@Override
	public boolean isVisible()
	{
		try
		{
			return this.getController().getApplicationController().getLogin().canRead(this.getController());
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
