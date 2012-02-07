package services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.cluster.Job;
import org.molgenis.cluster.Subjob;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.autohidelogin.AutoHideLoginModel;
import plugins.cluster.helper.Command;
import plugins.cluster.implementations.LocalComputationResource;
import plugins.cluster.interfaces.ComputationResource;

/**
 * Hide/unhide the login tab via a static boolean and this service to flip it.
 * See: /molgenis_apps/apps/xgap/plugins/autohidelogin/AutoHideLogin.java
 * @author joerivandervelde
 */
public class AutoHideLoginService  implements MolgenisService
{
	
	private MolgenisContext mc;
	
	public AutoHideLoginService(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	private static Logger logger = Logger.getLogger(AutoHideLoginService.class);

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		HttpServletResponse res = response.getResponse();
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html;charset=UTF8");
		
		OutputStream out = res.getOutputStream();
		PrintStream p = new PrintStream(new BufferedOutputStream(out), false, "UTF8");

		try
		{
			//if null, make true
			if(AutoHideLoginModel.isVisible == null)
			{
				AutoHideLoginModel.isVisible = true;
			}
			else
			{
				//if true, make false
				if(AutoHideLoginModel.isVisible == true)
				{
					AutoHideLoginModel.isVisible = false;
				}
				
				//if false, make true
				else
				{
					AutoHideLoginModel.isVisible = true;
				}
			}

			//write a meta refresh back to the login tab
			p.println("<HTML><HEAD><META HTTP-EQUIV=\"refresh\" CONTENT=\"0;URL=molgenis.do?__target=main&select=UserLogin\"></HEAD><BODY></BODY></HTML>");
			
			logger.info("serving " + request.getRequest().getRequestURI());
		}
		catch (Exception e)
		{
			p.print("\n\n");
			e.printStackTrace(p);
		}
		finally
		{
			p.flush();
			out.close();
		}
	}
}
