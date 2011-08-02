/* Date:        February 23, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.background;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.vo.BackgroundSummaryVO;


public class Background extends EasyPluginController<BackgroundModel>
{

	private static final long serialVersionUID = -5551425852519199587L;
	private MutationService mutationService;
	private PatientService patientService;
	private BackgroundSummaryVO backgroundSummaryVO;

	public Background(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new BackgroundModel(this));
		this.setView(new FreemarkerView("Background.ftl", getModel()));
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			this.mutationService = MutationService.getInstance(db);
			this.patientService  = PatientService.getInstance(db);
			this.getModel().setNumMutations(this.mutationService.getNumMutations());
			this.getModel().setNumPatients(this.patientService.getNumPatients());
			this.getModel().setNumPatientsUnpub(this.patientService.getNumUnpublishedPatients());
			this.getModel().setPhenotypeCountHash(this.patientService.getPhenotypeCounts());
		}
		catch(Exception e)
		{
			//...
		}
	}

	public BackgroundSummaryVO getBackgroundSummaryVO()
	{
		return this.backgroundSummaryVO;
	}
}
