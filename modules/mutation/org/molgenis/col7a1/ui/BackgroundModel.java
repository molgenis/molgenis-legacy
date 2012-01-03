/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.col7a1.ui;

import java.util.HashMap;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.col7a1.ui.Background;

/**
 * BackgroundModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class BackgroundModel extends EasyPluginModel
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;

	private int numMutations;
	private int numMutationsUnpub;
	private int numPatients;
	private int numPatientsUnpub;

	private HashMap<String, Integer> phenotypeCountHash;

	public int getNumMutations() {
		return numMutations;
	}
	public void setNumMutations(int numMutations) {
		this.numMutations = numMutations;
	}
	public int getNumMutationsUnpub() {
		return numMutationsUnpub;
	}
	public void setNumMutationsUnpub(int numMutationsUnpub) {
		this.numMutationsUnpub = numMutationsUnpub;
	}
	public int getNumPatients() {
		return numPatients;
	}
	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}
	public int getNumPatientsUnpub() {
		return numPatientsUnpub;
	}
	public HashMap<String, Integer> getPhenotypeCountHash() {
		return phenotypeCountHash;
	}
	public void setNumPatientsUnpub(int numPatientsUnpub) {
		this.numPatientsUnpub = numPatientsUnpub;
	}
	public Integer getPhenotypeCount(String phenotypeName) {
		if (this.phenotypeCountHash.containsKey(phenotypeName))
			return this.phenotypeCountHash.get(phenotypeName);
		else
			return 0;
	}
	public void setPhenotypeCountHash(HashMap<String, Integer> phenotypeCountHash) {
		this.phenotypeCountHash = phenotypeCountHash;
	}

	public BackgroundModel(Background controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
}
