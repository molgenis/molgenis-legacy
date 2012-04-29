package plinkbintocsv.sources;

import tritoplinkslice.sources.Biallele;
import tritoplinkslice.sources.MapEntry;
import csvtobin.sources.SimpleTuple;
import csvtobin.sources.Tuple;

public class BimEntry extends MapEntry
{

	private Biallele biallele;
	
	public BimEntry(String chromosome, String SNP, double cM, long bpPos,
			Biallele biallele)
	{
		super(chromosome, SNP, cM, bpPos);
		this.biallele = biallele;
	}

	public Biallele getBiallele()
	{
		return biallele;
	}
	
	public static String[] bimHeader(){
		return new String[]{"chr", "snp", "cm", "bp", "al1", "al2"};
	}
	
	public static Tuple bimToTuple(BimEntry bim){
		Tuple t = new SimpleTuple();
		t.set("chr", bim.getChromosome());
		t.set("snp", bim.getSNP());
		t.set("cm", bim.getcM());
		t.set("bp", bim.getBpPos());
		t.set("al1", bim.getBiallele().getAllele1());
		t.set("al2", bim.getBiallele().getAllele2());
		return t;
	}

}
