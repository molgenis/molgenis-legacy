package mydas.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.xgap.Gene;

import app.DatabaseFactory;

public class exampleGetFromDb {

	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException {
		
		Database db = DatabaseFactory.create("xgap.properties");
		
		List<Gene> genes = db.find(Gene.class);
		//TODO: Danny: Use or loose (perhaps the entire main ??)
		/*Gene g = */genes.get(0);

	}

}
