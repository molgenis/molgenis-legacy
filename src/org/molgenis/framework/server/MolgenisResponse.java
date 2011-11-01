package org.molgenis.framework.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class MolgenisResponse
{
	HttpServletResponse response;

	public MolgenisResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public PrintWriter getWriter() throws IOException
	{
		return response.getWriter();
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}
	
	

}
