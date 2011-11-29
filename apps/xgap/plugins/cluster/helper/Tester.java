package plugins.cluster.helper;

import org.molgenis.framework.db.Database;

import plugins.cluster.implementations.LocalComputationResource;
import app.DatabaseFactory;

public class Tester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Database db = DatabaseFactory.create("xgap.properties");
		LocalComputationResource lc = new LocalComputationResource();

		String out = lc.executeCommand(new Command("ls"));
		
	}

}
