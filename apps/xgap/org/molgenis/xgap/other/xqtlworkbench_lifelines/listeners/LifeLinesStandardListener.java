package org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
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
	private final int BATCH_SIZE = 10000;

	// track the rows/cols of your data
	private final Map<String, Measurement> measurements = new HashMap<String, Measurement>();
	private final Map<String, Individual> targets = new HashMap<String, Individual>();

	// each BZ_ID is a protocolApplication
	private final List<ProtocolApplication> protocolApps = new ArrayList<ProtocolApplication>();

	private final List<ObservedValue> values = new ArrayList<ObservedValue>();

	private Protocol protocol;
	private Investigation investigation;

	public LifeLinesStandardListener(Investigation investigation,
			Protocol protocol, Database db) throws DatabaseException {
		super(protocol.getName(), db);

		this.investigation = investigation;
		this.protocol = protocol;

		// WANTED: List<Measurement> result = protocol.getFeatures(db);

		for (Measurement m : db.query(Measurement.class)
				.in(Measurement.ID, protocol.getFeatures_Id()).find()) {
			measurements.put(m.getName(), m);
		}
	}

	private static int rowCount = 0;

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		// get reference to individual
		String pa_id = tuple.getString("PA_ID");
		if(pa_id == null) throw new Exception("PA_ID missing for protocol "+protocol.getName()+" in tuple: "+tuple);

		Individual target = new Individual();
		target.setName(pa_id);
		targets.put(target.getName(), target);

		ProtocolApplication app = new ProtocolApplication();
		app.setProtocol(protocol);
		app.setName(name + ": " + pa_id);
		app.setInvestigation(investigation);
		protocolApps.add(app);

		// we iterate through all fields. Each field that is also a Measurement
		for (String field : tuple.getFields()) {
			// only include fields that are selected as measurement
			String mName = field;
			if (measurements.containsKey(mName)) {
				ObservedValue v = new ObservedValue();
				v.setTarget(target);
				v.setInvestigation(investigation);
				v.setFeature(measurements.get(mName));
				v.setValue(tuple.getString(field));
				v.setProtocolApplication(app);

				values.add(v);
			}
		}

		++rowCount;
		if (values.size() > BATCH_SIZE) {
			System.out.println("BATCH INSERT " + rowCount);
			storeValuesInDatabase();
		}
	}

	private void storeValuesInDatabase() throws DatabaseException,
			ParseException {

		//only add targets if they are not already there
		db.update(new ArrayList(targets.values()),
				DatabaseAction.ADD_IGNORE_EXISTING, Individual.NAME);
		//add the values
		db.add(this.values);
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

	public static void resetRowCount() {
		rowCount = 0;
	}

}
