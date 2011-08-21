
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.AccordeonLayout;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MultipanelLayout;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.TabbedLayout;
import org.molgenis.framework.ui.html.TextParagraph;

/**
 * LayoutTestsController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>LayoutTestsModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>LayoutTestsView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class LayoutTests extends EasyPluginController<LayoutTestsModel>
{
	public LayoutTests(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new LayoutTestsModel(this)); //the default model
		this.setView(new FreemarkerView("LayoutTestsView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	public String getCustomHtmlHeaders()
	{
		return new AccordeonLayout(null).getCustomHtmlHeaders();
	}
	
	public String render()
	{
		MolgenisForm mf = new MolgenisForm(this);
		
		mf.add(new TextParagraph("Demo of accordeon"));
		
		MultipanelLayout l = new AccordeonLayout("demo1");
		
		l.add("Panel1", new TextParagraph("hello world 1"));
		
		l.add("Panel2", new TextParagraph("hello world 2"));
		
		l.add("Panel3", new TextParagraph("hello world 3"));
		
		TabbedLayout t = new TabbedLayout("demo2");
		
		t.add("Panel1", new TextParagraph("hello world 1"));
		
		t.add("Panel2", new TextParagraph("hello world 2"));
		
		t.add("Panel3", new TextParagraph("hello world 3"));
		
		
		mf.add(l);
		
		mf.add(new Newline());
		
		mf.add(new LabelInput("tabs:"));
		
		mf.add(new Newline());
		
		mf.add(t);
		
		return mf.render();
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