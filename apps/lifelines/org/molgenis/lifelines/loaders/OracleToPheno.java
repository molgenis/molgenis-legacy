package org.molgenis.lifelines.loaders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;


/**
 *
 * @author jorislops
 */
public class OracleToPheno implements Runnable {
	private final String sql = "SELECT %s FROM %s.%s WHERE pa_id > %s and pa_id <= %s and STID = %s";
	
    private final String tableName;
    private final String schemaName;
    private final String fields;
    private final Integer investigationId;
    private final Integer studyId;
    
    private final int protocolId;
    
    private final int startPA_ID;
    private final int endPA_ID;
    
    private static int activeThreadCount = 0;
    
    private final CountDownLatch doneSignal;

    private final EntityManagerFactory emf;
    
    public OracleToPheno(EntityManagerFactory emf, String schemaName, String tableName, String fields, Integer studyId, Integer investigationId, 
    		int protocolId, int startPA_ID, int endPA_ID,
    			CountDownLatch doneSignal
    		) throws Exception {
    	this.emf = emf;
    	this.schemaName = schemaName;
    	this.tableName = tableName;
    	this.fields = fields;
    	this.studyId = studyId;
    	this.investigationId = investigationId;
    	
    	this.startPA_ID = startPA_ID;
    	this.endPA_ID = endPA_ID;
    	
        this.protocolId = protocolId;
        
        this.doneSignal = doneSignal;
        
        synchronized(OracleToPheno.class) {
        	activeThreadCount++;
        }
    }
    
    private static int recordId  = 0;

	public int getProtocolId() {
		return protocolId;
	}

	@Override
	public void run() {
		System.out.println("start!!!");
        int targetCount = 0;
        Map<Integer, ObservationTarget> paidTargets = null;
		try {
		       Connection con = LoaderUtils.getConnection();
		        Statement stm = con.createStatement();
		        String exec = String.format(sql, fields, schemaName, tableName, startPA_ID, endPA_ID, studyId);
		        System.out.println(exec);
		        ResultSet rs = stm.executeQuery(exec);
		        ResultSetMetaData rsmd = rs.getMetaData();
		        
		        

		        
//		        JpaDatabase db = new JpaDatabase();
//		        EntityManager em = db.getEntityManager();
		        
//		        EntityManager em = db.createEntityManager();
	        
		       
		        EntityManager em = emf.createEntityManager();
		        Investigation investigation = em.find(Investigation.class, investigationId);
		        
//		        Investigation investigation = em.createQuery("SELECT Inv FROM Investigation Inv WHERE Inv.name = :name", Investigation.class)
//		                .setParameter("name", investigationName)
//		                .getSingleResult();
		        

		        List<Measurement> measurements = new ArrayList<Measurement>();
		        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
		        	if(rsmd.getColumnName(i).equals("RNUM")) {
		        		continue;
		        	}
		        	
		            String columnName = rsmd.getColumnName(i);        
		            Measurement m = em.createQuery("SELECT m FROM Measurement m WHERE m.name = :name AND m.investigation.id = :investigation", Measurement.class)
		              .setParameter("name", columnName)
		              .setParameter("investigation", investigation.getId())
		              .getSingleResult();
		            measurements.add(m);
		        }
		        
		        int patientIdColumn = -1;
		        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
		        	if(rsmd.getColumnName(i).equalsIgnoreCase("PA_ID")) {
		        		patientIdColumn = i;
		        	}
		        }
		        
		        if(patientIdColumn == -1) {
		        	throw new Exception("PA_ID column not found!");
		        }
		        
		        //retrieve targets from this investigation between the boundray's of PA_ID
		        List<ObservationTarget> targets = em.createQuery("SELECT t FROM ObservationTarget t WHERE t.paid > :startPA_ID and t.paid <= :endPA_ID AND t.investigation.id = :invId", ObservationTarget.class)
		        									.setParameter("startPA_ID", this.startPA_ID)
		        									.setParameter("endPA_ID", this.endPA_ID)
		        									.setParameter("invId", this.investigationId)
		        									.getResultList();
//		        List<ObservationTarget> targets = (List<ObservationTarget>) investigation.getInvestigationObservationTargetCollection();
		        paidTargets = new HashMap<Integer, ObservationTarget>();
		        for(ObservationTarget t : targets) {
		        	paidTargets.put(t.getPaid(), t);
		        }        	
		        
		        int w = 0;
		        em.getTransaction().begin();
		        while(rs.next()) {
		        	++w;
		            ObservationTarget target = null;
		            
		            int recId = 0;
		            synchronized(OracleToPheno.class) {
		            	recId = recordId;
		            	recordId++;
		            }

                	Integer paId = rs.getInt(patientIdColumn);
                	if(paidTargets.containsKey(paId)) {
                		target = paidTargets.get(paId);
                	} else {                	
	                    target = new ObservationTarget();
	                    target.setInvestigation(investigation);
	                    target.setName(paId.toString());
	                    target.setPaid(paId);
	                    em.persist(target);
	                    paidTargets.put(paId, target);
	                    targetCount++;
                	}
		            
		            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
		                Object value = rs.getObject(i);
		                ObservedValue ov = new ObservedValue();
		                ov.setFeature(measurements.get(i-1));
		                if(value != null) {
		                	ov.setRecordId(recId);
		                	ov.setProtocolId(protocolId);
		                    ov.setValue(value.toString());
		                } 
		                ov.setInvestigation(investigation);
		               
		                ov.setTarget(target);
		                if(value != null) {
		                    em.persist(ov);
		                }
		            }		            
		            
		            if(w % 1000 == 0) {
		            	em.flush();
		            	em.clear();
		            }
		            
		            if(recordId % 100 == 0) {
		            	System.out.println(recordId);
		            }          
		        }  
		        
		        rs.close();
		        con.close();
		        
		        em.flush();
		        em.clear();
		        em.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        synchronized(OracleToPheno.class) {
        	activeThreadCount--;
        }
        doneSignal.countDown();
	}
    
    public static int getActiveTHreadCount() {
        synchronized(OracleToPheno.class) {
        	return activeThreadCount;
        }    	
    }
    
}