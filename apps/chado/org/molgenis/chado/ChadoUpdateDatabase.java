package org.molgenis.chado;

import org.molgenis.Molgenis;

public class ChadoUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/org/molgenis/chado/chado.properties").updateDb(true);
	}
}
