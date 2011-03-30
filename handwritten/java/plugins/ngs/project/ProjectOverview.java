/** 
 * 
 * @author Jessica Lundberg
 * @date 16-10-2010
 * 
 * This class is the model portion for creating a project overview for NGS LIMS. A project overview allows a user
 * to view a project's details. Details currently include: budget, progress, lab technician, customer, etc (but can
 * change without notice). 
 */

package plugins.ngs.project;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ProjectOverview extends PluginModel<Entity>
{
	private static final long serialVersionUID = 5185429713136257187L;

	public ProjectOverview(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_ngs_project_ProjectOverview";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/ngs/project/ProjectOverview.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
	}

	@Override
	public void reload(Database db)
	{
		
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
}