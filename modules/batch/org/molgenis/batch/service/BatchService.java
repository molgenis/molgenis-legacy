package org.molgenis.batch.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;

public class BatchService {
    
    public List<MolgenisBatch> getBatches(Database db, int userId) throws DatabaseException, ParseException
    {
    	return db.query(MolgenisBatch.class).eq(MolgenisBatch.MOLGENISUSER, userId).find();
    }
    
    public MolgenisBatch getBatch(Database db, int batchId) throws DatabaseException
    {
    	return db.findById(MolgenisBatch.class, batchId);
    }
    
    public List<MolgenisBatchEntity> getBatchEntities(Database db, int batchId) throws DatabaseException, ParseException
    {
    	return db.query(MolgenisBatchEntity.class).eq(MolgenisBatchEntity.BATCH, batchId).sortASC("name").find();
    }

    /**
     * Get all observation targets that are not in any batch.
     * 
     * @return
     * @throws DatabaseException
     * @throws ParseException
     */
    public List<ObservationTarget> getObservationTargetsNotInBatch(Database db) throws DatabaseException, ParseException
    {
    	List<ObservationTarget> result  = new ArrayList<ObservationTarget>();
    	List<ObservationTarget> targets = db.query(ObservationTarget.class).sortASC("name").find();

    	for (ObservationTarget target : targets)
    	{
    		List<MolgenisBatchEntity> l = db.query(MolgenisBatchEntity.class).equals(MolgenisBatchEntity.OBJECTID, target.getId()).find();
    		if (l.size() == 0) {
    			result.add(target);
    		}
    	}
    	return result;
    }
    
    /**
     * Get all observation targets that are not in a given batch.
     * 
     * @param batchId : the id of the batch to test for
     * @return
     * @throws DatabaseException
     * @throws ParseException
     */
    public List<ObservationTarget> getObservationTargetsNotInCurrentBatch(Database db, int batchId) throws DatabaseException, ParseException
    {
    	List<ObservationTarget> result  = new ArrayList<ObservationTarget>();
    	List<ObservationTarget> targets = db.query(ObservationTarget.class).sortASC("name").find();

    	for (ObservationTarget target : targets)
    	{
    		Query<MolgenisBatchEntity> q = db.query(MolgenisBatchEntity.class);
    		q.addRules(new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, batchId));
    		q.addRules(new QueryRule(MolgenisBatchEntity.OBJECTID, Operator.EQUALS, target.getId()));
    		List<MolgenisBatchEntity> l = q.find();
    		if (l.size() == 0) {
    			result.add(target);
    		}
    	}
    	return result;
    }

    public void removeFromBatch(Database db, Integer batchId, List<Integer> entityIds) throws DatabaseException, IOException
    {
    	for (Integer id : entityIds)
    	{
    		MolgenisBatchEntity entity = new MolgenisBatchEntity();
    		entity.setId(id);
    		entity.setBatch(batchId);
    		db.remove(entity);
    	}
    }
    
    public void addToBatch(Database db, Integer batchId, List<Integer> targetIds) throws DatabaseException, ParseException, IOException
    {
    	for (Integer targetId : targetIds)
    	{
    		ObservationTarget target = db.findById(ObservationTarget.class, targetId);
        	
    		//TODO: Danny use or loose
        	/*List<OntologyTerm> terms = db.query(OntologyTerm.class).equals(OntologyTerm.NAME, target.getClass().toString()).find();*/
        	
//        	if (terms.size() != 1)
//        		throw new DatabaseException("No OntologyTerm found for " + target.getClass().toString());

        	MolgenisBatchEntity entity = new MolgenisBatchEntity();
//    		entity.setEntityType(terms.get(0));
    		entity.setBatch(batchId);
    		entity.setName(target.getName());
    		entity.setObjectId(target.getId());
    		db.add(entity);
    	}
    }
    
//    public String getTargetLabel(int targetId) throws DatabaseException, ParseException {
//    	return cq.getObservationTargetLabel(targetId);
//    }
}
