package mydas.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.xgap.Gene;

import app.JDBCDatabase;

public class exampleGetFromDb {

	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException {
		
		JDBCDatabase db = new JDBCDatabase("xgap.properties");
		
		List<Gene> genes = db.find(Gene.class);
		//TODO: Danny: Use or loose (perhaps the entire main ??)
		/*Gene g = */genes.get(0);
		//System.out.println(g.);

	}

}
