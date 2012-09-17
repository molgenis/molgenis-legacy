package org.molgenis.myo5b;
import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class Myo5bGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/myo5b/org/molgenis/myo5b/myo5b.properties").generate();
	}
}
