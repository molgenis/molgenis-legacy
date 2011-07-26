package org.molgenis.compute;


import org.molgenis.Molgenis;

public class ComputeUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("modules/compute/org/molgenis/compute/compute.properties").updateDb();
	}
}