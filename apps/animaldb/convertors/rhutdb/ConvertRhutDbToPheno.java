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
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
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
	private Logger logger;
	private String userName;
	private String invName;
	private List<ProtocolApplication> protocolAppsToAddList;
	private List<Individual> animalsToAddList;
	private List<String> animalNames;
	private List<ObservedValue> valuesToAddList;
	private List<Panel> panelsToAddList;
	private Map<String, String> appMap;
	private SimpleDateFormat dbFormat = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat dobFormat = new SimpleDateFormat("d-M-y", Locale.US);
	private SimpleDateFormat expDbFormat = new SimpleDateFormat("yyyy-M-d H:mm:ss", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private SimpleDateFormat yearOnlyFormat = new SimpleDateFormat("yyyy", Locale.US);
	private Map<String, String> sourceMap;
	private Map<String, Integer> parentgroupNrMap;
	private Map<String, Integer> litterNrMap;
	private Map<String, List<String>> litterMap;
	private Map<String, String> decMap;
	private Map<String, String> alternativeDecMap;
	private Map<String, Integer> researcherMap;
	private Map<String, ObservedValue> activeMap;
	private Map<String, String> animalMap;
	private Map<String, Date> removalDateMap;
	private int highestNr = 0;
	
	public ConvertRhutDbToPheno(Database db, Login login) throws Exception
	{
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(login.getUserId(), false);
		logger = Logger.getLogger("LoadUliDb");
		
		userName = login.getUserName();
		
		// If needed, make investigation
		invName = "RoelofHutLegacyImport";
		int invId = ct.getInvestigationId(invName);
		if (invId == -1) {
			Investigation newInv = new Investigation();
			newInv.setName(invName);
			newInv.setOwns_Name(userName);
			db.add(newInv);
			invId = newInv.getId();
		}
		
		// Init lists that we can later add to the DB at once
		protocolAppsToAddList = new ArrayList<ProtocolApplication>();
		animalsToAddList = new ArrayList<Individual>();
		animalNames = new ArrayList<String>();
		valuesToAddList = new ArrayList<ObservedValue>();
		panelsToAddList = new ArrayList<Panel>();
		
		appMap = new HashMap<String, String>();
		sourceMap = new HashMap<String, String>();
		parentgroupNrMap = new HashMap<String, Integer>();
		litterNrMap = new HashMap<String, Integer>();
		litterMap = new HashMap<String, List<String>>();
		decMap = new HashMap<String, String>();
		alternativeDecMap = new HashMap<String, String>();
		researcherMap = new HashMap<String, Integer>();
		activeMap = new HashMap<String, ObservedValue>();
		animalMap = new HashMap<String, String>();
		removalDateMap = new HashMap<String, Date>();
		
		// Create lines
		createLine("WT", invId, login.getUserId());
		createLine("Per dKO", invId, login.getUserId());
		createLine("Cry dKO", invId, login.getUserId());
		createLine("PerCry", invId, login.getUserId());
		createLine("CBA/CaJ (line)", invId, login.getUserId());
		createLine("C57BL/6j (line)", invId, login.getUserId());
		createLine("C3H/He", invId, login.getUserId());
		createLine("DBA", invId, login.getUserId());
		createLine("ICR(CD-1)", invId, login.getUserId());
		createLine("Swing", invId, login.getUserId());
		createLine("CK1e", invId, login.getUserId());
	}
	
	private void createLine(String lineName, int invId, int userId) throws DatabaseException, IOException, ParseException
	{
		panelsToAddList.add(ct.createPanel(invId, lineName, userId));
		// Label it as line using the (Set)TypeOfGroup protocol and feature
		Date now = new Date();
		valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup", lineName, 
				"Line", null));
		// Set the source of the line (always 'Kweek chronobiologie')
		valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, "Source", lineName, 
				null, "Kweek chronobiologie"));
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
		parseParentRelations(path + "Litters.csv");
		populateDec(path + "Experiments.csv");
		parseDecRelations(path + "IDsInExp.csv");
		
		writeToDb();
	}

	public void writeToDb() throws Exception {
		
		db.add(protocolAppsToAddList);
		logger.debug("Protocol applications successfully added");
		
		db.add(animalsToAddList);
		logger.debug("Animals successfully added");
		
		// Make entry in name prefix table with highest animal nr.
		NamePrefix namePrefix = new NamePrefix();
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
			namePrefix.setTargetType("parentgroup");
			namePrefix.setPrefix("PG_" + lineName + "_");
			namePrefix.setHighestNumber(parentgroupNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String lineName : litterNrMap.keySet()) {
			namePrefix = new NamePrefix();
			namePrefix.setTargetType("litter");
			namePrefix.setPrefix("LT_" + lineName + "_");
			namePrefix.setHighestNumber(litterNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		db.add(prefixList);
		logger.debug("Prefixes successfully added");
		
		// Add remaining Active values to value list
		valuesToAddList.addAll(activeMap.values());
		
		int batchSize = 1000;
		for (int valueStart = 0; valueStart < valuesToAddList.size(); valueStart += batchSize) {
			int valueEnd = Math.min(valuesToAddList.size(), valueStart + batchSize);
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
				String animalName = ct.prependZeros(Integer.toString(highestNr++), 6);
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invName, animalName, userName);
				animalsToAddList.add(newAnimal);
				
				// ID -> OldRhutDbAnimalId
				String oldAnimalId = tuple.getString("ID");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbAnimalId"), 
						now, null, "OldRhutDbAnimalId", animalName, oldAnimalId, null));
				animalMap.put(oldAnimalId, animalName);
				
				// Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSpecies"), now, 
						null, "Species", animalName, null, "House mouse"));
				
				// litter nr -> OldRhutDbLitterId + AnimalType (if -1 then animaltype=GMO, if 0 then animaltype=WT (Gewoon dier))
				String litterId = tuple.getString("litter nr");
				List<String> animalNameList;
				if (litterMap.get(litterId) != null) {
					animalNameList = litterMap.get(litterId);
				} else {
					animalNameList = new ArrayList<String>();
				}
				animalNameList.add(animalName);
				litterMap.put(litterId, animalNameList);
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbLitterId"), now, 
						null, "OldRhutDbLitterId", animalName, litterId, null));
				String animalType = "B. Transgeen dier";
				if (litterId != null && litterId.equals("0")) {
					animalType = "A. Gewoon dier";
				}
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, 
						null, "AnimalType", animalName, animalType, null));
				
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
								now, null, "GeneState", animalName, geneState, null));
					}
					int index2 = genotype.indexOf("2");
					if (index2 != -1) {
						geneName = geneNameBase + "2";
						geneName += " KO";
						geneState = genotype.substring(index2 + 1, index2 + 4);
						if (geneState.equals("-/+")) geneState = "+/-";
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneModification", animalName, geneName, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype2"), 
								now, null, "GeneState", animalName, geneState, null));
					}
				}
				
				// arrival date and rem date -> Active start and end time
				// Don't set DeathDate (to rem date) because we do not know if the animal was terminated or removed
				String state = "Alive";
				String startDateString = tuple.getString("arrival date");
				Date startDate = null;
				Date remDate = null;
				if (startDateString != null) {
					startDate = dbFormat.parse(startDateString);
				}
				String remDateString = tuple.getString("rem date");
				if (remDateString != null) {
					state = "Dead";
					remDate = dbFormat.parse(remDateString);
					removalDateMap.put(animalName, remDate);
				}
				activeMap.put(animalName, ct.createObservedValue(invName, appMap.get("SetActive"), 
						startDate, remDate, "Active", animalName, state, null));
				
				//rem cause -> Removal
				String removal = tuple.getString("rem cause");
				if (removal != null && remDate != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemoval"), 
							remDate, null, "Removal", animalName, removal, null));
				}
				
				// remarks -> Source + sometimes DoB
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
					sourceMap.put(animalName, sourceName);
					if (source.toUpperCase().contains("DOB")) {
						int start = source.toUpperCase().indexOf("DOB");
						Pattern p = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
						Matcher m = p.matcher(source.substring(start));
						if (m.find()) {
							String year = m.group(3);
							if (year.length() == 1) {
								year = "200" + year;
							}
							String dobDateString = m.group(1) + "-" + m.group(2) + "-" + year;
							Date dobDate = dobFormat.parse(dobDateString);
							dobDateString = newDateOnlyFormat.format(dobDate);
							valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
									remDate, null, "DateOfBirth", animalName, dobDateString, null));
						}
					}
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
	
	public void parseParentRelations(String filename) throws Exception
	{	
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = new Date();
				
				// litter nr -> skip first two lines
				String litter = tuple.getString("litter nr");
				if (litter.equals("-1") || litter.equals("0")) {
					return;
				}
				// ID mother
				String motherName = animalMap.get(tuple.getString("ID mother"));
				// lit mtr -> SKIP
				// Gen mother
				String genMother = tuple.getString("Gen mother");
				genMother = extractGene(genMother);
				// ID father
				String fatherName = animalMap.get(tuple.getString("ID father"));
				// lit ftr -> SKIP
				// Gen father
				String genFather = tuple.getString("Gen father");
				genFather = extractGene(genFather);
				// GMO -> SKIP
				// Pair StartDate -> convert to yyyy-mm-dd format
				String startDate = tuple.getString("Pair StartDate");
				if (startDate != null && !startDate.equals("")) {
					Date tmpStartDate = dbFormat.parse(startDate);
					startDate = newDateOnlyFormat.format(tmpStartDate);
				}
				// DOB -> convert to yyyy-mm-dd format
				String dob = tuple.getString("DOB");
				Date dobDate = null;
				if (dob != null && !dob.equals("")) {
					dobDate = dbFormat.parse(dob);
					dob = newDateOnlyFormat.format(dobDate);
				}
				// Wean date -> convert to yyyy-mm-dd format
				String weanDate = tuple.getString("Wean date");
				if (weanDate != null && !weanDate.equals("")) {
					Date tmpWeanDate = dbFormat.parse(weanDate);
					weanDate = newDateOnlyFormat.format(tmpWeanDate);
				}
				// females weaned
				int femWeaned = 0;
				if (tuple.getInt("females weaned") != null ) {
					femWeaned = tuple.getInt("females weaned");
				}
				// males weaned
				int maleWeaned = 0;
				if (tuple.getInt("males weaned") != null) {
					maleWeaned = tuple.getInt("males weaned");
				}
				// . born (if not set, use sum of wean sizes)
				int nrBorn = femWeaned + maleWeaned;
				if (tuple.getInt(". born") != null) {
					nrBorn = tuple.getInt(". born");
				}
				// remarks
				String remark = tuple.getString("remarks");
				// To be Genotyped -> SKIP
				
				// Find out line based on parents' genes
				// if both (mother+father) WT offspring is WT
				// if both C57 offspring is C57
				// else offspring is line of parent who is not WT or C57
				// Note: WTPer and WTCry are also WT
				String lineName = "WT";
				if (genFather.equals("WT") || genMother.equals("WT")) {
					if (genFather.equals("WT")) {
						lineName = genMother;
					}
					if (genMother.equals("WT")) {
						lineName = genFather;
					}
				} else if (genFather.equals("C57BL/6j") || genMother.equals("C57BL/6j")) {
					if (genFather.equals("C57BL/6j")) {
						lineName = genMother;
					}
					if (genMother.equals("C57BL/6j")) {
						lineName = genFather;
					}
				} else {
					if (genFather.equals(genMother)) {
						lineName = genFather;
					} else {
						lineName = genMother + " X " + genFather;
					}
				}
				
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
				// Link parents to parentgroup (if known)
				if (motherName != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), 
						now, null, "Mother", motherName, null, parentgroupName));
				}
				if (fatherName != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), 
						now, null, "Father", fatherName, null, parentgroupName));
				}
				// Set line of parentgroup
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
						now, null, "Line", parentgroupName, null, lineName));
				// Set StartDate
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetStartDate"), 
						now, null, "StartDate", parentgroupName, startDate, null));
				
				// Make a litter and set birth, wean and genotype dates + sizes
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
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
						now, null, "DateOfBirth", litterName, dob, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), 
						now, null, "WeanDate", litterName, weanDate, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotypeDate"), 
						now, null, "GenotypeDate", litterName, weanDate, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSize"), 
						now, null, "Size", litterName, Integer.toString(nrBorn), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSize"), 
						now, null, "WeanSize", litterName, Integer.toString(femWeaned + maleWeaned), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeMale"), 
						now, null, "WeanSizeMale", litterName, Integer.toString(maleWeaned), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeFemale"), 
						now, null, "WeanSizeFemale", litterName, Integer.toString(femWeaned), null));
				// Set Remark
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
						now, null, "Remark", litterName, remark, null));
				// Link litter to parentgroup
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroup"), 
						now, null, "Parentgroup", litterName, null, parentgroupName));
				
				// Find animals that came out of this litter, using 'litter' as index for the map of litters and animals
				if (litterMap.get(litter) != null) {
					for (String animalName : litterMap.get(litter)) {
						// Link animal to litter
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), 
								now, null, "Litter", animalName, null, litterName));
						// Set birth date and line also on animal
						// Get Active value from map; every animal has one
						ObservedValue activeValue = activeMap.get(animalName);
						if (activeValue.getTime() == null) {
							activeValue.setTime(dobDate);
						}
						activeMap.remove(animalName);
						valuesToAddList.add(activeValue);
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
								now, null, "DateOfBirth", animalName, dob, null));
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
								now, null, "Line", animalName, null, lineName));
					}
				}
			}
		});
	}
	
	public void populateDec(String filename) throws Exception
	{	
		final List<String> addedDecApps = new ArrayList<String>();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = new Date();
				
				// DEC. -> name for DEC project + DecNr + ExperimentNr
				String decNr = tuple.getString("DEC.");
				if (decNr.equals("0")) {
					return;
				}
				String project = "DEC";
				String expNr = "A";
				if (decNr.length() == 5) {
					expNr = decNr.substring(4).toUpperCase();
					decNr = decNr.substring(0, 4);
				}
				project += decNr;
				// Title -> name and ExperimentTitle for subproject, DecTitle for project
				String subproject = tuple.getString("Title");
				// If not added yet, make new DEC app
				if (!addedDecApps.contains(project)) {
					addedDecApps.add(project);
					panelsToAddList.add(ct.createPanel(invName, project, userName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
							now, null, "TypeOfGroup", project, "DecApplication", null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecProjectSpecs"), 
							now, null, "DecNr", project, decNr, null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecProjectSpecs"), 
							now, null, "DecTitle", project, subproject, null));
				}
				// Make a DEC subproject
				panelsToAddList.add(ct.createPanel(invName, subproject, userName));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
						now, null, "TypeOfGroup", subproject, "Experiment", null));
				// Link to DEC app
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecSubprojectSpecs"), 
						now, null, "DecApplication", subproject, null, project));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecSubprojectSpecs"), 
						now, null, "ExperimentNr", subproject, expNr, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecSubprojectSpecs"), 
						now, null, "ExperimentTitle", subproject, subproject, null));
				// Exp. -> OldRhutDbExperimentId (on the DEC subproject) + 
				// store in map for later linking of animals to subprojects
				String oldId = tuple.getString("Exp.");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldRhutDbExperimentId"), 
						now, null, "OldRhutDbExperimentId", subproject, oldId, null));
				decMap.put(oldId, subproject);
				alternativeDecMap.put(decNr + expNr, subproject);
				// Researcher -> DecApplicantId (on the DEC app)
				String res = tuple.getString("Researcher");
				res = res.split("/")[0]; // if multiple names, take first as leading
				res = res.replace(" ", "");
				Integer resId;
				if (!researcherMap.containsKey(res)) {
					MolgenisUser newUser = new MolgenisUser();
					newUser.setName(res);
					db.add(newUser);
					resId = newUser.getId();
					researcherMap.put(res, resId);
				} else {
					resId = researcherMap.get(res);
				}
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecProjectSpecs"), 
						now, null, "DecApplicantId", project, resId.toString(), null));
				// DECStartDate -> StartDate (on both)
				String startDate = tuple.getString("DECStartDate");
				Date tmpDate = expDbFormat.parse(startDate);
				startDate = newDateOnlyFormat.format(tmpDate);
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecProjectSpecs"), 
						now, null, "StartDate", project, startDate, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecSubprojectSpecs"), 
						now, null, "StartDate", subproject, startDate, null));
				// DECEndDate -> EndDate (on both)
				String endDate = tuple.getString("DECEndDate");
				tmpDate = expDbFormat.parse(endDate);
				endDate = newDateOnlyFormat.format(tmpDate);
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecProjectSpecs"), 
						now, null, "EndDate", project, endDate, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDecSubprojectSpecs"), 
						now, null, "EndDate", subproject, endDate, null));
				// Room . -> skip
			}
		});
	}
	
	public void parseDecRelations(String filename) throws Exception
	{	
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				// Case -> skip
				// Exp. or DEC. -> to relate to Experiments.csv (DEC is leading)
				String subproject;
				String oldId = tuple.getString("Exp.");
				if (oldId.equals("0")) {
					return;
				}
				String dec = tuple.getString("DEC.");
				if (dec != null && !dec.equals("0")) {
					subproject = alternativeDecMap.get(dec.toUpperCase());
				} else {
					subproject = decMap.get(oldId);
				}
				if (subproject == null) {
					return;
				}
				// ID -> animal
				String animal = animalMap.get(tuple.getString("ID"));
				if (animal == null) {
					return;
				}
				// InExp Date -> start date of Experiment value
				Date startDate = dbFormat.parse(tuple.getString("InExp Date"));
				// OutExp Date -> end date of Experiment value
				Date endDate = null;
				if (tuple.getString("OutExp Date") != null) {
					endDate = dbFormat.parse(tuple.getString("OutExp Date"));
				} else {
					// If animal is dead, use death date
					if (removalDateMap.get(animal) != null) {
						endDate = removalDateMap.get(animal);
					}
				}
				// Treatment -> skip
				
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("AnimalInSubproject"), 
						startDate, endDate, "Experiment", animal, null, subproject));
				// SourceTypeSubproject
				String sourceName = sourceMap.get(animal);
				String sourceType = null;
				if (sourceName != null) {
					if (sourceName.equals("ErasmusMC")) {
						sourceType = "Geregistreerde fok/aflevering in Nederland";
					}
					if (sourceName.equals("Kweek moleculaire neurobiologie")) {
						sourceType = "Geregistreerde fok/aflevering in Nederland";
					}
					if (sourceName.equals("Harlan")) {
						sourceType = "Geregistreerde fok/aflevering in Nederland";
					}
					if (sourceName.equals("JacksonCharlesRiver")) {
						sourceType = "Andere herkomst";
					}
				} else {
					// assume default source is eigen kweek
					sourceType = "Geregistreerde fok/aflevering in Nederland";
				}
				// Check for reuse in the year of this experiment
				String startOfExpYear = yearOnlyFormat.format(startDate) + "-01-01";
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animal));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.NOT, subproject));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.GREATER_EQUAL, startOfExpYear));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.LESS_EQUAL, newDateOnlyFormat.format(startDate)));
				if (q.find().size() == 1) {
					sourceType = "Hergebruik eerste maal in het registratiejaar";
				}
				if (q.find().size() > 1) {
					sourceType = "Hergebruik tweede, derde enz. maal in het registratiejaar";
				}
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("AnimalInSubproject"), 
						endDate, null, "SourceTypeSubproject", animal, sourceType, null));
				
				if (endDate != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("AnimalFromSubproject"), 
							endDate, null, "FromExperiment", animal, null, subproject));
				}
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		// lines
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetSource");
		// animals
		makeProtocolApplication("SetOldRhutDbAnimalId");
		makeProtocolApplication("SetSpecies");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetOldRhutDbLitterId");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetBackground");
		makeProtocolApplication("SetGenotype", "SetGenotype1");
		makeProtocolApplication("SetGenotype", "SetGenotype2");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetRemoval");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetOldRhutDbSampleDate");
		makeProtocolApplication("SetOldRhutDbSampleNr");
		// parent relations
		makeProtocolApplication("SetMother");
		makeProtocolApplication("SetFather");
		makeProtocolApplication("SetLine");
		makeProtocolApplication("SetStartDate");
		makeProtocolApplication("SetWeanDate");
		makeProtocolApplication("SetGenotypeDate");
		makeProtocolApplication("SetSize");
		makeProtocolApplication("SetWeanSize");
		makeProtocolApplication("SetWeanSizeMale");
		makeProtocolApplication("SetWeanSizeFemale");
		makeProtocolApplication("SetRemark");
		makeProtocolApplication("SetParentgroup");
		makeProtocolApplication("SetLitter");
		// DEC
		makeProtocolApplication("SetDecProjectSpecs");
		makeProtocolApplication("SetDecSubprojectSpecs");
		makeProtocolApplication("SetOldRhutDbExperimentId");
		// Animals in DECs
		makeProtocolApplication("AnimalInSubproject");
		makeProtocolApplication("AnimalFromSubproject");
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
	
	private String extractGene(String gene)
	{
		if (gene == null) {
			return "unknown";
		}
		if (gene.contains("WT")) {
			return "WT";
		}
		if (gene.toLowerCase().contains("per") && gene.toLowerCase().contains("cry")) {
			return "PerCry";
		}
		if (gene.toLowerCase().contains("cry")) {
			return "Cry dKO";
		}
		if (gene.toLowerCase().contains("per")) {
			return "Per dKO";
		}
		if (gene.contains("C57")) {
			return "C57BL/6j";
		}
		if (gene.contains("C3H/He")) {
			return "C3H/He";
		}
		if (gene.contains("DBA")) {
			return "DBA";
		}
		if (gene.contains("ICR(CD-1)")) {
			return "ICR(CD-1)";
		}
		if (gene.contains("Swing")) {
			return "Swing";
		}
		if (gene.contains("CK1e")) {
			return "CK1e";
		}
		if (gene.contains("CBA/CaJ")) {
			return "CBA/CaJ";
		}
		return "unknown";
	}
}
