/**
 * @author Jessica Lundberg
 * @date 22-03-2011
 * 
 * This class is the service layer for the Batches plugin
 */
package plugins.batch;

import java.util.List;

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;

import commonservice.CommonService;

class BatchService {

    private Database db;
    private CommonService cs = CommonService.getInstance();
    
    public BatchService() {
    }
    
    public void setDatabase(Database db, int userId) {
    	this.db = db;
		if(true)
		{
			throw new UnsupportedOperationException("this is animaldb specific???");
		}
//    	cs.setDatabase(db);
//    	cs.makeObservationTargetNameMap(userId, false);
    }
    /** Finds batches given a user id
     * 
     * @param userId
     * @return
     * @throws DatabaseException
     */
    public List<MolgenisBatch> findAllBatches(int userId) throws DatabaseException {
    	return db.find(MolgenisBatch.class, new QueryRule(MolgenisBatch.MOLGENISUSER, Operator.EQUALS, userId));
    }

    /** Finds entities given a batch id
     * 
     * @param batchId
     * @return
     * @throws DatabaseException
     */
    public List<MolgenisBatchEntity> findEntities(int batchId) throws DatabaseException {
    	return db.find(MolgenisBatchEntity.class, new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, batchId));
    }

    public ObservationTarget findObservationTarget(Integer objectId) throws DatabaseException {
    	return db.findById(ObservationTarget.class, objectId);
    }

	public Object getTargetLabel(Integer id) {
		try {
			return cs.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}
    
  
}
