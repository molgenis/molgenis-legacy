package org.molgenis.lifelines;


import org.molgenis.Molgenis;

import app.JDBCDatabase;


public class lifelinesUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/lifelines/lifelines.molgenis.properties").updateDb(true);
		
		//loader
		//TODO: Danny old code ? please remove then
		/*Database db =*/new JDBCDatabase("handwritten/apps/org/molgenis/lifelines/lifelines.molgenis.properties");
		
		//MolgenisUser u = new MolgenisUser();
		//u.setName("admin");
		//u.setPassword("admin");
		//u.setSuperuser(true);
		//u.setFirstname("Despoina");
		//u.setLastname("Antonakaki");
		//u.setEmailaddress("antonakd@gmail.com");
		
		//db.add(u);
		
		//do batch import
	}
}
