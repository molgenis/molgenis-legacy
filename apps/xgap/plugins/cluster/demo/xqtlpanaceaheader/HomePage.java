/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.demo.xqtlpanaceaheader;

import org.molgenis.framework.ui.ScreenController;


public class HomePage extends plugins.cluster.demo.ClusterDemo
{

	private static final long serialVersionUID = -3744678801173089268L;

	public HomePage(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_xqtlpanaceaheader_HomePage";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/xqtlpanaceaheader/HomePage.ftl";
	}


}
