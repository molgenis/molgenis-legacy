/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.chd7.ui;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.chd7.ui.Background;

/**
 * BackgroundModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class BackgroundModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;
	
	private int numPathogenicMutations;
	private int numPathogenicPatients;
	private int numUnclassifiedMutations;
	private int numUnclassifiedPatients;
	private int numBenignMutations;
	private int numPatientsUnpub;

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

	public int getNumPatientsUnpub() {
		return numPatientsUnpub;
	}
	public void setNumPatientsUnpub(int numPatientsUnpub) {
		this.numPatientsUnpub = numPatientsUnpub;
	}
	public BackgroundModel(Background controller)
	{
		super(controller);
	}
}
