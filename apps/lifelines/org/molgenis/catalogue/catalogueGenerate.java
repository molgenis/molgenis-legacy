package org.molgenis.catalogue;


import org.molgenis.Molgenis;


public class catalogueGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/lifelines/org/molgenis/catalogue/catalogue.molgenis.properties").generate();
	}
}
