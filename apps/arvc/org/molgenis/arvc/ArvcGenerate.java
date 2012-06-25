package org.molgenis.arvc;

import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class ArvcGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/arvc/org/molgenis/arvc/arvc.properties").generate();
	}
}
