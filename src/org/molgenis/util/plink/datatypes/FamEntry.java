package org.molgenis.util.plink.datatypes;

public class FamEntry
{
	int family;
	int individual;
	int father;
	int mother;
	byte sex;
	//"A PED file must have 1 and only 1 phenotype in the sixth column."
	//see: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml
	double phenotype;

	public FamEntry(int family, int individual, int father,
			int mother, byte sex, double phenotype)
	{
		this.family = family;
		this.individual = individual;
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.phenotype = phenotype;
	}

	public int getFamily()
	{
		return family;
	}

	public int getIndividual()
	{
		return individual;
	}

	public int getFather()
	{
		return father;
	}

	public int getMother()
	{
		return mother;
	}

	public byte getSex()
	{
		return sex;
	}

	public double getPhenotype()
	{
		return phenotype;
	}
	
}
