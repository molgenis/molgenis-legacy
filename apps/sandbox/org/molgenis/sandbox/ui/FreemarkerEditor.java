
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.FreemarkerInput;

/**
 * FreemarkerEditorController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>FreemarkerEditorModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>FreemarkerEditorView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class FreemarkerEditor extends EasyPluginController<FreemarkerEditorModel>
{
	public FreemarkerEditor(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new FreemarkerEditorModel(this)); //the default model
		this.setView(new FreemarkerView("FreemarkerEditorView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	public String getCustomHtmlHeaders()
	{
		return new FreemarkerInput("blaat").getCustomHtmlHeaders();
	}
	
	public String render()
	{
		return new FreemarkerInput("blaat").render();
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

}