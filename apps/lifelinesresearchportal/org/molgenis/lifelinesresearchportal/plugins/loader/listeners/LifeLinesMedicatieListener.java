package org.molgenis.lifelinesresearchportal.plugins.loader.listeners;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Tuple;

public class LifeLinesMedicatieListener extends ImportTupleListener
{
	private final int BATCH_SIZE = 1000;

	Protocol protocol;
	private final Map<String, Measurement> measurements = new LinkedHashMap<String, Measurement>();
	private final List<ObservedValue> values = new ArrayList<ObservedValue>();
	private final Map<String, Individual> targets = new HashMap<String, Individual>();
	private final Map<String, ProtocolApplication> protocolApps = new HashMap<String, ProtocolApplication>();

	private Logger logger;

	private int rowCount = 0;
	private int valueCount = 0;
	private int batchCount = 0;

	public LifeLinesMedicatieListener(Database db, Protocol protocol) throws DatabaseException
	{
		super(protocol.getName(), db);

		this.logger = Logger.getLogger("LLimport(" + protocol.getName() + ")");
		
		//remove all Measurement for this protocol
		try {
			protocol = Protocol.findById(db, protocol.getId());
		} catch (ParseException e) {
//			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Measurement> mList = db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures()).find();
		protocol.getFeatures().clear();
		protocol.getFeatures_Name().clear();
		
		//update mref
		db.update(protocol);
		
		//remove measurement
		db.remove(mList);
		
		this.protocol = protocol;
	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception
	{
		// get reference to individual
		String pa_id = tuple.getString("PA_ID");
		if (pa_id == null) throw new Exception("PA_ID missing for protocol " + protocol.getName() + 
				" in tuple: " + tuple);

		Individual target = new Individual();
		target.setName(pa_id);
		target.setInvestigation(protocol.getInvestigation());
		targets.put(target.getName(), target);
		
		ProtocolApplication app = new ProtocolApplication();
		app.setProtocol(protocol);
		app.setName(protocol.getName() + "_" + pa_id + "_" + line_number);
		app.setInvestigation(protocol.getInvestigation());
		protocolApps.put(app.getName(), app);
		
		// each unique measurement needs to be logged (check for divergent
		// units)
		String atccode = tuple.getString("ATCCODE");
		if (measurements.get(atccode) == null)
		{
			// ATCCODE
			Measurement m = new Measurement();
			m.setName(atccode);
			m.setDataType("bool");
			m.setInvestigation(protocol.getInvestigation());
			protocol.getFeatures_Name().add(m.getName());
			measurements.put(m.getName(), m);
			
			Measurement m2 = new Measurement();
			m2.setName(atccode+"_REDEN");
			m2.setDataType("string");
			m2.setInvestigation(protocol.getInvestigation());
			protocol.getFeatures_Name().add(m2.getName());
			measurements.put(m2.getName(), m2);
		}

		ObservedValue v = new ObservedValue();
		v.setTarget_Name(tuple.getString("PA_ID"));
		v.setInvestigation(protocol.getInvestigation());
		v.setFeature(measurements.get(atccode));
		v.setValue("yes");
		v.setProtocolApplication(app);
		
		ObservedValue v2 = new ObservedValue();
		v2.setTarget_Name(tuple.getString("PA_ID"));
		v2.setInvestigation(protocol.getInvestigation());
		v2.setFeature(measurements.get(atccode+"_REDEN"));
		v2.setValue(tuple.getString("REDEN"));
		v2.setProtocolApplication(app);
		
		values.add(v);
		values.add(v2);

		valueCount++;
		batchCount++;

		++rowCount;
		if (batchCount > BATCH_SIZE)
		{
			logger.info("parsed row: " + rowCount + "(valuecount='" + valueCount + "')");
			storeValuesInDatabase();
			batchCount = 0;
		}

	}

	private void storeValuesInDatabase() throws DatabaseException, ParseException
	{
		// add protocol applications
		db.add(new ArrayList<ProtocolApplication>(protocolApps.values()));
				
		// only add targets if they are not already there
		db.update(new ArrayList<Individual>(targets.values()), DatabaseAction.ADD_IGNORE_EXISTING, Individual.NAME);

		// only add measurements if they are not already there
		db.update(new ArrayList<Measurement>(measurements.values()), DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME);
		
		//update the mref (using name so we need to clear ids)
		protocol.getFeatures().clear();
		db.update(protocol);
		
		// clear the targets for next batch
		this.targets.clear();
		
		// clear the measurements for next batch
		this.measurements.clear();

		// add the values
		db.add(this.values);

		// clear the values and protocol applications
		values.clear();
		protocolApps.clear();
	}

	@Override
	public void commit() throws DatabaseException, ParseException
	{
		storeValuesInDatabase();
	}
}
