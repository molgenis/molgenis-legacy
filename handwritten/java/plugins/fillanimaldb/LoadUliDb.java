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

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
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
		final int speciesId = ct.getObservationTargetId("House mouse");
		final SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy H:mm:ss", Locale.US);
		final SimpleDateFormat sdfMolgenis = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
		final Calendar calendar = Calendar.getInstance();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				Date now = calendar.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Tiernummer -> make new animal + CustomId
				String oldAnimalId = tuple.getString("Tiernummer");
				ObservationTarget newAnimal;
				if (ct.getObservationTargetId("animal" + oldAnimalId) == -1) { // check if one with this name already exists
					newAnimal = ct.createIndividual(invid, "animal" + oldAnimalId, login.getUserId());
				} else {
					newAnimal = ct.createIndividual(invid, "animal" + oldAnimalId + "_dup", login.getUserId());
				}
				db.add(newAnimal);
				int newAnimalId = newAnimal.getId();
				int protocolId = ct.getProtocolId("SetCustomId");
				int measurementId = ct.getMeasurementId("CustomId");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newAnimalId, oldAnimalId, 0));
				
				// laufende Nr -> OldUliDbId
				protocolId = ct.getProtocolId("SetOldUliDbId");
				measurementId = ct.getMeasurementId("OldUliDbId");
				String oldUliDbId = tuple.getString("laufende Nr");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newAnimalId, oldUliDbId, 0));
				
				// Tierkategorie -> Species (always Mus musculus)
				protocolId = ct.getProtocolId("SetSpecies");
				measurementId = ct.getMeasurementId("Species");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
						protocolId, measurementId, newAnimalId, null, speciesId));
				
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
				if (state.equals("lebt")) {
					state = "Alive";
				} else {
					state = "Dead";
				}
				protocolId = ct.getProtocolId("SetActive");
				measurementId = ct.getMeasurementId("Active");
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, startDate, endDate, 
						protocolId, measurementId, newAnimalId, state, 0));
				
				// TODO ...
				
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
