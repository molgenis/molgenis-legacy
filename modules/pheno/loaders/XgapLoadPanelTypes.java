package loaders;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

import app.DatabaseFactory;

public class XgapLoadPanelTypes
{

	public XgapLoadPanelTypes(Database db) throws DatabaseException, IOException
	{

		String[] types = new String[]
		{ "f2", "bc", "riself", "risib", "4way", "dh", "special", "natural", "parental", "f1", "rcc", "css", "unknown",
				"other" };

		for (String type : types)
		{
			OntologyTerm ot = new OntologyTerm();
			ot.setName("xgap_rqtl_straintype_" + type);
			ot.setDefinition(type);
			db.add(ot);
		}

	}

	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException
	{
		Database db = DatabaseFactory.create("handwritten/properties/gcc.properties");
		new XgapLoadPanelTypes(db);
		db.close();
	}

}
