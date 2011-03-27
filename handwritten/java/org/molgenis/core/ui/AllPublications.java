/* Date:        January 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.core.ui;

import java.util.List;

import org.molgenis.core.service.PublicationService;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Tuple;

public class AllPublications extends PluginModel
{
	private List<PublicationVO> publications;

	public AllPublications(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_core_ui_AllPublications";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/core/ui/AllPublications.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			PublicationService service = PublicationService.getInstance(db);
			this.publications          = service.getAll();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public List<PublicationVO> getPublicationVOs()
	{
		return this.publications;
	}
}
