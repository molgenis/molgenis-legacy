//package org.molgenis.framework.ui;
//
//import org.apache.log4j.Logger;
//
//import com.vaadin.Application;
//
///** Under development. Idea is this you only need to implement this class and that MOLGENIS than automatically solves all vaadin things for you */
//public abstract class VaadinView extends Application implements ScreenView
//{
//	// wrapper of this VaadinApplication
//	private ScreenModel model;
//	private transient Logger logger = Logger.getLogger(this.getClass());
//	
//	public VaadinView(ScreenModel model)
//	{
//		this.model = model;
//	}
//	
//	@Override
//	public abstract void init();
//	
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		String divId = this.model.getController().getName();
//		//path of this app; should also work when null (but doesn't)
//		String appPath = "";//"/molgenis_apps/"; 
//		//path with the vaadin app serlvet (including trailing slash)
//		String servletPath = appPath + "vaadin/"+this.model.getController().getName()+"/";
//
//		
//		return "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\""+appPath+"VAADIN/themes/molgenis/styles.css\">" +
//		"<script type=\"text/javascript\">"+
//		"var vaadin = {"+
//		//optionally repeat for each div
//		"	vaadinConfigurations: {"+
//		"		'"+divId+"' :{"+
//		"			appUri:'"+servletPath+"',"+ 
//		"			themeUri: '"+appPath+"VAADIN/themes/molgenis', "+
//		"			versionInfo : {vaadinVersion:\"6.5.2\",applicationVersion:\"NONVERSIONED\"}"+
//		"		}"+
//		"	}};"+
//		"</script>"+
//		"<script language='javascript' src='"+appPath+"VAADIN/widgetsets/com.vaadin.terminal.gwt.DefaultWidgetSet/com.vaadin.terminal.gwt.DefaultWidgetSet.nocache.js'></script>";
//	}
//
//
//
//	@Override
//	public String render()
//	{
//		return new FreemarkerView("VaadinView.ftl",model).render();
//	}
//}
