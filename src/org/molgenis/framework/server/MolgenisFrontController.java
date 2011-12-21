package org.molgenis.framework.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.DatabaseException;

public abstract class MolgenisFrontController extends HttpServlet implements
		MolgenisService
{
	// helper vars
	private static final long serialVersionUID = -2141508157810793106L;
	protected Logger logger;
	
	// map of all services for this app
	protected Map<String, MolgenisService> services;
	
	// list of all connections
	protected Map<UUID, Connection> connections;
	
	// the used molgenisoptions, set by generated MolgenisServlet
	protected MolgenisOptions usedOptions = null;
	
	//context
	protected MolgenisContext context;
	
	// the database given  1 connection per request (setup stored in session, connectionless after request)
	// return a UUID of the connection that was given to this database and was stored in Map<UUID, Connection> connections
	public abstract UUID createDatabase(MolgenisRequest request)  throws DatabaseException, SQLException;
	
	//the datasource to be put in the context
	public abstract DataSource createDataSource();
	
	// get login from session and set it to database, or create new login
	public abstract void createLogin(MolgenisRequest request) throws Exception;

	// the one and only service() used in the molgenis app
	public void service(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			//wrap request and response
			MolgenisRequest req = new MolgenisRequest(request, response); //TODO: Bad, but needed for redirection. DISCUSS.
			MolgenisResponse res = new MolgenisResponse(response);
			
			//handle the request with current database + login
			this.handleRequest(req, res);
		}
		catch (Exception e)
		{
            //TODO: send generic error page with details
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response)
			throws ParseException, DatabaseException, IOException
	{
		HttpServletRequest req = request.getRequest();
		String path = req.getRequestURI().substring(context.getVariant().length()+1);
		if(path.equals("")) path = "/";
	
		for (String p : services.keySet())
		{
			if (path.startsWith(p))
			{
				
				
				//if mapped to "/", we assume we are serving out a file, and do not manage security/connections
				if(p.equals("/"))
				{
					System.out.println("> serving file: " + path);
					services.get(p).handleRequest(request, response);
				}
				else
				{
					System.out.println("> new request to '"+path+"' handled by " + services.get(p).getClass().getSimpleName() + " mapped on path " + p);
					System.out.println("request content: " + request.toString());
					
					UUID connId = getSecuredDatabase(request);
					
					System.out.println("database status: " + (request.getDatabase().getSecurity().isAuthenticated() ? "authenticated as "
							+ request.getDatabase().getSecurity().getUserName() : "not authenticated"));
					
					request.setServicePath(p);
					
					try
					{
						services.get(p).handleRequest(request, response);
					}
					finally
					{
						manageConnection(connId);
					}
					
					
					//printSessionInfo(req.getSession());
					//context.getTokenFactory().printTokens();
				}
				
				return;
			}
		}
	}
	
	protected UUID getSecuredDatabase(MolgenisRequest req) throws DatabaseException
	{
		try
		{
			//create database, add a single connection from the pool and set in request for use
			UUID connId = this.createDatabase(req);
			
			//setup login credentials, or reuse from session and apply to database
			this.createLogin(req);
			
			//return connection id
			return connId;
		}
		catch(Exception e)
		{
			throw new DatabaseException(e);
		}
	}
	
	protected void manageConnection(UUID connId) throws DatabaseException
	{
		try
		{
			//close the connection and check if it really was closed
			connections.get(connId).close();
			if(!connections.get(connId).isClosed())
			{
				throw new DatabaseException("ERROR: connection was not closed!");
			}
		}
		catch(SQLException sqle)
		{
			throw new DatabaseException(sqle);
		}
		
		//remove from list (does not happen if Exception was thrown)
		connections.remove(connId);
		
		System.out.println("< request was handled, active database connections: " + connections.size());
	}
	
	protected void createLogger(MolgenisOptions mo) throws ServletException
	{
		try{
			//get logger and remove appenders added by classpath JARs. (= evil)
			Logger rootLogger = Logger.getRootLogger();
			rootLogger.removeAllAppenders();
	
			//the pattern used to format the logger output
			PatternLayout pattern = new PatternLayout("%-4r %-5p [%c] %m%n");

			//get the level from the molgenis options
			rootLogger.setLevel(mo.log_level);
	      
			//console appender
			if(mo.log_target.equals(MolgenisOptions.LogTarget.CONSOLE))
			{
				rootLogger.addAppender(new ConsoleAppender(pattern));
				System.out.println("Log4j CONSOLE appender added log level " + mo.log_level);
			}
			
			//file appender
			if(mo.log_target.equals(MolgenisOptions.LogTarget.FILE))
			{
				RollingFileAppender fa = new RollingFileAppender(pattern, "logger.out");
				fa.setMaximumFileSize(100000000); //100MB
				rootLogger.addAppender(fa);
				System.out.println("Log4j FILE appender added with level " + mo.log_level + ", writing to: " + new File(fa.getFile()).getAbsolutePath());
			}
			
			//add no appender at all  
			if(mo.log_target.equals(MolgenisOptions.LogTarget.OFF))
			{
				System.out.println("Log4j logger turned off");
			}			
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
	
	private void printSessionInfo(HttpSession session)
	{
		Date created = new Date(session.getCreationTime());
		Date accessed = new Date(session.getLastAccessedTime());
		System.out.println("SESSION ID " + session.getId());
		//System.out.println("SESSION Created: " + created);
		//System.out.println("SESSION Last Accessed: " + accessed);

		// print session contents

		Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();
			String value = session.getAttribute(name).toString();
			System.out.println("SESSION_ATTRIB " + name + " = " + value);
		}
	}

	


	// if (path != null && path.contains("/api/find"))
	// {
	// this.handleDownload(request, response);
	// }
	// else if (path != null && path.contains("/api/add"))
	// {
	// this.handleUpload(request, response);
	// }
	// else if (path != null && path.contains("/api/R"))
	// {
	// this.handleRAPIrequest(request, response);
	// }
	// else if (path != null && (path.contains("/api/soap")))
	// {
	// this.handleSOAPrequest(request, response);
	// }
	// else if (path != null && path.contains("/xref/find"))
	// {
	// this.handleXREFrequest(request, response);
	// }
	// else if (path != null && path.contains("/download/"))
	// {
	// this.handleDownloadFile(request, response);
	// }

}
