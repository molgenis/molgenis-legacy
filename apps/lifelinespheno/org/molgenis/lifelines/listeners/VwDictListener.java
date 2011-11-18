package org.molgenis.lifelines.listeners;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;

/**
 * This listener creates Measurement for each field and Protocol for each table in VW_DICT
 * 
 * TABNAAM
GROEP
VELDNR
VELD -> Measurement.name
VLDTYPE -> Measurement.dataType
OMSCHR -> Measurement.description

 * @author jorislops
 *
 */
public class VwDictListener extends ImportTupleListener {

	Map<String, Protocol> protocols = new LinkedHashMap<String,Protocol>();
	List<Measurement> measurements = new ArrayList<Measurement>();
	
	public VwDictListener(String name, Database db) {
		super(name, db);
	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		
		//create new protocol if not yet known
		Protocol p = protocols.get(tuple.getString("TABNAAM"));
		if(p == null)
		{
			p = new Protocol();
			p.setName(tuple.getString("TABNAAM"));
			protocols.put(tuple.getString("TABNAAM"), p);
		}
		
		Measurement m = new Measurement();
		m.setName(tuple.getString("TABNAAM")+"_"+tuple.getString("VELD"));
		m.setDataType("string");
		m.setDescription(tuple.getString("OMSCHR"));
		
		p.getFeatures_Name().add(m.getName());
		
		measurements.add(m);
	}
	
	public void commit() throws DatabaseException
	{
		db.add(new ArrayList<Protocol>(protocols.values()));
		db.add(measurements);
	}

}
