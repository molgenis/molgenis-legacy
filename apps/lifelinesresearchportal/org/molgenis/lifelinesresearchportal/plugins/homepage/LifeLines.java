package org.molgenis.lifelinesresearchportal.plugins.homepage;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class LifeLines extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5324788471624447907L;
	private Investigation inv = null;
	
	public LifeLines(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_homepage_LifeLines";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/lifelinesresearchportal/plugins/homepage/LifeLines.ftl";
	}
	
	@Override
	public void reload(Database db) {
		try
		{
			List<Investigation> invList = db.find(Investigation.class);
			if (invList.size() > 0) {
				inv = invList.get(0);
			}
		}
		catch (DatabaseException e)
		{
			// do nothing
		}
	}
	
	public String getStudyInfo() {
		if (inv != null) {
			return inv.getName() + ": " + inv.getDescription();
		} else {
			return null;
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws HandleRequestDelegationException, Exception
	{
		// TODO Auto-generated method stub
		
	}

}
