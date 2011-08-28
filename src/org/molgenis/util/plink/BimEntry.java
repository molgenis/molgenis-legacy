package org.molgenis.util.plink;

public class BimEntry
{
	private String chromosome;
	private String SNP;
	private Double cM;
	private Long bpPos;
	private Character allele1;
	private Character allele2;
	
	public BimEntry(String chromosome, String SNP, Double cM, Long bpPos, Character allele1, Character allele2)
	{
		this.chromosome = chromosome;
		this.SNP = SNP;
		this.cM = cM;
		this.bpPos = bpPos;
		this.allele1 = allele1;
		this.allele2 = allele2;
	}
	
	public String getChromosome()
	{
		return chromosome;
	}
	public String getSNP()
	{
		return SNP;
	}
	public Double getcM()
	{
		return cM;
	}
	public Long getBpPos()
	{
		return bpPos;
	}
	public Character getAllele1()
	{
		return allele1;
	}
	public Character getAllele2()
	{
		return allele2;
	}
	
	
}
