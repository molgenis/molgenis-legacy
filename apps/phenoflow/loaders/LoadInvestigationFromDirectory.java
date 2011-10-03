package loaders;

import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.SimpleTuple;

import app.CsvImport;
import app.DatabaseFactory;
import app.JDBCDatabase;

public class LoadInvestigationFromDirectory {
	public static void main(String[] args) throws Exception {
		
		new Molgenis("apps/phenoflow/phenoflow.properties").updateDb(true);

		Database db = DatabaseFactory.create("apps/phenoflow/phenoflow.properties");
		String directory;
		
		// europhenome
		//directory = "../phenoflow_data/Europhenome2";
		
		//CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
		//		DatabaseAction.ADD, "N/A");

		// MPD
		directory = "../phenoflow_data/MPD";
		CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
				DatabaseAction.ADD, "N/A");
		directory = "../phenoflow_data/MPD/temp";
		CsvImport.importAll(new File(directory), db, new SimpleTuple(), null,
				DatabaseAction.ADD, "N/A");

		System.out.println("Done");
	}
}
