package org.molgenis.generators;


public class JpaDataTypeGen extends ForEachEntityGenerator
{
	public JpaDataTypeGen()
	{
		//include abstract entities
		super(true);
	}
	
	@Override
	public String getDescription()
	{
		return "Generates classes for each entity (simple 'bean's or 'pojo's).";
	}
	
	public String getType()
	{
		return "";		
	}
}
