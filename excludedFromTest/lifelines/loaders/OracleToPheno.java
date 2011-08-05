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

import javax.persistence.EntityManager;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

/**
 *
 * @author jorislops
 */
public class OracleToPheno {
    private String sql = "SELECT * FROM LLPOPER.%s";
    private String tableName = "OV027LABDATA";
    private Integer investigationId = null;
    
    private static int protocolId = 0;
    
//    public static void main(String[] args) throws Exception {
//        new OracleToPheno();
//    }

    public OracleToPheno(String tableName, Integer investigationId) throws Exception {
    	this.tableName = tableName;
    	this.investigationId = investigationId;

        protocolId++;
    	
        Connection con = LoaderUtils.getConnection();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(String.format(sql, tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        
        JpaDatabase db = new JpaDatabase();
        EntityManager em = db.getEntityManager();
        
        Investigation investigation = em.find(Investigation.class, investigationId);
        
//        Investigation investigation = em.createQuery("SELECT Inv FROM Investigation Inv WHERE Inv.name = :name", Investigation.class)
//                .setParameter("name", investigationName)
//                .getSingleResult();
        
        List<Measurement> measurements = new ArrayList<Measurement>();
        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
            String columnName = rsmd.getColumnName(i);        
            Measurement m = em.createQuery("SELECT m FROM Measurement m WHERE m.name = :name AND m.investigation = :investigation", Measurement.class)
              .setParameter("name", columnName)
              .setParameter("investigation", investigation)
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
        Map<String, ObservationTarget> paidTargets = new HashMap<String, ObservationTarget>();
        for(ObservationTarget t : targets) {
        	paidTargets.put(t.getName().toString(), t);
        }        	

        
        int w = 0;
        
        em.getTransaction().begin();
        while(rs.next()) {
        	++w;
            ObservationTarget target = null;
            
            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
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
                	}
                }                                
                
                ObservedValue ov = new ObservedValue();
                ov.setFeature(measurements.get(i-1));
                if(value != null) {
                	ov.setRecordId(recordId);
                	ov.setProtocolId(protocolId);
                    ov.setValue(value.toString());
                } 
                ov.setInvestigation(investigation);
                ov.setTarget(target);
                if(value != null) {
                    em.persist(ov);
                }
            }
            if(w % 10 == 0) {
            	em.flush();
            	em.clear();
            }
            
            if(recordId % 100 == 0) {
            	System.out.println(recordId);
            }          
            recordId++;
        }   
        em.getTransaction().commit();
    }
    
    private static int recordId  = 0;

	public static int getProtocolId() {
		return protocolId;
	}
    
    
    
}