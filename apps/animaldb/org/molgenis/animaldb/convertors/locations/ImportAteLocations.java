package org.molgenis.animaldb.convertors.locations;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;


public class ImportAteLocations
{
	private Database db;
	private CommonService ct;
	private Logger logger;
	private List<String> seenLocs = new ArrayList<String>();
	private int userId;

	public ImportAteLocations(Database db, Login login) throws Exception
	{
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		logger = Logger.getLogger("ImportAteLocations");
		userId = login.getUserId();
	}
	
	public void doImport(String filename) throws Exception
	{
		final int investigationId = ct.getOwnUserInvestigationIds(userId).get(0);
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				int buildingId = -1;
				// gebouw nr
				String buildingNr = tuple.getString("gebouw nr");
				if (!seenLocs.contains(buildingNr)) {
					seenLocs.add(buildingNr);
					Location newBuilding = new Location();
					newBuilding.setName(buildingNr);
					newBuilding.setInvestigation(investigationId);
					db.add(newBuilding);
					buildingId = newBuilding.getId();
				} else {
					buildingId = db.query(Location.class).eq(Location.NAME, buildingNr).
							find().get(0).getId();
				}
				// etage
				String floor = tuple.getString("etage");
				// kamer nr
				String room = tuple.getString("kamer nr");
				// omschrijving -> skip
				
				// Make location and link to building
				Location newLoc = new Location();
				newLoc.setName(floor + "." + room);
				newLoc.setInvestigation(investigationId);
				db.add(newLoc);
				int protocolId = ct.getProtocolId("SetSublocationOf");
				int measurementId = ct.getMeasurementId("Location");
				db.add(ct.createObservedValueWithProtocolApplication(investigationId, new Date(), null, 
						protocolId, measurementId, newLoc.getId(), null, buildingId));
			}
		});
	}
	
}
