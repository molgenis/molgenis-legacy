package org.molgenis.util.plink.datatypes;

import java.util.List;

public class PedEntry extends FamEntry
{

	List<Biallele> bialleles;

	public PedEntry(String family, String individual, String father,
			String mother, byte sex, double phenotype, List<Biallele> bialleles)
	{
		super(family, individual, father, mother, sex, phenotype);
		this.bialleles = bialleles;
	}

	public List<Biallele> getBialleles()
	{
		return bialleles;
	}
	
}
