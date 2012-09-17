package org.molgenis.compute;


import org.molgenis.Molgenis;


public class ComputeGenerate
{
	public static void main(String[] args) throws Exception
	{
		Molgenis m = new Molgenis("apps/compute/org/molgenis/compute/compute.properties");
//		m.getGenerators().add(new ComputeContextListenerGen());
		
		m.generate();
	}
}