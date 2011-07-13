package org.molgenis.mutation.ui.search;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

//TODO: This class should either go to org.molgenis.util or become unnecessary
public class RedirectTextWrapper extends HttpServletResponseWrapper
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
