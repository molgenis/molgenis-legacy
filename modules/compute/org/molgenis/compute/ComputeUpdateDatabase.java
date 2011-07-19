package org.molgenis.compute;


import org.molgenis.Molgenis;

public class ComputeUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/compute/compute.properties").updateDb();
	}
}