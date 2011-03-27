package org.molgenis.batchtest;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class BatchGenerate {
    
    public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("BatchGenerate");
		new Molgenis("handwritten/apps/org/molgenis/batchtest/batch.properties").generate();
		logger.info("Batch generated successfully!");
	}
}
