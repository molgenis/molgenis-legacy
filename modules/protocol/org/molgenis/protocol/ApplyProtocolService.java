package org.molgenis.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

/**
 * A subset of the AnimalDB-specific CommonService functionality.
 * These methods were copied so the protocol module can function independently from AnimalDB.
 * TODO: get rid of the resulting code duplication. Maybe factor out CommonService altogether?
 * 
 * @author erikroos
 *
 */
public class ApplyProtocolService {
	
	private Database db;
	private static int protAppCounter = 0;

	public void setDatabase(Database db) {
		this.db = db;
	}
	
	public Database getDatabase() {
		return db;
	}

	public List<Integer> getOwnUserInvestigationIds(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		List<Investigation> invList;
		List<Integer> returnList = new ArrayList<Integer>();
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		if (invList != null && invList.size() > 0) {
			for (Investigation inv : invList) {
				returnList.add(inv.getId());
			}
		} else {
			return null;
		}
		return returnList;
	}

	public List<Integer> getWritableUserInvestigationIds(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANWRITE, Operator.EQUALS, userId));
		List<Integer> returnList = new ArrayList<Integer>();
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		for (Investigation inv : invList) {
			returnList.add(inv.getId());
		}
		return returnList;
	}
	
	public int getOwnUserInvestigationId(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return -1;
		}
		if (invList.size() == 1) {
			return invList.get(0).getId();
		} else {
			return -1;
		}
	}

	public int makeProtocolApplication(int investigationId, int protocolId) throws DatabaseException {
		Date now = Calendar.getInstance().getTime();
		ProtocolApplication pa = new ProtocolApplication();
		pa.setInvestigation_Id(investigationId);
		pa.setName(protocolId + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol_Id(protocolId);
		pa.setTime(now);
		db.add(pa);
		return pa.getId();
	}

	public List<ObservedValue> getObservedValuesByTargetAndFeature(int targetId, 
			Measurement measurement, List<Integer> investigationIds, 
			int investigationToBeAddedToId) throws DatabaseException, ParseException
	{
		List<Measurement> measurementList = new ArrayList<Measurement>();
		measurementList.add(measurement);
		return getObservedValuesByTargetAndFeatures(targetId, measurementList, investigationIds, 
				investigationToBeAddedToId);
	}
	
	public List<ObservedValue> getObservedValuesByTargetAndFeatures(int targetId, 
			List<Measurement> measurements, List<Integer> investigationIds, 
			int investigationToAddToId) throws DatabaseException, ParseException
	{

		List<ObservedValue> values = new ArrayList<ObservedValue>();

		for (Measurement m : measurements)
		{ // for each feature, find/make value(s)
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetId));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, m.getId()));
			q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
			q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
			List<ObservedValue> vals = q.find();
			
			if (vals.isEmpty())
			{ // if value doesn't exist, create new one
				ObservedValue newOV = new ObservedValue();
				newOV.setFeature_Id(m.getId());
				newOV.setValue("");
				// don't set relation, as that can then never be reset to null
				newOV.setTarget_Id(targetId);
				newOV.setInvestigation_Id(investigationToAddToId);
				values.add(newOV);
			} else {
				values.addAll(vals);
			}

		}

		return values;
	}

	public int getObservationTargetId(String targetName)
	{
		ObservationTarget tmpTarget;
		try {
			tmpTarget = ObservationTarget.findByName(db, targetName);
		} catch (Exception e) {
			return -1;
		}
		if (tmpTarget != null) {
			return tmpTarget.getId();
		} else {
			return -1;
		}
	}

	public List<Measurement> getMeasurementsByProtocol(int protocolId) throws DatabaseException, ParseException {
		
		Protocol protocol = db.findById(Protocol.class, protocolId);

		List<Measurement> features = new ArrayList<Measurement>();
	    for (Integer i : protocol.getFeatures_Id()) {
			features.add(db.findById(Measurement.class, i));
	    }
		
		return features;
	}

	public List<String> getTargetsFromBatch(int id) {
		List<String> returnList = new ArrayList<String>();
		Query<MolgenisBatchEntity> q = db.query(MolgenisBatchEntity.class);
		q.addRules(new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, id));
		// TODO: check if type is ObservationTarget? Is type stored anyway?
		
		try {
			List<MolgenisBatchEntity> entities = q.find();
		
			for(MolgenisBatchEntity m : entities) {
			    returnList.add(m.getObjectId().toString());
			}
		} catch (Exception e) {
			// Do nothing, return empty list
		}
	
		return returnList;
    }
	
	public int getMeasurementId(String measurementName) throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		q.eq(Measurement.NAME, measurementName);
		List<Measurement> featList = q.find();
		if (featList.size() > 0) {
		    return featList.get(0).getId();
		} else {
		    throw new DatabaseException("Id could be found for" +
			    " Measurement with name: " + measurementName);
		}
	}

	public List<Code> getAllCodesForFeature(String featureName)
			throws DatabaseException, ParseException
	{
		int featureId = getMeasurementId(featureName);
		return db.query(Code.class).eq(Code.FEATURE, featureId).find();
	}
	
	public List<String> getAllCodesForFeatureAsStrings(String featurename)
			throws DatabaseException, ParseException
	{
		int featureid = getMeasurementId(featurename);
		Query<Code> q = db.query(Code.class);
		q.eq(Code.FEATURE, featureid);
		List<String> returnList = new ArrayList<String>();
		List<Code> tmpList = q.find();
		for (Code code : tmpList) {
			returnList.add(code.getDescription());
		}
		return returnList;
	}
	
	public List<ObservationTarget> getObservationTargets(List<Integer> idList) throws DatabaseException, ParseException {
		if (idList.size() > 0) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			targetQuery.addRules(new QueryRule(ObservationTarget.ID, Operator.IN, idList));
			targetQuery.addRules(new QueryRule(Operator.SORTASC, ObservationTarget.NAME));
			return targetQuery.find();
		} else {
		    return new ArrayList<ObservationTarget>();
		}
	}

	public List<ObservationTarget> getAllMarkedPanels(String mark, List<Integer> investigationIds)
			throws DatabaseException, ParseException
	{
		List<Integer> panelIdList = new ArrayList<Integer>();

		Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
		valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, getMeasurementId("TypeOfGroup")));
		valueQuery.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, mark));
		QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
		QueryRule qr2 = new QueryRule(Operator.OR);
		QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
		valueQuery.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigations
		List<ObservedValue> valueList = valueQuery.find();
		for (ObservedValue value : valueList) {
			panelIdList.add(value.getTarget_Id());
		}

		return getObservationTargets(panelIdList);
	}

	public String getEntityName(int entityId) {
		try {
			return db.findById(MolgenisEntity.class, entityId).getName();
		} catch (DatabaseException e) {
			return null;
		}
	}

}
