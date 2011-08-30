package org.molgenis.util.plink.datatypes;

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

}
