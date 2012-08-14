package services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.ui.ApplicationController;

import plugins.autohidelogin.AutoHideLogin;

/**
 * Hide/unhide the login tab via a boolean and this service to flip it.
 * See: /molgenis_apps/modules/xgap/plugins/autohidelogin/AutoHideLogin.java
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

		ApplicationController molgenis = (ApplicationController) request.getRequest().getSession().getAttribute("application");
		
		try
		{
			//if null, make true
			if(molgenis.sessionVariables.get(AutoHideLogin.AUTOHIDE_LOGIN) == null)
			{
				molgenis.sessionVariables.put(AutoHideLogin.AUTOHIDE_LOGIN, true);
			}
			else
			{
				//if true, make false
				if((Boolean)molgenis.sessionVariables.get(AutoHideLogin.AUTOHIDE_LOGIN) == true)
				{
					molgenis.sessionVariables.put(AutoHideLogin.AUTOHIDE_LOGIN, false);
				}
				
				//if false, make true
				else
				{
					molgenis.sessionVariables.put(AutoHideLogin.AUTOHIDE_LOGIN, true);
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
