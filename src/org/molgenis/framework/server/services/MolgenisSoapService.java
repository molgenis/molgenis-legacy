package org.molgenis.framework.server.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;

/**
 * 
 * FIXME NOT WORKING, seems to get stuck on:
 * 
 * Dec 6, 2011 10:59:16 PM
 * org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper toResponse WARNING:
 * WebApplicationException has been caught : no cause is available
 * 
 */
public class MolgenisSoapService extends CXFNonSpringServlet implements MolgenisService
{
	private static final long serialVersionUID = 1L;
	private MolgenisContext mc;
	private boolean cxfLoaded;

	protected Database freshDatabase = null;

	public MolgenisSoapService(MolgenisContext mc) throws ServletException
	{
		this.mc = mc;

		super.init(mc.getServletConfig());
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws IOException
	{
		freshDatabase = request.getDatabase();

		try
		{
			if (this.cxfLoaded == false && (this.getSoapImpl() != null))
			{
				super.loadBus(mc.getServletConfig());
				Bus bus = this.getBus();
				BusFactory.setDefaultBus(bus);
				// FIXME: unhardcode path
				Endpoint.publish("/api/soap", this.getSoapImpl());
				this.cxfLoaded = true;
			}

			super.doPost(request.getRequest(), response.getResponse());
		}
		catch (Exception e)
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
	public Object getSoapImpl() throws ServletException
	{
		throw new UnsupportedOperationException("Don't use this! Try the generated subclass 'SoapService' instead.");
	}
}
