/**
 * @author Jessica Lundberg
 * @author Erik Roos
 * @date Feb 24, 2011
 * 
 * This class is the service for the ApplyProtocolPlugin
 */
package plugins.protocol;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisEntity;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;

import commonservice.CommonService;

public class ProtocolPluginService {

    private Database db;
    private CommonService cq;

    public ProtocolPluginService() {
    	cq = CommonService.getInstance();
    }

    public void setDatabase(Database db, int userId) {
		this.db = db;
		cq.setDatabase(db);
		cq.makeObservationTargetNameMap(userId);
    }

    public Database getDatabase() {
    	return db;
    }

    public List<Protocol> getProtocols() throws DatabaseException, ParseException {
    	return cq.getAllProtocolsSorted(Protocol.NAME, "ASC");
    }
    
    public List<ProtocolApplication> getProtocolApplications(int protocolId) throws DatabaseException, ParseException {
		return cq.getAllProtocolApplicationsByType(protocolId);
	}
    
    public int makeProtocolApplication(int investigationId, int protocolId) throws DatabaseException, IOException {
    	return cq.makeProtocolApplication(investigationId, protocolId);
    }

    /**
     * Get all ObservationTargets currently in the database.
     * 
     * @return
     */
    public List<ObservationTarget> getTargets() {
    	return cq.getAllObservationTargets();
    }

    /**
     * Get all MolgenisBatches currently in the database.
     * 
     * @return
     */
    public List<MolgenisBatch> getBatches() {
    	return cq.getAllBatches();
    }

    public List<Integer> getObservableFeaturesInProtocol(int protocolId) throws DatabaseException, ParseException {
    	Protocol protocol = cq.getProtocolById(protocolId);
		return protocol.getFeatures();
    }

    public Measurement getMeasurement(Integer measurementId) throws DatabaseException, ParseException {
    	return cq.getMeasurementById(measurementId);
    }

    public List<String> getCodesForMeasurement(Integer measurementId) throws DatabaseException, ParseException {
    	return cq.getAllCodesForFeatureAsStrings(cq.getMeasurementById(measurementId).getName());
    }

    public ObservationTarget getObservationTarget(Integer targetId) throws DatabaseException, ParseException {
    	return cq.getObservationTargetById(targetId);
    }
    
    /**
     * Get the observed values for the given observation target on the given features.
     * Side effect: makes a new observed value if one cannot be found.
     * 
     * @param targetId
     * @param features
     * @return list of retrieved or newly made observed values
     * @throws DatabaseException
     * @throws ParseException
     */
    public List<ObservedValue> getObservedValuesByTargetAndFeatures(int targetId, List<Integer> features) throws DatabaseException, ParseException {
    	return cq.getObservedValueByTargetAndFeatures(targetId, features);
    }
    
    /**
     * Get the observed value for the given observation target on the given feature,
     * within the given protocol application.
     * Side effect: makes a new observed value if one cannot be found.
     * 
     * @param protocolApplicationId : id of the protocol application
     * @param targetId : id of the observation target
     * @param featureId : id of the observable feature
     * @return retrieved or newly made observed value
     * @throws DatabaseException
     * @throws ParseException
     */
    public ObservedValue getObservedValueByProtocolApplication(int protocolApplicationId, int targetId, int featureId)
    	throws DatabaseException, ParseException {
    	
    	Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetId));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
		List<ObservedValue> values = q.find();
		if (values.size() > 0) {
			return values.get(0);
		} else {
			// Make a placeholder observed value
			ObservedValue newValue = new ObservedValue();
			newValue.setFeature(featureId);
			newValue.setValue("");
			// don't set relation, as that can then never be reset to null
			newValue.setTarget(targetId);
			newValue.setInvestigation(cq.getObservationTargetById(targetId).getInvestigation_Id());
			return newValue;
		}
	}

    public void update(ObservedValue value) throws DatabaseException, IOException {
    	db.update(value);
    }

    public List<String> getTargetsFromBatch(int id) {
		List<String> returnList = new ArrayList<String>();
		Query<MolgenisBatchEntity> q = db.query(MolgenisBatchEntity.class);
		q.addRules(new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, id));
		// TODO: check if type is ObservationTarget? Is type stored anyway?
		
		try {
			List<MolgenisBatchEntity> entities = q.find();
		
			for(MolgenisBatchEntity m : entities) {
			    returnList.add(db.findById(ObservationTarget.class, m.getObjectId()).getId().toString());
			}
		} catch (Exception e) {
			// Do nothing, return empty list
		}
	
		return returnList;
    }
	
    public boolean isMolgenisBatch(Object id) throws DatabaseException {
		Integer batchId = Integer.parseInt(id.toString());
	
		MolgenisBatch batch = db.findById(MolgenisBatch.class, batchId);
	
		if (batch != null) {
		    return true;
		}
	
		return false;
    }

    public List<Integer> getAllObservationTargetIds() throws DatabaseException, ParseException {
    	return cq.getAllObservationTargetIds(null, false);
    }

	public List<Integer> getObservationTargetsInProtocolApplication(
			int protocolApplicationId) throws DatabaseException, ParseException {
		
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
		List<ObservedValue> values = q.find();
		List<Integer> returnList = new ArrayList<Integer>();
		for (ObservedValue v : values) {
			int targetId = v.getTarget_Id();
			if (!returnList.contains(targetId)) {
				returnList.add(targetId);
			}
		}
		return returnList;
	}
	
	public int getInvestigationId(String invName) throws DatabaseException, ParseException {
		return cq.getInvestigationId(invName);
	}

	/**
	 * Get the name of the given MolgenisEntity. Returns null if it doesn't exist.
	 * 
	 * @param entityId
	 * @return
	 */
	public String getEntityName(int entityId) {
		try {
			return db.findById(MolgenisEntity.class, entityId).getName();
		} catch (DatabaseException e) {
			return null;
		}
	}

	/**
	 * Get all Panels labeled as 'panelLabel'. Returns an empty list if none can be found.
	 * 
	 * @param panelLabel
	 * @return
	 */
	public List<ObservationTarget> getLabeledPanels(String panelLabel) {
		try {
			return cq.getAllMarkedPanels(panelLabel);
		} catch (Exception e) {
			return new ArrayList<ObservationTarget>();
		}
	}

	public String getObservationTargetLabel(Integer id) throws DatabaseException, ParseException {
		return cq.getObservationTargetLabel(id);
	}

}
