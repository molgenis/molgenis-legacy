package org.molgenis.framework.db.jpa;

public class JpaUtil {
    public static void createTables(JpaDatabase db) {
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());
    }

    public static void dropAndCreateTables(JpaDatabase db) {
        JpaFrameworkFactory.createFramework().dropTables(db.getPersistenceUnitName());
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());
    }

    public static void dropTables(JpaDatabase db) {
        JpaFrameworkFactory.createFramework().createTables(db.getPersistenceUnitName());

    }
}
