/* Date:        January 28, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.core.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.core.service.PublicationService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class AllPublications extends EasyPluginController<AllPublicationsModel>
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
				result += "<meta http-equiv=\"refresh\" content=\"0; URL=molgenis.do?select=Publications&__target=Publications&__action=\">";

		return result;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
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
	
	public String include(Tuple request, String path)
	{
		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
		HttpServletRequest httpRequest   = rt.getRequest();
		HttpServletResponse httpResponse = rt.getResponse();
		RedirectTextWrapper respWrapper  = new RedirectTextWrapper(httpResponse);
			
		// Call/include page
		try
		{
			RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(path);
			if (dispatcher != null)
				dispatcher.include(httpRequest, respWrapper);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return respWrapper.getOutput();
	}
	
	private class RedirectTextWrapper extends HttpServletResponseWrapper
	{
		private PrintWriter printWriter;
		private StringWriter stringWriter;

		public RedirectTextWrapper(HttpServletResponse response)
		{
			super(response);
			this.stringWriter = new StringWriter();
			this.printWriter  = new PrintWriter(stringWriter);
		}

		@Override
		public PrintWriter getWriter()
		{
			return this.printWriter;
		}

		public String getOutput()
		{
			return this.stringWriter.toString();
		}
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// nothing to do here
	}
}
