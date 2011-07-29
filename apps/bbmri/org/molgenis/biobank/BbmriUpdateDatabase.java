package org.molgenis.biobank;


import org.molgenis.Molgenis;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;

import app.DatabaseFactory;


public class BbmriUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties").updateDb(true);
		
		Database db = DatabaseFactory.create("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties");
		
		MolgenisUser u = new MolgenisUser();
		u.setName("bbmri");
		u.setPassword("bbmri");
		u.setSuperuser(true);
		u.setFirstname("Margreet");
		u.setLastname(" Brandsma");
		u.setEmailaddress("m.brandsma@bbmri.nl");
		
		db.add(u);
		
		//TODO : do batch import
	}
}
