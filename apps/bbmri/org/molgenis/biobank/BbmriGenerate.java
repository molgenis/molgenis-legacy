package org.molgenis.biobank;


import org.molgenis.Molgenis;


public class BbmriGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties").generate();
	}
}
