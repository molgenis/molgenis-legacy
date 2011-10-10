/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.demo.xqtliccheader;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;



/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class MolgenisHeader extends PluginModel<Entity>
{
	private static final long serialVersionUID = -8210616687815863944L;

	public MolgenisHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_xqtliccheader_MolgenisHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/xqtliccheader/MolgenisHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//static
	}

	@Override
	public void reload(Database db)
	{
		//static
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/xqtl_icc_colors.css\">";
	}
}
