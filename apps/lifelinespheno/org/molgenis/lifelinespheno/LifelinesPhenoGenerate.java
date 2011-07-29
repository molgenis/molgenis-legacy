package org.molgenis.lifelinespheno;
import org.apache.log4j.Logger;
import org.molgenis.Molgenis;


public class LifelinesPhenoGenerate {
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("LifelinesPhenoGenerate");
		new Molgenis("apps/lifelinespheno/org/molgenis/lifelinespheno/LifelinesPheno.properties").generate();
		logger.info("LifelinesPheno generated successfully!");
	}
}
