package org.molgenis.Catalogue;


import org.molgenis.Molgenis;


public class CatalogueGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/lifelines/org/molgenis/Catalogue/Catalogue.molgenis.properties").generate();
	}
}
