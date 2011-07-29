package org.molgenis.framework.db.jpa;

import org.molgenis.framework.db.Database;

public class JpaUtil {
    public static void createTables(Database db) {
        JpaUtil.createTables((JpaDatabase)db, true);
    }	
	
	public static void createTables(Database db, boolean clear) {
        JpaUtil.createTables((JpaDatabase)db, clear);
    }	
	
    public static void createTables(JpaDatabase db, boolean clear) {
        if(clear) {
        	db.getEntityManager().clear();
        }
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());
    }

    public static void dropAndCreateTables(Database db) {
    	JpaUtil.dropAndCreateTables((JpaDatabase)db, true);
    }
    
    public static void dropAndCreateTables(Database db, boolean clear) {
    	JpaUtil.dropAndCreateTables((JpaDatabase)db, clear);
    }
    
    public static void dropAndCreateTables(JpaDatabase db, boolean clear) {
        if(clear) {
        	db.getEntityManager().clear();
        }
    	JpaFrameworkFactory.createFramework().dropTables(db.getPersistenceUnitName());
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());
    }

    public static void dropTables(Database db) {
    	JpaUtil.dropTables((JpaDatabase)db, true);
    }
    
    public static void dropTables(Database db, boolean clear) {
    	JpaUtil.dropTables((JpaDatabase)db, clear);
    }
    
    public static void dropTables(JpaDatabase db, boolean clear) {
        if(clear) {
        	db.getEntityManager().clear();
        }
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());
    }
}
