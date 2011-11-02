package org.molgenis.framework.server;

import java.io.IOException;
import java.io.OutputStream;
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

	public OutputStream getOutputStream() throws IOException
	{
		return response.getOutputStream();
	}

	public void setStatus(int status)
	{
		response.setStatus(status);
		
	}

	public void setContentLength(int length)
	{
		response.setContentLength(length);
		
	}

	public void setContentType(String string)
	{
		response.setContentType(string);
		
	}

	public void setHeader(String string, String string2)
	{
		response.setHeader(string, string2);
		
	}


	

}
