/* Date:        February 23, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.col7a1.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.service.StatisticsService;

/**
 * Background page specific to col7a1
 */
public class Background extends EasyPluginController<BackgroundModel>
{

	private static final long serialVersionUID = -5551425852519199587L;

	public Background(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new BackgroundModel(this));
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("Background.ftl", getModel());
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			StatisticsService statisticsService = ServiceLocator.instance().getStatisticsService();
			this.getModel().setNumMutations(statisticsService.getNumMutations());
			this.getModel().setNumPatients(statisticsService.getNumPatients());
			this.getModel().setNumPatientsUnpub(statisticsService.getNumUnpublishedPatients());
			this.getModel().setPhenotypeCountHash(statisticsService.getPhenotypeCounts());
		}
		catch(Exception e)
		{
			//...
		}
	}
}
