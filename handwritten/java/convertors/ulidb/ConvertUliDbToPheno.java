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
	private Map<String, String> customIdMap;
	private Map<String, String> appMap;
	private Calendar calendar;
	private SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat sdfMolgenis = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);

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
		invName = "UliEisel";
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
		customIdMap = new HashMap<String, String>();
		appMap = new HashMap<String, String>();
	}
	
	public void writeToDb() {
		try {
			db.add(protocolAppsToAddList);
			db.add(animalsToAddList);
			db.add(panelsToAddList);
			db.add(valuesToAddList);
		} catch (Exception e) {
			logger.error("Writing to DB failed: " + e.getMessage());
			e.printStackTrace();
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
				// laufende Nr -> make new animal
				String oldAnimalId = tuple.getString("laufende Nr");
				String animalName = "animal" + oldAnimalId;
				while (animalNames.contains(animalName)) { // make sure we have a unique name
					animalName += "_dup";
				}
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invName, animalName, userName);
				animalsToAddList.add(newAnimal);
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		makeProtocolApplication("SetCustomID");
		makeProtocolApplication("SetOldUliDbId");
		makeProtocolApplication("SetSpecies");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetDeathDate");
		makeProtocolApplication("SetSource");
		makeProtocolApplication("SetOldUliDbKuerzel");
		makeProtocolApplication("SetRemark");
		makeProtocolApplication("SetOldUliDbAktenzeichen");
		makeProtocolApplication("SetOldUliDbExperimentator");
		makeProtocolApplication("SetOldUliDbTierschutzrecht");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetColor");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetGenotype");
		makeProtocolApplication("SetBackground");
		makeProtocolApplication("SetOldUliDbMotherInfo");
		makeProtocolApplication("SetOldUliDbFatherInfo");
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetMother");
		makeProtocolApplication("SetFather");
		makeProtocolApplication("SetLine");
		makeProtocolApplication("SetParentgroup");
		makeProtocolApplication("SetLitter");
		makeProtocolApplication("SetWeanDate");
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
				
				// Tiernummer -> CustomId
				String oldAnimalId = tuple.getString("Tiernummer");
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetCustomID"), now, 
						null, "CustomID", newAnimalName, oldAnimalId, null));
				customIdMap.put(oldAnimalId, newAnimalName);
				
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
					startDate = sdf.parse(startDateString);
					String dateOfBirth = sdfMolgenis.format(startDate);
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), 
							startDate, null, "DateOfBirth", newAnimalName, dateOfBirth, null));
				}
				String endDateString = tuple.getString("Abgangsdatum");
				Date endDate = null;
				if (endDateString != null) {
					endDate = sdf.parse(endDateString);
					String dateOfDeath = sdfMolgenis.format(endDate);
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
						// 51: Zucht- oder Liefereinrichtung innerhalb Deutschlands, die für ihre Tätigkeit eine Erlaubnis nach § 11 Abs. 1 Satz 1 Nr. 1 des Tierschutzgesetzes erhalten hat
						// 52: andere amtlich registrierte oder zugelassene Einrichtung innerhalb der EU
						// --> SourceType for both: Van EU-lid-staten
						sourceName = "UliEisel51and52";
					}
					if (uliSourceId == 55) {
						// 55: Switserland
						// --> SourceType: Andere herkomst
						sourceName = "UliEisel55";
					}
					if (uliSourceId != 0) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, 
								"Source", newAnimalName, null, sourceName));
					}
				}
				
				// Kürzel -> OldUliDbKuerzel
				String kuerzel = tuple.getString("Kürzel");
				if (kuerzel != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbKuerzel"), 
							now, null, "OldUliDbKuerzel", newAnimalName, kuerzel, null));
				}
				
				// Bemerkungen -> Remark
				String remark = tuple.getString("Bemerkungen");
				if (remark != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), 
							now, null, "Remark", newAnimalName, remark, null));
				}
				
				// Aktenzeichen -> OldUliDbAktenzeichen
				String aktenzeichen = tuple.getString("Aktenzeichen");
				if (aktenzeichen != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbAktenzeichen"), 
							now, null, "OldUliDbAktenzeichen", newAnimalName, aktenzeichen, null));
				}
				
				// Experimentator -> OldUliDbExperimentator
				String experimentator = tuple.getString("Experimentator");
				if (experimentator != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbExperimentator"), 
							now, null, "OldUliDbExperimentator", newAnimalName, experimentator, null));
				}
				
				// Tierschutzrecht -> OldUliDbTierschutzrecht
				// TODO: actually this corresponds to Goal, but in AnimalDB that is linked
				// to a DEC subproject (Experiment) instead of to the individual animals.
				// For now, store in OldUliDbTierschutzrecht.
				String tierschutzrecht = tuple.getString("Tierschutzrecht");
				if (tierschutzrecht != null) {;
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldUliDbTierschutzrecht"), 
							now, null, "OldUliDbTierschutzrecht", newAnimalName, tierschutzrecht, null));
				}
				
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
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetColor"), 
							now, null, "Color", newAnimalName, color, null));
				}
				
				// Ohrmarkierung1 -> Earmark
				String earmark = tuple.getString("Ohrmarkierung1");
				if (earmark != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetEarmark"), 
							now, null, "Earmark", newAnimalName, earmark, null));
				}
				
				// Gen and tg -> Gene and GeneState (in a SetGenotype protocol application)
				String gene = tuple.getString("Gen");
				String geneState = tuple.getString("tg");
				if (gene != null && geneState != null) {
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype"), 
							now, null, "GeneName", newAnimalName, gene, null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype"), 
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
					Date birthDate = sdf.parse(birthDateString);
					weanDate = sdfMolgenis.format(birthDate);
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
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (CustomID)
						String motherName = null;
						if (oldMotherId.toString().length() == 5) {
							motherName = oldUliDbIdMap.get(oldMotherId.toString());
						} else {
							motherName = customIdMap.get(oldMotherId.toString());
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
						// If 5 digits, it's a laufende Nr (OldUliDbId); if fewer, it's a Tiernummer (CustomId)
						String fatherName = null;
						if (oldFatherId.toString().length() == 5) {
							fatherName = oldUliDbIdMap.get(oldFatherId.toString());
						} else {
							fatherName = customIdMap.get(oldFatherId.toString());
						}
						if (fatherName != null) {
							fatherList.add(fatherName);
						}
					}
				}
				
				// Put date of birth, mother info and father info into one string and chack if we've
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
				
					// Create a parentgroup
					String parentgroupName = "OldUliDbParentgroup_" + newAnimalName;
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
					String line = tuple.getString("Linie");
					if (line != null) {
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), 
								now, null, "Line", parentgroupName, null, line));
					}
					
					// Make a litter and set wean date
					String litterName = "OldUliDbLitter_" + newAnimalName;
					panelsToAddList.add(ct.createPanel(invName, litterName, userName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), 
							now, null, "TypeOfGroup", litterName, "Litter", null));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), 
							now, null, "WeanDate", litterName, weanDate, null));
					
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
				// Make line panel
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
				// Every gene becomes a code for the 'GeneName' feature
				String geneName = tuple.getString("Gen");
				ct.makeCode(geneName, geneName, "Genename");
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
		ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
		protocolAppsToAddList.add(app);
		appMap.put(protocolName, app.getName());
	}
}
