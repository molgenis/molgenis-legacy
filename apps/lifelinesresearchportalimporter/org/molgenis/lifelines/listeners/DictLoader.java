package org.molgenis.lifelines.listeners;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.CsvFileReader;
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
public class DictLoader {

	private Map<String, Protocol> protocols = new LinkedHashMap<String, Protocol>();
	
	private final Investigation investigation;
	private final boolean shareMeasurements; 

	private final EntityManager em;

	private final List<String> protocolsToImport;

	private final CsvFileReader csvFileReader; 
	
	public DictLoader(final File file, final String encoding, 
			final Investigation investigation, final boolean shareMeasurements, final EntityManager em, List<String> protocolsToImport) throws IOException, DataFormatException {
		this.investigation = investigation;
		this.shareMeasurements = shareMeasurements;
		this.protocolsToImport = protocolsToImport;
		
		this.em = em;
		
		this.csvFileReader = new CsvFileReader(file, encoding);
	}

	public void load() throws Exception {
		em.setFlushMode(FlushModeType.AUTO); //force to reload		
		em.getTransaction().begin();
		
		final Iterator<Tuple> iterator = csvFileReader.iterator();
		while(iterator.hasNext()) {
			final Tuple tuple = iterator.next();
			
			final String protocolName = tuple.getString("TABNAAM");
			
			if(protocolsToImport != null) {
				if(!protocolsToImport.contains(protocolName.toUpperCase())) {
					return;
				}
			}		
			
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
			final List<Measurement> ms = em.createQuery("SELECT m FROM Measurement m WHERE m.name = :name", Measurement.class)
				.setParameter("name", measurmentName)
				.getResultList();
			
			//List<Measurement> ms = db.query(Measurement.class).eq(Measurement.NAME, measurmentName).find();
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
		
		em.getTransaction().commit();	
		em.setFlushMode(FlushModeType.COMMIT);
	}

	public Map<String, Protocol> getProtocols() {
		return protocols;
	}
}
