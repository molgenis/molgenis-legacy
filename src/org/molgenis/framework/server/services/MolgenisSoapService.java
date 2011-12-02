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
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.ServeConfig;

/**
 * 
 * NOT IMPLEMENTED
 * NOT TESTED
 * 
 */
public class MolgenisSoapService extends CXFNonSpringJaxrsServlet implements MolgenisService 
{
	private static final long serialVersionUID = -6699220792069809444L;
	Logger logger = Logger.getLogger(MolgenisRapiService.class);
	Hashtable<String,Object> restParams;
	
	private MolgenisContext mc;
	private boolean cxfLoaded = false;
	
	public MolgenisSoapService(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	public MolgenisSoapService() throws ServletException
	{
//		restParams = new Hashtable<String,Object>();
//		restParams.put("jaxrs.serviceClasses", "app.servlet.RestApi");
	}
	
	public void handleRequest(MolgenisRequest r,
			MolgenisResponse response) throws IOException
	{
//		if (this.cxfLoaded == false && (this.getSoapImpl() != null))// ||
//			// this.getRestImpl()
//			// != null))
//			{
//				super.loadBus(this.getServletConfig());
//				Bus bus = this.getBus();
//				BusFactory.setDefaultBus(bus);
//				Endpoint.publish("/soap/", this.getSoapImpl());
//				this.cxfLoaded = true;
//			}
//
//			super.doPost(request, response);
	}
}
