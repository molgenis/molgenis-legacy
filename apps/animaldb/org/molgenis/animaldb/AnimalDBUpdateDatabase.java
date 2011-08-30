package org.molgenis.animaldb;


import org.molgenis.Molgenis;
import org.molgenis.framework.db.jpa.JpaUtil;

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
		new Molgenis("apps/animaldb/org/molgenis/animaldb/animaldb.properties").updateDb(true);
		// TODO: make something like the method above in JPA that can run the generated_metadata.sql
	}
}

/*
public class AnimalDBUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/animaldb/org/molgenis/animaldb/animaldb.properties").updateDb();
		FillMetadata.fillMetadata(DatabaseFactory.create());
		// TODO: make something like the method above in JPA that can run the generated_metadata.sql
	}
}
*/