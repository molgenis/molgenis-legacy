package org.molgenis.lifelinesresearchportal.loaders;
//package org.molgenis.lifelines.loaders;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//
//import org.apache.log4j.Logger;
//import org.molgenis.lifelinespheno.LLTarget;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.ObservationTarget;
//import org.molgenis.pheno.ObservedValue;
//import org.molgenis.protocol.Protocol;
//import org.molgenis.protocol.ProtocolApplication;
//
//
///**
// *
// * @author joris lops
// */
//public class OracleToPheno implements Runnable {
//	private final static int FLUSH_MOD = 1000; //number of records to process before flushed to database and removed from cache. 
//	
//	private static Logger log = Logger.getLogger(OracleToPheno.class);
//	
//	private final String selectBucketSql = "SELECT %s FROM %s.%s WHERE pa_id > %s and pa_id <= %s and STID = %s";
//	
//    private final String tableName;
//    private final String schemaName;
//    private final String fields;
//    private final Integer investigationId;
//    private final Integer studyId;
//    
//    //private final int protocolId;
//    private final Protocol protocol;
//    
//    private final int startPA_ID;
//    private final int endPA_ID;
//    
//    private final CountDownLatch doneSignal;
//
//    private final EntityManagerFactory emf;
//    private final EntityManager em;
//    
//    public OracleToPheno(EntityManagerFactory emf, String schemaName, String tableName, String fields, Integer studyId, Integer investigationId, 
//    		int protocolId, int startPA_ID, int endPA_ID,
//    			CountDownLatch doneSignal
//    		) throws Exception {
//    	this.emf = emf;
//    	this.em = emf.createEntityManager();
//    	
//    	this.schemaName = schemaName;
//    	this.tableName = tableName;
//    	this.fields = fields;
//    	this.studyId = studyId;
//    	this.investigationId = investigationId;
//    	
//    	this.startPA_ID = startPA_ID;
//    	this.endPA_ID = endPA_ID;
//    	
//        //this.protocolId = protocolId;
//        
//        this.protocol = em.find(Protocol.class, protocolId); 
//        
//        this.doneSignal = doneSignal;
//    }
//    
//    private static int recordId  = 0;
//
////	public int getProtocolId() {
////		return protocolId;
////	}
//
//	@Override
//	public void run() {
//		log.trace(String.format("[%d-%s] Thread started", studyId, tableName));
//        int targetCount = 0;
//        Map<Integer, LLTarget> paidTargets = null;
//		try {
//		       	final Connection con = LoaderUtils.getConnection();
//		        final Statement stm = con.createStatement();
//		        final String exec = String.format(selectBucketSql, fields, schemaName, tableName, startPA_ID, endPA_ID, studyId);
//            	log.debug(String.format("[%d-%s] %s", studyId, tableName, exec));
//		        final ResultSet rs = stm.executeQuery(exec);
//		        final ResultSetMetaData rsmd = rs.getMetaData();
//		        
//		        final Investigation investigation = em.find(Investigation.class, investigationId);
//		        
//		        final List<Measurement> measurements = new ArrayList<Measurement>();
//		        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
//		            final String columnName = rsmd.getColumnName(i);        
//		            final String jql = "SELECT m FROM Measurement m WHERE m.name = :name AND m.investigation.id = :investigation";
//		            final Measurement m = em.createQuery(jql, Measurement.class)
//		              .setParameter("name", columnName)
//		              .setParameter("investigation", investigation.getId())
//		              .getSingleResult();
//		            measurements.add(m);
//		        }
//		        
//		        int patientIdColumn = -1;
//		        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
//		        	if(rsmd.getColumnName(i).equalsIgnoreCase("PA_ID")) {
//		        		patientIdColumn = i;
//		        	}
//		        }
//		        
//		        if(patientIdColumn == -1) {
//		        	throw new Exception("PA_ID column not found!");
//		        }
//		        
//		        //retrieve targets from this investigation between the boundray's of PA_ID
//		        final List<ObservationTarget> targets = em.createQuery("SELECT t FROM ObservationTarget t WHERE t.paid > :startPA_ID and t.paid <= :endPA_ID AND t.investigation.id = :invId", ObservationTarget.class)
//		        									.setParameter("startPA_ID", this.startPA_ID)
//		        									.setParameter("endPA_ID", this.endPA_ID)
//		        									.setParameter("invId", this.investigationId)
//		        									.getResultList();
//		        paidTargets = new HashMap<Integer, LLTarget>();
//		        for(ObservationTarget t : targets) {
//                            //paidTargets.put(t.getPaid(), t);
//		        }        	
//		        
//		        int w = 0;
//		        em.getTransaction().begin();
//		        while(rs.next()) {
//		        	++w;
//		        	LLTarget target = null;
//		            
//		            int recId = 0;
//		            synchronized(OracleToPheno.class) {
//		            	recId = recordId;
//		            	recordId++;
//		            }
//		            
//		            Date time = new Date();
//		            ProtocolApplication pa = new ProtocolApplication();
//		            pa.setDescription(tableName + new Date().toString());
//		            pa.setInvestigation(investigationId);
//		            pa.setName(tableName + time.toString());
//		            pa.setProtocol(protocol);
//		            pa.setTime(time);
//
//                	Integer paId = rs.getInt(patientIdColumn);
//                	if(paidTargets.containsKey(paId)) {
//                		target = paidTargets.get(paId);
//                	} else {                	
//	                    target = new LLTarget();
//	                    target.setInvestigation(investigation);
//	                    target.setName(paId.toString());
//	                    target.setPaid(paId);
//	                    em.persist(target);
//	                    paidTargets.put(paId, target);
//	                    targetCount++;
//                	}
//		            
//		            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
//		                Object value = rs.getObject(i);
//		                ObservedValue ov = new ObservedValue();
//		                ov.setFeature(measurements.get(i-1));
//		                if(value != null) {
//		                	//ov.setRecordId(recId);
//		                	ov.setProtocolApplication(pa);
//		                	//ov.setProtocolId(protocolId);
//		                    ov.setValue(value.toString());
//		                } 
//		                ov.setInvestigation(investigation);
//		               
//		                ov.setTarget(target);
//		                if(value != null) {
//		                    em.persist(ov);
//		                }
//		            }		            
//		            
//		            if(w % FLUSH_MOD == 0) {
//		            	log.trace(String.format("[%d-%s] RecordId %d flushed", studyId, tableName, recordId));
//		            	em.flush();
//		            	em.clear();
//		            }		            
//		        }  
//		        rs.close();
//		        con.close();
//		        
//		        em.flush();
//		        em.clear();
//		        em.getTransaction().commit();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//        doneSignal.countDown();
//		log.trace(String.format("[%d-%s] Thread ended", studyId, tableName));
//	}    
//}