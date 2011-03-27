package org.molgenis.animaldb;


import org.molgenis.Molgenis;

import plugins.fillanimaldb.FillAnimalDB;

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
