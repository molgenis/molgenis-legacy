package org.molgenis.gscf;


import org.molgenis.Molgenis;

public class GscfDBUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("gscf.molgenis.properties").updateDb();
	}
}
