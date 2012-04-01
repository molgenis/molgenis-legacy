package org.molgenis.animaldb.convertors.locations;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;
import org.molgenis.pheno.Location;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

public class ImportAteLocations
{
	private Database db;
	private CommonService ct;
	private List<String> seenLocs = new ArrayList<String>();
	private String userName;

	public ImportAteLocations(Database db, Login login) throws Exception
	{
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		userName = login.getUserName();
	}

	public void doImport(String filename) throws Exception
	{
		final String investigationName = ct.getOwnUserInvestigationNames(
				userName).get(0);

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (Tuple tuple : reader)
		{
			// gebouw nr -> name of building
			String buildingName = tuple.getString("gebouw nr");
			if (!seenLocs.contains(buildingName))
			{
				seenLocs.add(buildingName);
				Location newBuilding = new Location();
				newBuilding.setName(buildingName);
				newBuilding.setInvestigation_Name(investigationName);
				db.add(newBuilding);
			}
			// etage
			String floor = tuple.getString("etage");
			// kamer nr
			String room = tuple.getString("kamer nr");
			// omschrijving -> skip

			// Make location and link to building
			Location newLoc = new Location();
			newLoc.setName(floor + "." + room);
			newLoc.setInvestigation_Name(investigationName);
			db.add(newLoc);
			db.add(ct.createObservedValueWithProtocolApplication(
					investigationName, new Date(), null, "SetSublocationOf",
					"Location", newLoc.getName(), null, buildingName));
		}
	}

}
