package org.molgenis.examples;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class ExamplesGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("ExamplesGenerate");
		new Molgenis("apps/examples/org/molgenis/examples/examples.properties").generate();
		logger.info("Examples generated successfully!");
	}
}
