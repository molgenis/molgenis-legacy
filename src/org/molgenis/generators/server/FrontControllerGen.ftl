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
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.framework.server.MolgenisFrontController;
import org.molgenis.framework.server.MolgenisService;


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

public class FrontController extends MolgenisFrontController
{
	private static final long serialVersionUID = 3141439968743510237L;

	public FrontController() {
		this.usedOptions = new UsedMolgenisOptions();
		
		LinkedHashMap<String,MolgenisService> services = new LinkedHashMap<String,MolgenisService>();
		
		<#list services as service>
		services.put("${service?split('@')[1]}", new ${service?split('@')[0]}());
		</#list>
		
		this.services = services;
	}
	
	protected Database createDatabase() {
		try
		{
		<#if databaseImp = 'jpa'>
			return DatabaseFactory.create();	
		<#else>
			//The datasource is created by the servletcontext	
			<#if db_mode != 'standalone'>
			ServletContext sc = MolgenisContextListener.getInstance().getContext();
			DataSource dataSource = (DataSource)sc.getAttribute("DataSource");
			return DatabaseFactory.create(dataSource, new File("${db_filepath}"));
			<#else>
			BasicDataSource data_src = new BasicDataSource();
			data_src.setDriverClassName("${db_driver}");
			data_src.setUsername("${db_user}");
			data_src.setPassword("${db_password}");
			data_src.setUrl("${db_uri}"); // a path within the src folder?
			data_src.setMaxIdle(10);
			data_src.setMaxWait(1000);
		
			DataSource dataSource = (DataSource)data_src;
			Database db = new ${package}.JDBCDatabase(dataSource, new File("${db_filepath}"));
			return db;			
			</#if>
		</#if>
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public Database getDatabase() throws Exception
	{
		return super.getDatabase();
	}
	
}
