package org.molgenis.util.plink.datatypes;

import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class FamEntry
{
	//see: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml
	String family;
	String individual;
	String father;
	String mother;
	byte sex;
	double phenotype;
	
	public FamEntry(String family, String individual, String father,
			String mother, byte sex, double phenotype)
	{
		super();
		this.family = family;
		this.individual = individual;
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.phenotype = phenotype;
	}
	
	public static String[] famHeader(){
		return new String[]{"fam", "ind", "fa", "mo", "sex", "phen"};
	}
	
	public static Tuple famToTuple(FamEntry fam){
		Tuple t = new SimpleTuple();
		t.set("fam", fam.getFamily());
		t.set("ind", fam.getIndividual());
		t.set("fa", fam.getFather());
		t.set("mo", fam.getMother());
		t.set("sex", fam.getSex());
		t.set("phen", fam.getPhenotype());
		return t;
	}
	
	public String getFamily()
	{
		return family;
	}
	public String getIndividual()
	{
		return individual;
	}
	public String getFather()
	{
		return father;
	}
	public String getMother()
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
