package org.molgenis.lifelines;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.molgenis.lifelines.Entities.PublicDictionary;
import org.molgenis.lifelines.Entities.ValueLabels;

/**
 *
 * @author jorislops
 */
public class Test {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis");
        EntityManager em = emf.createEntityManager();
        
        List<PublicDictionary> metaData = em.createQuery("SELECT pd FROM org.molgenis.lifelines.Entities.PublicDictionary pd", PublicDictionary.class)
                .setMaxResults(100)
                .getResultList();
        
        for(PublicDictionary pd : metaData) {
            System.out.println(pd.toString());
            for(ValueLabels vl : pd.getValueLabels()) {
                System.out.println(vl.toString());
            }
        }
    }
}
