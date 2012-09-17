package org.molgenis.pheno;


import org.molgenis.Molgenis;

public class PhenoUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/pheno/org/molgenis/pheno/pheno.properties").updateDb();
	}
}
