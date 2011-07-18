package org.molgenis.animaldb;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class AnimalDBGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("AnimalDBGenerate");
		new Molgenis("handwritten/apps/org/molgenis/animaldb/animaldb.properties").generate();
		logger.info("AnimalDB generated successfully!");
	}
}
