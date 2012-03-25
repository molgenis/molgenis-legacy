package org.molgenis.mutation.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.service.GffService;

public class GffServlet implements MolgenisService
{
	private MolgenisContext mc;
	
	public GffServlet(MolgenisContext mc)
	{
		this.mc = mc;
	}

	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse resp) throws ParseException, DatabaseException, IOException
	{
		HttpServletResponse response = resp.getResponse();

		response.setContentType("text/plain");
		
		String type = req.getString("type");
		
		GffService gffService = ServiceLocator.instance().getGffService();

		String result = "";

		if ("exon".equalsIgnoreCase(type))
		{
			result = gffService.exportExons();
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
