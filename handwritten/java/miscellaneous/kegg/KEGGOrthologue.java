package miscellaneous.kegg;

import keggapi.SSDBRelation;

public class KEGGOrthologue
{
	String sourceEntry;
	String targetEntry;
	SSDBRelation SSDBRelation = new SSDBRelation();
	
	
	public String getSourceEntry()
	{
		return sourceEntry;
	}
	public void setSourceEntry(String sourceEntry)
	{
		this.sourceEntry = sourceEntry;
	}
	public String getTargetEntry()
	{
		return targetEntry;
	}
	public void setTargetEntry(String targetEntry)
	{
		this.targetEntry = targetEntry;
	}
	public SSDBRelation getSSDBRelation()
	{
		return SSDBRelation;
	}
	public void setSSDBRelation(SSDBRelation relation)
	{
		SSDBRelation = relation;
	}
	
	
}
