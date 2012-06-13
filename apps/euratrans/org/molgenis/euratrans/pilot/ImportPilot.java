package org.molgenis.euratrans.pilot;

import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.CsvImport;
import app.DatabaseFactory;

public class ImportPilot
{
	public static void main(String[] args) throws Exception
	{
		Database db = DatabaseFactory.create();
		
		//clean db
		new Molgenis("apps/euratrans/org/molgenis/euratrans/euratrans.properties").updateDb(true);
		
		//default the study
		Investigation s = new Investigation();
		s.setName("EURATRANS");
		db.add(s);
		
		
		//run csv importer
		CsvImport importer = new CsvImport();
		Tuple defaults = new SimpleTuple();
		defaults.set(ObservationTarget.INVESTIGATION_NAME, "EURATRANS");
		importer.importAll(new File(ConvertPilot.target), db, defaults);
		
		
	}

	
}
