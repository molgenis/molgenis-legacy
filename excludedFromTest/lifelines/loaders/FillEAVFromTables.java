package lifelines.loaders;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;

import app.DatabaseFactory;

public class FillEAVFromTables {
    public static void main(String[] args) throws Exception {
    	String investigation = "Dataset9";
    	String schemaName = "llpoper";
        String schemaToExportView = null;
        String[] tableNames = new String[]{"LL_DATASET9"};    	
    	
        String databaseTarget = "oracle";
        
        Investigation inv = new Investigation();
        inv.setName(String.format("%s %s",investigation, new Date().toString()));        
        
        Database db = DatabaseFactory.create();
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        em.persist(inv);
        em.getTransaction().commit();
        
        long start = System.currentTimeMillis();
        int idxStart = 1;
        int protocolId = 0;
        
        final int RECORDS_PER_THREAD = 500;
        final int MAX_THREADS = 20;
        
        for(String tableName : tableNames) {
        	OracleToLifelinesPheno oracleToLifelinesPheno = new OracleToLifelinesPheno(schemaName, tableName, inv.getId());
        	
        	int N = (int) Math.ceil((float)oracleToLifelinesPheno.getRowCount() / RECORDS_PER_THREAD);
        	
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(N);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 200, TimeUnit.SECONDS, workQueue);
            
            CountDownLatch doneSignal = new CountDownLatch(N);
        	for(int i = 0; i < N; ++i) {
        	   executor.execute(new OracleToPheno(schemaName, tableName, inv.getId(), protocolId, idxStart, RECORDS_PER_THREAD-1, doneSignal));
        	   idxStart += RECORDS_PER_THREAD;
        	}
            
            Thread monitor = new Thread(new MyMonitorThread(executor));
            monitor.start();

        	//int N = 5;
            executor.shutdown(); //when all task are complete ThreadPoolExecutor is terminated --> program can end!!
       	   	doneSignal.await();  //wait for all tasks to finish
       	   
       	   	new EAVToView(schemaName, tableName, schemaToExportView, protocolId, databaseTarget, inv.getId());
        }
        
        long end = System.currentTimeMillis();        
        System.out.println("Time: " + (end - start) / 1000);
        
    }
}
