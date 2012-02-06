package org.molgenis.lifelines.listeners;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Standard importer for lifelines. Fields with name 'PA_ID' are considered to
 * be Individual'
 */
public class LifeLinesStandardListener extends ImportTupleListener {
	private final int BATCH_SIZE = 500;

	private final Map<String, Measurement> measurements = new HashMap<String, Measurement>();
	
	private final Set<String> targetNames = new HashSet<String>();
	private final List<ProtocolApplication> protocolApps = new ArrayList<ProtocolApplication>();
	private final List<ObservedValue> values = new ArrayList<ObservedValue>();
	
	private Protocol protocol;
	private Investigation investigation;
	
	private final EntityManager em;
	
	private Map<String, ObservationTarget> targets = new HashMap<String, ObservationTarget>();
	
	private final boolean shareMeasurements;
	
	public LifeLinesStandardListener(Investigation investigation, Protocol protocol, Database db, boolean shareMeasurements, List<SimpleTuple> tuples)
			throws DatabaseException {
		super(protocol.getName(), db, tuples);
		
		//trick to prevent compilation problem in Hudson, should be changed!
		this.em = ((JpaDatabase)db).getEntityManager().getEntityManagerFactory().createEntityManager();
		
		this.investigation = investigation;
		this.protocol = protocol;
		this.shareMeasurements = shareMeasurements;
		
		List<Measurement> tempMeasurements = (List<Measurement>)(List)protocol.getFeatures();
		for(Measurement m : tempMeasurements) {
			measurements.put(m.getName(), m);	
		}
		
		
	}
	


	private static Integer rowCount = 0;
	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		// get reference to individual
		String pa_id = tuple.getString("PA_ID");
		
		ObservationTarget target = new ObservationTarget();
			//target.setPaid(Integer.parseInt(pa_id));
			target.setName(pa_id);

		//put target in hash
		if(!targets.containsKey(target.getName()))
			targets.put(target.getName(), target);


		ProtocolApplication app = new ProtocolApplication();
		app.setProtocol(protocol);
		app.setName(name + ": " + pa_id);
		//app.setInvestigation(investigation);
		protocolApps.add(app);

		// we iterate through all fields. Each field that is also a Measurement
		for (String field : tuple.getFields()) {
			//only include fields that are selected as measurement
			String mName = null;
			if(shareMeasurements) {
				mName = field;
			} else {
				mName = protocol.getName() + "_" + field;				
			}
			
			if (measurements.containsKey(mName))
			{				
				ObservedValue v = new ObservedValue();
				v.setTarget(target);
				v.setInvestigation(investigation);
				v.setFeature_Id(measurements.get(mName).getId());
				v.setValue(tuple.getString(field));
				v.setProtocolApplication(app);

				values.add(v);
			}
		}

		if (values.size() > BATCH_SIZE) {
			synchronized(rowCount) {
				rowCount += values.size();
			}
			//System.out.println("BATCH INSERT " + rowCount);
			storeValuesInDatabase();
		}
	}

	private void storeValuesInDatabase() {		
		em.getTransaction().begin();
		investigation = em.find(Investigation.class, investigation.getId());
		protocol = em.find(Protocol.class, protocol.getId());
		
		Map<Integer, Measurement> nameMeasurements = index(protocol.getFeatures(), 				
				on(Measurement.class).getId());
		
		Map<String, ObservationTarget> dbTargets = retrieveTargets(targets.keySet());		
		
		for(ObservedValue ov : values) {
			String targetName = ov.getTarget().getName();
			if(dbTargets.containsKey(targetName)) {
				ov.setTarget(dbTargets.get(targetName));
			} else {
				ObservationTarget target = targets.get(targetName);
				ov.setTarget(target);
			}
			ov.setInvestigation(investigation);
			ov.getTarget().setInvestigation(investigation);			
			ov.setFeature(nameMeasurements.get(ov.getFeature_Id()));			
			ov.getProtocolApplication().setInvestigation(investigation);
			ov.getProtocolApplication().setProtocol(protocol);
			em.persist(ov);
			
		}	
		em.flush();
		em.getTransaction().commit();
		em.clear();
		
		values.clear();
		targetNames.clear();
		targets.clear();
	}
	
	private Map<String, ObservationTarget> retrieveTargets(Set<String> paids) {
		if(paids.size() > 0) {		
			return index(
					em.createQuery("SELECT t FROM ObservationTarget t WHERE t.investigation = :investigation AND t.name IN :targetNames", ObservationTarget.class)
					.setParameter("investigation", investigation)
					.setParameter("targetNames", paids)
					.getResultList(), 				
					on(ObservationTarget.class).getName());
		} else {
			return new HashMap<String, ObservationTarget>();
		}
	}	

	@Override
	public void commit() throws DatabaseException {
		storeValuesInDatabase();
	}

	public Investigation getInvestigation() {
		return investigation;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public List<Measurement> getMeasurements() {
		return new ArrayList<Measurement>(measurements.values()); 
	}
	
	public static void resetRowCount() {
		rowCount = 0;
	}
	
}
