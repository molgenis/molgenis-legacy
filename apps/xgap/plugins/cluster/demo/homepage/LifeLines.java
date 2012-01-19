package plugins.cluster.demo.homepage;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;

public class LifeLines extends plugins.cluster.demo.ClusterDemo
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
		return "plugins/cluster/demo/homepage/LifeLines.ftl";
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

}
