package org.molgenis.scrum;


import org.molgenis.Molgenis;
import org.molgenis.framework.db.jpa.JpaUtil;

/**
 * Updates the Scrum database, clearing existing tables but not removing obsolete ones.
 * Also adds security items needed for first login.
 * 
 * @author erikroos
 *
 */
public class ScrumUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/scrum/org/molgenis/scrum/scrum.properties").updateDb(true);
		// TODO: make something like the method above in JPA that can run the generated_metadata.sql
	}
}
