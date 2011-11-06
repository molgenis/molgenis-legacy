package loaders;

import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.SimpleTuple;

import app.CsvImport;
import app.DatabaseFactory;
import app.JDBCDatabase;

public class LoadAllEBIData {
	public static void main(String[] args) throws Exception {
		
		new Molgenis("apps/phenoflow/phenoflow.properties").updateDb(true);

		Database db = DatabaseFactory.create("apps/phenoflow/phenoflow.properties");
		String directory;
		
		// Europhenome
		directory = "../pheno_data/Europhenome2";
		
		CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
				DatabaseAction.ADD, "N/A");

		// MPD
		directory = "../pheno_data/MPD";
		CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
				DatabaseAction.ADD, "N/A");
		directory = "../pheno_data/MPD/temp";
		CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
				DatabaseAction.ADD, "N/A");
		
		// Load dbGaP
		LoadDbGapDownloads.loadDbGaPData(db);
		
		System.out.println("Done");
	}
}
