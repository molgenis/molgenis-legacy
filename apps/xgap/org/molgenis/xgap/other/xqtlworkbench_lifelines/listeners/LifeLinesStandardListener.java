package org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Tuple;

/**
 * Standard importer for lifelines. Fields with name 'PA_ID' are considered to
 * be Individual'
 */
public class LifeLinesStandardListener extends ImportTupleListener {
	private final int BATCH_SIZE = 1000;
	
	private Logger logger;

	private final Map<String, Measurement> measurements = new HashMap<String, Measurement>();
	private final Map<String, Individual> targets = new HashMap<String, Individual>();
	private final Map<String, ProtocolApplication> protocolApps = new HashMap<String, ProtocolApplication>();
	private final List<ObservedValue> values = new ArrayList<ObservedValue>();

	private Protocol protocol;
	private Investigation investigation;

	public LifeLinesStandardListener(Investigation investigation,
			Protocol protocol, Database db) throws DatabaseException {
		super(protocol.getName(), db);

		this.investigation = investigation;
		this.protocol = protocol;
		
		this.logger = Logger.getLogger("LLimport("+protocol.getName()+")");

		// WANTED: List<Measurement> result = protocol.getFeatures(db);
		for (Measurement m : db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id()).find()) {
			measurements.put(m.getName(), m);
		}
	}

	private int rowCount = 0;
	private int valueCount = 0;
	private int batchCount = 0;

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		// get reference to individual
		String pa_id = tuple.getString("PA_ID");
		if (pa_id == null) throw new Exception("PA_ID missing for protocol " + protocol.getName() + 
				" in tuple: " + tuple);

		Individual target = new Individual();
		target.setName(pa_id);
		target.setInvestigation(investigation);
		targets.put(target.getName(), target);

		ProtocolApplication app = new ProtocolApplication();
		app.setProtocol(protocol);
		app.setName(protocol.getName() + "_" + pa_id + "_" + line_number);
		app.setInvestigation(investigation);
		protocolApps.put(app.getName(), app);

		// we iterate through all fields. Each field that is also a Measurement
		for (String field : tuple.getFields()) {
			// only include fields that are selected as measurement
			String mName = protocol.getName() + "_" + field; // temporarily prepend measurement name with table (protocol) name
			if (measurements.containsKey(mName)) {
				ObservedValue v = new ObservedValue();
				v.setTarget(target);
				v.setInvestigation(investigation);
				v.setFeature(measurements.get(mName));
				v.setValue(tuple.getString(field));
				v.setProtocolApplication(app);

				values.add(v);
				
				valueCount++;
				batchCount++;
			}
		}

		++rowCount;
		if (batchCount > BATCH_SIZE) {
			logger.info("parsed row: " + rowCount +"(valuecount='"+valueCount+"')");
			storeValuesInDatabase();
			batchCount = 0;
		}
	}

	private void storeValuesInDatabase() throws DatabaseException,
			ParseException {
		
		// add protocol applications
		db.add(new ArrayList(protocolApps.values()));

		//only add targets if they are not already there
		db.update(new ArrayList(targets.values()),
				DatabaseAction.ADD_IGNORE_EXISTING, Individual.NAME);
		
		//update all _names with the _ids
		for(ObservedValue v: values)
		{
			v.setTarget(targets.get(v.getTarget_Name()));
			v.setProtocolApplication(protocolApps.get(v.getProtocolApplication_Name()));
		}
		
		//clear the targets and protocol applications for next batch
		this.targets.clear();
		this.protocolApps.clear();
		
		//add the values
		db.add(this.values);
		
		//clear the values
		values.clear();
	}

	@Override
	public void commit() throws DatabaseException, ParseException {
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

}
