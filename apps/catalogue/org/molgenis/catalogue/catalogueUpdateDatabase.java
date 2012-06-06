package org.molgenis.catalogue;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.molgenis.Molgenis;
import org.molgenis.auth.MolgenisGroup;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisRoleGroupLink;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.Person;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.SimpleLogin;
import org.molgenis.pheno.Measurement;

import app.DatabaseFactory;
import app.FillMetadata;


public class catalogueUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		//PropertyConfigurator.configure("apps/catalogue/org/molgenis/catalogue/catalogue_log4j.properties");
		new Molgenis("apps/catalogue/org/molgenis/catalogue/catalogue.molgenis.properties").updateDb(true);
		
//		final Map<String, Object> config = new HashMap<String, Object>();
//		config.put("hibernate.hbm2ddl.auto", "create-drop");
//		Database db = DatabaseFactory.create(config);
//		
//		FillMetadata.fillMetadata(db, false);
//		
		Database db = DatabaseFactory.create("apps/catalogue/org/molgenis/catalogue/catalogue.molgenis.properties");
		
		// Only add  user if type of Login allows for this
//		if (!(db.getLogin() instanceof SimpleLogin)) {
//			MolgenisUser u = new MolgenisUser();
//			u.setName("biobank");
//			u.setPassword("biobank");
//			u.setSuperuser(true);
//			db.add(u);
//			Person p = new Person();
//			p.setFirstName("Despoina");
//			p.setLastName("Antonakaki");
//			p.setEmail("despoina.antonakaki@gmail.com");
//			db.add(p);
//		}
		
		MolgenisUser demo = new MolgenisUser();
		demo.setName("demo");
		demo.setPassword("d3m0c@t@logue");
		demo.setActive(true);
		db.add(demo);
		
		//Create a group :SimpleUsers
		MolgenisGroup mg = new MolgenisGroup();
		mg.setName("SimpleUsers");
		db.add(mg);
		
		//add demo to group Simple Users
		MolgenisRoleGroupLink mrgl = new MolgenisRoleGroupLink();
		mrgl.setRole_Name("demo");
		mrgl.setGroup_Name("SimpleUsers");
		db.add(mrgl);
		
		//TODO: add anonymous user in Simple users 
		mrgl = new MolgenisRoleGroupLink();
		mrgl = db.find(MolgenisRoleGroupLink.class, new QueryRule(MolgenisRoleGroupLink.ROLE__NAME, Operator.EQUALS, "anonymous")).get(0);
		mrgl.setGroup_Name("SimpleUsers");
		System.out.println(mrgl);
		db.update(mrgl);
		
		
		//Now set permission for the demo account 
		for (int i=2; i<95; i++) {
			MolgenisPermission mp = new MolgenisPermission();
			mp.setRole_Name("SimpleUsers");
			mp.setEntity_Id(i);
			mp.setPermission("own");
			db.add(mp);
			System.out.println("Inserting molgenis permisssion for :" + i);
		}
		
		//except from : admin
		MolgenisPermission mp = new MolgenisPermission();
		System.out.println(db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.AdminMenu")));
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.AdminMenu")).get(0);
		db.remove(mp);

		//requests form controller 
		mp = new MolgenisPermission();
		System.out.println(mp);
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.RequestsFormController")).get(0);
		System.out.println(mp);

		db.remove(mp);
		
		//import data menu  
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.ImportDataMenu")).get(0);
		db.remove(mp);
		
		//BiobankImporterPlugin  
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.BiobankImporterPlugin")).get(0);
		db.remove(mp);
	
		//generic wizard 	
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.GenericWizardPlugin")).get(0);
		db.remove(mp);
		
		//settings 	
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.SettingsPlugin")).get(0);
		db.remove(mp);
		
		//usermanagement 
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.usermanagementMenu")).get(0);
		db.remove(mp);
		
		
		//app.ui.MolgenisUserFormController 
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.MolgenisUserFormController")).get(0);
		db.remove(mp);
		
		//MolgenisGroup 
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.MolgenisGroupFormController")).get(0);
		db.remove(mp);
		
		//app.ui.MappingsMenu 
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.MappingsMenu")).get(0);
		db.remove(mp);
		
		//app.ui.selectionModelMenu	
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.selectionModelMenu")).get(0);
		db.remove(mp);
		
		//PredictionModelSelection
		mp = new MolgenisPermission();
		mp = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "app.ui.PredictionModelSelectionPlugin")).get(0);
		db.remove(mp);
	}
}
