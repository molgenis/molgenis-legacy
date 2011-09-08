
package org.molgenis.ngs.ui;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;

import app.servlet.UsedMolgenisOptions;

/**
 * AdminDBPluginController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>AdminDBPluginModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>AdminDBPluginView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class AdminDBPlugin extends EasyPluginController<AdminDBPluginModel>
{
	public AdminDBPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AdminDBPluginModel(this)); //the default model
		this.setView(new FreemarkerView("AdminDBPluginView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
//		//example: update model with data from the database
//		Query q = db.query(Investigation.class);
//		q.like("name", "molgenis");
//		getModel().investigations = q.find();
	}
	
	/**
	 * When action="updateDate": update model and/or view accordingly.
	 *
	 * Exceptions will be logged and shown to the user automatically.
	 * All db actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception
	{
		getModel().date = request.getDate("date");
	
//		//Easily create object from request and add to database
//		Investigation i = new Investigation(request);
//		db.add(i);
//		this.setMessage("Added new investigation");

		getModel().setSuccess("update succesfull");
	}
	
	public void resetDatabase(Database db, Tuple request) throws Exception
	{

		
		
		print("resetDatabase pressed");
		
		new emptyDatabase(db, true);
		
		print("done with resetDatabase");
	}
	
	private void print(String str) {
		System.out.println(">> " + str);
	}
}