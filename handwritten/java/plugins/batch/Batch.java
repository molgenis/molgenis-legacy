/**
 * @author Jessica Lundberg
 * @date 22-03-2011
 * 
 * This class represents a batch and the entities that are in that batch.
 */
package plugins.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.ObservationTarget;

class Batch {

    private List<MolgenisBatchEntity> entities;
    private List<ObservationTarget> actualEntities;
    private int id;
    private boolean hasQuery;
    private String query;
    private String name;
    
    private static transient Logger logger = Logger.getLogger(Batch.class);

    private BatchService service;


    /** Creates a new batch. Note: Batches dynamically created by queries are not yet available,
     * and calling this constructor will results in an UnsupportOperationException.
     * 
     * @param id
     * @param name
     * @param query
     * @param service
     */
    public Batch(int id, String name, String query, BatchService service) {
		this.id = id;
		this.name = name;
		this.query = query;
		hasQuery = true;
		this.service = service;
		findEntities();
		fillActualEntities();
    }

    /** Creates a new batch and fills it with its containing entities.
     * 
     * @param id
     * @param name
     * @param entities
     * @param service
     */
    public Batch(int id, String name, BatchService service) {
		this.id = id;
		this.name = name;
		hasQuery = false;
		this.service = service;
		findEntities();
		fillActualEntities();
    }

    /** Finds all entities for a given batch
     * 
     */
    private void findEntities() {
		try {
		    if(hasQuery) {
		    	throw new UnsupportedOperationException();
		    } else {
		    	this.entities = service.findEntities(id);
		    }
		} catch (Exception db) {
			db.printStackTrace();
		    logger.error("An exception occured while finding batch entities in the database", db);
		}
    }

    public List<MolgenisBatchEntity> getMolgenisBatchEntities() {
    	return entities;
    }

    public String getQuery() {
    	return query;
    }

    public String getName() {
    	return name;
    }

    public int getId() {
    	return id;
    }

    public List<?> getEntities() {
    	return actualEntities;
    }

    @Override
    public boolean equals(Object o) {
		Batch obj2 = (Batch) o;
		if (obj2 == null) {
		    return false;
		} else {
			if (this.getId() != obj2.getId()  || !this.getName().equals(obj2.getName())) {
				return false;
			} else {
				return true;
			}
		}
    }

    @Override
    public int hashCode() {
    	return id;
    }

    /** Retrieve all ObservationTargets for a list of MolgenisBatchEntity(s)
     * 
     */
    private void fillActualEntities() {
    	
    	actualEntities = new ArrayList<ObservationTarget>();
    	
		try {
		    for (MolgenisBatchEntity mbe : entities) {
		    	ObservationTarget o = service.findObservationTarget(mbe.getObjectId());
		    	actualEntities.add(o);
		    }
	
		} catch (Exception dbe) {
			dbe.printStackTrace();
		    logger.error("An exception occurred while finding observation targets in the database", dbe);
		}
    }
}
