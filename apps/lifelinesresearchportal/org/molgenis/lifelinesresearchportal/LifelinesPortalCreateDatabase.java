package org.molgenis.lifelinesresearchportal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

import org.molgenis.framework.db.Database;

import app.DatabaseFactory;
import app.FillMetadata;

public class LifelinesPortalCreateDatabase {
	public static void main(String[] args) throws Exception {
		
		Map<String, Object> configOverrides = new HashMap<String, Object>();
		configOverrides.put("hibernate.hbm2ddl.auto", "create-drop");
		Persistence.createEntityManagerFactory("molgenis", configOverrides);
		
		Database db = DatabaseFactory.create();		
		FillMetadata.fillMetadata(db, false);
		db.close();
	}
}
