/* Date:        March 1, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.batch.ui;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class AnimalDBHeader extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1489108528638869730L;

	public AnimalDBHeader(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_batch_ui_AnimalDBHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/batch/ui/AnimalDBHeader.ftl";
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reload(Database db) {
		// TODO Auto-generated method stub
		
	}
}
