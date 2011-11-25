//package org.molgenis.lifelines;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import javax.persistence.EntityManager;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.lifelines.ThreadReaders.CSVFileSpliter;
//import org.molgenis.lifelines.listeners.ImportTupleListener;
//import org.molgenis.lifelines.listeners.VWCategoryListener;
//import org.molgenis.lifelines.listeners.VwDictListener;
//import org.molgenis.lifelines.loaders.EAVToView;
//import org.molgenis.lifelines.loaders.LoaderUtils;
//import org.molgenis.lifelines.loaders.MyMonitorThread;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.protocol.Protocol;
//import org.molgenis.util.CsvFileReader;
//import org.molgenis.util.CsvReader;
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
//	//for each table / csv file there will be one entry
//	static Map<String, ImportTupleListener> mappings = new LinkedHashMap<String, ImportTupleListener>();
//	
//	/** for testing only!
//	 * @throws Exception */
//	public static void main(String[] args) throws Exception
//	{
//		//path to directory with csv files
//		String path = "/Users/jorislops/Desktop/LifelinesCSV3/";
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
//		//create all listeners
////		for(Protocol protocol : dicListener.getProtocols().values()) {
////			mappings.put(protocol.getName(), new LifeLinesStandardListener(inv, protocol,db)); //will import values
////		}		
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
//			CSVFileSpliter csvFileSpliter = new CSVFileSpliter(path + "VW_" + protocol.getName() +"_DATA.csv", 1000, "PA_ID");
//			
////			String prevFristPaID = "";
////			String prevLastPaId = "";
////			while(csvFileSpliter.hasMore()) {
////				List<SimpleTuple> tuples = csvFileSpliter.getTuples();
////				
////				String firstPaID = tuples.get(0).getString("PA_ID");
////				String lastPaID = tuples.get(tuples.size()-1).getString("PA_ID");
////				
////				System.out.println(firstPaID);
////				System.out.println();
////				System.out.println(lastPaID);
////				
////				if(prevLastPaId == firstPaID) {
////					System.out.println("Overlapping ID!");
////				}
////				
////				prevFristPaID = firstPaID;
////				prevLastPaId = lastPaID;
////				
////			}
//			
//			
//			//int N = 20;
//			
//			
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
//			
//			executor.shutdown();
//			executor.awaitTermination(5, TimeUnit.MINUTES);
//
////			//create CsvReader
////			reader = new CsvFileReader(new File(path + "VW_" + csvFileName +"_DATA.csv"));
////			reader.parse(importer);
////			importer.commit();			
//		}
//		
//	}
//	
//}
