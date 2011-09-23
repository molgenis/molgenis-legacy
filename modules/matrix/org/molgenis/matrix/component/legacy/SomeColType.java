package org.molgenis.matrix.component.legacy;

public class SomeColType
{
	Integer id;
	String commonName;
	String ncbiId;
	String keggId;
	Integer bpPos;
	String chromosome;
	
	public SomeColType(Integer id, String commonName, String ncbiId, String keggId, Integer bpPos, String chromosome)
	{
		this.id = id;
		this.commonName = commonName;
		this.ncbiId = ncbiId;
		this.keggId = keggId;
		this.bpPos = bpPos;
		this.chromosome = chromosome;
	}

	public Integer getId()
	{
		return id;
	}

	public String getCommonName()
	{
		return commonName;
	}

	public String getNcbiId()
	{
		return ncbiId;
	}

	public String getKeggId()
	{
		return keggId;
	}

	public Integer getBpPos()
	{
		return bpPos;
	}

	public String getChromosome()
	{
		return chromosome;
	}
	
}
