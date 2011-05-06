/*
 * Date: April 7, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.framework.db.paging.LimitOffsetPager;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ListView;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Tuple;

public class InvestigationPager extends PluginModel<Investigation>
{
	private static final long serialVersionUID = -986743840712674407L;

	// link to the backend
	DatabasePager<Investigation> pagerModel;

	// link to the frontend
	PagerHtmlInput pagerView;
	ListView listView; // = update during reload

	public InvestigationPager(String name, ScreenController<?> parent)
	{
		super(name, parent);

	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_sandbox_ui_InvestigationPager";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/sandbox/ui/InvestigationPager.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// replace example below with yours
		// try
		// {
		// //start database transaction
		// db.beginTx();
		//
		// //get the "__action" parameter from the UI
		// String action = request.getAction();
		//		
		// if( action.equals("do_add") )
		// {
		// Experiment e = new Experiment();
		// e.set(request);
		// db.add(e);
		// }
		//
		// //commit all database actions above
		// db.commitTx();
		//
		// } catch(Exception e)
		// {
		// db.rollbackTx();
		// //e.g. show a message in your form
		// }
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			if (pagerModel == null)
			{
				pagerModel = new LimitOffsetPager<Investigation>(Investigation.class,
						Investigation.NAME);
				pagerView = new PagerHtmlInput("pager", pagerModel);
			}
			//refresh count
			int count = pagerModel.getCount(db);
			System.out.println("count");

		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}

	public PagerHtmlInput getPagerView()
	{
		return pagerView;
	}

}
