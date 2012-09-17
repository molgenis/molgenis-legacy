package org.molgenis.pheno;


import org.molgenis.Molgenis;

public class PhenoGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("apps/pheno/org/molgenis/pheno/pheno.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
