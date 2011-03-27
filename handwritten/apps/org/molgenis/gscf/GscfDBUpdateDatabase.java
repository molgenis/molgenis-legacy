package org.molgenis.gscf;


import org.molgenis.Molgenis;

import plugins.fillanimaldb.FillAnimalDB;

public class GscfDBUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("gscf.molgenis.properties").updateDb();
	}
}
