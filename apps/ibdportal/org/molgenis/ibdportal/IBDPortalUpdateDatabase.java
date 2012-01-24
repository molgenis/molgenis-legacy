package org.molgenis.ibdportal;


import org.molgenis.Molgenis;
import org.molgenis.framework.db.jpa.JpaUtil;

/**
 * Updates the IBD Portal database, clearing existing tables but not removing obsolete ones.
 * Also adds security items needed for first login.
 * 
 * @author erikroos
 *
 */
public class IBDPortalUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/ibdportal/org/molgenis/ibdportal/ibdportal.properties").updateDb(true);
		// TODO: make something like the method above in JPA that can run the generated_metadata.sql
	}
}
