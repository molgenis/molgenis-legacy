package org.molgenis.designgg;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class DesignGGGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("DesignGG generate");
		new Molgenis("handwritten/apps/org/molgenis/designgg/designgg.properties").generate();
		logger.info("DesignGG generated successfully!");
	}
}
