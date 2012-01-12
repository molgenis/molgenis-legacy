package org.molgenis.lifelinesresearchportal.plugins.loader;

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
import org.molgenis.lifelinesresearchportal.plugins.loader.listeners.ImportTupleListener;
import org.molgenis.lifelinesresearchportal.plugins.loader.listeners.LifeLinesMedicatieListener;
import org.molgenis.lifelinesresearchportal.plugins.loader.listeners.LifeLinesStandardListener;
import org.molgenis.lifelinesresearchportal.plugins.loader.listeners.VWCategoryListener;
import org.molgenis.lifelinesresearchportal.plugins.loader.listeners.VwDictListener;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;

import app.DatabaseFactory;

/**
 * This class is responsible for mapping tuples from a source schema to entities
 * in MOLGENIS. The tuples can come from a CSV file or from SQL queries on some
 * database. The target schema is molgenis.pheno
 */
public class ImportMapper
{

	private static Logger logger = Logger.getLogger(ImportMapper.class);

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
		importData("C:\\lifelinesdata\\all.zip", 0);
	}

	public static void importData(String zipFileName, int studyNr) throws Exception
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
		switch (studyNr) {
			case 0:
				inv.setName("Test" + new Date());
				break;
			case 1:
				inv.setName("OV004+OV013 Steverink");
				inv.setDescription("The mediating role of postive and negative affects in the relation between social support and health. Social relationship factors, SWB, self-regulation, health outcomes");
				break;
			case 2:
				inv.setName("OV039 Boezen");
				inv.setDescription("Identifying novel genes for lung function and lung function decline in LifeLines");
				break;
			case 3:
				inv.setName("OV077 Van der Harst");
				inv.setDescription("Heritability, Genetics and Prognosis of PR conduction");
				break;
			default:
				inv.setName("Test" + new Date());
				break;
		}
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

		// iterate through the protocols map assuming CSV files
		// exclude a few views:
		// BEZOEK is skipped because we use BEZOEK_PIVOT, where each subvisit has become a row
		List<String> exclude = Arrays.asList(new String[] { "BEZOEK", "BEP_OMSCHR", "DICT_HULP", "ONDERZOEK" });
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
				}
				else
				{
					llListener = new LifeLinesStandardListener(inv, protocol, db);
				}

				reader = new CsvFileReader(f);
				reader.parse(llListener);
				llListener.commit();
			}
		}

		logger.info("LifeLines Publish data import complete!");
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
