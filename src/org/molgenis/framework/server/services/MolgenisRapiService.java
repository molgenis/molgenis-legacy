package org.molgenis.framework.server.services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.server.FrontControllerAuthenticator;
import org.molgenis.framework.server.FrontControllerAuthenticator.LoginStatus;
import org.molgenis.framework.server.FrontControllerAuthenticator.LogoutStatus;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;

public class MolgenisRapiService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisRapiService.class);
	
	private MolgenisContext mc;
	
	public MolgenisRapiService(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	/**
	 * Delegate to handle request for the R api.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void handleRequest(MolgenisRequest request,
			MolgenisResponse response) throws IOException
	{
		 //as used in /molgenis/src/org/molgenis/generators/R/RApiGen.R.ftl, must match!
			String pwdString = MolgenisServiceAuthenticationHelper.LOGIN_PASSWORD;
			String usrString = MolgenisServiceAuthenticationHelper.LOGIN_USER_NAME;
			
			//Utils.console("starting RApiServlet");
			OutputStream outs = response.getResponse().getOutputStream();
			PrintStream out = new PrintStream(new BufferedOutputStream(outs), false, "UTF8"); // 1.4
			
			if(request.getString(usrString) != null && request.getString(pwdString) != null )
			{
				String usr = request.getString(usrString);
				String pwd = request.getString(pwdString);

				LoginStatus login = FrontControllerAuthenticator.login(request, usr, pwd);
				
				String responseLine;
				if(login == LoginStatus.ALREADY_LOGGED_IN)
				{
					responseLine = "You are already logged in. Log out first.";
				}
				else if(login == LoginStatus.SUCCESSFULLY_LOGGED_IN)
				{
					responseLine = "Welcome, " + usr + "!";
				}
				else if(login == LoginStatus.AUTHENTICATION_FAILURE)
				{
					responseLine = "User or password unknown.";
				}
				else if(login == LoginStatus.EXCEPTION_THROWN)
				{
					responseLine = "An error occurred. Contact your administrator.";
				}
				else
				{
					throw new IOException("Unknown login status: " + login);
				}
				
				writeResponse(response, responseLine, out);
				return;
			}
			
			if(request.getString("logout") != null && request.getString("logout").equals("logout"))
			{
				
				LogoutStatus logout = FrontControllerAuthenticator.logout(request);
				
				String responseLine;
				if(logout == LogoutStatus.ALREADY_LOGGED_OUT)
				{
					responseLine = "You are already logged out. Log in first.";
				}
				else if(logout == LogoutStatus.SUCCESSFULLY_LOGGED_OUT)
				{
					responseLine = "You are successfully logged out.";
				}
				else if(logout == LogoutStatus.EXCEPTION_THROWN)
				{
					responseLine = "An error occurred. Contact your administrator.";
				}
				else
				{
					throw new IOException("Unknown logout status: " + logout);
				}
				
				writeResponse(response, responseLine, out);
				return;
			}
			
			
			//Utils.console("URI path: " +request.getRequest().getRequestURI());
			String fullServicePath = request.getRequest().getServletPath() + request.getServicePath();
			//Utils.console("servlet path: " +fullServicePath);
			int loc = request.getRequest().getRequestURI().lastIndexOf(fullServicePath);
			String filename = request.getRequest().getRequestURI().substring(loc+fullServicePath.length());

			//Utils.console("filename is now: " + filename);
			
			String s = "";

			
			if (filename.startsWith("/")){
				filename = filename.substring(1);
			}
			// if R file exists, return that
			if (!filename.equals("") && !filename.endsWith(".R")){
				//Utils.console("bad request: no R extension");
				s += "you can only load .R files\n";
			} else if (filename.equals(""))
			{
				//Utils.console("getting default file");
				String localName = request.getRequest().getLocalName();
				if (localName.equals("0.0.0.0")) localName = "localhost";
				String server = "http://" + request.getRequest().getLocalName() + ":" + request.getRequest().getLocalPort() + "/"+mc.getVariant();
				String rSource = server + "/api/R/";
				// getRequestURL omits port!
				s +=("#first time only: install RCurl and bitops\n");
				s +=("#install.packages(\"RCurl\", lib=\"~/libs\")\n");
				s +=("#install.packages(\"bitops\", lib=\"~/libs\")\n");
				s +=("\n");
				s +=("#load RCurl and bitops\n");
				s +=("library(bitops, lib.loc=\"~/libs\")\n");
				s +=("library(RCurl, lib.loc=\"~/libs\")\n");
				s +=("\n");
				s +=("#get server paths to R API\n");
				s +=("molgenispath <- paste(\"" + rSource + "\")\n");
				s +=("serverpath <- paste(\"" + server + "\")\n");
				s +=("\n");
				s +=("#load autogenerated R interfaces\n");
				s +=("source(\"" + rSource + "source.R\")\n");
				s +=("\n");
				s +=("#connect to the server\n");
				s +=("MOLGENIS.connect(\"" + server + "\")\n");
				s +=("\n");
				s +=("#Use 'MOLGENIS.login(\"username\",\"password\")' to authenticate.\n");
			}
			else{
				// otherwise return the default R code to source all
				//Utils.console("getting specific R file");
				filename = filename.replace(".", "/");
				filename = filename.substring(0, filename.length() - 2) + ".R";
				
				System.out.println("filename: " + filename);
				
				//map to hard drive, minus path app/servlet
				Class FC = null;
				try
				{
					FC = Class.forName("app.servlet.FrontController");
				}
				catch (ClassNotFoundException e)
				{
					throw new IOException(e);
				}
				
				File root = new File(FC.getResource("source.R")
						.getFile()).getParentFile().getParentFile().getParentFile();
				System.out.println("root: " + root.getAbsolutePath());

				System.out.println("root: " + root.getAbsolutePath());
				
				if (filename.equals("source.R"))
				{
					root = new File(root.getAbsolutePath() + "/app/servlet");
				}
				File source = new File(root.getAbsolutePath() + "/" + filename);
				
				System.out.println("source: " + source.getAbsolutePath());
				
				//up to root of app	
				//Utils.console("trying to load R file: " + filename + " from path " + source);
				if(source.exists()){
					String str = this.printScript(source.toURI().toURL(), "");
					s +=(str);
				}else{
					s +=("File '" + filename + "' not found\n");
				}
				//Utils.console("done getting specific R file");
			}
			writeResponse(response, s, out);
			//Utils.console("closed & flushed");		
		
		
	}

	private void writeResponse(MolgenisResponse response, String responseLine, PrintStream out) throws IOException{
		response.getResponse().setStatus(HttpServletResponse.SC_OK);		
		response.getResponse().setContentLength(responseLine.length());
		response.getResponse().setCharacterEncoding("UTF8");
		response.getResponse().setContentType("text/plain");
		out.print(responseLine);
		out.flush();
		out.close();
		response.getResponse().flushBuffer();
	}

	private String printScript( URL source, String out ) throws IOException
	{
		//Utils.console("reading file to be outputted");
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		String sourceLine;
		while( (sourceLine = reader.readLine()) != null )
		{
			out += sourceLine + "\n";
		}
		reader.close();
		//Utils.console("done reading");
		return out;
	}
	
}
