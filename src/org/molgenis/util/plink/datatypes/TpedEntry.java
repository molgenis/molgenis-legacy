package org.molgenis.util.plink.datatypes;

import java.util.List;

import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class TpedEntry extends MapEntry
{

	//list iterates individuals, so 1 list per SNP
	//NOTE: this is the inverse of PED format!
	List<Biallele> bialleles;
	
	public TpedEntry(String chromosome, String SNP, double cM, long bpPos, List<Biallele> bialleles)
	{
		super(chromosome, SNP, cM, bpPos);
		this.bialleles = bialleles;
	}

	public static String[] tpedHeader(){
		return new String[]{"chr", "snp", "cm", "bp", "bial"};
	}
	
	public static Tuple tpedToTuple(TpedEntry tped){
		Tuple t = new SimpleTuple();
		t.set("chr", tped.getChromosome());
		t.set("snp", tped.getSNP());
		t.set("cm", tped.getcM());
		t.set("bp", tped.getBpPos());
		t.set("bial", tped.getBialleles());
		return t;
	}

	public List<Biallele> getBialleles()
	{
		return bialleles;
	}


	
	
}
