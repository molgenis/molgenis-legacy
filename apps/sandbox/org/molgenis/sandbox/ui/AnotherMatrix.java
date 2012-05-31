
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;

public class AnotherMatrix extends EasyPluginController<AnotherMatrixModel>
{
	public AnotherMatrix(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new AnotherMatrixModel(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new AnotherMatrixView(getModel());
	}

	@Override
	public void reload(Database db) throws Exception
	{	
	}
}