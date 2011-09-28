/* Date:        January 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.core.ui;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.core.service.PublicationService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.IntegratedPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class AllPublications extends IntegratedPluginController<AllPublicationsModel>
{
	private static final long serialVersionUID = -5252927756111530842L;

	public AllPublications(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AllPublicationsModel(this));
		this.setView(new FreemarkerView("AllPublications.ftl", getModel()));
		this.getModel().setPublicationPager("res/mutation/publicationPager.jsp");
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String result = super.getCustomHtmlHeaders();
		
		if (CollectionUtils.isEmpty(this.getModel().getPublicationVOList()))
				result += "<meta http-equiv=\"refresh\" content=\"0; URL=molgenis.do?select=Publications&__target=Publications&__action=show\">";

		return result;
	}

	public void show(Database db, Tuple request)
	{
		try
		{
			PublicationService publicationService = PublicationService.getInstance(db);
			this.getModel().setPublicationVOList(publicationService.getAll());
			((HttpServletRequestTuple) request).getRequest().setAttribute("publicationVOList", this.getModel().getPublicationVOList());
			this.getModel().setRawOutput(this.include(request, this.getModel().getPublicationPager()));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// Dirty hack: Actually the PublicationPager should be included here.
		// But we need the HttpServletRequest which is not passed to reload().
		// So the customHtmlHeaders will issue a request with action==show
		// which does the work for us.
	}
}
