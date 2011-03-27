package org.molgenis.ngs;


import org.molgenis.Molgenis;


public class NGSGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/ngs/ngs.properties").generate();
	}
}