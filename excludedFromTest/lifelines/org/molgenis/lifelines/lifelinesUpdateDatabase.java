package org.molgenis.lifelines;


import org.molgenis.Molgenis;

public class lifelinesUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/lifelines/lifelines.molgenis.properties").updateDb(true);
	}
}
