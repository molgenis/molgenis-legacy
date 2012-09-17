package org.molgenis.lifelinesresearchportal;

import org.molgenis.Molgenis;

public class LifelinesResearchPortalUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis(
				"apps/lifelinesresearchportal/org/molgenis/lifelinesresearchportal/lifelinesresearchportal.properties")
				.updateDb(true);
	}
}
