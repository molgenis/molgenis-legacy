package convertors.ulidb;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.core.OntologyTerm;
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


public class ConvertUliDbToPheno
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
	private Map<String, String> oldUliDbIdMap;
	private Map<String, String> oldUliDbTiernummerMap;
	private Map<String, String> appMap;
	private Map<String, ObservedValue> activeMap;
	private Calendar calendar;
	private SimpleDateFormat dbFormat = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private Map<String, Integer> parentgroupNrMap;
	private Map<String, Integer> litterNrMap;
	private int highestNr;
	private String sourceName;
	private List<String> geneList = new ArrayList<String>();

	public ConvertUliDbToPheno(Database db, Login login) throws Exception
	{
		calendar = Calendar.getInstance();
		
		userName = login.getUserName();
		
		this.db = db;
		this.login = login;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(userName, false);
		logger = Logger.getLogger("LoadUliDb");
		
		highestNr = ct.getHighestNumberForPrefix("mm_") + 1;
		
		sourceName = "Kweek moleculaire neurobiologie"; // for breeding, it's always this source
		
		// If needed, make investigation
		invName = "FDD";
		int invid = ct.getInvestigationId(invName);
		if (invid == -1) {
			Investigation newInv = new Investigation();
			newInv.setName(invName);
			newInv.setOwns_Name(userName);
			newInv.setCanRead_Name("admin");
			invid = db.add(newInv);
		}
		
		// Add some measurements that we'll need:
		int stringUnitId = db.query(OntologyTerm.class).eq(OntologyTerm.NAME, "String").find().get(0).getId();
		ct.makeMeasurement(invid, "OldUliDbTiernummer", stringUnitId, null, null, false, "string", "To set an animal's Tiernummer in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbId", stringUnitId, null, null, false, "string", "To set an animal's ID (laufende Nummer) in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbMotherInfo", stringUnitId, null, null, false, "string", "To set an animal's mother info in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbFatherInfo", stringUnitId, null, null, false, "string", "To set an animal's father info in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbLine", stringUnitId, null, null, false, "string", "To set an animal's line in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbGene", stringUnitId, null, null, false, "string", "To set an animal's gene(s) in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbBackground", stringUnitId, null, null, false, "string", "To set an animal's background info in the old Uli Eisel DB.", login.getUserId());
		
		// Init lists that we can later add to the DB at once
		protocolAppsToAddList = new ArrayList<ProtocolApplication>();
		animalsToAddList = new ArrayList<Individual>();
		animalNames = new ArrayList<String>();
		valuesToAddList = new ArrayList<ObservedValue>();
		panelsToAddList = new ArrayList<Panel>();
		
		oldUliDbIdMap = new HashMap<String, String>();
		oldUliDbTiernummerMap = new HashMap<String, String>();
		appMap = new HashMap<String, String>();
		parentgroupNrMap = new HashMap<String, Integer>();
		litterNrMap = new HashMap<String, Integer>();
		activeMap = new HashMap<String, ObservedValue>();
		
		// Create two lines that are not in the import but that will be needed later
		createLine("ki hu TNF R1", "ki Humanized TNF R1", "Lawri", ct.getInvestigationId(invName));
		createLine("ki hu TNF R2", "ki Humanized TNF R2", "Conrad", ct.getInvestigationId(invName));
	}
	
	public void convertFromFile(String filename) throws Exception {
		
		//File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		//String path = tmpDir.getAbsolutePath() + File.separatorChar;
		
		// Run convertor steps
		populateAnimal(filename);
		populateProtocolApplication();
		populateValue(filename);
		parseParentRelations(filename);
		
		writeToDb();
	}
	
	public void writeToDb() throws Exception {
		db.add(protocolAppsToAddList);
		logger.debug("Protocols successfully added");
		
		db.add(animalsToAddList);
		// Make entry in name prefix table with highest animal nr.
		List<NamePrefix> prefixList = db.query(NamePrefix.class).eq(NamePrefix.TARGETTYPE, "animal").eq(NamePrefix.PREFIX, "mm_").find();
		if (prefixList.size() == 1) {
			NamePrefix namePrefix = prefixList.get(0);
			namePrefix.setHighestNumber(highestNr);
			db.update(namePrefix);
		} else {
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType("animal");
			namePrefix.setPrefix("mm_");
			namePrefix.setHighestNumber(highestNr);
			db.add(namePrefix);
		}
		logger.debug("Animals successfully added");
		
		db.add(panelsToAddList);
		// Make entries in name prefix table with highest parentgroup nrs.
		for (String pgName : parentgroupNrMap.keySet()) {
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType("parentgroup");
			namePrefix.setPrefix("PG_" + pgName + "_");
			namePrefix.setHighestNumber(parentgroupNrMap.get(pgName));
			db.add(namePrefix);
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String litterName : litterNrMap.keySet()) {
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType("litter");
			namePrefix.setPrefix("LT_" + litterName + "_");
			namePrefix.setHighestNumber(litterNrMap.get(litterName));
			db.add(namePrefix);
		}
		logger.debug("Panels successfully added");
		
		// Add values from maps and then add them all, batchwise
		valuesToAddList.addAll(activeMap.values());
		for (int valueStart = 0; valueStart < valuesToAddList.size(); valueStart += 1000) {
			int valueEnd = Math.min(valuesToAddList.size(), valueStart + 1000);
			db.add(valuesToAddList.subList(valueStart, valueEnd));
			logger.debug("Values " + valueStart + " through " + valueEnd + " successfully added");
		}
		
		ct.makeObservationTargetNameMap(userName, true);
	}
	
	public void populateAnimal(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				String animalName = "mm_" + ct.prependZeros(Integer.toString(highestNr++), 6);
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invName, animalName, userName);
				animalsToAddList.add(newAnimal);
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		makeProtocolApplication("SetOldUliDbTiernummer");
		makeProtocolApplication("SetOldUliDbId");
		makeProtocolApplication("SetOldUliDbLine");
		makeProtocolApplication("SetOldUliDbBackground");
		makeProtocolApplication("SetOldUliDbGene");
		makeProtocolApplication("SetSpecies");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetDeathDate");
		makeProtocolApplication("SetSource");
		makeProtocolApplication("SetRemark");
		makeProtocolApplication("SetResponsibleResearcher");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetColor");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetGenotype", "SetGenotype1");
		makeProtocolApplication("SetGenotype", "SetGenotype2");
		makeProtocolApplication("SetBackground");
		makeProtocolApplication("SetOldUliDbMotherInfo");
		makeProtocolApplication("SetOldUliDbFatherInfo");
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetParentgroupMother");
		makeProtocolApplication("SetParentgroupFather");
		makeProtocolApplication("SetMother");
		makeProtocolApplication("SetFather");
		makeProtocolApplication("SetLine");
		makeProtocolApplication("SetParentgroup");
		makeProtocolApplication("SetLitter");
		makeProtocolApplication("SetLine");
		makeProtocolApplication("SetWeanDate");
		makeProtocolApplication("SetGenotypeDate");
	}
	
	public void populateValue(String filename) throws Exception
	{
		final String speciesName = "House mouse";
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws Exception
			{
				Date now = calendar.getTime();
				
				String newAnimalName = animalsToAddList.get(line_number - 1).getName();
				
				// Tiernummer -> OldUliDbTiernummer
				String tiernummer = tuple.getString("Tiernummer");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbTiernummer"), now, 
						null, "OldUliDbTiernummer", newAnimalName, tiernummer, null));
				oldUliDbTiernummerMap.put(tiernummer, newAnimalName);
				
				// laufende Nr -> OldUliDbId
				String oldUliDbId = tuple.getString("laufende Nr");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbId"), now, 
						null, "OldUliDbId", newAnimalName, oldUliDbId, null));
				oldUliDbIdMap.put(oldUliDbId, newAnimalName);
				
				// Tierkategorie -> Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSpecies"), now, 
						null, "Species", newAnimalName, null, speciesName));
				
				// AnimalType (always "B. Transgeen dier", except for PKU mice)
				if (tuple.getString("OldUliDbLine") != null && tuple.getString("OldUliDbLine").equals("PKU")) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, 
							null, "AnimalType", newAnimalName, "A. normaal dier", null));
				} else {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, 
							null, "AnimalType", newAnimalName, "B. Transgeen dier", null));
				}
				// Eingangsdatum, Abgangsdatum and StatusNew ->
				// DateOfBirth, DeathDate and Active + start and end time
				// Default start date is 01-01-2006
				Calendar cal = Calendar.getInstance();
				cal.set(2006, Calendar.JANUARY, 1);
				Date startDate = cal.getTime();
				String startDateString = tuple.getString("Eingangsdatum");
				if (startDateString != null) {
					startDate = dbFormat.parse(startDateString);
					String dateOfBirth = newDateOnlyFormat.format(startDate);
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
							startDate, null, "DateOfBirth", newAnimalName, dateOfBirth, null));
				}
				// Default end date is 31-12-2011
				cal.set(2011, Calendar.DECEMBER, 31);
				Date endDate = cal.getTime();
				String endDateString = tuple.getString("Abgangsdatum");
				if (endDateString != null) {
					endDate = dbFormat.parse(endDateString);
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDeathDate"), 
							startDate, null, "DeathDate", newAnimalName, newDateOnlyFormat.format(endDate), null));
				}
				String state = tuple.getString("StatusNew");
				if (state != null) {
					state = "Alive";
					endDate = null;
				} else {
					state = "Dead";
				}
				activeMap.put(newAnimalName, ct.createObservedValue(invName, appMap.get("SetActive"), 
						startDate, endDate, "Active", newAnimalName, state, null));
				
				// Herkunft -> Source
				Integer uliSourceId = tuple.getInt("Herkunft");
				if (uliSourceId != null) {
					String sourceName = null;
					if (uliSourceId == 51 || uliSourceId == 52) {
						// 51: Zucht- oder Liefereinrichtung innerhalb Deutschlands, die fuer ihre Taetigkeit eine Erlaubnis nach Par. 11 Abs. 1 Satz 1 Nr. 1 des Tierschutzgesetzes erhalten hat
						// 52: andere amtlich registrierte oder zugelassene Einrichtung innerhalb der EU
						// --> SourceType for both: Van EU-lid-staten
						//sourceName = "UliEisel51and52";
						sourceName = "Stuttgart (Uli Eisel)";
					}
					if (uliSourceId == 55) {
						// 55: Switserland
						// --> SourceType: Andere herkomst
						//sourceName = "UliEisel55";
						sourceName = "Stuttgart (Uli Eisel)";
					}
					if (uliSourceId != 0) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, 
								"Source", newAnimalName, null, sourceName));
					}
				}
				
				// Bemerkungen -> Remark
				String remark = tuple.getString("Bemerkungen");
				if (remark != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
							now, null, "Remark", newAnimalName, remark, null));
				}

				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetResponsibleResearcher"), 
						now, null, "ResponsibleResearcher", newAnimalName, "Uli Eisel", null));
				
				// BeschrGeschlecht -> Sex
				String sex = tuple.getString("BeschrGeschlecht");
				if (sex != null) {
					String sexName;
					if (sex.equals("w")) {
						sexName = "Female";
					} else {
						sexName = "Male";
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSex"), 
							now, null, "Sex", newAnimalName, null, sexName));
				}
				
				// Farbe -> Color
				String color = tuple.getString("Farbe");
				if (color != null) {
					String colorName = null;
					if (color.equals("beige")) colorName = "beige";
					if (color.equals("braun")) colorName = "brown";
					if (color.equals("gelb")) colorName = "yellow";
					if (color.equals("grau")) colorName = "gray";
					if (color.equals("grau-braun")) colorName = "gray-brown";
					if (color.equals("rotbraun")) colorName = "red-brown";
					if (color.equals("schwarz")) colorName = "black";
					if (color.equals("Schwarz-braun")) colorName = "black-brown";
					if (color.equals("schwarz-gray")) colorName = "black-gray";
					if (color.equals("weiss")) colorName = "white";
					if (color.equals("zimt")) colorName = "cinnamon";
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetColor"), 
							now, null, "Color", newAnimalName, colorName, null));
				}
				
				// Ohrmarkierung1 -> Earmark
				String earmark = tuple.getString("Ohrmarkierung1");
				if (earmark != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetEarmark"), 
							now, null, "Earmark", newAnimalName, earmark, null));
				}
				
				// GENETIC FACTORS (that may have been changed from Uli DB) from here on:
				
				// Line
				String oldLineName = tuple.getString("OldUliDbLine");
				String lineName = tuple.getString("Line");
				String lineFullName = tuple.getString("LineFullName");
				if (lineName != null) {
					if (ct.getObservationTargetId(lineName) == -1) {
						createLine(lineName, lineFullName, null, ct.getInvestigationId(invName));
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
							now, null, "Line", newAnimalName, null, lineName));
				}
				if (oldLineName != null && !oldLineName.equals(lineName)) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
							now, null, "Remark", newAnimalName, 
							"Line changed from " + oldLineName + " to " + lineName + " during conversion to AnimalDB", null));
				}
				
				// Gene and tg -> Gene and GeneState (in one or more SetGenotype protocol applications)
				String oldGeneName = tuple.getString("OldUliDbGene");
				String geneName = tuple.getString("Gene");
				if (geneName != null) {
					if (!geneList.contains(geneName)) {
						createGene(geneName);
						geneList.add(geneName);
					}
					if (oldGeneName != null && !oldGeneName.equals(geneName)) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
								now, null, "Remark", newAnimalName, 
								"Gene changed from " + oldGeneName + " to " + geneName + " during conversion to AnimalDB", null));
					}
					String geneState = tuple.getString("tg"); // Allowed flavors: -/- +/- +/+ ntg wt unknown transgenic
					// First do some normalization
					if (geneState == null || geneState.equals("Unknown")) {
						geneState = "unknown";
					}
					if (geneState.equals("WT")) {
						geneState = "wt";
					}
					// Then check if geneState is singular or double
					if (!geneState.equals("+/+") && !geneState.equals("+/-") && !geneState.equals("-/-") && 
							!geneState.equals("ntg") && !geneState.equals("transgenic") && !geneState.equals("unknown") && 
							!geneState.equals("wt")) {
						// Double geneState, so split (first 3 chars and last 3 chars, ignoring all the spaces and slashes in between)
						String geneState1 = geneState.substring(0, 3);
						String geneState2 = geneState.substring(geneState.length() - 3, geneState.length());
						// Try to split geneName, on slash (if present)
						// TODO: find out from Uli if this is OK!
						String geneName1 = geneName;
						String geneName2 = geneName;
						String[] geneNames = geneName.split("/");
						if (geneNames.length == 2) {
							geneName1 = geneNames[0];
							geneName2 = geneNames[1];
						}
						// Add to values list
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneModification", newAnimalName, geneName1, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneState", newAnimalName, geneState1, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneModification", newAnimalName, geneName2, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneState", newAnimalName, geneState2, null));
					} else {
						// geneName and geneState can remain as is
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneModification", newAnimalName, geneName, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype1"), 
								now, null, "GeneState", newAnimalName, geneState, null));
					}
				}
				
				// Background
				String oldBackground = tuple.getString("OldUliDbBackground");
				String background = tuple.getString("Background");
				if (background != null) {
					if (ct.getObservationTargetId(background) == -1) {
						createBackground(background, ct.getInvestigationId(invName));
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetBackground"), 
							now, null, "Background", newAnimalName, null, background));
				}
				if (oldBackground != null && !oldBackground.equals(background)) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
							now, null, "Remark", newAnimalName, 
							"Background changed from " + oldBackground + " to " + background + " during conversion to AnimalDB", null));
				}
			}
		});
	}
	
	public void parseParentRelations(String filename) throws Exception
	{	
		final Map<String, String> litterMap = new HashMap<String, String>();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = calendar.getTime();
				
				String newAnimalName = animalsToAddList.get(line_number - 1).getName();
				
				// Eingangsdatum -> DateOfBirth
				String birthDateString = tuple.getString("Eingangsdatum");
				String weanDate = null;
				if (birthDateString != null) {
					Date birthDate = dbFormat.parse(birthDateString);
					weanDate = newDateOnlyFormat.format(birthDate);
				}
				
				// Mutter-Nr -> Mother
				List<String> motherList = new ArrayList<String>();
				String motherIdsString = tuple.getString("Mutter-Nr");
				if (motherIdsString != null) {
					// First, store literal value
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbMotherInfo"), 
							now, null, "OldUliDbMotherInfo", newAnimalName, motherIdsString, null));
					for (Integer oldMotherId : SplitParentIdsString(motherIdsString)) {
						// Find corresponding animal
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (OldUliDbTiernummer)
						String motherName = null;
						if (oldMotherId.toString().length() == 5) {
							motherName = oldUliDbIdMap.get(oldMotherId.toString());
						} else {
							motherName = oldUliDbTiernummerMap.get(oldMotherId.toString());
						}
						if (motherName != null) {
							motherList.add(motherName);
						}
					}
				}
				
				// Vater-Nr -> Father
				List<String> fatherList = new ArrayList<String>();
				String fatherIdsString = tuple.getString("Vater-Nr");
				if (fatherIdsString != null) {
					// First, store literal value
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbFatherInfo"), 
							now, null, "OldUliDbFatherInfo", newAnimalName, fatherIdsString, null));
					for (Integer oldFatherId : SplitParentIdsString(fatherIdsString)) {
						// Find corresponding animal
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (OldUliDbTiernummer)
						String fatherName = null;
						if (oldFatherId.toString().length() == 5) {
							fatherName = oldUliDbIdMap.get(oldFatherId.toString());
						} else {
							fatherName = oldUliDbTiernummerMap.get(oldFatherId.toString());
						}
						if (fatherName != null) {
							fatherList.add(fatherName);
						}
					}
				}
				
				// Put date of birth, mother info and father info into one string and check if we've
				// seen this combination before
				String litterInfo = birthDateString + motherList.toString() + fatherList.toString();
				if (litterMap.containsKey(litterInfo)) {
					
					// This combination of birth date and parents has been seen before,
					// so retrieve litter and link animal directly to it
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
							now, null, "Litter", newAnimalName, null, litterMap.get(litterInfo)));
					// Set parents also on animal
					for (String motherName : motherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), 
								now, null, "Mother", newAnimalName, null, motherName));
					}
					for (String fatherName : fatherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), 
								now, null, "Father", newAnimalName, null, fatherName));
					}
					
				} else {
					
					String lineName = tuple.getString("Line");
					if (lineName == null) {
						// Don't bother to set litter info of animals in lines that are not used anymmore
						return;
					}
					// This combination of birth date and parents has not been seen before,
					// so start a new parentgroup and litter
					int parentgroupNr = 1;
					if (parentgroupNrMap.containsKey(lineName)) {
						parentgroupNr = parentgroupNrMap.get(lineName) + 1;
					}
					parentgroupNrMap.put(lineName, parentgroupNr);
					String parentgroupNrPart = ct.prependZeros("" + parentgroupNr, 6);
					String parentgroupName = "PG_" + lineName + "_" + parentgroupNrPart;
					panelsToAddList.add(ct.createPanel(invName, parentgroupName, userName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
							now, null, "TypeOfGroup", parentgroupName, "Parentgroup", null));
					
					// Link parent(s) to parentgroup and animal
					for (String motherName : motherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroupMother"), 
								now, null, "ParentgroupMother", parentgroupName, null, motherName));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), 
								now, null, "Mother", newAnimalName, null, motherName));
					}
					for (String fatherName : fatherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroupFather"), 
								now, null, "ParentgroupFather", parentgroupName, null, fatherName));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), 
								now, null, "Father", newAnimalName, null, fatherName));
					}
					
					// Set Line and Source of parentgroup
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
							now, null, "Line", parentgroupName, null, lineName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), 
							now, null, "Source", parentgroupName, null, sourceName));
					
					// Make a litter and set wean and genotype dates
					int litterNr = 1;
					if (litterNrMap.containsKey(lineName)) {
						litterNr = litterNrMap.get(lineName) + 1;
					}
					litterNrMap.put(lineName, litterNr);
					String litterNrPart = ct.prependZeros("" + litterNr, 6);
					String litterName = "LT_" + lineName + "_" + litterNrPart;
					panelsToAddList.add(ct.createPanel(invName, litterName, userName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
							now, null, "TypeOfGroup", litterName, "Litter", null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), 
							now, null, "WeanDate", litterName, weanDate, null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotypeDate"), 
							now, null, "GenotypeDate", litterName, weanDate, null));
					// Set source also on litter
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), 
							now, null, "Source", litterName, null, sourceName));
					// Set line also on litter
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
							now, null, "Line", litterName, null, lineName));
					// Link litter to parentgroup
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroup"), 
							now, null, "Parentgroup", litterName, null, parentgroupName));
					// Link animal to litter
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
							now, null, "Litter", newAnimalName, null, litterName));
					// Add litter to hashmap for reuse with siblings of this animal
					litterMap.put(litterInfo, litterName);
				}
			}
		});
	}
	
	private void createLine(String lineName, String lineFullName, String remark, int investigationId) throws DatabaseException, IOException, ParseException
	{
		Date now = new Date();
		
		// Make line panel
		int lineId = ct.makePanel(investigationId, lineName, login.getUserId());
		// Label it as line using the (Set)TypeOfGroup protocol and feature
		int featureId = ct.getMeasurementId("TypeOfGroup");
		int protocolId = ct.getProtocolId("SetTypeOfGroup");
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, lineId, 
				"Line", 0));
		// Set the source of the line
		featureId = ct.getMeasurementId("Source");
		protocolId = ct.getProtocolId("SetSource");
		int sourceId = ct.getObservationTargetId(sourceName);
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, lineId, 
				null, sourceId));
		// Set the species of the line (always House mouse)
		featureId = ct.getMeasurementId("Species");
		protocolId = ct.getProtocolId("SetSpecies");
		int speciesId = ct.getObservationTargetId("House mouse");
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, lineId, 
				null, speciesId));
		// Set full name (if known)
		if (lineFullName != null) {
			featureId = ct.getMeasurementId("LineFullName");
			protocolId = ct.getProtocolId("SetLineFullName");
			db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, lineId, 
					lineFullName, 0));
		}
		// Set remark (if known)
		if (remark != null) {
			featureId = ct.getMeasurementId("Remark");
			protocolId = ct.getProtocolId("SetRemark");
			db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, lineId, 
					remark, 0));
		}
	}
	
	public void createGene(String geneName) throws Exception
	{
		ct.makeCategory(geneName, geneName, "GeneModification");
	}
	
	public void createBackground(String bkgName, int investigationId) throws Exception
	{
		Date now = new Date();
		
		// Make background panel
		int bkgId = ct.makePanel(investigationId, bkgName, login.getUserId());
		// Label it as background using the (Set)TypeOfGroup protocol and feature
		int featureId = ct.getMeasurementId("TypeOfGroup");
		int protocolId = ct.getProtocolId("SetTypeOfGroup");
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, bkgId, 
				"Background", 0));
		// Set the species of the background (always House mouse)
		featureId = ct.getMeasurementId("Species");
		protocolId = ct.getProtocolId("SetSpecies");
		int speciesId = ct.getObservationTargetId("House mouse");
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, now, null, protocolId, featureId, bkgId, 
				null, speciesId));
	}
	
	public List<Integer> SplitParentIdsString(String parentIdsString) {
		List<Integer> idsList = new ArrayList<Integer>();
		// Separators can be: comma + whitespace OR forward slash OR whitespace OR
		// period + whitespace OR whitespace + plus sign + whitespace
		String[] ids = parentIdsString.split("[(,\\s)/\\s(\\.\\s)(\\s\\+\\s)]");
		for (String idString : ids) {
			Integer id = 0;
			try {
				id = Integer.parseInt(idString);
			} catch (Exception e) {
				//
			}
			if (id != 0) {
				// We now know it's a number
				if (idString.length() == 8) {
					// Two Tiernummers glued together
					idsList.add(Integer.parseInt(idString.substring(0, 4)));
					idsList.add(Integer.parseInt(idString.substring(4, 8)));
				} else {
					if (idString.length() == 10) {
						// Two laufende Nrs glued together
						idsList.add(Integer.parseInt(idString.substring(0, 5)));
						idsList.add(Integer.parseInt(idString.substring(5, 10)));
					} else {
						idsList.add(id);
					}
				}
			}
		}
		return idsList;
	}
	
	public void makeProtocolApplication(String protocolName) throws ParseException, DatabaseException, IOException {
		makeProtocolApplication(protocolName, protocolName);
	}
	
	public void makeProtocolApplication(String protocolName, String protocolLabel) throws ParseException, DatabaseException, IOException {
		ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
		protocolAppsToAddList.add(app);
		appMap.put(protocolLabel, app.getName());
	}
	
}
