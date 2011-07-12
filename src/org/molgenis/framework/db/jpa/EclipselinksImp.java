/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.framework.db.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import org.molgenis.util.Entity;

/**
 *
 * @author jorislops
 */
public class EclipselinksImp implements JpaFramework {

    @Override
    public <E extends Entity> List<E> findByExample(EntityManager em, E example) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createTables(String persistenceUnitName) {
        throw new UnsupportedOperationException("Not supported yet.");
//		Persistence.createEntityManagerFactory("molgenis_test").getCache().evictAll();
//		Persistence.createEntityManagerFactory("molgenis_test").close();
//		
//		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
//		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
//		
//		Session session = emi.getServerSession();
//		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
//		TableCreator tc = dtg.generateDefaultTableCreator();
//		//tc.dropTables((DatabaseSession) session);
//		tc.createTables((DatabaseSession)session);
//		return emi;        
    }

    @Override
    public void dropTables(String persistenceUnitName) {
        throw new UnsupportedOperationException("Not supported yet.");
//		em.clear();
//		em.close();
//		Persistence.createEntityManagerFactory("molgenis_test").getCache().evictAll();
//		Persistence.createEntityManagerFactory("molgenis_test").close();
//		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("molgenis_test");
//		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
//		
//		Session session = emi.getServerSession();
//		DefaultTableGenerator dtg = new DefaultTableGenerator(emi.getActiveSession().getProject());
//		TableCreator tc = dtg.generateDefaultTableCreator();
//		tc.dropTables((DatabaseSession) session);
//
//		emfi.close();
    }
    
}
