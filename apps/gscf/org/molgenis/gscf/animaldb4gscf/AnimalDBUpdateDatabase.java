package org.molgenis.gscf.animaldb4gscf;


import org.molgenis.Molgenis;

/**
 * Updates the AnimalDB database, clearing existing tables but not removing obsolete ones.
 * Also adds security items needed for first login.
 * Note: this class no longer pre-fills the db for you. Run "Load required data" from the UI instead.
 * 
 * @author erikroos
 *
 */
public class AnimalDBUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/animaldb/animaldb.properties").updateDb(true);
	}
}
