package org.molgenis.CardiovascularDisease;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class CardiovascularDiseaseGenerate {
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger("CardiovascularDiseaseGenerate");
		new Molgenis("handwritten/apps/org/molgenis/CardiovascularDisease/CardiovascularDisease.properties").generate();
		logger.info("LifelinesPheno generated successfully!");
	}
}
