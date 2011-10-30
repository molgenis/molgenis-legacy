
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.TableBeta;

public class AnotherMatrix extends EasyPluginController<AnotherMatrixModel>
{
	public AnotherMatrix(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AnotherMatrixModel(this)); //the default model
		this.setView(new AnotherMatrixView(getModel())); //<plugin flavor="freemarker"
	}
	

	@Override
	public void reload(Database db) throws Exception
	{	
	}
}