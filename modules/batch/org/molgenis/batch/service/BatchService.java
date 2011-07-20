package org.molgenis.batch.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;

public class BatchService {

    private Database db;
    //private CommonService cq = CommonService.getInstance();
    
    public void setDatabase(Database db, int userId)
    {
    	this.db = db;
		
//    	cq.setDatabase(db);
//    	cq.makeObservationTargetNameMap(userId, false);
    }
    
    public List<MolgenisBatch> getBatches(int userId) throws DatabaseException, ParseException
    {
    	return db.query(MolgenisBatch.class).eq(MolgenisBatch.MOLGENISUSER, userId).find();
    }
    
    public MolgenisBatch getBatch(int batchId) throws DatabaseException
    {
    	return db.findById(MolgenisBatch.class, batchId);
    }
    
    public List<MolgenisBatchEntity> getBatchEntities(int batchId) throws DatabaseException, ParseException
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
    public List<ObservationTarget> getObservationTargetsNotInBatch() throws DatabaseException, ParseException
    {
    	List<ObservationTarget> result  = new ArrayList<ObservationTarget>();
    	List<ObservationTarget> targets = this.db.query(ObservationTarget.class).sortASC("name").find();

    	for (ObservationTarget target : targets)
    	{
    		List<MolgenisBatchEntity> l = this.db.query(MolgenisBatchEntity.class).equals(MolgenisBatchEntity.OBJECTID, target.getId()).find();
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
    public List<ObservationTarget> getObservationTargetsNotInCurrentBatch(int batchId) throws DatabaseException, ParseException
    {
    	List<ObservationTarget> result  = new ArrayList<ObservationTarget>();
    	List<ObservationTarget> targets = this.db.query(ObservationTarget.class).sortASC("name").find();

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

    public void removeFromBatch(Integer batchId, List<Integer> entityIds) throws DatabaseException, IOException
    {
    	this.db.beginTx();

    	for (Integer id : entityIds)
    	{
    		MolgenisBatchEntity entity = new MolgenisBatchEntity();
    		entity.setId(id);
    		entity.setBatch(batchId);
    		this.db.remove(entity);
    	}
    	this.db.commitTx();
    }
    
    public void addToBatch(Integer batchId, List<Integer> targetIds) throws DatabaseException, ParseException, IOException
    {
    	this.db.beginTx();

    	for (Integer targetId : targetIds)
    	{
    		ObservationTarget target = this.db.findById(ObservationTarget.class, targetId);
        	
    		//TODO: Danny use or loose
        	/*List<OntologyTerm> terms = */this.db.query(OntologyTerm.class).equals(OntologyTerm.NAME, target.getClass().toString()).find();
        	
//        	if (terms.size() != 1)
//        		throw new DatabaseException("No OntologyTerm found for " + target.getClass().toString());

        	MolgenisBatchEntity entity = new MolgenisBatchEntity();
//    		entity.setEntityType(terms.get(0));
    		entity.setBatch(batchId);
    		entity.setName(target.getName());
    		entity.setObjectId(target.getId());
    		this.db.add(entity);
    	}
    	this.db.commitTx();
    }
    
//    public String getTargetLabel(int targetId) throws DatabaseException, ParseException {
//    	return cq.getObservationTargetLabel(targetId);
//    }
}
