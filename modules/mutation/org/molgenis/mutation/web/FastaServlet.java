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
import org.molgenis.mutation.service.FastaService;

public class FastaServlet implements MolgenisService
{
	private MolgenisContext mc;
	
	public FastaServlet(MolgenisContext mc)
	{
		this.mc = mc;
	}

	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse resp) throws ParseException, DatabaseException, IOException
	{
		HttpServletResponse response = resp.getResponse();

		response.setContentType("text/plain");
		
		String type = req.getString("type");
		
		FastaService fastaService = new FastaService();
		fastaService.setDatabase(req.getDatabase());

		String result = "";

		if ("gene".equalsIgnoreCase(type))
		{
			result = fastaService.exportGene();
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
