package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.RichtextInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.util.Tuple;

/**
 * HtmlEditorController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>HtmlEditorModel holds application state and business logic on top of
 * domain model. Get it via this.getModel()/setModel(..) <li>HtmlEditorView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class HtmlEditor extends EasyPluginController<HtmlEditorModel>
{
	String value = "Hello world";

	public HtmlEditor(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new HtmlEditorModel(this)); // the default model
		this.setView(new FreemarkerView("HtmlEditorView.ftl", getModel())); // <plugin
																			// flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception
	{
	}

	public void update(Database db, Tuple request)
	{
		this.value = request.getString("htmleditor");
	}

	public String getCustomHtmlHeaders()
	{
		return new RichtextInput("blaat").getCustomHtmlHeaders();
	}

	public String render()
	{
		MolgenisForm mf = new MolgenisForm(this);
		mf.add(new RichtextInput("htmleditor", value));
		mf.add(new TextInput("texteditor", value));
		mf.add(new ActionInput("update"));
		return mf.render();
	}
}