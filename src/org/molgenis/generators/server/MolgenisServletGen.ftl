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
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.framework.server.AbstractMolgenisServlet;
import ${package}.<#if databaseImp = 'jpa'>Jpa<#else>JDBC</#if>Database;
import org.molgenis.util.EmailService;
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

	private static final long serialVersionUID = 3141439968743510237L;
	
	public MolgenisServlet(){
		this.usedOptions = new UsedMolgenisOptions();
	}

	public Database getDatabase() throws Exception
	{

		<#if databaseImp = 'jpa'>
			JpaDatabase db = new ${package}.JpaDatabase();
			return db;		
		<#elseif db_mode = 'standalone'>
			BasicDataSource data_src = new BasicDataSource();
			data_src.setDriverClassName("${db_driver}");
			data_src.setUsername("${db_user}");
			data_src.setPassword("${db_password}");
			data_src.setUrl("${db_uri}"); // a path within the src folder?
			data_src.setMaxIdle(10);
			data_src.setMaxWait(1000);
		
			DataSource dataSource = (DataSource)data_src;
			JDBCDatabase db = new ${package}.JDBCDatabase(dataSource, new File("${db_filepath}"));
			db.getFileSourceHelper().setVariantId("${model.name}");
			return db;
		<#else>
			//The datasource is created by the servletcontext	
			DataSource dataSource = (DataSource)getServletContext().getAttribute("DataSource");
			<#if databaseImp = 'jpa'>Jpa<#else>JDBC</#if>Database db = new ${package}.<#if databaseImp = 'jpa'>Jpa<#else>JDBC</#if>Database(dataSource, new File("${db_filepath}"));
			db.getFileSourceHelper().setVariantId("${model.name}");
			return db;
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

	public Login createLogin( Database db, HttpServletRequest request )
	{
	<#if auth_redirect != ''>
		return new ${loginclass}(<#if loginclass != 'org.molgenis.framework.security.SimpleLogin'>db, "${auth_redirect}"</#if>);
	<#else>
		return new ${loginclass}(<#if loginclass != 'org.molgenis.framework.security.SimpleLogin'>db</#if>);
	</#if>
	}

	public ApplicationController createUserInterface( Login userLogin )
	{
		ApplicationController app = new ApplicationController( userLogin);
		app.getModel().setLabel("${model.label}");
		app.getModel().setVersion("${version}");
		
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
		new ${package}.ui.${JavaName(subscreen)}${screentype}<#if screentype == "Form">Controller</#if>(app);
</#list>
		return app;
	}
	
	public static String getMolgenisVariantID()
	{
		return "${model.name}";
	}	
	
	@Override
	public Object getSoapImpl() throws Exception
	{
		return new ${package}.servlet.SoapApi((Database)getDatabase());
	}

}
