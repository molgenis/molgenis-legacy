/* Date:        November 26, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.mycol7a1;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.ui.LimitOffsetPager;
import org.molgenis.mutation.vo.MyCOL7A1VO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class MyCOL7A1 extends PluginModel<Entity>
{

	private static final long serialVersionUID = -623854680198101512L;
	private String action         = "init";
	private MyCOL7A1VO myCOL7A1VO = new MyCOL7A1VO();
//	private LimitOffsetPager<PatientSummaryVO> pager;

	public MyCOL7A1(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_mutation_ui_mycol7a1_MyCOL7A1";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/mutation/ui/mycol7a1/MyCOL7A1.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			this.setMessages();

			this.action = request.getAction();

			if (this.action.startsWith("patientsFirstPage"))
			{
				this.myCOL7A1VO.getPager().first();
			}
			else if (this.action.startsWith("patientsPrevPage"))
			{
				this.myCOL7A1VO.getPager().prev();
			}
			else if (this.action.startsWith("patientsNextPage"))
			{
				this.myCOL7A1VO.getPager().next();
			}
			else if (this.action.startsWith("patientsLastPage"))
			{
				this.myCOL7A1VO.getPager().last();
			}
		}
		catch (Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=MyCOL7A1&__action=init\">Return to home page</a>";
			this.getMessages().add(new ScreenMessage(message, false));
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			if ("init".equals(this.action))
			{
				PatientService patientService            = PatientService.getInstance(db);

				PatientSearchCriteriaVO criteria         = new PatientSearchCriteriaVO();
				criteria.setUserId(getLogin().getUserId());

				List<PatientSummaryVO> patientSummaryVOs = patientService.findPatients(criteria);
				this.myCOL7A1VO.setPager(new LimitOffsetPager<PatientSummaryVO>(patientSummaryVOs, 20, 0));
			}
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
		if (getLogin().isAuthenticated())
			return true;
		else
			return false;
	}
	
	public MyCOL7A1VO getMyCOL7A1VO()
	{
		return this.myCOL7A1VO;
	}

	public String getUser() throws Exception
	{
		if (getLogin().isAuthenticated())
			return getLogin().getUserName();

		throw new Exception("Not logged in.");
	}
	
	//TODO: Where to put this?
	/**
	 * Get URL of PubMed
	 * @return PubMed URL
	 */
	public String getPubMedURL()
	{
		return "http://www.ncbi.nlm.nih.gov/pubmed/";
	}
}
