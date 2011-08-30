package org.molgenis.util.plink.datatypes;

import java.util.List;

public class PedEntry extends FamEntry
{

	List<Biallele> bialleles;
	
	public PedEntry(int family, int individual, int father, int mother,
			byte sex, double phenotype, List<Biallele> bialleles)
	{
		super(family, individual, father, mother, sex, phenotype);
		this.bialleles = bialleles;
	}

	public List<Biallele> getBialleles()
	{
		return bialleles;
	}
	
}
