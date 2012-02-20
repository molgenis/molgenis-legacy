package org.molgenis.lifelines.listeners;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

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
	
	private final Investigation investigation;
	private final boolean shareMeasurements; 

	private final EntityManager em; 
	
	public VwDictListener(Investigation investigation, String name, boolean shareMeasurements, Database db) {
		super(name, db);
		this.investigation = investigation;
		this.shareMeasurements = shareMeasurements;
		
		em = db.getEntityManager();
		
		em.setFlushMode(FlushModeType.AUTO); //force to reload		
		em.getTransaction().begin();
	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		
		String protocolName = tuple.getString("TABNAAM");
		
		//create new protocol if not yet known
		Protocol p = protocols.get(protocolName);
		if(p == null)
		{
			p = new Protocol();
			p.setName(protocolName);
			//p.setInvestigation_Id(investigationId);
			p.setInvestigation(investigation);
			protocols.put(protocolName, p);
		}
		
		String measurmentName = null;
		if(shareMeasurements) {
			measurmentName = tuple.getString("VELD");
		} else {
			measurmentName = protocolName + "_" +tuple.getString("VELD");	
		}
			
		
		Measurement m = null;
		List<Measurement> ms = db.query(Measurement.class).eq(Measurement.NAME, measurmentName).find();
		if(!ms.isEmpty()) {
			m = ms.get(0);
		} else {		
			m = new Measurement();
			m.setName(measurmentName);
			m.setDataType( tuple.getString("VLDTYPE")  );
			m.setDescription(tuple.getString("OMSCHR"));
			m.setInvestigation(investigation);
		}
		p.getFeatures().add(m);	
		
		System.out.println("Protocol: " +p.getName() +" Mn: " + m.getName());
		
		em.persist(p);
	}
	
	public void commit() throws DatabaseException
	{
		em.getTransaction().commit();	
		em.setFlushMode(FlushModeType.COMMIT);
	}

	public Map<String, Protocol> getProtocols() {
		return protocols;
	}
}
