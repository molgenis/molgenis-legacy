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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.framework.server.AbstractMolgenisServlet;

import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.services.MolgenisGuiService;

import java.io.IOException;
import java.text.ParseException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

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
import org.molgenis.framework.db.DatabaseException;
<#else>
import ${package}.DatabaseFactory;
import javax.servlet.ServletContext;
import org.molgenis.framework.db.jdbc.JndiDataSourceWrapper;
</#if>

public class GuiService extends MolgenisGuiService implements MolgenisService
{
	private static final long serialVersionUID = 3141439968743510237L;

	public GuiService(MolgenisContext mc)
	{
		super(mc);
	}

	public ApplicationController createUserInterface( Login userLogin )
	{
		ApplicationController app = null;
		try {
			final Database dbForController = super.db;
			//enhance the ApplicationController with a method to getDatabase 
			app = new ApplicationController( new UsedMolgenisOptions(), userLogin)
			{
				private static final long serialVersionUID = 6962189567229247434L;
			
				@Override
				public Database getDatabase()
				{
					return dbForController;
				}
			};
			app.getModel().setLabel("${model.label}");
			app.getModel().setVersion("${version}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		<#if mail_smtp_user != '' && mail_smtp_au != ''>
		EmailService service = new SimpleEmailService();
		service.setSmtpFromAddress("${mail_smtp_from}");	
		service.setSmtpProtocol("${mail_smtp_protocol}");
		service.setSmtpHostname("${mail_smtp_hostname}");
		service.setSmtpPort(${mail_smtp_port});
		service.setSmtpUser("${mail_smtp_user}");
		service.setSmtpAu("${mail_smtp_au}");	
		app.setEmailService(service);</#if>
		
		<#list model.userinterface.children as subscreen>
			<#assign screentype = Name(subscreen.getType().toString()?lower_case) />
			new ${package}.ui.${JavaName(subscreen)}${screentype}<#if screentype == "Form">Controller</#if>(app);
		</#list>
		return app;
	}
	
	
}
