package plugins.cluster.demo.homepage;

import org.molgenis.framework.ui.ScreenController;

public class Tifn extends plugins.cluster.demo.ClusterDemo
{
	private static final long serialVersionUID = -5324788471624447907L;

	public Tifn(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_homepage_Tifn";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/homepage/Tifn.ftl";
	}

}
