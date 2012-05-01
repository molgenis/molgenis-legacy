
package org.molgenis.ngs.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

public class NgsHeader extends EasyPluginController<NgsHeader>
{
	public NgsHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("NgsHeaderView.ftl", this);
	}
	
	@Override
	public void reload(Database db) throws Exception
	{	
	}
}