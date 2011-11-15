package org.molgenis.chado;

import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class ChadoGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/chado/org/molgenis/chado/chado.properties").generate();
	}
}
