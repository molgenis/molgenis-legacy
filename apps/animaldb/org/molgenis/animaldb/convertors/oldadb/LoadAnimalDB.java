package org.molgenis.animaldb.convertors.oldadb;

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
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;


/**
 * Class to load data from Ate's old AnimalDB into the new version with the Pheno model.
 * 
 * @author erikroos
 *
 */
public class LoadAnimalDB
{
	private Database db;
	private CommonService ct;
	private String invName;
	private String userName;
	
	public LoadAnimalDB(Database db, Login login) throws Exception
	{
		this.db = db;
		userName = login.getUserName();
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		
		// If needed, make investigation
		invName = "FDD";
		if (ct.getInvestigationId(invName) == -1) {
			Investigation newInv = new Investigation();
			newInv.setName(invName);
			newInv.setOwns_Name(login.getUserName());
			newInv.setCanRead_Name("admin");
			db.add(newInv);
		}
		
		// Add some measurements that we'll need
		ct.makeMeasurement(invName, "OldAnimalDBAnimalID", "String", null, null, false, "string", "To set an animal's ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBAnimalCustomID", "String", null, null, false, "string", "To set an animal's Custom ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBLocationID", "String", null, null, false, "string", "To set a location's ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBLitterID", "String", null, null, false, "string", "To link an animal to a litter with this ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBExperimentID", "String", null, null, false, "string", "To set an experiment's ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBDecApplicationID", "String", null, null, false, "string", "To link an experiment to a DEC application with this ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBBroughtinDate", "Datetime", null, null, true, "datetime", "To set a target's date of arrival in the system/ on the location in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBExperimentalManipulationRemark", "String", null, null, false, "string", "To store Experiment remarks about the animal, from the Experimental manipulation event, from the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBPresetID", "String", null, null, false, "string", "To link a targetgroup to a preset this ID in the old version of AnimalDB.", userName);
		ct.makeMeasurement(invName, "OldAnimalDBRemarks", "String", null, null, false, "string", "To store remarks about the animal in the animal table, from the old version of AnimalDB.", userName);
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
		populateAnimal(path + "oldanimals.csv");
		populateLocation(path + "oldlocations.csv");
		populateLitter(path + "oldlitters.csv");
		populateExperiment(path + "oldexperiments.csv");
		populateDECApplication(path + "olddecapplications.csv");
		populateAnimalsInExperiments(path + "oldexperimentanimals.csv");
		populatePreset(path + "oldpresets.csv");
		populatePresetAnimals(path + "oldpresetanimals.csv");
		populateEvents(path + "oldevents.csv");
	}

