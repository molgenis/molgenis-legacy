package org.molgenis.CardiovascularDisease;

import org.molgenis.Molgenis;

public class CardiovascularDiseaseUpdateDatabase {
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/CardiovascularDisease/CardiovascularDisease.properties").updateDb(true);
	}
}
