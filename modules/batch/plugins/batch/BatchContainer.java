/**
 * @author Jessica Lundberg
 * @date 22-03-2011
 * 
 * This class is a container for all batches for a given user
 */
package plugins.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.framework.db.DatabaseException;

class BatchContainer {

    private List<Batch> batches;
    private BatchService service;
    private int userId;
    private static transient Logger logger = Logger.getLogger(BatchContainer.class);
    
    public BatchContainer(BatchService service, int userId) {
		this.service = service;
		this.userId = userId;
		this.batches = initializeBatches();
    }

    /**
     * Initializes the internal List of Batches with all the user's batches.
     * 
     * @return
     */
    private List<Batch> initializeBatches() {
		List<Batch> myBatches = new ArrayList<Batch>();
		try {
		    List<MolgenisBatch> userBatches = service.findAllBatches(userId);
		    
		    for(MolgenisBatch mb : userBatches) {
				myBatches.add(new Batch(mb.getId(), mb.getName(), service));
			}
		    
		} catch (DatabaseException e) {
		    logger.error("An exception occurred while finding batches in the database", e);
		}
		return myBatches;
    }
    
    public List<Batch> getBatches() {
    	return batches;
    }
    
   /**
    * Updates the internal List of Batches with any new batches for the logged in user.
    */
    public void updateBatches() {
		try {
		    List<MolgenisBatch> userBatches = service.findAllBatches(userId);
		    
		    for(MolgenisBatch mb : userBatches) {
		    	Batch newBatch = new Batch(mb.getId(), mb.getName(), service);
				if (!batches.contains(newBatch)) {
				    batches.add(newBatch);
				}
		    }
		} catch (DatabaseException e) {
		    logger.error("An exception occurred while finding batches in the database", e);
		}
    }
    
    
}
