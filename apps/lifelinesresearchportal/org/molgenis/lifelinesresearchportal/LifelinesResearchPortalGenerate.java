package org.molgenis.lifelinesresearchportal;

import org.molgenis.Molgenis;

public class LifelinesResearchPortalGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis(
					"apps/lifelinesresearchportal/org/molgenis/lifelinesresearchportal/lifelinesresearchportal.properties")
					.generate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
