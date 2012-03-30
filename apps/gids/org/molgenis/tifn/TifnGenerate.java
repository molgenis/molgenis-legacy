package org.molgenis.tifn;
import org.apache.log4j.Logger;
import org.molgenis.Molgenis;


public class TifnGenerate {
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("TifnGenerate");
		new Molgenis("apps/tifn/org/molgenis/tifn/tifn.properties").generate();
		logger.info("GIDS generated successfully!");
	}
}
