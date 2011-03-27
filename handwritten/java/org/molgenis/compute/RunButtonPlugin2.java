/* Date:        February 15, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.util.Tuple;

/** 
 * This uses the Freemarkerless plugin, also known as GenericPlugin*
 */
public class RunButtonPlugin2 extends GenericPlugin
{	
	//here we only have a button which will result in request tuple {'__action'="doRun", '__target'=this.getName()}
	ActionInput aButton = new ActionInput("doRun");
	
	//this methods links your plugin to the rest of the application
	public RunButtonPlugin2(String name, ScreenModel parent)
	{
		super(name, parent);
	}
	
	//this method will render html using molgenis Java widgets (instead of Freemarker)
	@Override
	public String render()
	{
		//see org.molgenis.framework.ui.html for available widgets
		return aButton.toHtml();
	}
	
	//this method is automatically called based on the parameter '__action="doRun"
	//optionally you can do this yourself by @Override public void handleRequest(Database db, Tuple request)
	public void doRun(Database db, Tuple request)
	{
		System.out.println("Action is run for request "+request.toString());
		aButton.setLabel("Pushed "+System.currentTimeMillis()+" Request was: "+request.toString());
	}
	
	@Override
	public void reload(Database db)
	{
		//does nothing in this case
	}
	
}
