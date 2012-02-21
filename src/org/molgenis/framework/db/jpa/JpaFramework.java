/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.framework.db.jpa;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityManager;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Entity;

/**
 *
 * @author joris lops
 */
public interface JpaFramework {
    
	public <E extends Entity> List<E> findByExample(EntityManager em, E example);
    
	/**
	 * Create tables based on annotations.
	 * 
	 * @param persistenceUnitName
	 */
    public void createTables(String persistenceUnitName);
    
    /**
     * Drop tables based on annotations
     * 
     * @param persistenceUnitName
     */
    public void dropTables(String persistenceUnitName);  
    
    @Deprecated
    public Connection getConnection(EntityManager em);
}
