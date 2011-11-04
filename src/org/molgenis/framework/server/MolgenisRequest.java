package org.molgenis.framework.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.util.HttpServletRequestTuple;

public class MolgenisRequest extends HttpServletRequestTuple
{

	
	public MolgenisRequest(HttpServletRequest request) throws Exception
	{
		super(request);

	}

	public MolgenisRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		super(request, response);

	}

}
