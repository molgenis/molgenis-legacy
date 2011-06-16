
package org.molgenis.compute;

import app.ui.WorksheetFormController;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.*;
import org.molgenis.ngs.Worksheet;
import org.molgenis.ngs.ui.WorksheetForm;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

/**
 * StartNgsController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>StartNgsModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>StartNgsView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class StartNgs extends EasyPluginController<StartNgsModel>
{
	public StartNgs(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new StartNgsModel(this)); //the default model
		this.setView(new FreemarkerView("StartNgsView.ftl", getModel())); //<plugin flavor="freemarker"
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
	public void buttonStart(Database db, Tuple request) throws Exception
	{

        System.out.println("pipeline started");
        ScreenController<?> parentController = (ScreenController<?>) this.getParent();
		FormModel<Worksheet> parentForm = (FormModel<Worksheet>) ((FormController)parentController).getModel();
		Worksheet data = parentForm.getRecords().get(0);
        //data.
		getModel().setSuccess("update succesfull");
	}
}
