package convertors.ulidb;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

import commonservice.CommonService;

public class ConvertUliDbToPheno
{
	private Database db;
	private CommonService ct;
	private Login login;
	private Logger logger;
	final List<ProtocolApplication> protocolAppsToAddList;
	final List<Individual> animalsToAddList;
	final List<String> animalNames;
	final List<ObservedValue> valuesToAddList;
	final List<Panel> panelsToAddList;

	public ConvertUliDbToPheno(Database db, Login login) throws Exception
	{
		this.db = (JDBCDatabase) db;
		this.login = login;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		logger = Logger.getLogger("LoadUliDb");
		// Init lists that we can later add to the DB at once
		protocolAppsToAddList = new ArrayList<ProtocolApplication>();
		animalsToAddList = new ArrayList<Individual>();
		animalNames = new ArrayList<String>();
		valuesToAddList = new ArrayList<ObservedValue>();
		panelsToAddList = new ArrayList<Panel>();
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
		final int userId = login.getUserId();
		final int invid = ct.getInvestigationId("AnimalDB");
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				// laufende Nr -> make new animal
				String oldAnimalId = tuple.getString("laufende Nr");
				String animalName = "animal" + oldAnimalId;
				while (animalNames.contains(animalName)) { // make sure we have a unique name
					animalName += "_dup";
				}
				animalNames.add(animalName);
				Individual newAnimal = ct.createIndividual(invid, animalName, userId);
				animalsToAddList.add(newAnimal);
			}
		});
	}
	
	public void populateProtocolApplication() throws Exception
	{
		final int invid = ct.getInvestigationId("AnimalDB");
		// Protocols we want to retrieve only once:
		final int customIdProtocolId = ct.getProtocolId("SetCustomId");
		final int oldUliDbIdProtocolId = ct.getProtocolId("SetOldUliDbId");
		final int speciesProtocolId = ct.getProtocolId("SetSpecies");
		final int activeProtocolId = ct.getProtocolId("SetActive");
		final int sourceProtocolId = ct.getProtocolId("SetSource");
		final int oldUliDbKuerzelProtocolId = ct.getProtocolId("SetOldUliDbKuerzel");
		final int remarkProtocolId = ct.getProtocolId("SetRemark");
		final int oldUliDbAktenzeichenProtocolId = ct.getProtocolId("SetOldUliDbAktenzeichen");
		final int oldUliDbExperimentatorProtocolId = ct.getProtocolId("SetOldUliDbExperimentator");
		final int oldUliDbTierschutzrechtProtocolId = ct.getProtocolId("SetOldUliDbTierschutzrecht");
		final int sexProtocolId = ct.getProtocolId("SetSex");
		final int colorProtocolId = ct.getProtocolId("SetColor");
		final int earmarkProtocolId = ct.getProtocolId("SetEarmark");
		final int genotypeProtocolId = ct.getProtocolId("SetGenotype");
		final int backgroundProtocolId = ct.getProtocolId("SetBackground");
		// Protocols for parent relations:
		final int typeOfGroupProtocolId = ct.getProtocolId("SetTypeOfGroup");
		final int motherProtocolId = ct.getProtocolId("SetMother");
		final int fatherProtocolId = ct.getProtocolId("SetFather");
		final int lineProtocolId = ct.getProtocolId("SetLine");
		final int parentgroupProtocolId = ct.getProtocolId("SetParentgroup");
		final int litterProtocolId = ct.getProtocolId("SetLitter");
		
		// Make protocol apps for each protocol and add them to the list
		
		ProtocolApplication customIdApp = ct.createProtocolApplication(invid, customIdProtocolId);
		protocolAppsToAddList.add(customIdApp);
		
		ProtocolApplication oldUliDbIdApp = ct.createProtocolApplication(invid, oldUliDbIdProtocolId);
		protocolAppsToAddList.add(oldUliDbIdApp);
		
		ProtocolApplication speciesApp = ct.createProtocolApplication(invid, speciesProtocolId);
		protocolAppsToAddList.add(speciesApp);
		
		ProtocolApplication activeApp = ct.createProtocolApplication(invid, activeProtocolId);
		protocolAppsToAddList.add(activeApp);
		
		ProtocolApplication sourceApp = ct.createProtocolApplication(invid, sourceProtocolId);
		protocolAppsToAddList.add(sourceApp);
		
		ProtocolApplication oldUliDbKuerzelApp = ct.createProtocolApplication(invid, oldUliDbKuerzelProtocolId);
		protocolAppsToAddList.add(oldUliDbKuerzelApp);
		
		ProtocolApplication remarkApp = ct.createProtocolApplication(invid, remarkProtocolId);
		protocolAppsToAddList.add(remarkApp);
		
		ProtocolApplication oldUliDbAktenzeichenApp = ct.createProtocolApplication(invid, oldUliDbAktenzeichenProtocolId);
		protocolAppsToAddList.add(oldUliDbAktenzeichenApp);
		
		ProtocolApplication oldUliDbExperimentatorApp = ct.createProtocolApplication(invid, oldUliDbExperimentatorProtocolId);
		protocolAppsToAddList.add(oldUliDbExperimentatorApp);
		
		ProtocolApplication oldUliDbTierschutzrechtApp = ct.createProtocolApplication(invid, oldUliDbTierschutzrechtProtocolId);
		protocolAppsToAddList.add(oldUliDbTierschutzrechtApp);
		
		ProtocolApplication sexApp = ct.createProtocolApplication(invid, sexProtocolId);
		protocolAppsToAddList.add(sexApp);
		
		ProtocolApplication colorApp = ct.createProtocolApplication(invid, colorProtocolId);
		protocolAppsToAddList.add(colorApp);
		
		ProtocolApplication earmarkApp = ct.createProtocolApplication(invid, earmarkProtocolId);
		protocolAppsToAddList.add(earmarkApp);
		
		ProtocolApplication genotypeApp = ct.createProtocolApplication(invid, genotypeProtocolId);
		protocolAppsToAddList.add(genotypeApp);
		
		ProtocolApplication backgroundApp = ct.createProtocolApplication(invid, backgroundProtocolId);
		protocolAppsToAddList.add(backgroundApp);
		
		ProtocolApplication typeOfGroupApp = ct.createProtocolApplication(invid, typeOfGroupProtocolId);
		protocolAppsToAddList.add(typeOfGroupApp);
		
		ProtocolApplication motherApp = ct.createProtocolApplication(invid, motherProtocolId);
		protocolAppsToAddList.add(motherApp);
		
		ProtocolApplication fatherApp = ct.createProtocolApplication(invid, fatherProtocolId);
		protocolAppsToAddList.add(fatherApp);
		
		ProtocolApplication lineApp = ct.createProtocolApplication(invid, lineProtocolId);
		protocolAppsToAddList.add(lineApp);
		
		ProtocolApplication parentgroupApp = ct.createProtocolApplication(invid, parentgroupProtocolId);
		protocolAppsToAddList.add(parentgroupApp);
		
		ProtocolApplication litterApp = ct.createProtocolApplication(invid, litterProtocolId);
		protocolAppsToAddList.add(litterApp);
	}
	
	public void populateValue(String filename) throws Exception
	{
		final String speciesName = "House mouse";
		final int invid = ct.getInvestigationId("AnimalDB");
		final SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
		final Calendar calendar = Calendar.getInstance();
		// Measurements we want to retrieve only once:
		final int customIdMeasurementId = ct.getMeasurementId("CustomId");
		final int oldUliDbIdMeasurementId = ct.getMeasurementId("OldUliDbId");
		final int speciesMeasurementId = ct.getMeasurementId("Species");
		final int activeMeasurementId = ct.getMeasurementId("Active");
		final int sourceMeasurementId = ct.getMeasurementId("Source");
		final int oldUliDbKuerzelMeasurementId = ct.getMeasurementId("OldUliDbKuerzel");
		final int remarkMeasurementId = ct.getMeasurementId("Remark");
		final int oldUliDbAktenzeichenMeasurementId = ct.getMeasurementId("OldUliDbAktenzeichen");
		final int oldUliDbExperimentatorMeasurementId = ct.getMeasurementId("OldUliDbExperimentator");
		final int oldUliDbTierschutzrechtMeasurementId = ct.getMeasurementId("OldUliDbTierschutzrecht");
		final int sexMeasurementId = ct.getMeasurementId("Sex");
		final int colorMeasurementId = ct.getMeasurementId("Color");
		final int earmarkMeasurementId = ct.getMeasurementId("Earmark");
		final int geneMeasurementId = ct.getMeasurementId("Gene");
		final int geneStateMeasurementId = ct.getMeasurementId("GeneState");
		final int backgroundMeasurementId = ct.getMeasurementId("Background");
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				Date now = calendar.getTime();
				
				Individual newAnimal = animalsToAddList.get(line_number - 1);
				
				// Tiernummer -> CustomId
				String oldAnimalId = tuple.getString("Tiernummer");
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(0).getName(), now, 
						null, customIdMeasurementId, newAnimal.getName(), oldAnimalId, null));
				
				// laufende Nr -> OldUliDbId
				String oldUliDbId = tuple.getString("laufende Nr");
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(1).getName(), now, 
						null, oldUliDbIdMeasurementId, newAnimal.getName(), oldUliDbId, null));
				
				// Tierkategorie -> Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(2).getName(), now, 
						null, speciesMeasurementId, newAnimal.getName(), null, speciesName));
				
				// Eingangsdatum, Abgangsdatum and Status -> Active + start and end time
				String startDateString = tuple.getString("Eingangsdatum");
				Date startDate = null;
				if (startDateString != null) {
					startDate = sdf.parse(startDateString);
				}
				String endDateString = tuple.getString("Abgangsdatum");
				Date endDate = null;
				if (endDateString != null) {
					endDate = sdf.parse(endDateString);
				}
				// TODO: Abgangsdatum is often empty
				String state = tuple.getString("Status");
				if (state != null) {
					if (state.equals("lebt")) {
						state = "Alive";
					} else {
						state = "Dead";
					}
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(3).getName(), 
							startDate, endDate, activeMeasurementId, newAnimal.getName(), state, null));
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
						valuesToAddList.add(ct.createObservedValue(invid, 
								protocolAppsToAddList.get(4).getName(), now, null, sourceMeasurementId, 
								newAnimal.getName(), null, sourceName));
					}
				}
				
				// Kürzel -> OldUliDbKuerzel
				String kuerzel = tuple.getString("Kürzel");
				if (kuerzel != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(5).getName(), 
							now, null, oldUliDbKuerzelMeasurementId, newAnimal.getName(), kuerzel, null));
				}
				
				// Bemerkungen -> Remark
				String remark = tuple.getString("Bemerkungen");
				if (remark != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(6).getName(), 
							now, null, remarkMeasurementId, newAnimal.getName(), remark, null));
				}
				
				// Aktenzeichen -> OldUliDbAktenzeichen
				String aktenzeichen = tuple.getString("Aktenzeichen");
				if (aktenzeichen != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(7).getName(), 
							now, null, oldUliDbAktenzeichenMeasurementId, newAnimal.getName(), aktenzeichen, 
							null));
				}
				
				// Experimentator -> OldUliDbExperimentator
				String experimentator = tuple.getString("Experimentator");
				if (experimentator != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(8).getName(), 
							now, null, oldUliDbExperimentatorMeasurementId, newAnimal.getName(), 
							experimentator, null));
				}
				
				// Tierschutzrecht -> OldUliDbTierschutzrecht
				// TODO: actually this corresponds to Goal, but in AnimalDB that is linked
				// to a DEC subproject (Experiment) instead of to the individual animals.
				// For now, store in OldUliDbTierschutzrecht.
				String tierschutzrecht = tuple.getString("Tierschutzrecht");
				if (tierschutzrecht != null) {;
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(9).getName(), 
							now, null, oldUliDbTierschutzrechtMeasurementId, newAnimal.getName(), 
							tierschutzrecht, null));
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
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(10).getName(), 
							now, null, sexMeasurementId, newAnimal.getName(), null, sexName));
				}
				
				// Farbe -> Color
				String color = tuple.getString("Farbe");
				if (color != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(11).getName(), 
							now, null, colorMeasurementId, newAnimal.getName(), color, null));
				}
				
				// Ohrmarkierung1 -> Earmark
				String earmark = tuple.getString("Ohrmarkierung1");
				if (earmark != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(12).getName(), 
							now, null, earmarkMeasurementId, newAnimal.getName(), earmark, null));
				}
				
				// Gen and tg -> Gene and GeneState (in a SetGenotype protocol application)
				String gene = tuple.getString("Gen");
				String geneState = tuple.getString("tg");
				if (gene != null && geneState != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(13).getName(), 
							now, null, geneMeasurementId, newAnimal.getName(), gene, null));
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(13).getName(), 
							now, null, geneStateMeasurementId, newAnimal.getName(), geneState, null));
				}
				
				// gen Hintergrund-Tier -> Background
				String background = tuple.getString("gen Hintergrund-Tier");
				if (background != null) {
					valuesToAddList.add(ct.createObservedValue(invid, 
								protocolAppsToAddList.get(14).getName(), now, null, backgroundMeasurementId, 
								newAnimal.getName(), null, background));
				}
			}
		});
	}
	
	// TODO: rewrite using lists!
	public void parseParentRelations(String filename) throws Exception
	{
		final int invid = ct.getInvestigationId("AnimalDB");
		final Calendar calendar = Calendar.getInstance();
		// Measurements we want to retrieve only once:
		final int oldUliDbIdFeatureId = ct.getMeasurementId("OldUliDbId");
		final int typeOfGroupMeasurementId = ct.getMeasurementId("TypeOfGroup");
		final int motherMeasurementId = ct.getMeasurementId("Mother");
		final int fatherMeasurementId = ct.getMeasurementId("Father");
		final int lineMeasurementId = ct.getMeasurementId("Line");
		final int parentgroupMeasurementId = ct.getMeasurementId("Parentgroup");
		final int litterMeasurementId = ct.getMeasurementId("Litter");
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				Date now = calendar.getTime();
				
				Individual newAnimal = animalsToAddList.get(line_number - 1);
				
				// Mutter-Nr -> Mother
				List<String> motherList = new ArrayList<String>();
				String motherIdsString = tuple.getString("Mutter-Nr");
				String[] motherIds = motherIdsString.split(",\\s");
				for (String motherIdString : motherIds) {
					int oldMotherId = Integer.parseInt(motherIdString);
					// Find corresponding animal
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, oldUliDbIdFeatureId));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldMotherId));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() > 0) {
						ObservedValue tmpValue = valueList.get(0);
						motherList.add(tmpValue.getTarget_Name());
					}
				}
				
				// Vater-Nr -> Father
				List<String> fatherList = new ArrayList<String>();
				String fatherIdsString = tuple.getString("Vater-Nr");
				String[] fatherIds = fatherIdsString.split(",\\s");
				for (String fatherIdString : fatherIds) {
					int oldFatherId = Integer.parseInt(fatherIdString);
					// Find corresponding animal
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, oldUliDbIdFeatureId));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldFatherId));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() > 0) {
						ObservedValue tmpValue = valueList.get(0);
						fatherList.add(tmpValue.getTarget_Name());
					}
				}
				
				// Create a parentgroup
				String parentgroupName = "OldUliDbParentgroup" + line_number;
				panelsToAddList.add(ct.createPanel(invid, parentgroupName, login.getUserId()));
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(15).getName(), 
						now, null, typeOfGroupMeasurementId, parentgroupName, "Parentgroup", null));
				
				// Link parent(s) to parentgroup
				for (String motherName : motherList) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(16).getName(), 
							now, null, motherMeasurementId, parentgroupName, null, motherName));
				}
				for (String fatherName : fatherList) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(17).getName(), 
							now, null, fatherMeasurementId, parentgroupName, null, fatherName));
				}
				
				// Set line (Linie) of parentgroup
				String line = tuple.getString("Linie");
				if (line != null) {
					valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(18).getName(), 
							now, null, lineMeasurementId, parentgroupName, null, line));
				}
				
				// Make a litter
				String litterName = "OldUliDbLitter" + line_number;
				panelsToAddList.add(ct.createPanel(invid, litterName, login.getUserId()));
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(15).getName(), 
						now, null, typeOfGroupMeasurementId, litterName, "Litter", null));
				
				// Link litter to parentgroup
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(19).getName(), 
						now, null, parentgroupMeasurementId, litterName, null, parentgroupName));
				
				// Link animal to litter
				// TODO: now we make a litter for each animal, although multiple animals may be from the
				// same litter. However, we cannot know this for sure, since no litter information is
				// stored in the old Uli Eisel DB. How do we solve this?
				valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(20).getName(), 
						now, null, litterMeasurementId, newAnimal.getName(), null, litterName));
			}
		});
	}
	
	public void populateLine(String filename) throws Exception
	{
		final int invid = ct.getInvestigationId("AnimalDB");
		
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
				// Every gene becomes a code for the 'Gene' feature
				String geneName = tuple.getString("Gen");
				ct.makeCode(geneName, geneName, "Gene");
			}
		});
	}
	
	public void populateBackground(String filename) throws Exception
	{
		final int featureId = ct.getMeasurementId("TypeOfGroup");
		final int protocolId = ct.getProtocolId("SetTypeOfGroup");
		final int invid = ct.getInvestigationId("AnimalDB");
		
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
}
