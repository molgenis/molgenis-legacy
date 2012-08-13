package org.molgenis.hemodb.plugins;

import matrix.general.DataMatrixHandler;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;

import plugins.matrix.manager.MatrixManager;

@SuppressWarnings("serial")
public class MatrixManagerHemodb extends MatrixManager
{

	public MatrixManagerHemodb(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		if (this.dmh == null)
		{
			dmh = new DataMatrixHandler(db);
		}

		if (this.model.getSelectedFilterDiv() == null)
		{
			this.model.setSelectedFilterDiv("filter2");
		}

		if (this.getMyModel().getBrowser() != null) this.createHeaders();
	}

}
