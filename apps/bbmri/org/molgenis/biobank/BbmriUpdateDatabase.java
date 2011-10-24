package org.molgenis.biobank;


import org.molgenis.Molgenis;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.SimpleLogin;

import app.DatabaseFactory;


public class BbmriUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties").updateDb(true);
		
		Database db = DatabaseFactory.create("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties");
		
		// Only add "Margreet Brandsma" user if type of Login allows for this
		if (!(db.getSecurity() instanceof SimpleLogin)) {
			MolgenisUser u = new MolgenisUser();
			u.setName("bbmri");
			u.setPassword("bbmri");
			u.setSuperuser(true);
			u.setFirstName("Margreet");
			u.setLastName("Brandsma");
			u.setEmail("m.brandsma@bbmri.nl");
			db.add(u);
		}
		
		//TODO : do batch import
	}
}
