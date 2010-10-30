package org.molgenis.generators.db;

import org.molgenis.generators.ForEachViewGenerator;

public class ViewMapperGen extends ForEachViewGenerator
{
	@Override
	public String getType()
	{
		return "Mapper";
	}

	@Override
	public String getDescription()
	{
		return "generate the mappers for each database view";
	}

}
