package org.molgenis.lifelines.loaders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.molgenis.organization.Investigation;

public class FillEAVFromTables {
	public static String sqlNumerBuckets = "select ceil(count(*)/%d) from %s.%s";
	public static String sqlBucketsByPA_ID = 
		 "select  pa_idm, max(bucket) bucketm "
		+" from    ( "
	    +"	select  bucket, max(pa_id) pa_idm "
	    +"    from    ( "
	    +"      select  pa_id, ntile(%d) over (order by pa_id) bucket "
	    +"      from    %s.%s "
	    +"      where   stid = %d "
	    +"					) "
	    +"    group by bucket "
	    +"    order by bucket "
	    +"                  )"
	    +"    group by pa_idm "
	    +"   order by bucketm ";
	
	public static String sqlGetTableWithColumns = 
		"select 'll_' || tabnaam, LISTAGG(veld, ',') WITHIN GROUP (ORDER BY veld) AS velden "
		+" from LLPOPER.publ_dict_studie "
		+" group by tabnaam ";
	
    public static void main(String[] args) throws Exception {
    	int studyId = 101;
    	String schemaName = "llpoper";
        String schemaToExportView = null;
        String[] tableNames = new String[]{"ll_bloeddrukavg", "ll_ecgparam"};    	
   	
        String databaseTarget = "oracle";
        
        Investigation inv = new Investigation();
        inv.setName(String.format("StudyId: %d Loaded: %s",studyId, new Date().toString()));        
        
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        configOverrides.put("hibernate.hbm2ddl.auto", "create-drop");		        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis", configOverrides);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(inv);
        em.getTransaction().commit();
       
        long start = System.currentTimeMillis();
        int protocolId = 0;
        
        final int RECORDS_PER_THREAD = 500;
        final int MAX_THREADS = 10;
        
        
        @SuppressWarnings("unchecked")
		List<Object[]> tables = em.createNativeQuery(sqlGetTableWithColumns).getResultList();
        
        for(Object[] tableRec : tables) {
        	String tableName = (String)tableRec[0];
        	String fieldNames = (String)tableRec[1];
        	if(!fieldNames.toUpperCase().contains("PA_ID"))
        	{
        		System.err.println(String.format("Table: %s doesn't contain PA_ID column! So data is not loaded into EAV!", tableName));
        		continue;
        	}
        	if(!fieldNames.toUpperCase().contains("STID"))
        	{        	
        		System.err.println(String.format("Table: %s doesn't contain STID column! So data is not loaded into EAV!", tableName));        		        		
        	}
        	
        	
        	new OracleToLifelinesPheno(em, schemaName, tableName, fieldNames, inv.getId());
        	
        	BigDecimal numberOfBuckets = (BigDecimal) em.createNativeQuery(
        			String.format(sqlNumerBuckets, RECORDS_PER_THREAD, schemaName, tableName))
        			.getSingleResult();
        	int N = numberOfBuckets.toBigInteger().intValue();
        	
        	@SuppressWarnings("unchecked")
			List<Object[]> results = em.createNativeQuery(
        				String.format(sqlBucketsByPA_ID, N, schemaName, tableName, studyId)).getResultList();
        	
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(N);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 200, TimeUnit.SECONDS, workQueue);
            
            CountDownLatch doneSignal = new CountDownLatch(N);
            int prevPA_ID = 0;
            for(int i = 0; i < N; ++i) {        		
        	   int endPA_ID = ((BigDecimal) results.get(i)[0]).intValue();
        	   executor.execute(new OracleToPheno(emf, schemaName, tableName, fieldNames, studyId, inv.getId(), protocolId, prevPA_ID, endPA_ID, doneSignal));
        	   prevPA_ID = endPA_ID;
        	}
            
            Thread monitor = new Thread(new MyMonitorThread(executor));
            monitor.start();

        	//int N = 5;
            executor.shutdown(); //when all task are complete ThreadPoolExecutor is terminated --> program can end!!
       	   	doneSignal.await();  //wait for all tasks to finish
       	   
       	   	new EAVToView(schemaName, tableName, fieldNames, schemaToExportView, protocolId, databaseTarget, inv.getId());
       	   	protocolId++;
        }
        em.close();
        
        long end = System.currentTimeMillis();        
        System.out.println("Time: " + (end - start) / 1000);
    }
}
