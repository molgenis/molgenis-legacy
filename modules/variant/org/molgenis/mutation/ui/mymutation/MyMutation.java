/* Date:        November 26, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.mymutation;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.IntegratedPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class MyMutation extends IntegratedPluginController<MyMutationModel>
{
	private static final long serialVersionUID = -623854680198101512L;

	public MyMutation(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new MyMutationModel(this));
		this.getModel().setPatientPager("res/mutation/patientPager.jsp");
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("MyMutation.ftl", getModel());
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String result = super.getCustomHtmlHeaders();
		
		if (CollectionUtils.isEmpty(this.getModel().getPatientSummaryVOList()))
				result += "<meta http-equiv=\"refresh\" content=\"0; URL=molgenis.do?select=MyMutation&__target=MyMutation&__action=show\">";

		return result;
	}

	public void show(Database db, Tuple request)
	{
		try
		{
			SearchService searchService = ServiceLocator.instance().getSearchService();
			Login securityService       = ServiceLocator.instance().getSecurityService();

			List<PatientSummaryDTO> patientSummaryVOs = searchService.findPatientsByUserId(securityService.getUserId());
	
			this.getModel().setPatientSummaryVOList(patientSummaryVOs);
			((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOList());
			this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		// Dirty hack: Actually the PatientPager should be included here.
		// But we need the HttpServletRequest which is not passed to reload().
		// So the customHtmlHeaders will issue a request with action==show
		// which does the work for us.
	}
}
