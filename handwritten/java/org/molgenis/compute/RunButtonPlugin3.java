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
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.util.Tuple;

public class RunButtonPlugin3 extends GenericPlugin
{	
	//in complicated cases, put this in seperate 'Model' object, in MVC terms
	String hello = "world";
	
	public RunButtonPlugin3(String name, ScreenModel parent)
	{
		super(name, parent);
	}
	
	public String render()
	{
		//create the view, in MVC terms
		TablePanel view = new TablePanel("myTablePanel", null);
		
		//create an input for 'name' with value 'name'
		StringInput helloInput = new StringInput("name",this.hello);
		
		//create a button to set the Hello <name> value
		ActionInput changeInput = new ActionInput("changeName");
		
		//add inputs to view
		view.add(helloInput);
		view.add(changeInput);
		
		//return html version of the view
		return view.toHtml();
	}
	
	//handle changeName event
	public void changeName(Database db, Tuple request)
	{
		if(request.notNull("name"))
		{
			this.hello = request.getString("name");
		}
		else
		{
			this.hello = "world";
		}
	}
}
