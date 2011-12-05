package org.molgenis.framework.server.services;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.ServeConfig;

/**
 * 
 * NOT WORKING
 * NOT TESTED
 * 
 */
public class MolgenisSoapService extends CXFNonSpringJaxrsServlet
		implements MolgenisService
{
	private static final long serialVersionUID = -6699220792069809444L;
	Logger logger = Logger.getLogger(MolgenisRapiService.class);

	protected MolgenisContext mc;
	private boolean cxfLoaded = false;
	protected Database freshDatabase = null;

	public MolgenisSoapService(MolgenisContext mc) throws ServletException
	{
//		Hashtable<String,Object> params = new Hashtable<String,Object>();
//		params.put("javax.ws.rs.Application", "app.servlet.SoapService");
//		ServletConfig sc = new ServeConfig( mc.getServletContext(), params, "/");
//		
//		super.init(sc);
		this.mc = mc;
	}

	public void handleRequest(MolgenisRequest request, MolgenisResponse response)
			throws IOException
	{
		freshDatabase = request.getDatabase();

		try
		{
			if (this.cxfLoaded == false && (this.getSoapImpl(mc) != null))// ||
			{
				Hashtable<String,Object> params = new Hashtable<String,Object>();
				params.put("javax.ws.rs.Application", "app.servlet.SoapService");
				ServletConfig sc = new ServeConfig( mc.getServletContext(), params, "/");
//				
//				super.init(sc);
				super.loadBus(sc);
				Bus bus = this.getBus();
				BusFactory.setDefaultBus(bus);
				Endpoint.publish("/soap/", this.getSoapImpl(mc));
				this.cxfLoaded = true;
			}
			super.doPost(request.getRequest(), response.getResponse());
		}
		catch (ServletException e)
		{
			throw new IOException(e);
		}
	}

	/**
	 * Constructor that the generated subclass (SoapService) will override in
	 * order to return itself as an object
	 * 
	 * @param mc
	 * @return
	 * @throws ServletException 
	 */
	public Object getSoapImpl(MolgenisContext mc) throws ServletException
	{
		throw new UnsupportedOperationException("Don't use this! Try the generated subclass 'SoapService' instead.");
	}
}
