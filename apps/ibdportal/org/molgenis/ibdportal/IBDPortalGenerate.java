package org.molgenis.ibdportal;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class IBDPortalGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("IBDPortalGenerate");
		new Molgenis("apps/ibdportal/org/molgenis/ibdportal/ibdportal.properties").generate();
		logger.info("IBD Portal generated successfully!");
	}
}
