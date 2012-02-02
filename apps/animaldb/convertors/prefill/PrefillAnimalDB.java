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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.ContactInfo;
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.auth.MolgenisGroup;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
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
	private List<Measurement> measurementsToAddList = new ArrayList<Measurement>();
	private Map<String, String> appMap = new HashMap<String, String>();
	
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	
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
		
		writeToDb();
	}

	public void writeToDb() throws Exception {
		
		db.add(ontologiesToAddList);
		logger.debug("Ontologies successfully added");
		
		db.add(ontologyTermsToAddList);
		logger.debug("Ontology terms successfully added");
		
		db.add(measurementsToAddList);
		logger.debug("Measurements successfully added");
		
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
				measurementsToAddList.add(newMeas);
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		// makeProtocolApplication("SetTypeOfGroup"); TODO etc
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
