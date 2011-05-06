/* Date:        April 12, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.sandbox.plugins;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Tuple;

public class VaadinPlugin2 extends PluginModel
{
	
	private static final long serialVersionUID = 1L;
	private String Status = "";
	
	public VaadinPlugin2(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setStatus("This is a test of Vaadin");
		
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_sandbox_plugins_VaadinPlugin2";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/sandbox/plugins/VaadinPlugin2.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//			//start database transaction
//			db.beginTx();
//
//			//get the "__action" parameter from the UI
//			String action = request.getAction();
//		
//			if( action.equals("do_add") )
//			{
//				Experiment e = new Experiment();
//				e.set(request);
//				db.add(e);
//			}
//
//			//commit all database actions above
//			db.commitTx();
//
//		} catch(Exception e)
//		{
//			db.rollbackTx();
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}
	
	public String getCustomHtmlHeaders() {
		
			String divId = "vaadin_test";
			//path with the vaadin app serlvet (including trailing slash)
			String servletPath = "/molgenis_apps/vaadin-apps/test/";
			//path of this app; should also work when null (but doesn't)
			String appPath = "/molgenis_apps/";
			
			return "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\""+appPath+"VAADIN/themes/molgenis/styles.css\">" +
			"<script type=\"text/javascript\">"+
			"var vaadin = {"+
			//optionally repeat for each div
			//"	vaadinConfigurations: {"+
			//"		'"+divId+"' :{"+
			//"			appUri:'"+servletPath+"',"+ 
			//"			themeUri: '"+appPath+"VAADIN/themes/molgenis', "+
			//"			versionInfo : {vaadinVersion:\"6.5.2\",applicationVersion:\"NONVERSIONED\"}"+
			//"		}"+
			//"	}};"+
			"</script>"+
			"<script language='javascript' src='"+appPath+"VAADIN/widgetsets/com.vaadin.terminal.gwt.DefaultWidgetSet/com.vaadin.terminal.gwt.DefaultWidgetSet.nocache.js'></script>";
	}
}
