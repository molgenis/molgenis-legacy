<#include "GeneratorHelper.ftl">

package app;

import java.util.HashMap;
import java.util.Map;

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
		
		private EMFactory() {
			addEntityManagerFactory("molgenis");
		}
		
		private static void addEntityManagerFactory(String persistenceUnit) {
			if(!emfs.containsKey(persistenceUnit)) {
				emfs.put(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit));
			}
		}
		
		public static EntityManager createEntityManager(String persistenceUnit) {
			if(instance == null) {
				instance = new EMFactory();
			}		
			if(!emfs.containsKey(persistenceUnit)) {
				addEntityManagerFactory(persistenceUnit);
			}			
			return emfs.get(persistenceUnit).createEntityManager();		
		}
		
		public static EntityManager createEntityManager() {
			if(instance == null) {
				instance = new EMFactory();
			}		
			return emfs.get("molgenis").createEntityManager();		
		}		
	}
	
	static
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
		putMapper(${entity.namespace}.${Name(entity)}.class, (DatabaseMapper)new ${entity.namespace}.db.${Name(entity)}Mapper(null));
		</#if></#list>	
	}	

	public JpaDatabase() throws DatabaseException
	{
		super(EMFactory.createEntityManager(), new JDBCMetaDatabase());
	}
	
	public JpaDatabase(String persistenceUnit) throws DatabaseException {
		super(EMFactory.createEntityManager(persistenceUnit), new JDBCMetaDatabase());
	}
	
	public JpaDatabase(boolean testDatabase) throws DatabaseException {
		super(new JDBCMetaDatabase());
		if(testDatabase) {
			super.setEntityManager(EMFactory.createEntityManager("molgenis_test"));
		} else {
			super.setEntityManager(EMFactory.createEntityManager());
		}
	}
}
