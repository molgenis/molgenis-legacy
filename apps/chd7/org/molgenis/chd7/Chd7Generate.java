package org.molgenis.chd7;
import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class Chd7Generate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/chd7/org/molgenis/chd7/chd7.properties").generate();
	}
}
