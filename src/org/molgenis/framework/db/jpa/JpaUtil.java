package org.molgenis.framework.db.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

public class JpaUtil {
	
	public static EntityManager createTables() {
		Persistence.createEntityManagerFactory("molgenis_test").getCache().evictAll();
		Persistence.createEntityManagerFactory("molgenis_test").close();
		
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
		
		Session session = emi.getServerSession();
		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
		TableCreator tc = dtg.generateDefaultTableCreator();
		//tc.dropTables((DatabaseSession) session);
		tc.createTables((DatabaseSession)session);
		return emi;
	}
	
	public static EntityManager dropAndCreateTables() {
		return dropAndCreateTables(null);
	}
	
	public static EntityManager dropAndCreateTables(EntityManager em) {
		if(em != null) {
			em.clear();
			em.close();
		}
		Persistence.createEntityManagerFactory("molgenis_test").getCache().evictAll();
		Persistence.createEntityManagerFactory("molgenis_test").close();
		
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
		
		Session session = emi.getServerSession();
		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
		TableCreator tc = dtg.generateDefaultTableCreator();
		tc.dropTables((DatabaseSession) session);
		tc.createTables((DatabaseSession)session);
		return emi;
	}
	
	public static void dropDatabase(EntityManager em) {
		em.clear();
		em.close();
		Persistence.createEntityManagerFactory("molgenis_test").getCache().evictAll();
		Persistence.createEntityManagerFactory("molgenis_test").close();
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
		
		Session session = emi.getServerSession();
		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
		TableCreator tc = dtg.generateDefaultTableCreator();
		tc.dropTables((DatabaseSession) session);

		emfi.close();
	}
}
