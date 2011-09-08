/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jpa.JpaUtil;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;

/**
 *
 * @author jorislops
 */
public class TestInserts {

    public static void main(String[] args) throws DatabaseException, Exception {
        Investigation investigation = new Investigation();
        investigation.setName("Test4");

        ObservationTarget ot = new ObservationTarget();
        ot.setName("Target4");
        ot.setInvestigation(investigation);
       
        
        Measurement m = new Measurement();
        m.setName("Measurement4");
        m.setInvestigation(investigation);

        investigation.getInvestigationMeasurementCollection().add(m);
        
        
        
        JpaDatabase db = new JpaDatabase();
        //JpaUtil.dropAndCreateTables(db);
        EntityManager em = db.getEntityManager();
        EntityManagerFactory emf = em.getEntityManagerFactory();
        Connection c = db.createJDBCConnection();
        
        
        em.getTransaction().begin();
        em.persist(investigation);
        em.getTransaction().commit();
        
        
        


    }
}
