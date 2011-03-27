package org.molgenis.pheno;


import org.molgenis.Molgenis;

public class PhenoGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/pheno/pheno.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
