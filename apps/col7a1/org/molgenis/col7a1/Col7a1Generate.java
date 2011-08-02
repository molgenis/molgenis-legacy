package org.molgenis.col7a1;
import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class Col7a1Generate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/col7a1/org/molgenis/col7a1/col7a1.properties").generate();
	}
}
