package org.molgenis.generators.ui;

import org.apache.log4j.Logger;
import org.molgenis.generators.ForEachEntityGenerator;

public class HtmlFormGen extends ForEachEntityGenerator
{
	private static final Logger logger = Logger.getLogger(HtmlFormGen.class);

	@Override
	public String getDescription()
	{
		return "Generates html forms for each entity.";
	}

	@Override
	public String getType()
	{
		return "Form";
	}
}
