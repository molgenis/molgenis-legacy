package org.molgenis.lifelinesresearchportal.listeners;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
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

	private Map<String, Protocol> protocols = new LinkedHashMap<String, Protocol>();
	private List<Measurement> measurements = new ArrayList<Measurement>();
	private final Investigation investigation;
	
	public VwDictListener(Investigation investigation, String name, Database db) {
		super(name, db);
		this.investigation = investigation;
	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		
		if (tuple.getString("TABNAAM") != null && !tuple.getString("TABNAAM").equals("")) {
			String protocolName = tuple.getString("TABNAAM");
			
			//create new protocol if not yet known
			Protocol p = protocols.get(protocolName);
			if(p == null)
			{
				p = new Protocol();
				p.setName(protocolName);
				p.setInvestigation(investigation);
			}
			
			if (tuple.getString("VELD") != null) {
				Measurement m = new Measurement();
				m.setName(p.getName() + "_" + tuple.getString("VELD")); // temporarily prepend measurement name with table (protocol) name
				m.setInvestigation(investigation);
				m.setDataType(convertDataType(tuple.getString("VLDTYPE")));
				m.setDescription(tuple.getString("OMSCHR"));
				measurements.add(m);
				p.getFeatures_Name().add(m.getName()); // link to protocol
			}
			
			protocols.put(protocolName, p);
		}
	}
	
	private String convertDataType(String string) {
		if(string == null)
			return "string";
		if(string.startsWith("NUMMER"))
				return "int";
		if(string.startsWith("DATUM"))
			return "datetime";
		//else
			return "string";
		
	}

	public void commit() throws DatabaseException
	{
		db.add(measurements);
		
		List<Protocol> protocolList = new ArrayList(protocols.values());
		db.add(protocolList);
		for(Protocol p: protocolList) protocols.put(p.getName(), p); 
	}

	public Map<String, Protocol> getProtocols() {
		return protocols;
	}
}
