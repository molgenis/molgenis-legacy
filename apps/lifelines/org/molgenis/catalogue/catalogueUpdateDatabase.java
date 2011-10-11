package org.molgenis.catalogue;


import org.molgenis.Molgenis;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.SimpleLogin;

import app.DatabaseFactory;


public class catalogueUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/lifelines/org/molgenis/catalogue/catalogue.molgenis.properties").updateDb(true);
		
		Database db = DatabaseFactory.create("apps/lifelines/org/molgenis/catalogue/catalogue.molgenis.properties");
		
		// Only add  user if type of Login allows for this
		if (!(db.getSecurity() instanceof SimpleLogin)) {
			MolgenisUser u = new MolgenisUser();
			u.setName("biobank");
			u.setPassword("biobank");
			u.setSuperuser(true);
			u.setFirstname("Despoina");
			u.setLastname("Antonakaki");
			u.setEmailaddress("despoina.antonakaki@gmail.com");
			db.add(u);
		}
		
		//TODO : do batch import
	}
}
