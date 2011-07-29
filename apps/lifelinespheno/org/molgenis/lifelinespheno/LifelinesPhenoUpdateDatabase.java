package org.molgenis.lifelinespheno;

import org.molgenis.Molgenis;

public class LifelinesPhenoUpdateDatabase {
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/lifelinespheno/org/molgenis/lifelinespheno/LifelinesPheno.properties").updateDb(true);
	}
}
