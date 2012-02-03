package convertors.prefill;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.ContactInfo;
import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class PrefillAnimalDB
{
	private Database db;
	private CommonService ct;
	private Logger logger;
	private String userName = "admin";
	private String invName = "System";
	
	private List<ProtocolApplication> protocolAppsToAddList = new ArrayList<ProtocolApplication>();
	private List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
	private List<Panel> panelsToAddList = new ArrayList<Panel>();
	private List<Ontology> ontologiesToAddList = new ArrayList<Ontology>();
	private List<OntologyTerm> ontologyTermsToAddList = new ArrayList<OntologyTerm>();
	private List<Category> categoriesToAddList = new ArrayList<Category>();
	private List<Protocol> protocolsToAddList = new ArrayList<Protocol>();
	private Map<String, String> appMap = new HashMap<String, String>();
	private Map<String, Measurement> measMap = new HashMap<String, Measurement>();
	
	
	public PrefillAnimalDB(Database db, Login login) throws Exception
	{
		this.db = db;
		
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		
		logger = Logger.getLogger("PrefillAnimalDB");
		
		// If needed, make investigation
		if (ct.getInvestigationId(invName) == -1) {
			Investigation newInv = new Investigation();
			newInv.setName(invName);
			newInv.setOwns_Name("admin");
			newInv.setCanRead_Name("AllUsers");
			db.add(newInv);
		}
	}

	public void prefillFromZip(String filename) throws Exception {
		// Path to store files from zip
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String path = tmpDir.getAbsolutePath() + File.separatorChar;
		// Extract zip
		ZipFile zipFile = new ZipFile(filename);
		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) entries.nextElement();
			copyInputStream(zipFile.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(path + entry.getName())));
		}
		// Run convertor steps
		populateProtocolApplication();
		populateContactInfo(path + "contactinfo.csv");
		populateOntology(path + "ontology.csv");
		populateOntologyTerm(path + "ontologyterm.csv");
		populateMeasurement(path + "measurement.csv");
		populateCategory(path + "category.csv");
		populateProtocol(path + "protocol.csv");
		populateSex(path + "sex.csv");
		populateSpecies(path + "species.csv");
		populateBackground(path + "background.csv");
		populateSource(path + "source.csv");
		// Add it all to the database
		writeToDb();
	}

	public void writeToDb() throws Exception {
		
		db.add(ontologiesToAddList);
		logger.debug("Ontologies successfully added");
		db.add(ontologyTermsToAddList);
		logger.debug("Ontology terms successfully added");
		db.add(categoriesToAddList);
		logger.debug("Categories successfully added");
		List<Measurement> measList = new ArrayList<Measurement>();
		for (Measurement meas : measMap.values()) {
			measList.add(meas);
		}
		db.add(measList);
		logger.debug("Measurements successfully added");
		db.add(protocolsToAddList);
		logger.debug("Protocols successfully added");
		db.add(protocolAppsToAddList);
		logger.debug("Protocol applications successfully added");
		db.add(panelsToAddList);
		logger.debug("Panels successfully added");
		db.add(valuesToAddList);
		logger.debug("Values successfully added");
	}
	
	public void populateContactInfo(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				ContactInfo ci = new ContactInfo();
				ci.setText(tuple.getString("text"));
				db.add(ci); // this one goes directly into the db, not through a list, because nothing links to it
			}
		});
	}
	
	public void populateOntology(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Ontology newOnt = new Ontology();
				newOnt.setName(tuple.getString("name"));
				ontologiesToAddList.add(newOnt);
			}
		});
	}
	
	public void populateOntologyTerm(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				OntologyTerm newOntTerm = new OntologyTerm();
				newOntTerm.setName(tuple.getString("termName"));
				newOntTerm.setDefinition(tuple.getString("termDefinition"));
				newOntTerm.setOntology_Name(tuple.getString("ontology"));
				ontologyTermsToAddList.add(newOntTerm);
			}
		});
	}
	
	public void populateMeasurement(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String name = tuple.getString("name");
				String unitName = tuple.getString("unit");
				String targettypeAllowedForRelationClassName = null;
				if (tuple.getString("targetType") != null) {
					targettypeAllowedForRelationClassName = db.getClassForName(tuple.getString("targetType")).getName();
				}
				String panelLabelAllowedForRelation = tuple.getString("panelLabel");
				boolean temporal = false;
				if (tuple.getString("temporal").equals("true")) {
					temporal = true;
				}
				String dataType = tuple.getString("dataType");
				String description = tuple.getString("description");
				Measurement newMeas = ct.createMeasurement(invName, name, unitName, 
						targettypeAllowedForRelationClassName, panelLabelAllowedForRelation, 
						temporal, dataType, description, userName);
				measMap.put(name, newMeas);
			}
		});
	}
	
	public void populateCategory(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String code = tuple.getString("code");
				String measName = tuple.getString("measurement");
				Category newCat = ct.createCategory(code, tuple.getString("description"), measName);
				newCat.setInvestigation_Name(invName);
				categoriesToAddList.add(newCat);
				Measurement meas = measMap.get(measName);
				meas.getCategories_Name().add(measName + "_" + code);
				measMap.put(measName, meas);
			}
		});
	}
	
	public void populateProtocol(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				List<String> measurementNameList = (List<String>) tuple.getList("measurements", ",");
				protocolsToAddList.add(ct.createProtocol(invName, tuple.getString("name"), 
						tuple.getString("description"), measurementNameList));
			}
		});
	}
	
	public void populateSex(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String sexName = tuple.getString("name");
				panelsToAddList.add(ct.createPanel(invName, sexName, userName));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), new Date(), 
						null, "TypeOfGroup", sexName, "Sex", null));
			}
		});
	}
	
	public void populateSpecies(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String specName = tuple.getString("name");
				panelsToAddList.add(ct.createPanel(invName, specName, userName));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), new Date(), 
						null, "TypeOfGroup", specName, "Species", null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetVWASpecies"), new Date(), 
						null, "VWASpecies", specName, tuple.getString("VwaSpecies"), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLatinSpecies"), new Date(), 
						null, "LatinSpecies", specName, tuple.getString("LatinSpecies"), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDutchSpecies"), new Date(), 
						null, "DutchSpecies", specName, tuple.getString("DutchSpecies"), null));
			}
		});
	}
	
	public void populateBackground(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String bkgName = tuple.getString("name");
				panelsToAddList.add(ct.createPanel(invName, bkgName, userName));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), new Date(), 
						null, "TypeOfGroup", bkgName, "Background", null));
			}
		});
	}
	
	public void populateSource(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String sourceName = tuple.getString("name");
				panelsToAddList.add(ct.createPanel(invName, sourceName, userName));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), new Date(), 
						null, "TypeOfGroup", sourceName, "Source", null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSourceType"), new Date(), 
						null, "SourceType", sourceName, tuple.getString("type"), null));
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetVWASpecies");
		makeProtocolApplication("SetLatinSpecies");
		makeProtocolApplication("SetDutchSpecies");
		makeProtocolApplication("SetSourceType");
	}

	
	public void makeProtocolApplication(String protocolName) throws Exception {
		makeProtocolApplication(protocolName, protocolName);
	}
	
	public void makeProtocolApplication(String protocolName, String protocolLabel) throws ParseException, DatabaseException, IOException {
		ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
		protocolAppsToAddList.add(app);
		appMap.put(protocolLabel, app.getName());
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
