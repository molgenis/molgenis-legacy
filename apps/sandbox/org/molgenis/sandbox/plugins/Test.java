
package org.molgenis.sandbox.plugins;

import java.io.IOException;
import java.text.ParseException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * TestController takes care of all user requests updates model and view accordingly (in MVC architecture).
 *
 * <li>TestModel holds application state and services on top of domain model 
 * <li>TestView are template/scripts to show the layout.
 */
public class Test extends EasyPluginController<TestModel>
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct new controller with unique name and optional parent screen.
	 */
	public Test(String name, ScreenController<?> parent)
	{
		//link this controller into its place in the user interface tree
		super(name, null, parent);
		
		//configure the model with the state of your UI screen
		this.setModel(new TestModel(this));
		
		//configure the view, default using Freemarker layout template TestView.ftl
		this.setView(new FreemarkerView("TestView.ftl", getModel()));

	}
	
	/**
	 * Reload data if needed into model and change view if needed.
	 * Exceptions will be caught, logged and shown to the user.
	 * All db actions are within one transaction.
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
		//example: update model with data from database
		Query<Investigation> q = db.query(Investigation.class);
		//q.like("name", "molgenis");
		getModel().investigations = q.find();
	}
		
	/**
	 * If a user sends a request it can be handled here.
	 * Default, it will be automatically mapped to methods based request.getAction();
	 */
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//automatically calls functions with same name as action
		try {
			delegate(request.getAction(), db, request);
		} catch (HandleRequestDelegationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Example of a custom action handler.
	 * @throws Exception 
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public void addInvestigation(Database db, Tuple request) throws Exception
	{
		//Easily create object from request and use it
		Investigation i = new Investigation();
		i.set(request);
		db.add(i);
		getModel().setMessages(new ScreenMessage("Added new investigation",true));
	}
}