	public void populateAnimal(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = new Date();

				String oldanimalid = tuple.getString("animalid");
				String oldanimalcustomid = tuple.getString("customid");
				String name = null;
				if (oldanimalcustomid != null) {
					name = oldanimalcustomid;
				} else {
					name = oldanimalid;
				}
				String weandate = tuple.getString("weandate");
				if (weandate.equals("NULL")) weandate = null;
				// TODO: parse weandate?
				//String status = tuple.getString("status");
				String oldsex = tuple.getString("sex");
				int oldspecies = tuple.getInt("species");
				String background = tuple.getString("background");
				int source = tuple.getInt("source");
				//String partgroup = tuple.getString("participantgroup");
				String remarks = tuple.getString("remarks");
				String oldlocid = tuple.getString("location");
				String oldlitterid = tuple.getString("litter");

				ObservationTarget newAnimal = ct.createIndividual(invName, name, userName);
				db.add(newAnimal);
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				// 'Active' is set when applying events from old AnimalDB
				// AnimalType
				String animalType = "A. Gewoon dier"; // safe assumption that this holds for all animals in OldADB
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetAnimalType", "AnimalType", name, animalType, null));
				// WeanDate
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetWeanDate", "WeanDate", name, weandate, null));
				// OldAnimalDBID
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBAnimalID", "OldAnimalDBAnimalID", name, oldanimalid, null));
				// OldAnimalDB customID
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBAnimalCustomID", "OldAnimalDBAnimalCustomID", name, oldanimalcustomid, null));
				// Sex
				String sexName = null;
				if (oldsex.equals("M")) {
					sexName = "Male";
				}
				if (oldsex.equals("F")) {
					sexName = "Female";
				}
				if (oldsex.equals("U")) {
					sexName = "UnknownSex";
				}
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSex", "Sex", name, null, sexName));
				// Species
				String speciesName = null;
				if (oldspecies == 1) {
					speciesName = "Syrian hamster";
				}
				if (oldspecies == 2) {
					speciesName = "European groundsquirrel";
				}
				if (oldspecies == 3) {
					speciesName = "House mouse";
				}
				if (oldspecies == 4) {
					speciesName = "Siberian hamster";
				}
				if (oldspecies == 5) {
					speciesName = "Gray mouse lemur";
				}
				if (oldspecies == 6) {
					speciesName = "Brown rat";
				}
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSpecies", "Species", name, null, speciesName));
				// Background
				String backgroundName = null;
				if (!background.equals("NULL")) {
					if (background.equals("1")) {
						backgroundName = "CD1";
					}
					if (background.equals("2")) {
						backgroundName = "C57BL/6J";
					}
				}
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetBackground", "Background", name, null, backgroundName));
				// Litter
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBLitterID", "OldAnimalDBLitterID", name, oldlitterid, null));
				// Source
				String newsourceName = null;
				if (source == 1) {
					newsourceName = "Harlan";
				}
				if (source == 2) {
					newsourceName = "Kweek chronobiologie";
				}
				if (source == 3) {
					newsourceName = "Kweek gedragsbiologie";
				}
				if (source == 4) {
					newsourceName = "Kweek dierfysiologie";
				}
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSource", "Source", name, null, newsourceName));
				// Location
				if (!oldlocid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldlocid));
					List<ObservedValue> valueList = q.find();
					for (ObservedValue locValue : valueList) {
						// Check that the target is a Location, since there are also Animals with OldAnimalDB ID's
						String newlocName = locValue.getTarget_Name();
						try {
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
									now, null, "SetLocation", "Location", name, null, newlocName));
							break;
						} catch (Exception le) {
							// ignore
						}
					}
				}
				// Participantgroup
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetParticipantGroup", "ParticipantGroup", name, "Chrono- en gedragsbiologie", null));
				// OldAnimalDBRemarks
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBRemarks", "OldAnimalDBRemarks", name, remarks, null));
				// ResponsibleResearcher
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetResponsibleResearcher", "ResponsibleResearcher", name, "Ate Boerema", null));
				
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
				Date now = new Date();

				int oldlocationid = tuple.getInt("locationid");
				String name = tuple.getString("name");
				String inloc = tuple.getString("inlocation");

				// Make location and set OldAnimalDBLocationID
				ct.makeLocation(invName, name, userName);
				db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBLocationID", "OldAnimalDBLocationID", name, Integer.toString(oldlocationid), null));
				// SetSublocationOf
				if (!inloc.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBLocationID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, inloc));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					String newLocationName = tmpValue.getTarget_Name();
					db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
							"SetSublocationOf", "Location", name, null, newLocationName));
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
				Date now = new Date();
				SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				// Name
				String name = tuple.getString("customlitterid");
				ct.makePanel(invName, name, userName);
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", name, "Litter", null));
				// pairstartdate -> time
				String pairStartDateString = tuple.getString("pairstartdate");
				Date pairStartDate = null;
				if (!pairStartDateString.equals("NULL")) {
					pairStartDate = dbFormat.parse(pairStartDateString);
				}
				// pairenddate -> endtime
				String pairEndDateString = tuple.getString("pairenddate");
				Date pairEndDate = null;
				if (!pairEndDateString.equals("NULL")) {
					pairEndDate = dbFormat.parse(pairEndDateString);
				}
				// Parentgroup
				String pgName = "Parentgroup" + name;
				ct.makePanel(invName, pgName, userName);
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", pgName, "Parentgroup", null));
				// Link litter to parentgroup
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, pairStartDate, 
						pairEndDate, "SetParentgroup", "Parentgroup", name, null, pgName));
				// mother -> via Parentgroup
				String motherName = tuple.getString("mother");
				if (!motherName.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, motherName));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					String newmotherName = tmpValue.getTarget_Name();
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
							pairStartDate, pairEndDate, "SetParentgroupMother", "ParentgroupMother", pgName, null, 
							newmotherName));
				}
				// father -> via Parentgroup
				String fatherName = tuple.getString("father");
				if (!fatherName.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, fatherName));
					List<ObservedValue> valueList = q.find();
					ObservedValue tmpValue = valueList.get(0);
					String newfatherName = tmpValue.getTarget_Name();
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
							pairStartDate, pairEndDate, "SetParentgroupFather", "ParentgroupFather", pgName, null, 
							newfatherName));
				}
				// birthsize -> Size
				Integer birthsize = tuple.getInt("birthsize");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetSize", "Size", name, birthsize.toString(), null));
				// birthday -> DateOfBirth
				String birthDayString = tuple.getString("birthday");
				Date birthDayDate = null;
				if (birthDayString.equals("NULL")) {
					birthDayString = null;
				} else {
					//change date formatting from mysql to molgenis style
					birthDayDate = dbFormat.parse(birthDayString);
					birthDayString = newDateOnlyFormat.format(birthDayDate);
				}
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetDateOfBirth", "DateOfBirth", name, birthDayString, null));
				// numberweaned -> WeanSize
				String weanSize = tuple.getString("numberweaned");
				if (!weanSize.equals("NULL")) {
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
							"SetWeanSize", "WeanSize", name, weanSize, null));
				}
				// remarks -> OldAnimalDBRemarks
				String remarks = tuple.getString("remarks");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBRemarks", "OldAnimalDBRemarks", name, remarks, null));
				// Link animals to litters
				String oldlitterid = tuple.getString("litterid");
				if (!oldlitterid.equals("NULL"))
				{
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBLitterID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldlitterid));
					List<ObservedValue> valueList = q.find();
					for (ObservedValue litterValue : valueList) {
						String animalName = litterValue.getTarget_Name();
						valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, 
								null, "SetLitter", "Litter", animalName, null, name));
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
				Date now = new Date();

				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Name
				String name = tuple.getString("title");
				ct.makePanel(invName, name, userName);
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", name, "Experiment", null));

				// Make protocol application to use with all the values
				ProtocolApplication app = ct.createProtocolApplication(invName, "SetDecSubprojectSpecs");
				db.add(app);
				String paName = app.getName();
				
				// experimentid
				String oldexperimentid = tuple.getString("experimentid");
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "OldAnimalDBExperimentID", name, 
						oldexperimentid, null));
				// decapplication
				String decapp = tuple.getString("decapplication");
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "OldAnimalDBDecApplicationID", name, 
						decapp, null));
				// experimentnr
				String experimentnr = tuple.getString("experimentnr");
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "ExperimentNr", name, 
						experimentnr, null));
				// pdfdecsubprojectapplication
				String pdfdec = tuple.getString("pdfdecsubprojectapplication");
				if (!pdfdec.equals("NULL")) {
					valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "DecSubprojectApplicationPdf", name, 
							pdfdec, null));
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
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "Concern", name, 
						codeValue, null));
				// goal (lookup)
				int goal = tuple.getInt("goal");
				if (goal == 37) {
					codeValue = "E. Wetensch.vraag m.b.t.: and. wetenschappelijke vraag";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "Goal", name, 
						codeValue, null));
				// specialtechn (lookup)
				int specialtechn = tuple.getInt("specialtechn");
				if (specialtechn == 1) {
					codeValue = "A. Geen van deze technieken/ingrepen";
				}
				if (specialtechn == 2) {
					codeValue = "B. Doden zonder voorafgaande handelingen";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "SpecialTechn", name, 
						codeValue, null));
				// lawprovisions (lookup)
				int lawprovisions = tuple.getInt("lawprovisions");
				if (lawprovisions == 1) {
					codeValue = "A. Geen wettelijke bepaling";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "LawDef", name, 
						codeValue, null));
				// toxres (lookup)
				int toxres = tuple.getInt("toxres");
				if (toxres == 1) {
					codeValue = "A. Geen toxicologisch onderzoek";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "ToxRes", name, 
						codeValue, null));
				// anaesthesia (lookup)
				int anaesthesia = tuple.getInt("anaesthesia");
				if (anaesthesia == 1) {
					codeValue = "A. Is niet toegepast (geen aanleiding)";
				}
				if (anaesthesia == 4) {
					codeValue = "D. Is wel toegepast";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "Anaesthesia", name, 
						codeValue, null));
				// painmanagement (lookup)
				int painmanagement = tuple.getInt("painmanagement");
				if (painmanagement == 1) {
					codeValue = "A. Is niet toegepast (geen aanleiding)";
				}
				if (painmanagement == 4) {
					codeValue = "D. Is wel toegepast";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "PainManagement", name, 
						codeValue, null));
				// animalendstatus (lookup)
				int animalendstatus = tuple.getInt("animalendstatus");
				if (animalendstatus == 1) {
					codeValue = "A. Dood in het kader van de proef";
				}
				if (animalendstatus == 3) {
					codeValue = "C. Na einde proef in leven gelaten";
				}
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "AnimalEndStatus", name, 
						codeValue, null));
				// remarks
				String remarks = tuple.getString("remarks");
				valuesToAddList.add(ct.createObservedValue(invName, paName, now, null, "OldAnimalDBRemarks", name, 
						remarks, null));
			
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
				Date now = new Date();

				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// Name
				String name = tuple.getString("projecttitle");
				String decName = "DEC project: " + name;
				ct.makePanel(invName, decName, userName);
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", decName, "DecApplication", null));

				// Make protocol application to use with all the values
				ProtocolApplication app = ct.createProtocolApplication(invName, "SetDecProjectSpecs");
				db.add(app);
				String paName = app.getName();
				
				SimpleDateFormat csvFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
				// applicationstartdate -> time
				String appStartDateString = tuple.getString("applicationstartdate");
				String appStartDateStringMolgenis = "";
				Date appStartDate = null;
				if (!appStartDateString.equals("NULL")) {
					appStartDate = csvFormat.parse(appStartDateString);
					appStartDateStringMolgenis = dateOnlyFormat.format(appStartDate);
				}
				// applicationenddate -> endtime
				String appEndDateString = tuple.getString("applicationenddate");
				String appEndDateStringMolgenis = "";
				Date appEndDate = null;
				if (!appEndDateString.equals("NULL")) {
					appEndDate = csvFormat.parse(appEndDateString);
					appEndDateStringMolgenis = dateOnlyFormat.format(appEndDate);
				}
				// StartDate
				valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
						"StartDate", decName, appStartDateStringMolgenis, null));
				// EndDate
				valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
						"EndDate", decName, appEndDateStringMolgenis, null));
				// Link experiments to DEC applications
				String olddecappid = tuple.getString("decapplicationid");
				if (!olddecappid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBDecApplicationID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, olddecappid));
					List<ObservedValue> valueList = q.find();
					for (ObservedValue expValue : valueList) {
						String experimentName = expValue.getTarget_Name();
						valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
								"DecApplication", experimentName, null, decName));
					}
				}
				// decnr
				String decnr = tuple.getString("decnr");
				valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
						"DecNr", decName, decnr, null));
				// decapplicant
				String decapplicant = tuple.getString("decapplicant");
				valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
						"DecApplicantId", decName, decapplicant, null));
				// pdfdecapplication
				String pdfdec = tuple.getString("pdfdecapplication");
				if (!pdfdec.equals("NULL")) {
					valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
							"DecApplicationPdf", decName, pdfdec, null));
				}
				// pdfdecapproval
				pdfdec = tuple.getString("pdfdecapproval");
				if (!pdfdec.equals("NULL")) {
					valuesToAddList.add(ct.createObservedValue(invName, paName, appStartDate, appEndDate, 
							"DecApprovalPdf", decName, pdfdec, null));
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
				// experimentid
				String newexpName = null;
				String oldexpid = tuple.getString("experimentid");
				if (!oldexpid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBExperimentID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldexpid));
					List<ObservedValue> valueList = q.find();
					newexpName = valueList.get(0).getTarget_Name();
				}
				// animalid
				String newanimalName = null;
				String oldanimalid = tuple.getString("animalid");
				if (!oldanimalid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					newanimalName = valueList.get(0).getTarget_Name();
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
				ProtocolApplication app = ct.createProtocolApplication(invName, "AnimalInSubproject");
				db.add(app);
				String protappName = app.getName();
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "Experiment", 
						newanimalName, null, newexpName));
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "SourceTypeSubproject",
						newanimalName, source, null));
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "PainManagement",
						newanimalName, painmanagement, null));
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "Anaesthesia",
						newanimalName, anaesthesia, null));
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "ExpectedDiscomfort",
						newanimalName, expdiscomfortlevel, null));
				valuesToAddList.add(ct.createObservedValue(invName, protappName, entryDate, exitDate, "ExpectedAnimalEndStatus",
						newanimalName, expexperimentendstatus, null));

				// If applicable, apply protocol 'from subproject'
				if (exitDate != null) {
					app = ct.createProtocolApplication(invName, "AnimalFromSubproject");
					db.add(app);
					protappName = app.getName();
					valuesToAddList.add(ct.createObservedValue(invName, protappName, exitDate, null, "FromExperiment", 
							newanimalName, null, newexpName));
					valuesToAddList.add(ct.createObservedValue(invName, protappName, exitDate, null, "ActualDiscomfort", 
							newanimalName, actdiscomfortlevel, null));
					valuesToAddList.add(ct.createObservedValue(invName, protappName, exitDate, null, "ActualAnimalEndStatus", 
							newanimalName, actexperimentendstatus, null));
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
				Date now = new Date();
				// Name
				String name = tuple.getString("name");
				// add the group in the newAnimaldb
				ct.makePanel(invName, name, userName);
				db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetTypeOfGroup", "TypeOfGroup", name, "Selection", null));
				// link the OldAnimalDBPResetID to the newly created group
				String oldpresetid = tuple.getString("presetid");
				db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, 
						"SetOldAnimalDBPresetID", "OldAnimalDBPresetID", name, oldpresetid, null));
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
				Date now = new Date();

				// oldanimalid --> new animalid
				String newanimalName = null;
				String oldanimalid = tuple.getString("animalid");
				if (!oldanimalid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					newanimalName = valueList.get(0).getTarget_Name();
				}
				// oldpresetid --> group id
				String newgroupName = null;
				String oldpresetid = tuple.getString("presetid");
				if (!oldpresetid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBPresetID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldpresetid));
					List<ObservedValue> valueList = q.find();
					newgroupName = valueList.get(0).getTarget_Name();
				}
				// add animal to selection group
				db.add(ct.addObservationTargetToPanel(invName, newanimalName, now, newgroupName));
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
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();

				// oldanimalid --> new animalid
				String newanimalName = null;
				String oldanimalid = tuple.getString("animal");
				if (!oldanimalid.equals("NULL")) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldAnimalDBAnimalID"));
					q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, oldanimalid));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() > 0) {
						newanimalName = valueList.get(0).getTarget_Name();
					}
				}

				if (newanimalName != null) {
					// date
					SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					SimpleDateFormat newDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					String dateString = tuple.getString("date");
					Date eventDate = null;
					if (!dateString.equals("NULL")) {
						// only parse events that have a valid date!
						eventDate = dbFormat.parse(dateString);
						// get the type
						String eventType = tuple.getString("type");
						String eventDetails = tuple.getString("details");
						// Set the status and add the events
						// NB: the csv file must be Ascending data sorted,
						// otherwise errors can occur!
						if (eventType.equals("Born") || eventType.equals("BroughtIn")) {
							// Check if there is already an Active value:
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, newanimalName));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, "Active"));
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
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
										eventDate, null, "SetActive", "Active", newanimalName, "Alive", null));
							}
							if (eventType.equals("Born")) {
								// Set the date of birth based on the oldanimaldb "Born" event
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
										eventDate, null, "SetDateOfBirth", "DateOfBirth", newanimalName, 
										newDateOnlyFormat.format(eventDate), null));
							} else {
								// Set the broughtinevent based on the oldanimaldb "Broughtin" event
								valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, 
										eventDate, null, "SetOldAnimalDBBroughtinDate", "OldAnimalDBBroughtinDate", newanimalName, 
										newDateOnlyFormat.format(eventDate), null));
							}
						}
						if (eventType.equals("Died")) {
							// Set the date of death based on the oldanimaldb "Died" event
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, 
									"SetDeathDate", "DeathDate", newanimalName, newDateOnlyFormat.format(eventDate), null));
							// Report as dead/removed by setting the endtime of the most recent Active value
							Query<ObservedValue> activeQuery = db.query(ObservedValue.class);
							activeQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, newanimalName));
							activeQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active"));
							activeQuery.sortDESC(ObservedValue.TIME);
							List<ObservedValue> activeValueList = activeQuery.find();
							if (activeValueList.size() > 0) {
								ObservedValue activeValue = activeValueList.get(0);
								activeValue.setEndtime(eventDate);
								activeValue.setValue("Dead");
								db.update(activeValue);
							}
							// If still in DEC subproject at time of death, end that
							Query<ObservedValue> q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, newanimalName));
							q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
							q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, eventDate));
							q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
							List<ObservedValue> valueList = q.find();
							if (valueList.size() == 1) // Safe assumption: animal can only be in one experiment at a time
							{
								ObservedValue value = valueList.get(0);
								// set end date-time
								value.setEndtime(eventDate);
								db.update(value);
								// Maybe TODO: get end status values from somewhere and apply them?
							}
						}
						// Set the the cagecleaningevents based on the oldanimaldb "Cleaned" event
						if (eventType.equals("Cleaned")) {
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, 
									"SetCageCleanDate", "CageCleanDate", newanimalName, newDateTimeFormat.format(eventDate), null));
						}
						// Set the weandate based on the oldanimaldb "Wean" event
						if (eventType.equals("Wean")) {
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, 
									"SetWeanDate", "Weandate", newanimalName, newDateOnlyFormat.format(eventDate), null));
						}
						// Set the Remark application based on the oldanimaldb "Remark" event
						if (eventType.equals("Remark")) {
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, 
									"SetRemark", "Remark", newanimalName, eventDetails, null));
						}
						// set the OldAnimalDBExperimentalManipulationRemark based on oldanimaldb Experimental manipulation events.
						if (eventType.equals("ExperimentalManipulation")) {
							valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, 
									"SetOldAnimalDBExperimentalManipulationRemark", "OldAnimalDBExperimentalManipulationRemark", 
									newanimalName, eventDetails, null));
						}

						// Set the location and moved events based on theoldanimaldb "Move" event
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
