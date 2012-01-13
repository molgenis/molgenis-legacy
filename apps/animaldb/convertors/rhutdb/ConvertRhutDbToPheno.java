package convertors.rhutdb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ConvertRhutDbToPheno
{
	private Database db;
	private CommonService ct;
	private Login login;
	private Logger logger;
	private String userName;
	private String invName;
	private List<ProtocolApplication> protocolAppsToAddList;
	private List<Individual> animalsToAddList;
	private List<String> animalNames;
	private List<ObservedValue> valuesToAddList;
	private List<Panel> panelsToAddList;
	private Map<String, String> oldRhutDbIdMap;
	private Map<String, String> appMap;
	private Calendar calendar;
	private SimpleDateFormat dbFormat = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	//private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	private Map<String, Integer> parentgroupNrMap;
	private Map<String, Integer> litterNrMap;
	private int highestNr = 0;

	public ConvertRhutDbToPheno(Database db, Login login) throws Exception
	{
		calendar = Calendar.getInstance();
		
		this.db = db;
		this.login = login;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(login.getUserId(), false);
		logger = Logger.getLogger("LoadUliDb");
		
		userName = login.getUserName();
		
		// If needed, make investigation
		invName = "RoelofHutLegacyImport";
		if (ct.getInvestigationId(invName) == -1) {
			Investigation newInv = new Investigation();
			newInv.setName(invName);
			newInv.setOwns_Name(userName);
			db.add(newInv);
		}
		
		// Init lists that we can later add to the DB at once
		protocolAppsToAddList = new ArrayList<ProtocolApplication>();
		animalsToAddList = new ArrayList<Individual>();
		animalNames = new ArrayList<String>();
		valuesToAddList = new ArrayList<ObservedValue>();
		panelsToAddList = new ArrayList<Panel>();
		
		oldRhutDbIdMap = new HashMap<String, String>();
		appMap = new HashMap<String, String>();
		parentgroupNrMap = new HashMap<String, Integer>();
		litterNrMap = new HashMap<String, Integer>();
	}
	
	public void convertFromZip(String filename) throws Exception {
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
		populateAnimal(path + "IDtable.csv");
		
		writeToDb();
	}
	
	public void writeToDb() throws Exception {
		
		db.add(protocolAppsToAddList);
		logger.debug("Protocol applications successfully added");
		
		db.add(animalsToAddList);
		logger.debug("Animals successfully added");
		
		// Make entry in name prefix table with highest animal nr.
		NamePrefix namePrefix = new NamePrefix();
		namePrefix.setUserId_Name(userName);
		namePrefix.setTargetType("animal");
		namePrefix.setPrefix("");
		namePrefix.setHighestNumber(highestNr);
		db.add(namePrefix);
		
		db.add(panelsToAddList);
		logger.debug("Panels successfully added");
		
		// Make entries in name prefix table with highest parentgroup nrs.
		List<NamePrefix> prefixList = new ArrayList<NamePrefix>();
		for (String lineName : parentgroupNrMap.keySet()) {
			namePrefix = new NamePrefix();
			namePrefix.setUserId_Name(userName);
			namePrefix.setTargetType("parentgroup");
			namePrefix.setPrefix("PG_" + lineName + "_");
			namePrefix.setHighestNumber(parentgroupNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String lineName : litterNrMap.keySet()) {
			namePrefix = new NamePrefix();
			namePrefix.setUserId_Name(userName);
			namePrefix.setTargetType("litter");
			namePrefix.setPrefix("LT_" + lineName + "_");
			namePrefix.setHighestNumber(litterNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		db.add(prefixList);
		logger.debug("Prefixes successfully added");
		
		for (int valueStart = 0; valueStart < valuesToAddList.size(); valueStart += 1000) {
			int valueEnd = Math.min(valuesToAddList.size(), valueStart + 1000);
			db.add(valuesToAddList.subList(valueStart, valueEnd));
			logger.debug("Values " + valueStart + " through " + valueEnd + " successfully added");
		}

	}
	
	public void populateAnimal(String filename) throws Exception
	{
		final Date now = new Date();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				// ID -> animal name
				String animalName = tuple.getString("ID");
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invName, animalName, userName);
				animalsToAddList.add(newAnimal);
				
				// Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSpecies"), now, 
						null, "Species", animalName, null, "House mouse"));
				
				// AnimalType (always "B. Transgeen dier")
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, 
						null, "AnimalType", animalName, "B. Transgeen dier", null));
				
				// litter nr -> OldRhutDbLitterId
				String litterId = tuple.getString("litter nr");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbLitterId"), now, 
						null, "OldRhutDbLitterId", animalName, litterId, null));
				
				// Sex -> Sex (0 = female, 1 = male)
				Integer sex = tuple.getInt("Sex");
				if (sex != null) {
					String sexName;
					if (sex == 0) {
						sexName = "Female";
					} else {
						sexName = "Male";
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSex"), 
							now, null, "Sex", animalName, null, sexName));
				}
				
				// Genotype -> Background (default C57BL/6j and otherwise CBA/CaJ) or Genotype (GeneModification + GeneState)
				String background = tuple.getString("Genotype");
				if (background != null) {
					String backgroundName;
					if (background.equals("CBA/CaJ")) {
						backgroundName = "CBA/CaJ";
					} else {
						backgroundName = "C57BL/6j";
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetBackground"), 
							now, null, "Background", animalName, null, backgroundName));
				}
				
				// Genotyped as -> Genotype (GeneModification + GeneState)
				String genotype = tuple.getString("Genotyped as");
				// if empty, try Genotype column (stored as background)
				if (genotype == null && background != null && (background.contains("Cry") || background.contains("Per"))) {
					genotype = background;
				}
				if (genotype != null) {
					String geneState;
					String geneName;
					String geneNameBase = null;
					if (genotype.contains("Cry")) geneNameBase = "Cry";
					if (genotype.contains("Per")) geneNameBase = "Per";
					int index1 = genotype.indexOf("1");
					if (index1 != -1) {
						geneName = geneNameBase + "1";
						geneName += " KO";
						geneState = genotype.substring(index1 + 1, index1 + 4);
						if (geneState.equals("-/+")) geneState = "+/-";
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneModification", animalName, geneName, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneModification", animalName, geneState, null));
					}
					int index2 = genotype.indexOf("2");
					if (index2 != -1) {
						geneName = geneNameBase + "2";
						geneName += " KO";
						geneState = genotype.substring(index2 + 1, index2 + 4);
						if (geneState.equals("-/+")) geneState = "+/-";
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneName", animalName, geneName, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneState", animalName, geneState, null));
					}
				}
				
				// arrival date and rem date -> Active start and end time
				String state = "Alive";
				String startDateString = tuple.getString("arrival date");
				Date startDate = null;
				Date remDate = null;
				if (startDateString != null) {
					startDate = dbFormat.parse(startDateString);
					startDateString = dateOnlyFormat.format(startDate);
				}
				String remDateString = tuple.getString("rem date");
				if (remDateString != null) {
					state = "Dead";
					remDate = dbFormat.parse(remDateString);
					remDateString = dateOnlyFormat.format(remDate);
				}
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), 
						startDate, remDate, "Active", animalName, state, null));
				
				//rem cause -> Removal
				String removal = tuple.getString("rem cause");
				if (removal != null && remDate != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemoval"), 
							remDate, null, "Removal", animalName, removal, null));
				}
				
				// remarks -> Source
				//Erasmus : ErasmusMC
				//Jaap : Kweek moleculaire neurobiologie (?)
				//Arjen : Kweek moleculaire neurobiologie (?)
				//(h/H)arlan : Harlan
				//Jackson/Charles River) : JacksonCharlesRiver
				// TODO: sometimes a DoB is mentioned; parse and store?
				String source = tuple.getString("remarks");
				if (source != null) {
					String sourceName = null;
					if (source.contains("Erasmus")) sourceName = "ErasmusMC";
					if (source.contains("Jaap")) sourceName = "Kweek moleculaire neurobiologie";
					if (source.contains("Arjen")) sourceName = "Kweek moleculaire neurobiologie";
					if (source.contains("arlan")) sourceName = "Harlan";
					if (source.contains("Jackson")) sourceName = "JacksonCharlesRiver";
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, 
							"Source", animalName, null, sourceName));
				}
						
				// Ear Code -> Earmark (R -> 1 r, L -> 1 l, RL -> 1 r 1 l)
				String earmark = tuple.getString("Ear Code");
				if (earmark != null) {
					if (earmark.equals("R")) earmark = "1 r";
					if (earmark.equals("L")) earmark = "1 l";
					if (earmark.equals("RL")) earmark = "1 r 1 l";
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetEarmark"), 
							now, null, "Earmark", animalName, earmark, null));
				}
				
				// sample date -> OldRhutDbSampleDate
				String sampleDate = tuple.getString("sample date");
				if (sampleDate != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbSampleDate"), 
							now, null, "OldRhutDbSampleDate", animalName, sampleDate, null));
				}
				
				//sample nr -> OldRhutDbSampleNr
				String sampleNr = tuple.getString("sample nr");
				if (sampleNr != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbSampleNr"), 
							now, null, "OldRhutDbSampleNr", animalName, sampleNr, null));
				}
				
				//Transp ID -> TransponderId -> SKIP (not filled in)
				//Fenotyped as -> SKIP (not filled in)
				//To Be Removed -> SKIP
				//Cage nr -> SKIP (not filled in)
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		makeProtocolApplication("SetSpecies");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetOldRhutDbLitterId");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetBackground");
		makeProtocolApplication("SetGenotype", "SetGenotype1");
		makeProtocolApplication("SetGenotype", "SetGenotype2");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetSource");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetOldRhutDbSampleDate");
		makeProtocolApplication("SetOldRhutDbSampleNr");
	}
	
	public void parseParentRelations(String filename) throws Exception
	{	
		//final Map<String, String> litterMap = new HashMap<String, String>();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
//				Date now = calendar.getTime();
//				
//				String newAnimalName = animalsToAddList.get(line_number - 1).getName();
//				
//				// Eingangsdatum -> DateOfBirth
//				String birthDateString = tuple.getString("Eingangsdatum");
//				String weanDate = null;
//				if (birthDateString != null) {
//					Date birthDate = dbFormat.parse(birthDateString);
//					weanDate = dateOnlyFormat.format(birthDate);
//				}
//				
//				// Mutter-Nr -> Mother
//				List<String> motherList = new ArrayList<String>();
//				String motherIdsString = tuple.getString("Mutter-Nr");
//				if (motherIdsString != null) {
//					// First, store literal value
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbMotherInfo"), 
//							now, null, "OldUliDbMotherInfo", newAnimalName, motherIdsString, null));
//					for (Integer oldMotherId : SplitParentIdsString(motherIdsString)) {
//						// Find corresponding animal
//						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (name)
//						String motherName = null;
//						if (oldMotherId.toString().length() == 5) {
//							motherName = oldUliDbIdMap.get(oldMotherId.toString());
//						} else {
//							int idx = animalNames.indexOf(oldMotherId.toString());
//							if (idx != -1) {
//								motherName = animalNames.get(idx);
//							}
//						}
//						if (motherName != null) {
//							motherList.add(motherName);
//						}
//					}
//				}
//				
//				// Vater-Nr -> Father
//				List<String> fatherList = new ArrayList<String>();
//				String fatherIdsString = tuple.getString("Vater-Nr");
//				if (fatherIdsString != null) {
//					// First, store literal value
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbFatherInfo"), 
//							now, null, "OldUliDbFatherInfo", newAnimalName, fatherIdsString, null));
//					for (Integer oldFatherId : SplitParentIdsString(fatherIdsString)) {
//						// Find corresponding animal
//						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (name)
//						String fatherName = null;
//						if (oldFatherId.toString().length() == 5) {
//							fatherName = oldUliDbIdMap.get(oldFatherId.toString());
//						} else {
//							int idx = animalNames.indexOf(oldFatherId.toString());
//							if (idx != -1) {
//								fatherName = animalNames.get(idx);
//							}
//						}
//						if (fatherName != null) {
//							fatherList.add(fatherName);
//						}
//					}
//				}
//				
//				// Put date of birth, mother info and father info into one string and check if we've
//				// seen this combination before
//				String litterInfo = birthDateString + motherList.toString() + fatherList.toString();
//				if (litterMap.containsKey(litterInfo)) {
//					
//					// This combination of birth date and parents has  been seen before,
//					// so retrieve litter and link animal directly to it
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
//							now, null, "Litter", newAnimalName, null, litterMap.get(litterInfo)));
//					
//				} else {
//					
//					// This combination of birth date and parents has not been seen before,
//					// so start a new parentgroup and litter
//					
//					String lineName = tuple.getString("Linie");
//				
//					// Create a parentgroup
//					int parentgroupNr = 1;
//					if (parentgroupNrMap.containsKey(lineName)) {
//						parentgroupNr = parentgroupNrMap.get(lineName) + 1;
//					}
//					parentgroupNrMap.put(lineName, parentgroupNr);
//					String parentgroupNrPart = ct.prependZeros("" + parentgroupNr, 6);
//					String parentgroupName = "PG_" + lineName + "_" + parentgroupNrPart;
//					panelsToAddList.add(ct.createPanel(invName, parentgroupName, userName));
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
//							now, null, "TypeOfGroup", parentgroupName, "Parentgroup", null));
//					
//					// Link parent(s) to parentgroup
//					for (String motherName : motherList) {
//						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), 
//								now, null, "Mother", motherName, null, parentgroupName));
//					}
//					for (String fatherName : fatherList) {
//						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), 
//								now, null, "Father", fatherName, null, parentgroupName));
//					}
//					
//					// Set line (Linie) of parentgroup
//					if (lineName != null) {
//						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
//								now, null, "Line", parentgroupName, null, lineName));
//					}
//					
//					// Make a litter and set wean and genotype dates
//					int litterNr = 1;
//					if (litterNrMap.containsKey(lineName)) {
//						litterNr = litterNrMap.get(lineName) + 1;
//					}
//					litterNrMap.put(lineName, litterNr);
//					String litterNrPart = ct.prependZeros("" + litterNr, 6);
//					String litterName = "LT_" + lineName + "_" + litterNrPart;
//					panelsToAddList.add(ct.createPanel(invName, litterName, userName));
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
//							now, null, "TypeOfGroup", litterName, "Litter", null));
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), 
//							now, null, "WeanDate", litterName, weanDate, null));
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotypeDate"), 
//							now, null, "GenotypeDate", litterName, weanDate, null));
//					
//					// Link litter to parentgroup
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroup"), 
//							now, null, "Parentgroup", litterName, null, parentgroupName));
//					
//					// Link animal to litter
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
//							now, null, "Litter", newAnimalName, null, litterName));
//					
//					// Set litter also on animal
//					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
//							now, null, "Line", newAnimalName, null, lineName));
//					
//					// Add litter to hashmap for reuse with siblings of this animal
//					litterMap.put(litterInfo, litterName);
//				
//				}
			}
		});
	}

	
	public void makeProtocolApplication(String protocolName) throws ParseException, DatabaseException, IOException {
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
