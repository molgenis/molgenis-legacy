package org.molgenis.scrum;
import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class ScrumGenerate
{
	public static void main(String[] args) throws Exception
	{
		String propertiesFile = "org/molgenis/scrum/scrum.properties";
		new Molgenis(propertiesFile).generate();
	}
}
