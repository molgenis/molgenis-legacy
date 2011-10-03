package loaders;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.CsvImport;
import app.DatabaseFactory;
import app.JDBCDatabase;

public class LoadMpdFromConvertor {
	public static void main(String[] args) throws Exception {
		
		Database db = DatabaseFactory.create("handwritten/apps/org/molgenis/pheno/pheno.properties");
		
		Tuple defaults = new SimpleTuple();
		
		//okay, this is wrong because multiple investigations!
		//defaults.set("investigation_name","mouse phenome database");
		
		//alternatively load the defaults from a file
		//Properties p = new Properties();
		//p.load(new FileInputStream(new File("default.properties"));
		//Tuple defaults = new PropertiesTuple(p);
		
		// Tuple t = new PropertiesTuple(new Properties());
		
		//empty the database
		//MolgenisUpdateDatabase.main(null);
		
		// full import
		CsvImport.importAll(new File("../molgenis4phenotype/data/MPD/output"), db, defaults, null, DatabaseAction.ADD_IGNORE_EXISTING, "");
		//CsvImport.importAll(new File("data/Europhenome"), db, defaults, null, DatabaseAction.ADD_IGNORE_EXISTING, "MISSINGVALUE?");
	}
}
