package plugins.fillanimaldb;

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
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

import commonservice.CommonService;

public class LoadUliDb
{
	private Database db;
	private CommonService ct;
	private Login login;
	private Logger logger;

	public LoadUliDb(Database db, Login login) throws Exception
	{
		this.db = (JDBCDatabase) db;
		this.login = login;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		logger = Logger.getLogger("LoadUliDb");
	}

	public void populateAnimal(String filename) throws Exception
	{
		final int speciesId = ct.getObservationTargetId("House mouse");
		final int invid = ct.getInvestigationId("AnimalDB");
		final SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy H:mm:ss", Locale.US);
		final Calendar calendar = Calendar.getInstance();
		// Lots of Measurements and Protocols we want to retrieve only once:
		final int customIdProtocolId = ct.getProtocolId("SetCustomId");
		final int customIdMeasurementId = ct.getMeasurementId("CustomId");
		final int oldUliDbIdProtocolId = ct.getProtocolId("SetOldUliDbId");
		final int oldUliDbIdMeasurementId = ct.getMeasurementId("OldUliDbId");
		final int speciesProtocolId = ct.getProtocolId("SetSpecies");
		final int speciesMeasurementId = ct.getMeasurementId("Species");
		final int activeProtocolId = ct.getProtocolId("SetActive");
		final int activeMeasurementId = ct.getMeasurementId("Active");
		final int sourceProtocolId = ct.getProtocolId("SetSource");
		final int sourceMeasurementId = ct.getMeasurementId("Source");
		final int oldUliDbKuerzelProtocolId = ct.getProtocolId("SetOldUliDbKuerzel");
		final int oldUliDbKuerzelMeasurementId = ct.getMeasurementId("OldUliDbKuerzel");
		final int remarkProtocolId = ct.getProtocolId("SetRemark");
		final int remarkMeasurementId = ct.getMeasurementId("Remark");
		final int oldUliDbAktenzeichenProtocolId = ct.getProtocolId("SetOldUliDbAktenzeichen");
		final int oldUliDbAktenzeichenMeasurementId = ct.getMeasurementId("OldUliDbAktenzeichen");
		final int oldUliDbExperimentatorProtocolId = ct.getProtocolId("SetOldUliDbExperimentator");
		final int oldUliDbExperimentatorMeasurementId = ct.getMeasurementId("OldUliDbExperimentator");
		final int oldUliDbTierschutzrechtProtocolId = ct.getProtocolId("SetOldUliDbTierschutzrecht");
		final int oldUliDbTierschutzrechtMeasurementId = ct.getMeasurementId("OldUliDbTierschutzrecht");
		final int sexProtocolId = ct.getProtocolId("SetSex");
		final int sexMeasurementId = ct.getMeasurementId("Sex");
		final int colorProtocolId = ct.getProtocolId("SetColor");
		final int colorMeasurementId = ct.getMeasurementId("Color");
		final int earmarkProtocolId = ct.getProtocolId("SetEarmark");
		final int earmarkMeasurementId = ct.getMeasurementId("Earmark");
		final int genotypeProtocolId = ct.getProtocolId("SetGenotype");
		final int geneMeasurementId = ct.getMeasurementId("Gene");
		final int geneStateMeasurementId = ct.getMeasurementId("GeneState");
		final int backgroundProtocolId = ct.getProtocolId("SetBackground");
		final int backgroundMeasurementId = ct.getMeasurementId("Background");
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				Date now = calendar.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Tiernummer -> make new animal + CustomId
				String oldAnimalId = tuple.getString("Tiernummer");
				String animalName = "animal" + oldAnimalId;
				ObservationTarget newAnimal;
				while (ct.getObservationTargetId(animalName) != -1) { // check if one with this name already exists
					animalName += "_dup";
				}
				newAnimal = ct.createIndividual(invid, animalName, login.getUserId());
				db.add(newAnimal);
				int newAnimalId = newAnimal.getId();
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						customIdProtocolId, customIdMeasurementId, newAnimalId, oldAnimalId, 0));
				
				// laufende Nr -> OldUliDbId
				String oldUliDbId = tuple.getString("laufende Nr");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						oldUliDbIdProtocolId, oldUliDbIdMeasurementId, newAnimalId, oldUliDbId, 0));
				
				// Tierkategorie -> Species (always Mus musculus)
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						speciesProtocolId, speciesMeasurementId, newAnimalId, null, speciesId));
				
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
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, startDate, endDate, 
							activeProtocolId, activeMeasurementId, newAnimalId, state, 0));
				}
				
				// Herkunft -> Source
				Integer uliSourceId = tuple.getInt("Herkunft");
				if (uliSourceId != null) {
					int sourceId = 0;
					if (uliSourceId == 51 || uliSourceId == 52) {
						// 51: Zucht- oder Liefereinrichtung innerhalb Deutschlands, die für ihre Tätigkeit eine Erlaubnis nach § 11 Abs. 1 Satz 1 Nr. 1 des Tierschutzgesetzes erhalten hat
						// 52: andere amtlich registrierte oder zugelassene Einrichtung innerhalb der EU
						// --> SourceType for both: Van EU-lid-staten
						sourceId = ct.getObservationTargetId("UliEisel51and52");
					}
					if (uliSourceId == 55) {
						// 55: Switserland
						// --> SourceType: Andere herkomst
						sourceId = ct.getObservationTargetId("UliEisel55");
					}
					if (uliSourceId != 0) {
						valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
								sourceProtocolId, sourceMeasurementId, newAnimalId, null, sourceId));
					}
				}
				
				// Kürzel -> OldUliDbKuerzel
				String kuerzel = tuple.getString("Kürzel");
				if (kuerzel != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							oldUliDbKuerzelProtocolId, oldUliDbKuerzelMeasurementId, newAnimalId, kuerzel, 0));
				}
				
				// Bemerkungen -> Remark
				String remark = tuple.getString("Bemerkungen");
				if (remark != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							remarkProtocolId, remarkMeasurementId, newAnimalId, remark, 0));
				}
				
				// Aktenzeichen -> OldUliDbAktenzeichen
				String aktenzeichen = tuple.getString("Aktenzeichen");
				if (aktenzeichen != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							oldUliDbAktenzeichenProtocolId, oldUliDbAktenzeichenMeasurementId, newAnimalId, 
							aktenzeichen, 0));
				}
				
				// Experimentator -> OldUliDbExperimentator
				String experimentator = tuple.getString("Experimentator");
				if (experimentator != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							oldUliDbExperimentatorProtocolId, oldUliDbExperimentatorMeasurementId, newAnimalId, 
							experimentator, 0));
				}
				
				// Tierschutzrecht -> OldUliDbTierschutzrecht
				// TODO: actually this corresponds to Goal, but in AnimalDB that is linked
				// to a DEC subproject (Experiment) instead of to the individual animals.
				// For now, store in OldUliDbTierschutzrecht.
				String tierschutzrecht = tuple.getString("Tierschutzrecht");
				if (tierschutzrecht != null) {;
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							oldUliDbTierschutzrechtProtocolId, oldUliDbTierschutzrechtMeasurementId, 
							newAnimalId, tierschutzrecht, 0));
				}
				
				// BeschrGeschlecht -> Sex
				String sex = tuple.getString("BeschrGeschlecht");
				if (sex != null) {
					int sexId;
					if (sex.equals("w")) {
						sexId = ct.getObservationTargetId("Female");
					} else {
						sexId = ct.getObservationTargetId("Male");
					}
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							sexProtocolId, sexMeasurementId, newAnimalId, null, sexId));
				}
				
				// Farbe -> Color
				String color = tuple.getString("Farbe");
				if (color != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							colorProtocolId, colorMeasurementId, newAnimalId, color, 0));
				}
				
				// Ohrmarkierung1 -> Earmark
				String earmark = tuple.getString("Ohrmarkierung1");
				if (earmark != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							earmarkProtocolId, earmarkMeasurementId, newAnimalId, earmark, 0));
				}
				
				// Gen and tg -> Gene and GeneState (in a SetGenotype protocol application)
				String gene = tuple.getString("Gen");
				String geneState = tuple.getString("tg");
				if (gene != null && geneState != null) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							genotypeProtocolId, geneMeasurementId, newAnimalId, gene, 0));
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							genotypeProtocolId, geneStateMeasurementId, newAnimalId, geneState, 0));
				}
				
				// gen Hintergrund-Tier -> Background
				String background = tuple.getString("gen Hintergrund-Tier");
				if (background != null) {
					int bkgId = ct.getObservationTargetId(background);
					if (bkgId != -1) {
						valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
								backgroundProtocolId, backgroundMeasurementId, newAnimalId, null, bkgId));
					}
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}
	
	public void parseParentRelations(String filename) throws Exception
	{
		final int invid = ct.getInvestigationId("AnimalDB");
		final Calendar calendar = Calendar.getInstance();
		// Lots of Measurements and Protocols we want to retrieve only once:
		final int oldUliDbIdFeatureId = ct.getMeasurementId("OldUliDbId");
		final int typeOfGroupProtocolId = ct.getProtocolId("SetTypeOfGroup");
		final int typeOfGroupMeasurementId = ct.getMeasurementId("TypeOfGroup");
		final int motherProtocolId = ct.getProtocolId("SetMother");
		final int motherMeasurementId = ct.getMeasurementId("Mother");
		final int fatherProtocolId = ct.getProtocolId("SetFather");
		final int fatherMeasurementId = ct.getMeasurementId("Father");
		final int lineProtocolId = ct.getProtocolId("SetLine");
		final int lineMeasurementId = ct.getMeasurementId("Line");
		final int parentgroupProtocolId = ct.getProtocolId("SetParentgroup");
		final int parentgroupMeasurementId = ct.getMeasurementId("Parentgroup");
		final int litterProtocolId = ct.getProtocolId("SetLitter");
		final int litterMeasurementId = ct.getMeasurementId("Litter");
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				Date now = calendar.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// laufende Nr -> OldUliDbId -> animal ID
				String oldUliDbId = tuple.getString("laufende Nr");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, oldUliDbIdFeatureId));
				q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldUliDbId));
				List<ObservedValue> valueList = q.find();
				ObservedValue tmpValue = valueList.get(0);
				int animalId = tmpValue.getTarget_Id();
				
				// Linie -> Line
				String line = tuple.getString("Linie");
				
				// Mutter-Nr -> Mother
				List<Integer> motherIdList = new ArrayList<Integer>();
				String motherIdsString = tuple.getString("Mutter-Nr");
				motherIdsString.replace(" ", "");
				String[] motherIds = motherIdsString.split("/,/");
				for (String motherIdString : motherIds) {
					int oldMotherId = Integer.parseInt(motherIdString);
					// Find corresponding animal
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, oldUliDbIdFeatureId));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldMotherId));
					valueList = q.find();
					tmpValue = valueList.get(0);
					motherIdList.add(tmpValue.getTarget_Id());
				}
				
				// Vater-Nr -> Father
				List<Integer> fatherIdList = new ArrayList<Integer>();
				String fatherIdsString = tuple.getString("Vater-Nr");
				fatherIdsString.replace(" ", "");
				String[] fatherIds = fatherIdsString.split("/,/");
				for (String fatherIdString : fatherIds) {
					int oldFatherId = Integer.parseInt(fatherIdString);
					// Find corresponding animal
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, oldUliDbIdFeatureId));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldFatherId));
					valueList = q.find();
					tmpValue = valueList.get(0);
					fatherIdList.add(tmpValue.getTarget_Id());
				}
				
				// Create a parentgroup
				int groupId = ct.makePanel(invid, "OldUliDbParentgroup" + line_number, login.getUserId());
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						typeOfGroupProtocolId, typeOfGroupMeasurementId, groupId, "Parentgroup", 0));
				
				// Link parent(s) to parentgroup
				for (int motherId : motherIdList) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							motherProtocolId, motherMeasurementId, groupId, null, motherId));
				}
				for (int fatherId : fatherIdList) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							fatherProtocolId, fatherMeasurementId, groupId, null, fatherId));
				}
				
				// Set line of parentgroup
				if (line != null) {
					int lineId = ct.getObservationTargetId(line);
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							lineProtocolId, lineMeasurementId, groupId, null, lineId));
				}
				
				// Make a litter
				int litterId = ct.makePanel(invid, "OldUliDbParentgroup" + line_number, login.getUserId());
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						typeOfGroupProtocolId, typeOfGroupMeasurementId, litterId, "Litter", 0));
				
				// Link litter to parentgroup
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
						null, parentgroupProtocolId, parentgroupMeasurementId, litterId, null, groupId));
				
				// Link animal to litter
				// TODO: now we make a litter for each animal, although multiple animals may be from the
				// same litter. However, we cannot know this for sure, since no litter information is
				// stored in the old Uli Eisel DB. How do we solve this?
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
						null, litterProtocolId, litterMeasurementId, animalId, null, litterId));

				// Add everything to DB
				db.add(valuesToAddList);
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
