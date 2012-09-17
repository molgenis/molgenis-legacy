
<#setting number_format="#"/>
<#include "GeneratorHelper.ftl">
package ${package}.servlet;

import org.gridgain.grid.GridFactory;
import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.compute.scriptserver.MCFServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 * @author jorislops
 */
public final class ContextListener implements ServletContextListener {
    private MCF mcf = null;
    private ServletContext context = null;


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {

                    context = sce.getServletContext();
        mcf = new MCFServer();
        context.setAttribute("MCF", mcf);


			ServletContext sc = sce.getServletContext();

				Context initContext = new InitialContext();
				//for tomcat
				Context envContext  = (Context)initContext.lookup("java:/comp/env");
				//for glassfish
				//Context envContext  = (Context)initContext.lookup("");
				DataSource dsource = (DataSource)envContext.lookup("${db_jndiname}");

			sc.setAttribute("DataSource", dsource);
		} catch (NamingException ex) {
			Logger.getLogger(ContextListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		sc.removeAttribute("DataSource");
        GridFactory.stop(true);

	}
}
