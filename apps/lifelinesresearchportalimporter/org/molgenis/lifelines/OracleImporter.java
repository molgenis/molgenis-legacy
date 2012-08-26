package org.molgenis.lifelines;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import oracle.jdbc.OraclePreparedStatement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.lifelines.listeners.CategoryLoader;
import org.molgenis.lifelines.listeners.DictLoader;
import org.molgenis.lifelines.utils.EAVToView;
import org.molgenis.lifelines.utils.LoaderUtils;
import org.molgenis.lifelines.utils.LoaderUtils.eDatabase;
import org.molgenis.lifelines.utils.MyMonitorThread;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;

import app.DatabaseFactory;
import app.FillMetadata;
import au.com.bytecode.opencsv.CSVReader;

class Holder<T>
{
	public Holder(T value)
	{
		this.value = value;
	}

	public T value;
}

public class OracleImporter
{
	private static final String CHAR_ENCODING = "UTF-8";

	public static void main(final String[] args) throws Exception
	{
		Locale.setDefault(Locale.US);
		PropertyConfigurator.configure("apps/lifelinesresearchportalimporter/org/molgenis/lifelines/log4j.properties");
		
		final String inputPath = "/Users/jorislops/Desktop/LLTEST1/";

		final Properties props = new Properties();
		final FileInputStream in = new FileInputStream(
				"apps/lifelinesresearchportalimporter/org/molgenis/lifelines/db.properties");
		props.load(in);
		in.close();

		final String url = props.getProperty("jdbc.url");
		final String username = props.getProperty("jdbc.username");
		final String password = props.getProperty("jdbc.password");

		final long begin = System.currentTimeMillis();
		final List<String> protocolToLoad = Arrays.asList("PATIENT", "MEDICATIE");
		new OracleImporter(inputPath, username, password, url, protocolToLoad);

		long end = System.currentTimeMillis();
		long time = (end - begin) / 1000;
		System.out.println("total Time to load dataset: " + time);
	}

	private static final boolean SHARED_MEASUREMENTS = true;
	private static final String DICT = "VW_DICT";
	private static final String CATE = "VW_DICT_VALUESETS";
	private static final eDatabase DATABASE_TYPE = LoaderUtils.eDatabase.ORACLE;

	private final String path;
	// private final String outputPath;
	private final String username;

	private final Investigation inv;

	private final Database db;
	private final EntityManager em;

	private final Map<String, Integer> targetDBId = new HashMap<String, Integer>();
	private final Map<String, Integer> measurementDBId = new HashMap<String, Integer>();
	private final Map<Integer, Integer> protocolAppDBId = new HashMap<Integer, Integer>();

	private List<String> protocolsToImport = null;

	public OracleImporter(String path, String userName, String password, String dbUrl, List<String> protocolsToImport)
			throws Exception
	{
		this.path = path;
		this.username = userName;
		// this.outputPath = outputPath;

		db = initDatabase(userName, password, dbUrl);
		this.em = db.getEntityManager();

		this.protocolsToImport = protocolsToImport;

		inv = new Investigation();
		inv.setName("Test" + new Date());
		db.beginTx();
		db.add(inv);
		db.commitTx();

		loadData();
	}

	private Database initDatabase(final String userName, final String password, final String dbUrl) throws Exception
	{
		final Map<String, Object> configOverrides = new HashMap<String, Object>();
		configOverrides.put("hibernate.hbm2ddl.auto", "create-drop");
		if (StringUtils.isNotEmpty(userName))
		{
			configOverrides.put("javax.persistence.jdbc.user", userName);
		}
		if (StringUtils.isNotEmpty(password))
		{
			configOverrides.put("javax.persistence.jdbc.password", password);
		}
		if (StringUtils.isNotEmpty(dbUrl))
		{
			configOverrides.put("javax.persistence.jdbc.url", dbUrl);
		}

		final Database database = DatabaseFactory.create(configOverrides);
		FillMetadata.fillMetadata(database, false);
		return database;
	}

