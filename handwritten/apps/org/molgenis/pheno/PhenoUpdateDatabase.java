package org.molgenis.pheno;


import org.molgenis.Molgenis;

public class PhenoUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/pheno/pheno.properties").updateDb();
	}
}
