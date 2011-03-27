package plugins.fillanimaldb;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

import commonservice.CommonService;

public class LoadAnimalDB
{
	private Database db;
	private CommonService ct;

	public LoadAnimalDB(Database db) throws Exception
	{
		this.db = (JDBCDatabase) db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
	}

	public void populateAnimal(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				String oldanimalid = tuple.getString("animalid");
				String oldanimalcustomid = tuple.getString("customid");
				//String name = tuple.getString("customid");
				String weandate = tuple.getString("weandate");
				if (weandate.equals("NULL")) weandate = null;
				//String status = tuple.getString("status");
				String oldsex = tuple.getString("sex");
				int oldspecies = tuple.getInt("species");
				String background = tuple.getString("background");
				int source = tuple.getInt("source");
				//String partgroup = tuple.getString("participantgroup");
				String remarks = tuple.getString("remarks");
				String oldlocid = tuple.getString("location");
				String oldlitterid = tuple.getString("litter");

				ObservationTarget newAnimal = ct.createIndividual(invid, "animal" + oldanimalid);
				db.add(newAnimal);
				int newanimalid = newAnimal.getId();
				int actorid = ct.getObservationTargetId("admin");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// 'Active' is set when applying events from old AnimalDB
				
				// AnimalType
				int protocolId = ct.getProtocolId("SetAnimalType");
				int measurementId = ct.getMeasurementId("AnimalType");
				String animalType = "A. Gewoon dier"; // safe assumption that this holds for all animals in OldADB
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, animalType, 0));

				// WeanDate
				protocolId = ct.getProtocolId("SetWeanDate");
				measurementId = ct.getMeasurementId("WeanDate");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, weandate, 0));

				// OldAnimalDBID
				protocolId = ct.getProtocolId("SetOldAnimalDBAnimalID");
				measurementId = ct.getMeasurementId("OldAnimalDBAnimalID");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, oldanimalid, 0));

				// OldAnimalDB customID
				protocolId = ct.getProtocolId("SetOldAnimalDBAnimalCustomID");
				measurementId = ct.getMeasurementId("OldAnimalDBAnimalCustomID");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, oldanimalcustomid, 0));
				
				// Sex
				int sexlink = 0;
				if (oldsex.equals("M")) {
					sexlink = ct.getObservationTargetId("Male");
				}
				if (oldsex.equals("F")) {
					sexlink = ct.getObservationTargetId("Female");
				}
				if (oldsex.equals("U")) {
					sexlink = ct.getObservationTargetId("UnknownSex");
				}
				protocolId = ct.getProtocolId("SetSex");
				measurementId = ct.getMeasurementId("Sex");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, null, sexlink));

				// Species
				int specieslink = 0;
				if (oldspecies == 1) {
					specieslink = ct.getObservationTargetId("Syrian hamster");
				}
				if (oldspecies == 2) {
					specieslink = ct.getObservationTargetId("European groundsquirrel");
				}
				if (oldspecies == 3) {
					specieslink = ct.getObservationTargetId("House mouse");
				}
				if (oldspecies == 4) {
					specieslink = ct.getObservationTargetId("Siberian hamster");
				}
				if (oldspecies == 5) {
					specieslink = ct.getObservationTargetId("Gray mouse lemur");
				}
				if (oldspecies == 6) {
					specieslink = ct.getObservationTargetId("Brown rat");
				}
				protocolId = ct.getProtocolId("SetSpecies");
				measurementId = ct.getMeasurementId("Species");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, null, specieslink));

				// Background
				int backgroundlink = 0;
				if (!background.equals("NULL")) {
					if (background.equals("1")) {
						backgroundlink = ct.getObservationTargetId("CD1");
					}
					if (background.equals("2")) {
						backgroundlink = ct.getObservationTargetId("C57black6J");
					}
				}
				protocolId = ct.getProtocolId("SetBackground");
				measurementId = ct.getMeasurementId("Background");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, null, backgroundlink));

				// Litter
				protocolId = ct.getProtocolId("SetOldAnimalDBLitterID");
				measurementId = ct.getMeasurementId("OldAnimalDBLitterID");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, oldlitterid, 0));

				// Source
				int newsource = 0;
				if (source == 1) {
					newsource = ct.getObservationTargetId("Harlan");
				}
				if (source == 2) {
					newsource = ct.getObservationTargetId("Kweek chronobiologie");
				}
				if (source == 3) {
					newsource = ct.getObservationTargetId("Kweek gedragsbiologie");
				}
				if (source == 4) {
					newsource = ct.getObservationTargetId("Kweek dierfysiologie");
				}
				protocolId = ct.getProtocolId("SetSource");
				measurementId = ct.getMeasurementId("Source");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, null, newsource));

				// Location
				if (!oldlocid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldlocid));
					List<ObservedValue> valueList = q.find();
					Iterator<ObservedValue> valueIt = valueList.iterator();
					while (valueIt.hasNext())
					{
						// Check that the target is a Location, since there are also Animals with OldAnimalDB ID's
						int newlocid = valueIt.next().getTarget_Id();
						ObservationTarget tmpTarget = ct.getObservationTargetById(newlocid);
						if (tmpTarget.getOntologyReference_Name().equals("Location")) {
							protocolId = ct.getProtocolId("SetLocation");
							measurementId = ct.getMeasurementId("Location");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
									now, null, protocolId, measurementId, newanimalid, null, newlocid));
							break;
						}
					}
				}

				// Participantgroup
				protocolId = ct.getProtocolId("SetParticipantGroup");
				measurementId = ct.getMeasurementId("ParticipantGroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, "Chrono- en gedragsbiologie", 0));

				// OldAnimalDBRemarks
				protocolId = ct.getProtocolId("SetOldAnimalDBRemarks");
				measurementId = ct.getMeasurementId("OldAnimalDBRemarks");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, remarks, 0));
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}

	public void populateLocation(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");

				int oldlocationid = tuple.getInt("locationid");
				String name = tuple.getString("name");
				String inloc = tuple.getString("inlocation");

				// Make location and set OldAnimalDBLocationID
				int locationId = ct.makeLocation(invid, name);
				int protocolId = ct.getProtocolId("SetOldAnimalDBLocationID");
				int featureId = ct.getMeasurementId("OldAnimalDBLocationID");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, featureId, locationId, Integer.toString(oldlocationid), 0));

				// SetSublocationOf
				if (!inloc.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule("value", Operator.EQUALS, inloc));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					int newLocationId = tmpValue.getTarget_Id();
					protocolId = ct.getProtocolId("SetSublocationOf");
					featureId = ct.getMeasurementId("Location");
					db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, featureId, locationId, null, newLocationId));
				}

			}
		});
	}

	public void populateLitter(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Name
				String name = tuple.getString("customlitterid");
				int litterid = ct.makePanel(invid, name);
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litterid, "Litter", 0));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				SimpleDateFormat sdfMolgenis = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				// pairstartdate -> time
				String pairStartDateString = tuple.getString("pairstartdate");
				Date pairStartDate = null;
				if (!pairStartDateString.equals("NULL")) {
					pairStartDate = sdf.parse(pairStartDateString);
					
				}

				// pairenddate -> endtime
				String pairEndDateString = tuple.getString("pairenddate");
				Date pairEndDate = null;
				if (!pairEndDateString.equals("NULL")) {
					pairEndDate = sdf.parse(pairEndDateString);
				}

				// Parentgroup
				int parentgroupid = ct.makePanel(invid, "Parentgroup" + litterid);
				protocolId = ct.getProtocolId("SetTypeOfGroup");
				measurementId = ct.getMeasurementId("TypeOfGroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, parentgroupid, "Parentgroup", 0));

				// Link litter to parentgroup
				protocolId = ct.getProtocolId("SetParentgroup");
				measurementId = ct.getMeasurementId("Parentgroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, pairStartDate, 
						pairEndDate, protocolId, measurementId, litterid, null, parentgroupid));

				// mother -> via Parentgroup
				String motherName = tuple.getString("mother");
				int motherid = 0;
				if (!motherName.equals("NULL")) {
					motherid = Integer.parseInt(motherName);
					int featureId = ct.getMeasurementId("OldAnimalDBAnimalID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule("value", Operator.EQUALS, motherid));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					int newmotherid = tmpValue.getTarget_Id();
					protocolId = ct.getProtocolId("SetMother");
					measurementId = ct.getMeasurementId("Mother");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
							pairStartDate, pairEndDate, protocolId, measurementId, parentgroupid, null, 
							newmotherid));
				}

				// father -> via Parentgroup
				String fatherName = tuple.getString("father");
				int fatherid = 0;
				if (!fatherName.equals("NULL")) {
					fatherid = Integer.parseInt(fatherName);
					int featureId = ct.getMeasurementId("OldAnimalDBAnimalID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule("value", Operator.EQUALS, fatherid));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					int newfatherid = tmpValue.getTarget_Id();
					protocolId = ct.getProtocolId("SetFather");
					measurementId = ct.getMeasurementId("Father");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
							pairStartDate, pairEndDate, protocolId, measurementId, parentgroupid, null, 
							newfatherid));
				}

				// birthsize -> Size
				Integer birthsize = tuple.getInt("birthsize");
				protocolId = ct.getProtocolId("SetSize");
				measurementId = ct.getMeasurementId("Size");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litterid, birthsize.toString(), 0));

				// birthday -> DateOfBirth
				String birthDayString = tuple.getString("birthday");
				Date birthDayDate = null;
				if (birthDayString.equals("NULL")) {
					birthDayString = null;
				}else {
					//change date formatting from mysql to molgenis style
					birthDayDate = sdf.parse(birthDayString);
					birthDayString = sdfMolgenis.format(birthDayDate);
				}
					
				protocolId = ct.getProtocolId("SetDateOfBirth");
				measurementId = ct.getMeasurementId("DateOfBirth");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litterid, birthDayString, 0));

				// numberweaned -> WeanSize
				String weanSize = tuple.getString("numberweaned");
				if (!weanSize.equals("NULL")) {
					protocolId = ct.getProtocolId("SetWeanSize");
					measurementId = ct.getMeasurementId("WeanSize");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
							protocolId, measurementId, litterid, weanSize, 0));
				}

				// remarks -> OldAnimalDBRemarks
				String remarks = tuple.getString("remarks");
				protocolId = ct.getProtocolId("SetOldAnimalDBRemarks");
				measurementId = ct.getMeasurementId("OldAnimalDBRemarks");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, litterid, remarks, 0));

				// Link animals to litters
				String oldlitterid = tuple.getString("litterid");
				if (!oldlitterid.equals("NULL"))
				{
					int featureId = ct.getMeasurementId("OldAnimalDBLitterID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldlitterid));
					List<ObservedValue> valueList = q.find();
					Iterator<ObservedValue> valueIt = valueList.iterator();
					while (valueIt.hasNext())
					{
						ObservedValue tmpValue = valueIt.next();
						int animalid = tmpValue.getTarget_Id();
						protocolId = ct.getProtocolId("SetLitter");
						measurementId = ct.getMeasurementId("Litter");
						valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, 
								null, protocolId, measurementId, animalid, null, litterid));
					}
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}

		});
	}

	public void populateExperiment(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Name
				String name = tuple.getString("title");
				int expid = ct.makePanel(invid, name);
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, expid, "Experiment", 0));

				// Make protocol application to use with all the values
				protocolId = ct.getProtocolId("SetDecSubprojectSpecs");
				ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
				db.add(app);
				int eventid = app.getId();
				
				// experimentid
				String oldexperimentid = tuple.getString("experimentid");
				measurementId = ct.getMeasurementId("OldAnimalDBExperimentID");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						oldexperimentid, 0));

				// decapplication
				String decapp = tuple.getString("decapplication");
				measurementId = ct.getMeasurementId("OldAnimalDBDecApplicationID");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						decapp, 0));

				// experimentnr
				String experimentnr = tuple.getString("experimentnr");
				measurementId = ct.getMeasurementId("ExperimentNr");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						experimentnr, 0));

				// pdfdecsubprojectapplication
				String pdfdec = tuple.getString("pdfdecsubprojectapplication");
				if (!pdfdec.equals("NULL")) {
					measurementId = ct.getMeasurementId("DecSubprojectApplicationPdf");
					valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
							pdfdec, 0));
				}

				String codeValue = "";

				// concern (lookup)
				int concern = tuple.getInt("concern");
				if (concern == 1) {
					codeValue = "A. Gezondheid/voed. ja";
				}
				if (concern == 2) {
					codeValue = "B. Gezondheid/voed. nee";
				}
				measurementId = ct.getMeasurementId("Concern");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// goal (lookup)
				int goal = tuple.getInt("goal");
				if (goal == 37) {
					codeValue = "E. Wetensch.vraag m.b.t.: and. wetenschappelijke vraag";
				}
				measurementId = ct.getMeasurementId("Goal");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// specialtechn (lookup)
				int specialtechn = tuple.getInt("specialtechn");
				if (specialtechn == 1) {
					codeValue = "A. Geen van deze technieken/ingrepen";
				}
				if (specialtechn == 2) {
					codeValue = "B. Doden zonder voorafgaande handelingen";
				}
				measurementId = ct.getMeasurementId("SpecialTechn");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// lawprovisions (lookup)
				int lawprovisions = tuple.getInt("lawprovisions");
				if (lawprovisions == 1) {
					codeValue = "A. Geen wettelijke bepaling";
				}
				measurementId = ct.getMeasurementId("LawDef");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// toxres (lookup)
				int toxres = tuple.getInt("toxres");
				if (toxres == 1) {
					codeValue = "A. Geen toxicologisch onderzoek";
				}
				measurementId = ct.getMeasurementId("ToxRes");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// anaesthesia (lookup)
				int anaesthesia = tuple.getInt("anaesthesia");
				if (anaesthesia == 1) {
					codeValue = "A. Is niet toegepast (geen aanleiding)";
				}
				if (anaesthesia == 4) {
					codeValue = "D. Is wel toegepast";
				}
				measurementId = ct.getMeasurementId("Anaesthesia");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// painmanagement (lookup)
				int painmanagement = tuple.getInt("painmanagement");
				if (painmanagement == 1) {
					codeValue = "A. Is niet toegepast (geen aanleiding)";
				}
				if (painmanagement == 4) {
					codeValue = "D. Is wel toegepast";
				}
				measurementId = ct.getMeasurementId("PainManagement");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// animalendstatus (lookup)
				int animalendstatus = tuple.getInt("animalendstatus");
				if (animalendstatus == 1) {
					codeValue = "A. Dood in het kader van de proef";
				}
				if (animalendstatus == 3) {
					codeValue = "C. Na einde proef in leven gelaten";
				}
				measurementId = ct.getMeasurementId("AnimalEndStatus");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						codeValue, 0));

				// remarks
				String remarks = tuple.getString("remarks");
				measurementId = ct.getMeasurementId("OldAnimalDBRemarks");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, now, null, measurementId, expid, 
						remarks, 0));
			
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}

	public void populateDECApplication(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Name
				String name = tuple.getString("projecttitle");
				int decappid = ct.makePanel(invid, "DEC Project: " + name);
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, decappid, "DecApplication", 0));

				// Make protocol application to use with all the values
				protocolId = ct.getProtocolId("SetDecProjectSpecs");
				ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
				db.add(app);
				int eventid = app.getId();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				SimpleDateFormat sdfMolgenis = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				// applicationstartdate -> time
				String appStartDateString = tuple.getString("applicationstartdate");
				String appStartDateStringMolgenis = "";
				Date appStartDate = null;
				if (!appStartDateString.equals("NULL")) {
					appStartDate = sdf.parse(appStartDateString);
					appStartDateStringMolgenis = sdfMolgenis.format(appStartDate);
				}

				// applicationenddate -> endtime
				String appEndDateString = tuple.getString("applicationenddate");
				String appEndDateStringMolgenis = "";
				Date appEndDate = null;
				if (!appEndDateString.equals("NULL")) {
					appEndDate = sdf.parse(appEndDateString);
					appEndDateStringMolgenis = sdfMolgenis.format(appEndDate);
				}

				// StartDate
				measurementId = ct.getMeasurementId("StartDate");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
						measurementId, decappid, appStartDateStringMolgenis, 0));

				// EndDate
				measurementId = ct.getMeasurementId("EndDate");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
						measurementId, decappid, appEndDateStringMolgenis, 0));

				// Link experiments to DEC applications
				String olddecappid = tuple.getString("decapplicationid");
				if (!olddecappid.equals("NULL")) {
					int featureId = ct.getMeasurementId("OldAnimalDBDecApplicationID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					q.addRules(new QueryRule("value", Operator.EQUALS, olddecappid));
					List<ObservedValue> valueList = q.find();
					Iterator<ObservedValue> valueIt = valueList.iterator();
					while (valueIt.hasNext()) {
						ObservedValue tmpValue = valueIt.next();
						int experimentId = tmpValue.getTarget_Id();
						measurementId = ct.getMeasurementId("DecApplication");
						valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
								measurementId, experimentId, null, decappid));
					}
				}

				// decnr
				String decnr = tuple.getString("decnr");
				measurementId = ct.getMeasurementId("DecNr");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
						measurementId, decappid, decnr, 0));

				// decapplicant
				String decapplicant = tuple.getString("decapplicant");
				measurementId = ct.getMeasurementId("DecApplicantId");
				valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
						measurementId, decappid, decapplicant, 0));

				// pdfdecapplication
				String pdfdec = tuple.getString("pdfdecapplication");
				if (!pdfdec.equals("NULL")) {
					measurementId = ct.getMeasurementId("DecApplicationPdf");
					valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
							measurementId, decappid, pdfdec, 0));
				}

				// pdfdecapproval
				pdfdec = tuple.getString("pdfdecapproval");
				if (!pdfdec.equals("NULL")) {
					measurementId = ct.getMeasurementId("DecApprovalPdf");
					valuesToAddList.add(ct.createObservedValue(invid, eventid, appStartDate, appEndDate, 
							measurementId, decappid, pdfdec, 0));
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}

	public void populateAnimalsInExperiments(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");

				// experimentid
				int newexpid = 0;
				String oldexpid = tuple.getString("experimentid");
				if (!oldexpid.equals("NULL")) {
					int featureid = ct.getMeasurementId("OldAnimalDBExperimentID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldexpid));
					List<ObservedValue> valueList = q.find();
					newexpid = valueList.get(0).getTarget_Id();
				}

				// animalid
				int newanimalid = 0;
				String oldanimalid = tuple.getString("animalid");
				if (!oldanimalid.equals("NULL")) {
					int featureid = ct.getMeasurementId("OldAnimalDBAnimalID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					newanimalid = valueList.get(0).getTarget_Id();
				}

				// source
				String source = tuple.getString("source");
				if (!source.equals("NULL")) {
					if (source.equals("1")) {
						source = "Eigen fok binnen uw organisatorische werkeenheid";
					}
					if (source.equals("3")) {
						source = "Geregistreerde fok/aflevering in Nederland";
					}
					if (source.equals("8")) {
						source = "Hergebruik eerste maal in het registratiejaar";
					}
				}

				// animaltype -> should not be set here but when bringing in an animal!
				/*String animaltype = tuple.getString("animaltype");
				if (!animaltype.equals("NULL")) {
					if (animaltype.equals("1")) {
						animaltype = "A. Gewoon dier";
					}
				}*/

				// painmanagement
				String painmanagement = tuple.getString("painmanagement");
				if (!painmanagement.equals("NULL")) {
					if (painmanagement.equals("1")) {
						painmanagement = "A. Is niet toegepast (geen aanleiding)";
					}
					if (painmanagement.equals("4")) {
						painmanagement = "D. Is wel toegepast";
					}
				}

				// anaesthesia
				String anaesthesia = tuple.getString("anaesthesia");
				if (!anaesthesia.equals("NULL")) {
					if (anaesthesia.equals("1")) {
						anaesthesia = "A. Is niet toegepast (geen aanleiding)";
					}
					if (anaesthesia.equals("4")) {
						anaesthesia = "D. Is wel toegepast";
					}
				}

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				// entrydate
				String entrydateString = tuple.getString("entrydate");
				Date entryDate = null;
				if (!entrydateString.equals("NULL")) {
					entryDate = sdf.parse(entrydateString);
				}
				// exitdate
				String exitdateString = tuple.getString("exitdate");
				Date exitDate = null;
				if (!exitdateString.equals("NULL")) {
					exitDate = sdf.parse(exitdateString);
				}

				// expdiscomfortlevel
				String expdiscomfortlevel = tuple.getString("expdiscomfortlevel");
				if (!expdiscomfortlevel.equals("NULL")) {
					if (expdiscomfortlevel.equals("1")) {
						expdiscomfortlevel = "A. Gering";
					}
					if (expdiscomfortlevel.equals("2")) {
						expdiscomfortlevel = "B. Gering/matig";
					}
					if (expdiscomfortlevel.equals("3")) {
						expdiscomfortlevel = "C. Matig";
					}
					if (expdiscomfortlevel.equals("4")) {
						expdiscomfortlevel = "D. Matig/ernstig";
					}
				}

				// actdiscomfortlevel
				String actdiscomfortlevel = tuple.getString("actdiscomfortlevel");
				if (!actdiscomfortlevel.equals("NULL")) {
					if (actdiscomfortlevel.equals("1")) {
						actdiscomfortlevel = "A. Gering";
					}
					if (actdiscomfortlevel.equals("2")) {
						actdiscomfortlevel = "B. Gering/matig";
					}
					if (actdiscomfortlevel.equals("3")) {
						actdiscomfortlevel = "C. Matig";
					}
					if (actdiscomfortlevel.equals("4")) {
						actdiscomfortlevel = "D. Matig/ernstig";
					}
				}

				// expexperimentendstatus
				String expexperimentendstatus = tuple.getString("expexperimentendstatus");
				if (!expexperimentendstatus.equals("NULL")) {
					if (expexperimentendstatus.equals("1")) {
						expexperimentendstatus = "A. Dood in het kader van de proef";
					}
					if (expexperimentendstatus.equals("2")) {
						expexperimentendstatus = "B. Gedood na beeindiging van de proef";
					}
					if (expexperimentendstatus.equals("3")) {
						expexperimentendstatus = "C. Na einde proef in leven gelaten";
					}
				}

				// actexperimentendstatus
				String actexperimentendstatus = tuple.getString("actexperimentendstatus");
				if (!actexperimentendstatus.equals("NULL")) {
					if (actexperimentendstatus.equals("1")) {
						actexperimentendstatus = "A. Dood in het kader van de proef";
					}
					if (actexperimentendstatus.equals("2")) {
						actexperimentendstatus = "B. Gedood na beeindiging van de proef";
					}
					if (actexperimentendstatus.equals("3")) {
						actexperimentendstatus = "C. Na einde proef in leven gelaten";
					}
				}
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Apply protocol 'in subproject'
				int protocolId = ct.getProtocolId("AnimalInSubproject");
				ProtocolApplication app = ct.createProtocolApplication(invid, protocolId);
				db.add(app);
				int protappid = app.getId();
				int measurementId = ct.getMeasurementId("Experiment");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate,measurementId, 
						newanimalid, null, newexpid));
				measurementId = ct.getMeasurementId("SourceTypeSubproject");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate, measurementId,
						newanimalid, source, 0));
				measurementId = ct.getMeasurementId("PainManagement");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate, measurementId,
						newanimalid, painmanagement, 0));
				measurementId = ct.getMeasurementId("Anaesthesia");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate, measurementId,
						newanimalid, anaesthesia, 0));
				measurementId = ct.getMeasurementId("ExpectedDiscomfort");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate, measurementId,
						newanimalid, expdiscomfortlevel, 0));
				measurementId = ct.getMeasurementId("ExpectedAnimalEndStatus");
				valuesToAddList.add(ct.createObservedValue(invid, protappid, entryDate, exitDate, measurementId,
						newanimalid, expexperimentendstatus, 0));

				// If applicable, apply protocol 'from subproject'
				if (exitDate != null) {
					protocolId = ct.getProtocolId("AnimalFromSubproject");
					app = ct.createProtocolApplication(invid, protocolId);
					db.add(app);
					protappid = app.getId();
					measurementId = ct.getMeasurementId("FromExperiment");
					valuesToAddList.add(ct.createObservedValue(invid, protappid, exitDate, null, measurementId, 
							newanimalid, null, newexpid));
					measurementId = ct.getMeasurementId("ActualDiscomfort");
					valuesToAddList.add(ct.createObservedValue(invid, protappid, exitDate, null, measurementId, 
							newanimalid, actdiscomfortlevel, 0));
					measurementId = ct.getMeasurementId("ActualAnimalEndStatus");
					valuesToAddList.add(ct.createObservedValue(invid, protappid, exitDate, null, measurementId, 
							newanimalid, actexperimentendstatus, 0));
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}

	public void populatePreset(String filename) throws Exception
	{

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");

				int actorid = ct.getObservationTargetId("admin");

				// Name
				String name = tuple.getString("name");

				// add the group in the newAnimaldb
				int groupId = ct.makePanel(invid, name);
				int protocolId = ct.getProtocolId("SetTypeOfGroup");
				int measurementId = ct.getMeasurementId("TypeOfGroup");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupId, "Selection", 0));

				// link the OldAnimalDBPResetID to the newly created group
				String oldpresetid = tuple.getString("presetid");
				protocolId = ct.getProtocolId("SetOldAnimalDBPresetID");
				measurementId = ct.getMeasurementId("OldAnimalDBPresetID");
				db.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, groupId, oldpresetid, 0));
			}
		});
	}

	public void populatePresetAnimals(String filename) throws Exception
	{

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();

				int invid = ct.getInvestigationId("AnimalDB");
				int actorid = ct.getObservationTargetId("admin");

				// oldanimalid --> new animalid
				int newanimalid = 0;
				String oldanimalid = tuple.getString("animalid");
				if (!oldanimalid.equals("NULL")) {
					int featureid = ct.getMeasurementId("OldAnimalDBAnimalID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					newanimalid = valueList.get(0).getTarget_Id();
				}
				// oldpresetid --> group id
				int newgroupid = 0;
				String oldpresetid = tuple.getString("presetid");
				if (!oldpresetid.equals("NULL")) {
					int featureid = ct.getMeasurementId("OldAnimalDBPresetID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldpresetid));
					List<ObservedValue> valueList = q.find();
					newgroupid = valueList.get(0).getTarget_Id();
				}

				// add animal to selection group
				db.add(ct.addObservationTargetToPanel(invid, newanimalid, now, newgroupid));
			}
		});
	}

	public void populateEvents(String filename) throws Exception
	{

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				int invid = ct.getInvestigationId("AnimalDB");
				int actorid = ct.getObservationTargetId("admin");
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// oldanimalid --> new animalid
				int newanimalid = 0;
				String oldanimalid = tuple.getString("animal");
				if (!oldanimalid.equals("NULL")) {
					int featureid = ct.getMeasurementId("OldAnimalDBAnimalID");
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
					q.addRules(new QueryRule("value", Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() > 0) {
						newanimalid = valueList.get(0).getTarget_Id();
					}
				}

				if (newanimalid > 0) {
					// date
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					String dateString = tuple.getString("date");
					Date eventDate = null;
					if (!dateString.equals("NULL")) {
						// only parse events that have a valid date!
						eventDate = sdf.parse(dateString);

						// get the type
						String eventType = tuple.getString("type");
						String eventDetails = tuple.getString("details");

						// Set the status and add the events
						// NB: the csv file must be Ascending data sorted,
						// otherwise errors can occur!
						if (eventType.equals("Born") || eventType.equals("BroughtIn")) {
							// Check if there is already an Active value:
							int measurementId = ct.getMeasurementId("Active");
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, newanimalid));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
							activeQuery.sortDESC(ObservedValue.TIME);
							List<ObservedValue> activeValueList = activeQuery.find();
							if (activeValueList.size() > 0) {
								ObservedValue activeValue = activeValueList.get(0);
								Date existingActiveStartdate = activeValue.getTime();
								// Compare dates and take the more recent one
								// (Probably this will be the BroughtIn one which should be later than Born)
								if (eventDate.after(existingActiveStartdate)) {
									activeValue.setTime(eventDate);
									db.update(activeValue);
								}
							} else {
								// make one
								int protocolId = ct.getProtocolId("SetActive");
								measurementId = ct.getMeasurementId("Active");
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
										eventDate, null, protocolId, measurementId, newanimalid, "Alive", 0));
							}
							if (eventType.equals("Born")) {
								// Set the date of birth based on the oldanimaldb "Born" event
								int protocolId = ct.getProtocolId("SetDateOfBirth");
								measurementId = ct.getMeasurementId("DateOfBirth");
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
										eventDate, null, protocolId, measurementId, newanimalid, 
										sdf.format(eventDate), 0));
							} else {
								// Set the broughtinevent based on the oldanimaldb "Broughtin" event
								int protocolId = ct.getProtocolId("SetOldAnimalDBBroughtinDate");
								measurementId = ct.getMeasurementId("OldAnimalDBBroughtinDate");
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, 
										eventDate, null, protocolId, measurementId, newanimalid, 
										sdf.format(eventDate), 0));
							}
						}
						if (eventType.equals("Died")) {
							// Set the date of death based on the oldanimaldb "Died" event
							String eventDateParsedString = sdf.format(eventDate);
							int protocolId = ct.getProtocolId("SetDeathDate");
							int measurementId = ct.getMeasurementId("DeathDate");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
									protocolId, measurementId, newanimalid, eventDateParsedString, 0));
							
							// Report as dead/removed by setting the endtime of the most recent Active value
							measurementId = ct.getMeasurementId("Active");
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, newanimalid));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
							activeQuery.sortDESC(ObservedValue.TIME);
							List<ObservedValue> activeValueList = activeQuery.find();
							if (activeValueList.size() > 0) {
								ObservedValue activeValue = activeValueList.get(0);
								activeValue.setEndtime(eventDate);
								activeValue.setValue("Dead");
								db.update(activeValue);
							}
							
							// If still in DEC subproject at time of death, end that
							measurementId = ct.getMeasurementId("Experiment");
							Query<ObservedValue> q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, newanimalid));
							q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
							q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, eventDateParsedString));
							q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
							List<ObservedValue> valueList = q.find();
							if (valueList.size() == 1) // Safe assumption: animal can only be in one experiment at a time
							{
								ObservedValue value = valueList.get(0);
								// set end date-time
								value.setEndtime(eventDate);
								db.update(value);
								// Maybe TODO: get end status values from somewhere and apply them?
								/*
								int protocolId = ct.getProtocolId("AnimalFromSubproject");
								int subprojectId = value.getRelatedObservationTarget();
								int protocolApplicationId = ct.makeProtocolApplication(investigationId, protocolId, now);
								featureId = ct.getMeasurementId("FromExperiment");
								ct.makeObservedValue(investigationId, protappid, deathDatetime, null, featureId, animalId,
										null, expid);
								featureId = ct.getMeasurementId("ActualDiscomfort");
								ct.makeObservedValue(investigationId, protappid, deathDatetime, null, featureId, animalId,
										discomfort, 0);
								featureId = ct.getMeasurementId("ActualAnimalEndStatus");
								ct.makeObservedValue(investigationId, protappid, deathDatetime, null, featureId, animalId,
										endstatus, 0);
								*/
							}
							
						}

						// Set the the cagecleaningevents based on the oldanimaldb "Cleaned" event
						if (eventType.equals("Cleaned")) {
							int protocolId = ct.getProtocolId("SetCageCleanDate");
							int measurementId = ct.getMeasurementId("CageCleanDate");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
									protocolId, measurementId, newanimalid, sdf.format(eventDate), 0));
						}

						// Set the weandate based on the oldanimaldb "Wean" event
						if (eventType.equals("Wean")) {
							int protocolId = ct.getProtocolId("SetWeanDate");
							int measurementId = ct.getMeasurementId("Weandate");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
									protocolId, measurementId, newanimalid, sdf.format(eventDate), 0));
						}

						// Set the Remark application based on the oldanimaldb "Remark" event
						if (eventType.equals("Remark")) {
							int protocolId = ct.getProtocolId("SetRemark");
							int measurementId = ct.getMeasurementId("Remark");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
									protocolId, measurementId, newanimalid, eventDetails, 0));
						}

						// set the OldAnimalDBExperimentalManipulationRemark based on oldanimaldb Experimental manipulation events.
						if (eventType.equals("ExperimentalManipulation")) {
							int protocolId = ct.getProtocolId("SetOldAnimalDBExperimentalManipulationRemark");
							int measurementId = ct.getMeasurementId("OldAnimalDBExperimentalManipulationRemark");
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, eventDate, null, 
									protocolId, measurementId, newanimalid, eventDetails, 0));
						}

						// Set the location and moved events based on the
						// oldanimaldb "Move" event

						// Set the

						// eventid: ignore for now
						// details: ignore for now
					}
				}
				
				// Add everything to DB
				db.add(valuesToAddList);
			}
		});
	}

}
