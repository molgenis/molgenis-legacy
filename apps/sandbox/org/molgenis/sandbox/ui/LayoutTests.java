
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.AccordeonLayout;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Label;
import org.molgenis.framework.ui.html.MenuInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MultipanelLayout;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.TabbedLayout;

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
		super(name, parent);
		this.setModel(new LayoutTestsModel(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("LayoutTestsView.ftl", getModel());
	}
	
	public String getCustomHtmlHeaders()
	{
		return new AccordeonLayout(null).getCustomHtmlHeaders();
	}
	
	public String render()
	{
		MolgenisForm mf = new MolgenisForm(this);
		
		MenuInput menu = new MenuInput("mymenu","My menu");
		menu.AddAction(new ActionInput("blaat1"));
		menu.AddAction(new ActionInput("blaat2"));
		
		MenuInput smenu = new MenuInput("submenu","Submenu");
		
		MenuInput ssmenu = new MenuInput("subsubmenu","SubSubmenu");
		
		ssmenu.AddAction(new ActionInput("sub-sub1"));
		ssmenu.AddAction(new ActionInput("sub=sub2"));
		
		smenu.AddMenu(ssmenu);
		
		smenu.AddAction(new ActionInput("sub1"));
		smenu.AddAction(new ActionInput("sub2"));
		
		menu.AddMenu(smenu);
		
		menu.AddAction(new ActionInput("blaat3"));
		
		
		
		mf.add(new Label("Demo of menu:"));
		
		mf.add(new Newline());
		
		mf.add(menu);
		
		mf.add(new Newline());
		
		mf.add(new Paragraph("Demo of accordeon"));
		
		MultipanelLayout l = new AccordeonLayout("demo1");
		
		l.add("Panel1", new Paragraph("hello world 1"));
		
		l.add("Panel2", new Paragraph("hello world 2"));
		
		l.add("Panel3", new Paragraph("hello world 3"));
		
		TabbedLayout t = new TabbedLayout("demo2");
		
		for(int i = 0; i < 20;i++)
		{
		t.add("Panel value"+i, new Paragraph("hello world "+i));
		}
		
		
		mf.add(l);
		
		mf.add(new Newline());
		
		mf.add(new Label("tabs:"));
		
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