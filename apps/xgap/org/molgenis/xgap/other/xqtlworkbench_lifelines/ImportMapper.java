//package org.molgenis.xgap.other.xqtlworkbench_lifelines;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import javax.persistence.EntityManager;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.protocol.Protocol;
//import org.molgenis.util.CsvFileReader;
//import org.molgenis.util.CsvReader;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.ThreadReaders.CSVFileSpliter;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.LifeLinesStandardListener;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.VWCategoryListener;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.VwDictListener;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.loaders.EAVToView;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.loaders.LoaderUtils;
//import org.molgenis.xgap.other.xqtlworkbench_lifelines.loaders.MyMonitorThread;
//
//import app.DatabaseFactory;
//
///**
// * This class is responsible for mapping tuples from a source schema to entities
// * in MOLGENIS. The tuples can come from a CSV file or from SQL queries on some
// * database. The target schema is molgenis.pheno
// */
//public class ImportMapper {
//
//	private static final int MAX_THREADS = 20;
//	private static final int THREAD_TIME_OUT_TIME = 60;
//	
//	
//	/** for testing only!
//	 * @throws Exception */
//	public static void main(String[] args) throws Exception
//	{
//		//path to directory with csv files
//		String path = "C:\\lifelinesdata\\";
//		
//		//target for output, either CsvWriter or Database
//		Database db = DatabaseFactory.create();
//		
//		Investigation inv = new Investigation();
//		inv.setName("Test" +new Date());
//		db.beginTx();
//		db.add(inv);
//		db.commitTx();
//		
//		//load dictonary
//		final String DICT = "VW_DICT";
//		VwDictListener dicListener = new VwDictListener(inv, DICT, db);
//		CsvReader reader = new CsvFileReader(new File(path + DICT +"_DATA.csv"));
//		reader.parse(dicListener);
//		dicListener.commit();		
//		
//		//load categories 
//		final String CATE = "VW_DICT_VALUESETS";
//		VWCategoryListener catListener = new VWCategoryListener(dicListener.getProtocols(), inv, CATE, db);
//		reader = new CsvFileReader(new File(path + CATE +"_DATA.csv"));
//		reader.parse(catListener);
//		catListener.commit();		
//		
//		//iterate through the map assuming CSV files
//		for(Protocol protocol : dicListener.getProtocols().values())
//		{
//			List<Measurement> measurements = (List<Measurement>)(List)protocol.getFeatures();
//			String selectOfView = EAVToView.createQuery(inv, protocol, measurements, db.getEntityManager(), LoaderUtils.eDatabase.ORACLE);
//			
//			String view = String.format("CREATE OR REPLACE VIEW LL_VW_%s AS %s", protocol.getName(), selectOfView);
//			System.out.println(view);
//			EntityManager em = db.getEntityManager();
//			em.getTransaction().begin();
//			Object result = em.createNativeQuery(view).executeUpdate();
//			em.getTransaction().commit();
//
//			//this information should be stored in dict
//			String primaryKeyColumn = "PA_ID";
//			if(protocol.getName().contains("BEP_OMSCHR")) {
//				continue;
//			}
//				
//			CSVFileSpliter csvFileSpliter = new CSVFileSpliter(path + "VW_" + protocol.getName() +"_DATA.csv", 1000, primaryKeyColumn);
//			
//			LifeLinesStandardListener.resetRowCount();
//			
//			List<LifeLinesStandardListener> listeners = new ArrayList<LifeLinesStandardListener>();
//            //CountDownLatch doneSignal = new CountDownLatch(N);
//			while(csvFileSpliter.hasMore()) {
//				listeners.add(new LifeLinesStandardListener(inv, protocol, db, csvFileSpliter.getTuples()));
//			}
//			
//            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(listeners.size());
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, THREAD_TIME_OUT_TIME, TimeUnit.SECONDS, workQueue);
//			for(LifeLinesStandardListener listener : listeners) {
//				executor.execute(listener);
//			}
//			Thread monitor = new Thread(new MyMonitorThread(executor, protocol.getName()));
//			monitor.start();
//			
//			executor.shutdown();
//			executor.awaitTermination(5, TimeUnit.MINUTES);		
//		}
//	
//		System.out.println("The End!");
//		
//	}
//	
//}
