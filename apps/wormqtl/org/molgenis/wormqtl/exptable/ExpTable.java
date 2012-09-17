/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.wormqtl.exptable;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Shows table of experiment information for WormQTL
 */
public class ExpTable extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private ExpTableModel model = new ExpTableModel();
	
	public ExpTableModel getMyModel()
	{
		return model;
	}

	public ExpTable(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "ExpTable";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/wormqtl/exptable/ExpTable.ftl";
	}
	
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}
	
	
	
	@Override
	public void reload(Database db)
	{

		

	}

}