	private void loadData() throws DatabaseException, FileNotFoundException, Exception, IOException
	{

		// load Measurements into db
		final DictLoader dictLoader = new DictLoader(new File(path + DICT + "_DATA_VIEW.csv"), CHAR_ENCODING, inv,
				SHARED_MEASUREMENTS, em, protocolsToImport);
		dictLoader.load();
		dictLoader.close();

		// load categories into db
		{
			final CategoryLoader catLoader = new CategoryLoader(new File(path + DICT + "_DATA_VIEW.csv"),
					CHAR_ENCODING, dictLoader.getProtocols(), inv, em, SHARED_MEASUREMENTS, protocolsToImport);
			catLoader.load();
			catLoader.close();
		}

		createOracleTrigger();

		loadTargets(dictLoader.getProtocols().values());

		for (final Protocol protocol : dictLoader.getProtocols().values())
		{
			final String fileName = getFileName(protocol);
			if (!new File(path + fileName).exists())
			{
				System.out.println(String.format("File: '%s' doesn't exists", fileName));
				System.exit(1);
			}
		}

		long beginTime = System.currentTimeMillis();
		for (final Protocol protocol : dictLoader.getProtocols().values())
		{
			if (protocolsToImport != null)
			{
				if (!protocolsToImport.contains(protocol.getName().toUpperCase()))
				{
					return;
				}
			}

			System.out.println("loading data for Protocol: " + protocol.getName());

			loadMeasurements(protocol);
			loadProtocolApplications(protocol);

			long beginTable = System.currentTimeMillis();
			storeObservedValuesInDatabase(protocol);
			long endTable = System.currentTimeMillis();

			System.out.println(String.format("Table %s loaded in %d", protocol.getName(),
					(endTable - beginTable / 1000)));

			// final BufferedWriter outputFile = new BufferedWriter(new
			// FileWriter(outputPath + getFileName(protocol)));
			// storeCsv(protocol, outputFile);
			// outputFile.close();
		}
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("All tables loaded in %d", (endTime - beginTime / 1000)));
		for (final Protocol protocol : dictLoader.getProtocols().values())
		{
			createViews(protocol);
		}
	}

	@SuppressWarnings("deprecation")
	private void createOracleTrigger() throws Exception
	{
		String trigger = "create or replace trigger OBSERVEDVALUE_INSERT_TRG " + " before insert on \"OBSERVEDVALUE\" "
				+ " for each row " + " begin " + " if inserting then " + " 	if :NEW.\"ID\" is null then "
				+ "      select OBSERVEDVALUE_SEQ.nextval into :NEW.\"ID\" from dual; " + "    end if; " + " end if; "
				+ " end; ";

		db.sql(trigger, null);
	}

	@SuppressWarnings("unchecked")
	private void createViews(final Protocol protocol) throws Exception
	{
		@SuppressWarnings("deprecation")
		final List<Measurement> measurements = (List<Measurement>) (List) protocol.getFeatures();
		final String selectOfView = EAVToView.createQuery(inv, protocol, measurements, db.getEntityManager(),
				DATABASE_TYPE);

		try
		{
			final EntityManager viewEm = db.getEntityManager().getEntityManagerFactory().createEntityManager();
			@SuppressWarnings(
			{ "rawtypes", "unchecked" })
			final String view = String
					.format("CREATE OR REPLACE VIEW LL_VW_%s AS %s", protocol.getName(), selectOfView);

			System.out.println("----------");
			System.out.println(view);
			System.out.println("----------");

			viewEm.getTransaction().begin();
			viewEm.createNativeQuery(view).executeUpdate();
			viewEm.getTransaction().commit();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try
		{
			final EntityManager viewEm = db.getEntityManager().getEntityManagerFactory().createEntityManager();
			final String checkMaterializedView = String
					.format("SELECT Count(*) FROM user_objects WHERE object_name = 'LL_VWM_%s' AND object_type = 'MATERIALIZED VIEW'",
							protocol.getName().toUpperCase());

			final String mview = String.format("CREATE MATERIALIZED VIEW LL_VWM_%s AS %s", protocol.getName(),
					selectOfView);

			final Number mViewCount = (Number) em.createNativeQuery(checkMaterializedView).getSingleResult();
			if (mViewCount.intValue() > 0)
			{
				em.getTransaction().begin();
				String dropView = String.format("DROP MATERIALIZED VIEW \"LL_VWM_%s\"", protocol.getName()
						.toUpperCase());
				em.createNativeQuery(dropView).executeUpdate();
				em.getTransaction().commit();
			}
			viewEm.getTransaction().begin();
			viewEm.createNativeQuery(mview).executeUpdate();
			viewEm.getTransaction().commit();

			System.out.println("----------");
			System.out.println(mview);
			System.out.println("----------");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private void storeObservedValuesInDatabase(final Protocol protocol) throws Exception
	{
		final String fileName = getFileName(protocol);
		// final CsvReader reader = new CsvFileReader(new File(path +
		// fileName));

		au.com.bytecode.opencsv.CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(new File(
				path + fileName)), CHAR_ENCODING));

		@SuppressWarnings("deprecation")
		final Connection conn = ((JpaDatabase) db).getConnection();
		final PreparedStatement ps = conn
				.prepareStatement("INSERT INTO OBSERVEDVALUE (TARGET, FEATURE, VALUE, PROTOCOLAPPLICATION, INVESTIGATION) "
						+ "VALUES (?, ?, ?, ?, ?)");
		((OraclePreparedStatement) ps).setExecuteBatch(50);
		long beginTime = System.currentTimeMillis();

		int batchCount = 0;
		int recordCnt = 0;

		final Holder<Integer> paIdx = new Holder<Integer>(null);
		final String[] headers = getHeaderInfo(reader, paIdx);

		String[] nextLine;
		while ((nextLine = reader.readNext()) != null)
		{
			final Integer paId = protocolAppDBId.get(recordCnt);
			final Integer targetId = targetDBId.get(nextLine[paIdx.value]);
			for (int i = 0; i < headers.length; ++i)
			{
				final String value = StringUtils.trim(nextLine[i]);
				final String fieldHeader = headers[i];
				if (StringUtils.isEmpty(value))
				{
					continue;
				}

				final int measurementId = measurementDBId.get(fieldHeader);

				ps.setInt(1, targetId);
				ps.setInt(2, measurementId);
				ps.setString(3, value);
				ps.setInt(4, paId);
				ps.setInt(5, inv.getId());
				// ps.setString(6, "ObservedValue");

				ps.executeUpdate(); // JDBC queues this for later execution
				if (batchCount % 50 == 0)
				{
					((OraclePreparedStatement) ps).sendBatch(); // JDBC sends
																// the queued
																// request
					System.out.println(String.format("Protocol: %s recordCnt: %d", protocol.getName(), recordCnt));
				}
				batchCount++;

			}
			recordCnt++;
		}

		((OraclePreparedStatement) ps).sendBatch(); // JDBC sends the queued
													// request
		conn.commit();
		ps.close();

		reader.close();

		long endTime = System.currentTimeMillis();
		long loadTime = (endTime - beginTime) / 1000;
		System.out.println(String.format("To load Protocol: %s takes %d", protocol.getName(), loadTime));
	}

	private static String[] getHeaderInfo(final CSVReader reader, final Holder<Integer> outPaIdx) throws Exception
	{
		final String[] headers = reader.readNext();
		for (int i = 0; i < headers.length; ++i)
		{
			final String header = headers[i];
			if (header.equalsIgnoreCase("PA_ID"))
			{
				outPaIdx.value = i; // out parameter
				break;
			}
		}
		return headers;
	}

	private void storeCsv(final Protocol protocol, final BufferedWriter bw) throws Exception
	{
		final String fileName = getFileName(protocol);
		final CSVReader reader = new CSVReader(new FileReader(new File(path + fileName)));

		bw.append("TARGET, FEATURE, VALUE, PROTOCOLAPPLICATION, INVESTIGATION, DTYPE");
		bw.newLine();

		final Holder<Integer> paIdx = new Holder<Integer>(null);
		final String[] headers = getHeaderInfo(reader, paIdx);

		int recordCnt = 0;
		String[] nextLine = null;
		while ((nextLine = reader.readNext()) != null)
		{
			final Integer paId = protocolAppDBId.get(recordCnt);
			final Integer targetId = targetDBId.get(nextLine[paIdx.value]);

			int colIdx = 0;
			for (final String fieldHeader : headers)
			{
				final int measurementId = measurementDBId.get(fieldHeader);
				final String value = nextLine[colIdx];
				bw.append(String.format("%s,%s,\"%s\",%s,%s,%s", targetId, measurementId, value, paId, inv.getId(),
						ObservedValue.class.getSimpleName()));
				bw.newLine();
				++colIdx;
			}
			recordCnt++;
		}
		bw.flush();
		bw.close();
	}

	static int protocolAppName = 0;

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void loadProtocolApplications(final Protocol protocol) throws Exception
	{
		final String fileName = getFileName(protocol);
		final List<ProtocolApplication> protocolApplications = new ArrayList<ProtocolApplication>();

		final CSVReader reader = new CSVReader(new FileReader(new File(path + fileName)));

		final Holder<Integer> paIdx = new Holder<Integer>(null);
		getHeaderInfo(reader, paIdx); // process first line

		while (reader.readNext() != null)
		{
			final ProtocolApplication protocolApplication = new ProtocolApplication();
			protocolApplication.setName("" + protocolAppName++);
			protocolApplication.setInvestigation(inv);
			protocolApplication.setProtocol(protocol);
			protocolApplications.add(protocolApplication);
		}
		saveEntitiesToDatabase((List<Entity>) (List) protocolApplications, protocol.getName());
		int idx = 0;
		for (final ProtocolApplication protocolApplication : protocolApplications)
		{
			protocolAppDBId.put(idx++, protocolApplication.getId());
		}
	}

	private void loadMeasurements(final Protocol protocol)
	{
		measurementDBId.clear(); // clear previous measurements

		@SuppressWarnings("unchecked")
		final List<Measurement> measurements = em
				.createQuery(
						"SELECT m FROM Protocol p JOIN p.features m WHERE p.investigation = :investigation AND p = :protocol")
				.setParameter("investigation", inv).setParameter("protocol", protocol).getResultList();

		for (final Measurement m : measurements)
		{
			measurementDBId.put(m.getName(), m.getId());
		}
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void loadTargets(final Collection<Protocol> protocols) throws Exception
	{
		final List<ObservationTarget> targets = new ArrayList<ObservationTarget>();
		for (final Protocol protocol : protocols)
		{
			if (protocolsToImport != null)
			{
				if (!protocolsToImport.contains(protocol.getName().toUpperCase()))
				{
					return;
				}
			}

			final String fileName = getFileName(protocol);
			System.out.println(fileName);

			final au.com.bytecode.opencsv.CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(
					path + fileName), CHAR_ENCODING));

			final Holder<Integer> paIdx = new Holder<Integer>(null);
			getHeaderInfo(reader, paIdx); // process header, find paIdx

			String[] line = null;
			while ((line = reader.readNext()) != null)
			{
				final String pa_id = line[paIdx.value];
				if (!targetDBId.containsKey(pa_id))
				{
					targetDBId.put(pa_id, -1);

					final ObservationTarget target = new ObservationTarget();
					target.setName(pa_id);
					target.setInvestigation(inv);
					targets.add(target);
				}
			}
			reader.close();
		}
		saveEntitiesToDatabase((List<Entity>) (List) targets, "Loading all Targets");
		for (final ObservationTarget target : targets)
		{
			targetDBId.put(target.getName(), target.getId());
		}
	}

	private String getFileName(final Protocol protocol)
	{
		if (protocol.getName().startsWith("VW_"))
		{
			return protocol.getName() + "_DATA_VIEW.csv";
		}
		else
		{
			return "VW_" + protocol.getName() + "_DATA_VIEW.csv";
		}
	}

	private final static int RECORDS_THREAD = 1000;
	private final static int MAX_THREADS = 20;
	private final static int THREAD_TIME_OUT_TIME = 60 * 60; // one hour

	// static int rowCount = 0;

	private void saveEntitiesToDatabase(final List<Entity> entities, String protocolName) throws Exception
	{
		int number = Math.round((float) entities.size() / (float) RECORDS_THREAD);
		if (number <= 0)
		{
			System.out.println(String.format("Protocol: %s has not records!", protocolName));
			return;
		}
		final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(number);
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, THREAD_TIME_OUT_TIME,
				TimeUnit.SECONDS, workQueue);
		final List<List<Entity>> split = ListUtils.split(entities, RECORDS_THREAD);
		for (final List<Entity> list : split)
		{
			executor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					final EntityManager em = db.getEntityManager().getEntityManagerFactory().createEntityManager();
					em.getTransaction().begin();
					for (final Entity entity : list)
					{
						em.persist(entity);
					}
					em.flush();
					em.clear();
					em.getTransaction().commit();
					em.close();
				}
			});
		}

		final Thread monitor = new Thread(new MyMonitorThread(executor, "blaat"));
		monitor.start();

		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
	}
}
