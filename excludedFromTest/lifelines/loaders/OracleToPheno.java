/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

/**
 *
 * @author jorislops
 */
public class OracleToPheno implements Runnable {
    private final String sql = "SELECT * "
    	+"FROM " 
    	+"( select a.*, rownum rnum "
    	+"from (select * from %s.%s) a "
    	+" where rownum <= %d ) "
    	+" where rnum >= %d";
    
    private final String tableName;
    private final String schemaName;
    private final Integer investigationId;
    
    private final int protocolId;
    
    private final int offset;
    private final int limit;
    
    private static int activeThreadCount = 0;
    
    private final CountDownLatch doneSignal;

    public OracleToPheno(String schemaName, String tableName, Integer investigationId, 
    		int protocolId, int offset, int limit,
    			CountDownLatch doneSignal
    		) throws Exception {
    	this.schemaName = schemaName;
    	this.tableName = tableName;
    	this.investigationId = investigationId;

    	this.offset = offset;
    	this.limit = limit;
    	
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
        Map<String, ObservationTarget> paidTargets = null;
		try {
		       Connection con = LoaderUtils.getConnection();
		        Statement stm = con.createStatement();
		        String exec = String.format(sql, schemaName, tableName, offset+limit, offset);
		        System.out.println(exec);
		        ResultSet rs = stm.executeQuery(exec);
		        ResultSetMetaData rsmd = rs.getMetaData();
		        
		        
//		        Map<String, Object> configOverrides = new HashMap<String, Object>();
//		        configOverrides.put("hibernate.hbm2ddl.auto", "validate");		        
//		        EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis", configOverrides);
//		        EntityManager em = emf.createEntityManager();
		        
		        JpaDatabase db = new JpaDatabase();
		        EntityManager em = db.getEntityManager();
		        
		        //EntityManager em = db.createEntityManager();
	        
		       
		        
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
		        
		        List<ObservationTarget> targets = (List<ObservationTarget>) investigation.getInvestigationObservationTargetCollection();
		        paidTargets = new HashMap<String, ObservationTarget>();
		        for(ObservationTarget t : targets) {
		        	paidTargets.put(t.getName().toString(), t);
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
		            
		            for(int i = 1; i <= rsmd.getColumnCount()-1; ++i) {
		                Object value = rs.getObject(i);
		                if(i == 1) {
		                	Object paId = rs.getObject(patientIdColumn);
		                	if(paidTargets.containsKey(paId.toString())) {
		                		target = paidTargets.get(paId.toString());
		                	} else {                	
			                    target = new ObservationTarget();
			                    target.setInvestigation(investigation);
			                    target.setName(paId.toString());
			                    em.persist(target);
			                    paidTargets.put(paId.toString(), target);
			                    targetCount++;
		                	}
		                }                                
		                
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
		            //recordId++;
		        }  
		        
		        rs.close();
		        con.close();
		        
		        em.flush();
		        em.clear();
		        em.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("end!!! targetsCount" + paidTargets.size());
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