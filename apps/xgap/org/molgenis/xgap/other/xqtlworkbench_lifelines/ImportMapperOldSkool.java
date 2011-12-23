package org.molgenis.xgap.other.xqtlworkbench_lifelines;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.ImportTupleListener;
import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.LifeLinesMedicatieListener;
import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.LifeLinesStandardListener;
import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.VWCategoryListener;
import org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners.VwDictListener;

import app.DatabaseFactory;

/**
 * This class is responsible for mapping tuples from a source schema to entities
 * in MOLGENIS. The tuples can come from a CSV file or from SQL queries on some
 * database. The target schema is molgenis.pheno
 */
public class ImportMapperOldSkool
{

	private static Logger logger = Logger.getLogger(ImportMapperOldSkool.class);

	/**
	 * for testing only!
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		// enable log
		BasicConfigurator.configure();

		// adjust for your situation!
		importData("C:\\lifelinesdata\\all.zip");
	}

	public static void importData(String zipFileName) throws Exception
	{

		// Path to store files from zip
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String path = tmpDir.getAbsolutePath() + File.separatorChar;
		// Extract zip
		ZipFile zipFile = new ZipFile(zipFileName);
		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) entries.nextElement();
			copyInputStream(zipFile.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(path + entry.getName())));
		}

		// target for output, either CsvWriter or Database
		Database db = DatabaseFactory.create();

		Investigation inv = new Investigation();
		inv.setName("Test" + new Date());
		db.beginTx();
		db.add(inv);
		db.commitTx();

		// load dictonary
		final String DICT = "VW_DICT";
		VwDictListener dicListener = new VwDictListener(inv, DICT, db);
		CsvReader reader = new CsvFileReader(new File(path + DICT + "_DATA.csv"));
		reader.parse(dicListener);
		dicListener.commit();

		// load categories
		final String CATE = "VW_DICT_VALUESETS";
		VWCategoryListener catListener = new VWCategoryListener(dicListener.getProtocols(), inv, CATE, db);
		reader = new CsvFileReader(new File(path + CATE + "_DATA.csv"));
		reader.parse(catListener);
		catListener.commit();

		// iterate through the map assuming CSV files
		List<String> exclude = Arrays.asList(new String[] { "BEP_OMSCHR", "DICT_HULP", "ONDERZOEK" });
		for (Protocol protocol : dicListener.getProtocols().values())
		{
			if (!exclude.contains(protocol.getName()))
			{

				File f = new File(path + "VW_" + protocol.getName() + "_DATA.csv");

				ImportTupleListener llListener;
				if ("MEDICATIE".equals(protocol.getName()))
				{
					protocol.getFeatures().clear();
					llListener = new LifeLinesMedicatieListener(db, protocol);
					
					reader = new CsvFileReader(f);
					reader.parse(llListener);
					llListener.commit();
				}
				else
				{
					llListener = new LifeLinesStandardListener(inv, protocol, db);
				}


			}
		}

		logger.info("LifeLines Publish data import complete!");

		// //this information should be stored in dict
		// // String primaryKeyColumn = "PA_ID";
		// // if(protocol.getName().contains("BEP_OMSCHR")) {
		// // continue;
		// // }
		//
		// //CSVFileSpliter csvFileSpliter = new CSVFileSpliter(path + "VW_" +
		// protocol.getName() +"_DATA.csv", 1000, primaryKeyColumn);
		//
		// //LifeLinesStandardListener.resetRowCount();
		//
		// List<LifeLinesStandardListener> listeners = new
		// ArrayList<LifeLinesStandardListener>();
		// //CountDownLatch doneSignal = new CountDownLatch(N);
		// // while(csvFileSpliter.hasMore()) {
		// // listeners.add(new LifeLinesStandardListener(inv, protocol, db,
		// csvFileSpliter.getTuples()));
		// // }
		//
		// BlockingQueue<Runnable> workQueue = new
		// ArrayBlockingQueue<Runnable>(listeners.size());
		// ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS,
		// MAX_THREADS, THREAD_TIME_OUT_TIME, TimeUnit.SECONDS, workQueue);
		// for(LifeLinesStandardListener listener : listeners) {
		// executor.execute(listener);
		// }
		// Thread monitor = new Thread(new MyMonitorThread(executor,
		// protocol.getName()));
		// monitor.start();
		//
		// executor.shutdown();
		// executor.awaitTermination(5, TimeUnit.MINUTES);
	}

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}
