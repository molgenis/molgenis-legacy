/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.lifelines;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author jorislops
 */
public class UpdateDatabase {
    public static void main(String[] args) {
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        configOverrides.put("hibernate.hbm2ddl.auto", "update"); //FIXME: should be changed to validate for production		        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis", configOverrides);
        EntityManager em = emf.createEntityManager();
    }
}
