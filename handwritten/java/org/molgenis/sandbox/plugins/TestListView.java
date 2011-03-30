/*
 * Date: January 14, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.sandbox.plugins;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ListView;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Extend GenericPlugin to use a plugin without freemarker.
 * Instead you have to implement the 'render' method that outputs as HTML string.
 * Here you can use the MOLGENIS UI framework to do the heavy lifting. An example below.
 * 
 * 
 */
public class TestListView extends GenericPlugin
{

	private static final long serialVersionUID = -3119343430895743631L;
	ListView listView = null;

	public TestListView(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	public String render()
	{
		return listView.toHtml();
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// replace example below with yours
		// try
		// {
		// Database db = this.getDatabase();
		// String action = request.getString("__action");
		//		
		// if( action.equals("do_add") )
		// {
		// Experiment e = new Experiment();
		// e.set(request);
		// db.add(e);
		// }
		// } catch(Exception e)
		// {
		// //e.g. show a message in your form
		// }
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			//TODO: Danny: use or loose
			/*List<Investigation> investigations = */db.find(Investigation.class);

			// borrow form builder from generated form (need to make trivial and configurable)
//			InvestigationForm f = new InvestigationForm();
//			//f.setEntity(investigations);
//
//			ActionInput edit = new ActionInput("edit");
//			edit.setIcon("generated-res/img/editview.gif");
//			
//			listView = new ListView("mylist");
//			for(HtmlForm form: f.getRecordInputs())
//			{
//				form.addAction(edit);
//				listView.addRow(form);
//			}
//			
//			listView.setSortedBy("name");
//			
//			
			

			// do something
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
