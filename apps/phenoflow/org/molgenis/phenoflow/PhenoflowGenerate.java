package org.molgenis.phenoflow;


import org.molgenis.Molgenis;


public class PhenoflowGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/phenoflow/phenoflow.properties").generate();
	}
}
