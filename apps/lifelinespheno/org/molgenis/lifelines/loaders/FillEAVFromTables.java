//package org.molgenis.lifelines.loaders;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//
//import org.apache.log4j.Logger;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.protocol.Protocol;
//
//public class FillEAVFromTables {
//	private static Logger log = Logger.getLogger(FillEAVFromTables.class);
//	
//    private static final int RECORDS_PER_THREAD = 500; 	//records each thread can process!
//    private static final int MAX_THREADS = 10;			//number of thread executing concurrently
//    private static final int THREAD_TIME_OUT_TIME = 200; //thread times out after 200 seconds
//		
//	//select number of buckets
//	private static String sqlNumerBuckets = "select ceil(count(*)/%d) from %s.%s";
//	
//	//divided table into N bucktes
//	private static String sqlBucketsByPA_ID = 
//		 "select  pa_idm, max(bucket) bucketm "
//		+" from    ( "
//	    +"	select  bucket, max(pa_id) pa_idm "
//	    +"    from    ( "
//	    +"      select  pa_id, ntile(%d) over (order by pa_id) bucket "
//	    +"      from    %s.%s "
//	    +"      where   stid = %d "
//	    +"					) "
//	    +"    group by bucket "
//	    +"    order by bucket "
//	    +"                  )"
//	    +"    group by pa_idm "
//	    +"   order by bucketm ";
//	
//	//result is of (tablename, a1,...,an')
//	private static String sqlGetTableWithColumns = 
//		"select tabnaam, LISTAGG(veld, ',') WITHIN GROUP (ORDER BY veld) AS velden "
//		+" from LLPOPER.publ_dict_studie "
//		+" where stid = %d "
//		+" group by tabnaam ";
//	
//    public static void main(String[] args) throws Exception {
// //   	PropertyConfigurator.configure("log4j.properties");
//    	
//    	int studyId = 101; //should be parameterized!
//    	
//    	
//    	log.info(String.format("[%s] Imported started into EAV (pheno model)", studyId));
//    	
//    	String schemaName = "llpoper";
//        String schemaToExportView = null;
//       
//        Investigation inv = new Investigation();
//        inv.setName(String.format("StudyId: %d Loaded: %s",studyId, new Date().toString()));    
//        
//        Map<String, Object> configOverrides = new HashMap<String, Object>();
//        configOverrides.put("hibernate.hbm2ddl.auto", "create-drop"); //FIXME: should be changed to validate for production		        
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis", configOverrides);
//        EntityManager em = emf.createEntityManager();
//        
//        String studyName = getStudyName(studyId, em);
//        
//        em.getTransaction().begin();
//        em.persist(inv);
//        em.getTransaction().commit();
//       
//        log.info(String.format("[%s] Investigation.name = %s", studyId, inv.getName()));
//        
//        long start = System.currentTimeMillis();
//       
//        
//        @SuppressWarnings("unchecked")
//		List<Object[]> tables = em.createNativeQuery(String.format(sqlGetTableWithColumns, studyId)).getResultList();
//		
//        for(Object[] tableRec : tables) {
//        	String tableName = (String)tableRec[0];
//        	String fieldNames = (String)tableRec[1];
//        	
//        	log.info(String.format("[%d-%s] Start importing table: %s", studyId, tableName, tableName));
//        	
//        	if(!fieldNames.toUpperCase().contains("PA_ID"))
//        	{
//        		log.warn(String.format("[%d-%s] Doesn't contain PA_ID column! So data is not loaded into EAV!", studyId, tableName));
//        		continue;
//        	}
//        	if(!fieldNames.toUpperCase().contains("STID"))
//        	{        	
//        		log.warn(String.format("[%d-%s] Table: %s doesn't contain STID column! So data is not loaded into EAV!", studyId, tableName, tableName));
//        		continue;
//        	}        	
//        	
//        	log.trace(String.format("[%d-%s]Start creating metaData.", studyId, tableName));
//        	OracleToLifelinesPheno oracleToLifelinesPheno =	new OracleToLifelinesPheno(studyId, em, schemaName, tableName, fieldNames, inv.getId());
//        	log.trace(String.format("[%d-%s] Meta data succesfully stored.", studyId, tableName));
//
//                Protocol protocol = oracleToLifelinesPheno.getProtocol();
//                int protocolId = protocol.getId();
//                
//        	BigDecimal numberOfBuckets = (BigDecimal) em.createNativeQuery(
//        			String.format(sqlNumerBuckets, RECORDS_PER_THREAD, schemaName, tableName))
//        			.getSingleResult();
//        	int N = numberOfBuckets.toBigInteger().intValue();
//        	
//        	log.info(String.format("[%d-%s] Table is divided into %d buckets", studyId, tableName, N));
//        	        	
//        	@SuppressWarnings("unchecked")
//			List<Object[]> results = em.createNativeQuery(
//        				String.format(sqlBucketsByPA_ID, N, schemaName, tableName, studyId)).getResultList();
//        	
//            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(N);
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, THREAD_TIME_OUT_TIME, TimeUnit.SECONDS, workQueue);
//                    CountDownLatch doneSignal = new CountDownLatch(N);
//                    int prevPA_ID = 0;
//                    if (results.size() > 0) {
//                        for (int i = 0; i < N; ++i) {
//                            int endPA_ID = ((BigDecimal) results.get(i)[0]).intValue();
//                            executor.execute(new OracleToPheno(emf, schemaName, tableName, fieldNames, studyId, inv.getId(), protocolId, prevPA_ID, endPA_ID, doneSignal));
//                            prevPA_ID = endPA_ID;
//                        }
//                        Thread monitor = new Thread(new MyMonitorThread(executor, tableName));
//                        monitor.start();
//
//                        executor.shutdown(); //when all task are complete ThreadPoolExecutor is terminated 
//                        doneSignal.await();  //wait for all tasks to finish (what will happen in case of timeout?)
//                        log.trace(String.format("[%d-%s] Data for Table is loaded", studyId, tableName));
//                    } else {
//                        log.trace(String.format("[%d-%s] There is no Data in Table for study ", studyId, tableName));
//                    }
// 
//       	   	log.trace(String.format("[%d-%s] Start EAVToView", studyId, tableName));
//       	   	String viewName = studyName + "_" + tableName;
//                
//       	   	new EAVToView(studyId, schemaName, viewName, 
//                        (List<Measurement>)(List)oracleToLifelinesPheno.getMeasurements(), 
//                        schemaToExportView, protocol, oracleToLifelinesPheno.getInvestigation());
//       	   	log.trace(String.format("[%d-%s] End EAVToView", studyId, tableName));
//       	   	       	   	
//       	   	log.info(String.format("[%d-%s] Processing of data is completed!", studyId, tableName));
//        }
//        em.close();
//        
//        long end = System.currentTimeMillis();        
//        long seconds = (end - start) / 1000;
//        log.info(String.format("[%d] Data Loading completed in %d seconds", studyId, seconds));
//        log.info(String.format("[%d] End of import", studyId));
//    }
//    
//    private static String getStudyName(int stid, EntityManager em) {
//    	String sql = "select studie from llpoper.studie where stid = :stid";
//    	
//    	String studyName = (String)em.createNativeQuery(sql).setParameter("stid", stid).getSingleResult();
//    	return studyName;
//    	
//    }
//}
