package convertors.ulidb;

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
	private Map<String, String> appMap;
	private Calendar calendar;
	private SimpleDateFormat dbFormat = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private Map<String, Integer> parentgroupNrMap;
	private Map<String, Integer> litterNrMap;
	private int highestNr = 0;

	public ConvertUliDbToPheno(Database db, Login login) throws Exception
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
		invName = "UliEiselLegacyImport";
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
		
		oldUliDbIdMap = new HashMap<String, String>();
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
		populateBackground(path + "Genetischer Hintergrund.txt");
		populateGene(path + "Gen.txt");
		populateLine(path + "Linie.txt");
		populateAnimal(path + "Tierdetails.txt");
		populateProtocolApplication();
		populateValue(path + "Tierdetails.txt");
		parseParentRelations(path + "Tierdetails.txt");
		
		writeToDb();
	}
	
	public void writeToDb() throws Exception {
		db.add(protocolAppsToAddList);
		logger.debug("Protocols successfully added");
		
		db.add(animalsToAddList);
		// Make entry in name prefix table with highest animal nr. (Tiernummer)
		NamePrefix namePrefix = new NamePrefix();
		namePrefix.setTargetType("animal");
		namePrefix.setPrefix("");
		namePrefix.setHighestNumber(highestNr);
		db.add(namePrefix);
		logger.debug("Animals successfully added");
		
		db.add(panelsToAddList);
		// Make entries in name prefix table with highest parentgroup nrs.
		for (String lineName : parentgroupNrMap.keySet()) {
			namePrefix = new NamePrefix();
			namePrefix.setTargetType("parentgroup");
			namePrefix.setPrefix("PG_" + lineName + "_");
			namePrefix.setHighestNumber(parentgroupNrMap.get(lineName));
			db.add(namePrefix);
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String lineName : litterNrMap.keySet()) {
			namePrefix = new NamePrefix();
			namePrefix.setTargetType("litter");
			namePrefix.setPrefix("LT_" + lineName + "_");
			namePrefix.setHighestNumber(litterNrMap.get(lineName));
			db.add(namePrefix);
		}
		logger.debug("Panels successfully added");
		
		for (int valueStart = 0; valueStart < valuesToAddList.size(); valueStart += 1000) {
			int valueEnd = Math.min(valuesToAddList.size(), valueStart + 1000);
			db.add(valuesToAddList.subList(valueStart, valueEnd));
			logger.debug("Values " + valueStart + " through " + valueEnd + " successfully added");
		}

	}
	
	public void populateAnimal(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				// Tiernummer -> make new animal
				String animalName = tuple.getString("Tiernummer");
				// Store highest animal nr. for later storage in name prefix table
				try {
					int animalNr = Integer.parseInt(animalName);
					if (animalNr > highestNr) {
						highestNr = animalNr;
					}
				} catch (NumberFormatException e) {
					// Not a parseable animal nr.: ignore
				}
				// Deal with empty names (of which there are a few in Uli's DB
				if (animalName == null) {
					animalName = "OldUliId_" + tuple.getString("laufende Nr");
				} else {
					// Prepend 0's to name
					animalName = ct.prependZeros(animalName, 6);
				}
				// Make sure we have a unique name
				while (animalNames.contains(animalName)) {
					animalName = ("Dup_" + animalName);
				}
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invName, animalName, userName);
				animalsToAddList.add(newAnimal);
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		makeProtocolApplication("SetOldUliDbId");
		makeProtocolApplication("SetSpecies");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetDeathDate");
		makeProtocolApplication("SetSource");
		//makeProtocolApplication("SetOldUliDbExperimentator");
		//makeProtocolApplication("SetOldUliDbTierschutzrecht");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetColor");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetGenotype", "SetGenotype1");
		makeProtocolApplication("SetGenotype", "SetGenotype2");
		makeProtocolApplication("SetBackground");
		makeProtocolApplication("SetOldUliDbMotherInfo");
		makeProtocolApplication("SetOldUliDbFatherInfo");
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetMother");
		makeProtocolApplication("SetFather");
		//makeProtocolApplication("SetLine");
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
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = calendar.getTime();
				
				String newAnimalName = animalsToAddList.get(line_number - 1).getName();
				
				// laufende Nr -> OldUliDbId
				String oldUliDbId = tuple.getString("laufende Nr");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbId"), now, 
						null, "OldUliDbId", newAnimalName, oldUliDbId, null));
				oldUliDbIdMap.put(oldUliDbId, newAnimalName);
				
				// Tierkategorie -> Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSpecies"), now, 
						null, "Species", newAnimalName, null, speciesName));
				
				// AnimalType (always "B. Transgeen dier")
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, 
						null, "AnimalType", newAnimalName, "B. Transgeen dier", null));
				
				// Eingangsdatum, Abgangsdatum and Status ->
				// DateOfBirth, DeathDate and Active + start and end time
				String startDateString = tuple.getString("Eingangsdatum");
				Date startDate = null;
				if (startDateString != null) {
					startDate = dbFormat.parse(startDateString);
					String dateOfBirth = newDateOnlyFormat.format(startDate);
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
							startDate, null, "DateOfBirth", newAnimalName, dateOfBirth, null));
				}
				String endDateString = tuple.getString("Abgangsdatum");
				Date endDate = null;
				if (endDateString != null) {
					endDate = dbFormat.parse(endDateString);
					String dateOfDeath = newDateOnlyFormat.format(endDate);
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDeathDate"), 
							startDate, null, "DeathDate", newAnimalName, dateOfDeath, null));
				}
				String state = tuple.getString("Status");
				if (state != null) {
					if (state.equals("lebt")) {
						state = "Alive";
					} else {
						state = "Dead";
					}
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), 
							startDate, endDate, "Active", newAnimalName, state, null));
				}
				
				// Herkunft -> Source
				Integer uliSourceId = tuple.getInt("Herkunft");
				if (uliSourceId != null) {
					String sourceName = null;
					if (uliSourceId == 51 || uliSourceId == 52) {
						// 51: Zucht- oder Liefereinrichtung innerhalb Deutschlands, die f�r ihre T�tigkeit eine Erlaubnis nach � 11 Abs. 1 Satz 1 Nr. 1 des Tierschutzgesetzes erhalten hat
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
				
				//  not needed, skip import (update ate @ 2011-09-20)
				// K�rzel -> OldUliDbKuerzel
				/*
				 * String kuerzel = tuple.getString("K�rzel");
				 * if (kuerzel != null) {
				 * 	valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbKuerzel"), 
				 * 		now, null, "OldUliDbKuerzel", newAnimalName, kuerzel, null));
				 * }
				*/
				
				// Bemerkungen -> Remark
				String remark = tuple.getString("Bemerkungen");
				if (remark != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
							now, null, "Remark", newAnimalName, remark, null));
				}
				
				//  not needed, skip import (update ate @ 2011-09-20)
				/*// Aktenzeichen -> OldUliDbAktenzeichen
				String aktenzeichen = tuple.getString("Aktenzeichen");
				if (aktenzeichen != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbAktenzeichen"), 
							now, null, "OldUliDbAktenzeichen", newAnimalName, aktenzeichen, null));
				}*/
				
				//  not needed, skip import (update ate @ 2011-09-20)
				/*// Experimentator -> OldUliDbExperimentator
				String experimentator = tuple.getString("Experimentator");
				if (experimentator != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbExperimentator"), 
							now, null, "OldUliDbExperimentator", newAnimalName, experimentator, null));
				}*/
				
				//  not needed, skip import (update ate @ 2011-09-20)
				// Tierschutzrecht -> OldUliDbTierschutzrecht
				// TODO: actually this corresponds to Goal, but in AnimalDB that is linked
				// to a DEC subproject (Experiment) instead of to the individual animals.
				// For now, store in OldUliDbTierschutzrecht.
				/*String tierschutzrecht = tuple.getString("Tierschutzrecht");
				if (tierschutzrecht != null) {;
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbTierschutzrecht"), 
							now, null, "OldUliDbTierschutzrecht", newAnimalName, tierschutzrecht, null));
				}*/
				
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
				
				// Gen and tg -> Gene and GeneState (in one or more SetGenotype protocol applications)
				String geneName = tuple.getString("Gen");
				if (geneName == null) {
					geneName = "unknown";
				}
				String geneState = tuple.getString("tg"); // Allowed flavors: -/- +/- +/+ ntg wt unknown transgenic
				// First do some normalization
				if (geneState == null || geneState.equals("Unknown")) {
					geneState = "unknown";
				}
				if (geneState.equals("WT")) {
					geneState = "wt";
				}
				logger.debug(geneState);
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
				
				// gen Hintergrund-Tier -> Background
				String background = tuple.getString("gen Hintergrund-Tier");
				if (background != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetBackground"), 
							now, null, "Background", newAnimalName, null, background));
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
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (name)
						String motherName = null;
						if (oldMotherId.toString().length() == 5) {
							motherName = oldUliDbIdMap.get(oldMotherId.toString());
						} else {
							int idx = animalNames.indexOf(oldMotherId.toString());
							if (idx != -1) {
								motherName = animalNames.get(idx);
							}
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
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (name)
						String fatherName = null;
						if (oldFatherId.toString().length() == 5) {
							fatherName = oldUliDbIdMap.get(oldFatherId.toString());
						} else {
							int idx = animalNames.indexOf(oldFatherId.toString());
							if (idx != -1) {
								fatherName = animalNames.get(idx);
							}
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
					
					// This combination of birth date and parents has  been seen before,
					// so retrieve litter and link animal directly to it
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
							now, null, "Litter", newAnimalName, null, litterMap.get(litterInfo)));
					
				} else {
					
					// This combination of birth date and parents has not been seen before,
					// so start a new parentgroup and litter
					
					String lineName = tuple.getString("Linie");
				
					// Create a parentgroup
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
					
					// Link parent(s) to parentgroup
					for (String motherName : motherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), 
								now, null, "Mother", motherName, null, parentgroupName));
					}
					for (String fatherName : fatherList) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), 
								now, null, "Father", fatherName, null, parentgroupName));
					}
					
					// Set line (Linie) of parentgroup
					if (lineName != null) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
								now, null, "Line", parentgroupName, null, lineName));
					}
					
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
					
					// Link litter to parentgroup
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroup"), 
							now, null, "Parentgroup", litterName, null, parentgroupName));
					
					// Link animal to litter
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
							now, null, "Litter", newAnimalName, null, litterName));
					
					// Set litter also on animal
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
							now, null, "Line", newAnimalName, null, lineName));
					
					// Add litter to hashmap for reuse with siblings of this animal
					litterMap.put(litterInfo, litterName);
				
				}
			}
		});
	}
	
	public void populateLine(String filename) throws Exception
	{
		final int invid = ct.getInvestigationId(invName);
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				
				String lineName = tuple.getString("Linie");
				// Make line panel (append 'line' if there is already a background with this name)
				if (ct.getObservationTargetId(lineName) != -1) {
					lineName += " (line)";
				}
				int lineId = ct.makePanel(invid, lineName, login.getUserId());
				// Label it as line using the (Set)TypeOfGroup protocol and feature
				int featureId = ct.getMeasurementId("TypeOfGroup");
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, protocolId, featureId, lineId, 
						"Line", 0));
				// Set the source of the line (always 'Kweek moleculaire neurobiologie')
				featureId = ct.getMeasurementId("Source");
				protocolId = ct.getProtocolId("SetSource");
				int sourceId = ct.getObservationTargetId("Kweek moleculaire neurobiologie");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, protocolId, featureId, lineId, 
						null, sourceId));
			}
		});
	}
	
	public void populateGene(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				// Every gene becomes a code for the 'GeneModification' feature
				String geneName = tuple.getString("Gen");
				ct.makeCode(geneName, geneName, "GeneModification");
			}
		});
	}
	
	public void populateBackground(String filename) throws Exception
	{
		final int featureId = ct.getMeasurementId("TypeOfGroup");
		final int protocolId = ct.getProtocolId("SetTypeOfGroup");
		final int invid = ct.getInvestigationId(invName);
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				
				String bkgName = tuple.getString("Genetischer Hintergrund");
				if (bkgName != null && ct.getObservationTargetId(bkgName) == -1) {
					// Make background panel
					int bkgId = ct.makePanel(invid, bkgName, login.getUserId());
					// Label it as background using the (Set)TypeOfGroup protocol and feature
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, protocolId, featureId, bkgId, 
							"Background", 0));
				}
			}
		});
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
