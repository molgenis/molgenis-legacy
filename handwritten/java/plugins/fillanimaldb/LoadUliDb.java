package plugins.fillanimaldb;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
	int invid;

	public LoadUliDb(Database db, Login login) throws Exception
	{
		this.db = (JDBCDatabase) db;
		this.login = login;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		
		invid = ct.getInvestigationId("AnimalDB");
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

				ObservationTarget newAnimal = ct.createIndividual(invid, "animal" + oldanimalid, login.getUserId());
				db.add(newAnimal);
				int newanimalid = newAnimal.getId();

				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// 'Active'
				
				// AnimalType
				int protocolId = ct.getProtocolId("SetAnimalType");
				int measurementId = ct.getMeasurementId("AnimalType");
				String animalType = "A. Gewoon dier"; // safe assumption that this holds for all animals in OldADB
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newanimalid, animalType, 0));

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
	
	public void parseParentRelations(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
			}
		});
	}
	
	public void populateLine(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
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
