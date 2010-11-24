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
	public static void dropAndCreateTables(EntityManager em) {
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
		
		Session session = emi.getServerSession();
		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
		TableCreator tc = dtg.generateDefaultTableCreator();
		tc.dropTables((DatabaseSession) session);
		tc.createTables((DatabaseSession)session);		
	}
}
