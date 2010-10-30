package org.molgenis.generators.ui;

import org.molgenis.generators.ForEachEntityGenerator;

public class HtmlFormGen extends ForEachEntityGenerator
{
	public HtmlFormGen()
	{
		//include abstract entities
		super(true);
	}
	
	@Override
	public String getDescription()
	{
		return "Generates html form class for each entity.";
	}
	
	public String getType()
	{
		return "HtmlForm";		
	}
	
	public Boolean skipSystem()
	{
		return false;
	}
}
