package tritoplinkslice.sources;

import csvtobin.sources.SimpleTuple;
import csvtobin.sources.Tuple;

/**
 * See: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#map
 *
 */
public class MapEntry
{
	private String chromosome;
	private String SNP;
	private double cM;
	private long bpPos;
	
	public MapEntry(String chromosome, String SNP, double cM, long bpPos)
	{
		this.chromosome = chromosome;
		this.SNP = SNP;
		this.cM = cM;
		this.bpPos = bpPos;
	}
	
	public static String[] mapHeader(){
		return new String[]{"chr", "snp", "cm", "bp"};
	}
	
	public static Tuple mapToTuple(MapEntry map){
		Tuple t = new SimpleTuple();
		t.set("chr", map.getChromosome());
		t.set("snp", map.getSNP());
		t.set("cm", map.getcM());
		t.set("bp", map.getBpPos());
		return t;
	}
	
	public String getChromosome()
	{
		return chromosome;
	}
	
	public String getSNP()
	{
		return SNP;
	}
	
	public double getcM()
	{
		return cM;
	}
	
	public long getBpPos()
	{
		return bpPos;
	}

}
