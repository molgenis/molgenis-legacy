package org.molgenis.util.plink;

public class FamEntry
{
	Integer family;
	Integer individual;
	Integer father;
	Integer mother;
	Byte sex;
	Double phenotype;

	public FamEntry(Integer family, Integer individual, Integer father,
			Integer mother, Byte sex, Double phenotype)
	{
		this.family = family;
		this.individual = individual;
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.phenotype = phenotype;
	}

	public Integer getFamily()
	{
		return family;
	}

	public Integer getIndividual()
	{
		return individual;
	}

	public Integer getFather()
	{
		return father;
	}

	public Integer getMother()
	{
		return mother;
	}

	public Byte getSex()
	{
		return sex;
	}

	public Double getPhenotype()
	{
		return phenotype;
	}
	
}
