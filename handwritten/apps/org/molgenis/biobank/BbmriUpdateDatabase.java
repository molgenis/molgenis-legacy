package org.molgenis.biobank;


import org.molgenis.Molgenis;
import app.JDBCDatabase;


public class BbmriUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/biobank/bbmri.molgenis.properties").updateDb(true);
		
		//loader
		//TODO: Danny old code ? please remove then
		/*Database db = */new JDBCDatabase("handwritten/apps/org/molgenis/biobank/bbmri.molgenis.properties");
		
//		MolgenisUser u = new MolgenisUser();
//		u.setName("admin");
//		u.setPassword("admin");
//		u.setSuperuser(true);
//		u.setFirstname("Despoina");
//		u.setLastname("Antonakaki");
//		u.setEmailaddress("antonakd@gmail.com");
//		
//		db.add(u);
		
		//do batch import
	}
}
