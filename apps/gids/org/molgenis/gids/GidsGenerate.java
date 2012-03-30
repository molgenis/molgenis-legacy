package org.molgenis.gids;
import org.apache.log4j.Logger;
import org.molgenis.Molgenis;


public class GidsGenerate {
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("GidsGenerate");
		new Molgenis("apps/gids/org/molgenis/gids/gids.properties").generate();
		logger.info("GIDS generated successfully!");
	}
}
