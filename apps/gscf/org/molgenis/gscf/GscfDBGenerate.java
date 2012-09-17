package org.molgenis.gscf;


import org.molgenis.Molgenis;


public class GscfDBGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/gscf/gscf.properties").generate();
	}
}
