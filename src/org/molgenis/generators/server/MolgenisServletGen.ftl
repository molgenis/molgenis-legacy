<#setting number_format="#"/>
<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/*
 * Created by: ${generator}
 * Date: ${date}
 */

package ${package}.servlet;

import java.io.File;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.molgenis.framework.security.Login;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.UserInterface;
import org.molgenis.framework.server.AbstractMolgenisServlet;

import org.molgenis.util.EmailService;
import org.molgenis.util.HtmlTools;
import org.molgenis.util.SimpleEmailService;
<#if generate_BOT>
import java.io.IOException;
import ircbot.IRCHandler;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import generic.JavaCompiler;
import generic.JavaCompiler.CompileUnit;
</#if>
<#if db_mode = 'standalone'>
import org.apache.commons.dbcp.BasicDataSource;
<#else>
import org.molgenis.framework.db.jdbc.JndiDataSourceWrapper;
</#if>

public class MolgenisServlet extends AbstractMolgenisServlet
{
	/** */
	private static final long serialVersionUID = 3141439968743510237L;
	/** */

	public Database getDatabase() throws DatabaseException, NamingException
	{
		<#if db_mode = 'standalone'>
			BasicDataSource data_src = new BasicDataSource();
			data_src.setDriverClassName("${db_driver}");
			data_src.setUsername("${db_user}");
			data_src.setPassword("${db_password}");
			data_src.setUrl("${db_uri}"); // a path within the src folder?
			data_src.setMaxIdle(10);
			data_src.setMaxWait(1000);
		
			DataSource dataSource = (DataSource)data_src;
			return new app.JDBCDatabase(dataSource, new File("attachedfiles"));
		<#elseif databaseImp = 'jpa'>
			return new ${package}.JpaDatabase();				
		<#else>
			//The datasource is created by the servletcontext!		
			DataSource dataSource = (DataSource)getServletContext().getAttribute("DataSource");
			return new ${package}.<#if databaseImp = 'jpa'>Jpa<#else>JDBC</#if>Database(dataSource, new File("${db_filepath}"));
		
			//TOMCAT
			//String jndiName = "java:comp/env/jdbc/molgenisdb";
			//JndiDataSourceWrapper source = new JndiDataSourceWrapper(jndiName);
			//return new ${package}.JDBCDatabase(source, new File("${db_filepath}"));
		
			//GLASSFISH
			//DataSource dataSource = (DataSource)getServletContext().getAttribute("DataSource");
			//return new ${package}.JDBCDatabase(dataSource, new File("${db_filepath}"));
		</#if>
	}
	
	<#if generate_BOT>

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		ServletConfig s = getServletConfig();
		IRCHandler bot;
		if((bot = (IRCHandler) s.getServletContext().getAttribute("bot")) == null){
			Thread t = new Thread(){
				public void run(){	
					JavaCompiler j = new JavaCompiler();
					CompileUnit source = j.newCompileUnit("handwritten\\webserver\\","buildBOT\\");
					source.addDependencies(new String[]{"handwritten\\webserver\\","WebContent\\WEB-INF\\lib\\pircbot.jar"});
					source.setMainClass("ircbot.IRCHandler");
					source.setCustomJarName("WebContent/dist/Bot");
					j.CompileTarget(source);
				}
			};
			t.start();
		
			s.getServletContext().setAttribute("bot", bot = new IRCHandler());
			new Thread(bot).start();
			System.err.println("Started a bot");
		}else{
			System.err.println("Found the bot");
		}
		super.service(request, response);
	}

	</#if>
	
	public static String getDBDriver(){
		return "${db_driver}";
	}

	public Login createLogin( Database db, HttpServletRequest request )
	{
	<#if auth_redirect != ''>
		return new ${loginclass}(<#if loginclass != 'org.molgenis.framework.security.SimpleLogin'>db, "${auth_redirect}"</#if>);
	<#else>
		return new ${loginclass}(<#if loginclass != 'org.molgenis.framework.security.SimpleLogin'>db</#if>);
	</#if>
	}

	public UserInterface createUserInterface( Login userLogin )
	{
		UserInterface app = new UserInterface( userLogin);
		app.setLabel("${model.label}");
		app.setVersion("${version}");
		
<#if mail_smtp_user != '' && mail_smtp_au != ''>
		EmailService service = new SimpleEmailService();
		service.setSmtpFromAddress("${mail_smtp_from}");	
		service.setSmtpProtocol("${mail_smtp_protocol}");
		service.setSmtpHostname("${mail_smtp_hostname}");
		service.setSmtpPort(${mail_smtp_port});
		service.setSmtpUser("${mail_smtp_user}");
		service.setSmtpAu("${mail_smtp_au}");	
		app.setEmailService(service);
</#if>
		
<#list model.userinterface.children as subscreen>
<#assign screentype = Name(subscreen.getType().toString()?lower_case) />
		new ${package}.ui.${JavaName(subscreen)}${screentype}<#if screentype == "Form">Model</#if>(app);
</#list>
		return app;
	}
	
	public static String getMolgenisVariantID()
	{
		return "${model.name}";
	}	
	
	@Override
	public Object getSoapImpl() throws DatabaseException, NamingException
	{
		return new ${package}.servlet.SoapApi((Database)getDatabase());
	}
	
	@Override
	public boolean linkoutOverlay() {
		return ${linkout_overlay?string};
	}
}
