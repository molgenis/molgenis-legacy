<#include "GeneratorHelper.ftl">

package app;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.DatabaseMapper;

public class JpaDatabase extends org.molgenis.framework.db.jpa.JpaDatabase
{
	private static class EMFactory {
		
		static private Map<String, EntityManagerFactory> emfs = new HashMap<String, EntityManagerFactory>(); 
		
		static EMFactory instance = null;
		
		private EMFactory(String persistenceUnit) {
			addEntityManagerFactory(persistenceUnit);
		}
		
		private static void addEntityManagerFactory(String persistenceUnit) {
			if(!emfs.containsKey(persistenceUnit)) {
				emfs.put(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit));
			}
		}
		
		public static EntityManager createEntityManager(String persistenceUnit) {
			if(instance == null) {
				instance = new EMFactory(persistenceUnit);
			}		
			if(!emfs.containsKey(persistenceUnit)) {
				addEntityManagerFactory(persistenceUnit);
			}			
			return emfs.get(persistenceUnit).createEntityManager();		
		}
		
		public static EntityManager createEntityManager() {
			if(instance == null) {
				instance = new EMFactory("molgenis");
			}		
			return emfs.get("molgenis").createEntityManager();		
		}		

                public static EntityManagerFactory getEntityManagerFactoryByName(String name) {
                    return emfs.get(name);
                }
	}
	
	public void initMappers(JpaDatabase db)
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
//		putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db));
		<#if entity.decorator?exists>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db)));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db))));
				</#if>	
			<#else>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db)));
				</#if>
			</#if>
		</#if></#list>	
	}	

    public JpaDatabase() throws DatabaseException {
        super("molgenis", EMFactory.createEntityManager(), new JDBCMetaDatabase());
        this.persistenceUnitName = "molgenis";
        initMappers(this);
    }

    public JpaDatabase(String persistenceUnitName) throws DatabaseException {
        super(persistenceUnitName, EMFactory.createEntityManager(persistenceUnitName), new JDBCMetaDatabase());
        this.persistenceUnitName = persistenceUnitName;
        initMappers(this);
    }

    public JpaDatabase(boolean testDatabase) throws DatabaseException {
        super(testDatabase ? "molgenis_test" : "molgenis", new JDBCMetaDatabase());
        persistenceUnitName = testDatabase ? "molgenis_test" : "molgenis";
        if (testDatabase) {
            super.setEntityManager(EMFactory.createEntityManager("molgenis_test"));
        } else {
            super.setEntityManager(EMFactory.createEntityManager());
        }
        initMappers(this);
    }
    private String persistenceUnitName = null;

    public EntityManagerFactory getEntityManagerFactory() {
        return EMFactory.getEntityManagerFactoryByName(persistenceUnitName);
    }

    public static EntityManagerFactory getEntityManagerFactoryByName(String name) {
        return EMFactory.getEntityManagerFactoryByName(name);
    }

    public static EntityManagerFactory getEntityManagerFactory(boolean testDatabase) {
        if(testDatabase) {
            return EMFactory.getEntityManagerFactoryByName("molgenis_test");    
        } else {
            return EMFactory.getEntityManagerFactoryByName("molgenis");    
        }            
    }

    public Connection createJDBCConnection() throws SQLException, ClassNotFoundException {
        EntityManagerFactory emf = getEntityManagerFactory();
        Map<String, Object> p = emf.getProperties();
        
        Class.forName((String)p.get("javax.persistence.jdbc.driver "));
        return DriverManager.getConnection((String)p.get("javax.persistence.jdbc.url"), 
                (String)p.get("javax.persistence.jdbc.user"), 
                (String)p.get("javax.persistence.jdbc.password"));
    }

}
