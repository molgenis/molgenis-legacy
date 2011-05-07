package org.molgenis.gids;

import org.molgenis.Molgenis;

public class GidsUpdateDatabase {
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/gids/gids.properties").updateDb(true);
	}
}
