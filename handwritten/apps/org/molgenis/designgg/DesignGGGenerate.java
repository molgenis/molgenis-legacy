package org.molgenis.designgg;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;
import org.molgenis.generators.server.MolgenisServletGen;
import org.molgenis.generators.ui.PluginScreenGen;

public class DesignGGGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("DesignGG generate");
		new Molgenis("handwritten/apps/org/molgenis/designgg/designgg.properties").generate();
		logger.info("DesignGG generated successfully!");
	}
}
