package org.molgenis.generators.db;


import org.apache.log4j.Logger;
import org.molgenis.generators.ForEachEntityGenerator;
import org.molgenis.generators.ui.PluginControllerGen;


public class JpaEntityListenerGen extends ForEachEntityGenerator
{
	public static final transient Logger logger = Logger.getLogger(PluginControllerGen.class);

	public JpaEntityListenerGen() {
		super(true); //include abstract entities
	}
	
	@Override
	public String getDescription()
	{
		return "Generates Entity Listener Skeleton for JPA";
	}
	
	
	@Override
	public String getType()
	{
		return "EntityListener";
	}
}