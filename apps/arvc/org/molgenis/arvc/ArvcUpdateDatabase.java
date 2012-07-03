package org.molgenis.arvc;

import org.molgenis.Molgenis;

public class ArvcUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/arvc/org/molgenis/arvc/arvc.properties").updateDb(true);
	}
}
