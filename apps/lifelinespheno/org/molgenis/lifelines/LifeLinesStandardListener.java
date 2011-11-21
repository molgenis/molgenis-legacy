package org.molgenis.lifelines;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.lifelines.listeners.ImportTupleListener;
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
	int BATCH_SIZE = 1000;

	final List<Measurement> measurements;
	final List<ProtocolApplication> protocolApps = new ArrayList<ProtocolApplication>();
	final List<ObservedValue> values = new ArrayList<ObservedValue>();

	final Protocol protocol;
	
	public LifeLinesStandardListener(int protocolId, String name, Database db)
			throws DatabaseException {
		super(name, db);
		
		protocol = db.findById(Protocol.class, protocolId);
		measurements = (List<Measurement>)(List)protocol.getFeatures();


	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		// get reference to individual
		String pa_id = tuple.getString("PA_ID");

		ProtocolApplication app = new ProtocolApplication();
		app.setProtocol_Name(name);
		app.setName(name + ": " + pa_id);
		protocolApps.add(app);

		// we iterate through all fields. Each field that is also a Measurement
		for (String field : tuple.getFields()) {
			//only include fields that are selected as measurement
			String mName = name +"_"+ field;
			if (measurements.contains(mName))

			{
				ObservedValue v = new ObservedValue();
				v.setTarget_Name(pa_id);
				v.setFeature_Name(mName);
				v.setValue(tuple.getString(field));
				v.setProtocolApplication_Name(app.getName());

				values.add(v);
			}
		}

		if (values.size() > BATCH_SIZE) {
			db.add(protocolApps);
			protocolApps.clear();

			db.add(values);
			values.clear();
		}
	}

	@Override
	public void commit() throws DatabaseException {
		db.add(protocolApps);
		protocolApps.clear();

		db.add(values);
		values.clear();

	}

}
