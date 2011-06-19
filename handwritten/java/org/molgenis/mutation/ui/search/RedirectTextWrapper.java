package org.molgenis.mutation.ui.search;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

//TODO: This class should either go to org.molgenis.util or become unnecessary
public class RedirectTextWrapper extends HttpServletResponseWrapper
{
	private PrintWriter printWriter;
	private CharArrayWriter caWriter;

	public RedirectTextWrapper(HttpServletResponse response)
	{
		super(response);
		this.caWriter = new CharArrayWriter();
		this.printWriter = new PrintWriter(caWriter);
	}

	@Override
	public PrintWriter getWriter()
	{
		return this.printWriter;
	}
	
	public String getOutput()
	{
		return new String(this.caWriter.toCharArray());
	}
}
