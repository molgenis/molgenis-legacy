/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.background;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;

public class Chd7Background extends Background
{
	private static final long serialVersionUID = 1L;
	private int numPathogenicMutations;
	private int numPathogenicPatients;
	private int numUnclassifiedMutations;
	private int numUnclassifiedPatients;
	private int numBenignMutations;

	public Chd7Background(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new BackgroundModel(this));
		this.setView(new FreemarkerView("Chd7Background.ftl", getModel()));
	}
	
	@Override
	public void reload(Database db)
	{
		try
		{
			MutationService mutationService = MutationService.getInstance(db);
			PatientService patientService   = PatientService.getInstance(db);
			
			this.numPathogenicMutations     = mutationService.getNumMutationsByPathogenicity("pathogenic");
			this.numPathogenicPatients      = patientService.getNumPatientsByPathogenicity("pathogenic");
			this.numUnclassifiedMutations   = mutationService.getNumMutationsByPathogenicity("unclassified variant");
			this.numUnclassifiedPatients    = patientService.getNumPatientsByPathogenicity("unclassified variant");
			this.numBenignMutations         = mutationService.getNumMutationsByPathogenicity("benign");
		}
		catch (Exception e)
		{
			
		}
	}

	public int getNumPathogenicMutations() {
		return numPathogenicMutations;
	}

	public void setNumPathogenicMutations(int numPathogenicMutations) {
		this.numPathogenicMutations = numPathogenicMutations;
	}

	public int getNumPathogenicPatients() {
		return numPathogenicPatients;
	}

	public void setNumPathogenicPatients(int numPathogenicPatients) {
		this.numPathogenicPatients = numPathogenicPatients;
	}

	public int getNumUnclassifiedMutations() {
		return numUnclassifiedMutations;
	}

	public void setNumUnclassifiedMutations(int numUnclassifiedMutations) {
		this.numUnclassifiedMutations = numUnclassifiedMutations;
	}

	public int getNumUnclassifiedPatients() {
		return numUnclassifiedPatients;
	}

	public void setNumUnclassifiedPatients(int numUnclassifiedPatients) {
		this.numUnclassifiedPatients = numUnclassifiedPatients;
	}

	public int getNumBenignMutations() {
		return numBenignMutations;
	}

	public void setNumBenignMutations(int numBenignMutations) {
		this.numBenignMutations = numBenignMutations;
	}
}
