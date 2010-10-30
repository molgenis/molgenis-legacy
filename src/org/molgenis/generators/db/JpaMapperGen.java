package org.molgenis.generators.db;

import org.apache.log4j.Logger;
import org.molgenis.generators.ForEachEntityGenerator;

public class JpaMapperGen extends ForEachEntityGenerator
{
	public static final transient Logger logger = Logger.getLogger(JpaMapperGen.class);

	
	@Override
	public String getDescription()
	{
		return "Generates database mappers for each entity using JPA.";
	}
	
	
	@Override
	public String getType()
	{
		return "Mapper";
	}

}
