package org.molgenis.framework.server.services;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.log4j.Logger;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.ServeConfig;

/**
 * 
 * NOT TESTED
 * 
 * See http://cxf.apache.org/docs/jax-rs.html
 * http://cxf.apache.org/docs/jaxrs-services-configuration.html
 * 
 */
public class MolgenisRestService extends CXFNonSpringJaxrsServlet implements MolgenisService
{
	private static final long serialVersionUID = -6699220792069809444L;
	Logger logger = Logger.getLogger(MolgenisRapiService.class);
	Hashtable<String, Object> restParams;

	private MolgenisContext mc;

	public MolgenisRestService(MolgenisContext mc)
	{
		this.mc = mc;
	}

	public MolgenisRestService() throws ServletException
	{
		restParams = new Hashtable<String, Object>();
		restParams.put("jaxrs.serviceClasses", "app.servlet.RestApi");
	}

	@Override
	public void handleRequest(MolgenisRequest r, MolgenisResponse response) throws IOException
	{
		try
		{
			super.init(new ServeConfig(mc.getServletContext(), restParams, "/"));
			super.service(r.getRequest(), response.getResponse());
			throw new IOException();
		}
		catch (ServletException e)
		{
			throw new IOException(e);
		}
	}
}
