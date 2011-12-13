package org.molgenis.util.plink.datatypes;

import java.util.List;

import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

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
	
	public static String[] pedHeader(){
		return new String[]{"fam", "ind", "fa", "mo", "sex", "phen", "bial"};
	}
	
	public static Tuple pedToTuple(PedEntry ped){
		Tuple t = new SimpleTuple();
		t.set("fam", ped.getFamily());
		t.set("ind", ped.getIndividual());
		t.set("fa", ped.getFather());
		t.set("mo", ped.getMother());
		t.set("sex", ped.getSex());
		t.set("phen", ped.getPhenotype());
		t.set("bial", ped.getBialleles());
		return t;
	}
}
