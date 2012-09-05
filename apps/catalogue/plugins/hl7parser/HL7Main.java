/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import org.molgenis.framework.db.Database;

import app.DatabaseFactory;

/**
 * 
 * @author roankanninga
 */
public class HL7Main {
	public static void main(String[] args) throws Exception {
		PropertiesPath path = new PropertiesPath();
		String file1 = "/Users/pc_iverson/Desktop/Input/HL7Files/Catalog-EX04.xml";
		// String file1 = "/Users/Roan/Work/HL7/Catalog-EX04.xml";
		String file2 = "/Users/pc_iverson/Desktop/Input/HL7Files/Catalog-EX04-valuesets.xml";
		// String file2 = path.getPath()+"Catalog-EX04-valuesets.xml";
		// String file2 = "/Users/Roan/Work/HL7/Catalog-EX04-valuesets.xml";

		// Read file, fill arraylists

		HL7Data ll = new HL7LLData(file1, file2);

		Database db = DatabaseFactory.create();

		HL7PhenoImporter importer = new HL7PhenoImporter();

		importer.start(ll, db);
	}

}
