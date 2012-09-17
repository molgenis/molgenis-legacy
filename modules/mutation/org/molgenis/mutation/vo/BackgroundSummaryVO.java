package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.HashMap;

public class BackgroundSummaryVO implements Serializable
{
	private static final long serialVersionUID = -3470506757761180549L;
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
	public void setNumPatientsUnpub(int numPatientsUnpub) {
		this.numPatientsUnpub = numPatientsUnpub;
	}
	public Integer getPhenotypeCount(String phenotypeName) {
		return this.phenotypeCountHash.get(phenotypeName);
	}
	public void setPhenotypeCountHash(HashMap<String, Integer> phenotypeCountHash) {
		this.phenotypeCountHash = phenotypeCountHash;
	}
}
