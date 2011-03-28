package plugins.cluster.helper;

import plugins.cluster.implementations.LocalComputationResource;
import app.JDBCDatabase;
import filehandling.generic.MolgenisFileHandler;

public class Tester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		JDBCDatabase db = new JDBCDatabase("xgap.properties");
		MolgenisFileHandler xlfh = new MolgenisFileHandler(db);
		LocalComputationResource lc = new LocalComputationResource(xlfh);

		String out = lc.executeCommand(new Command("ls"));
		
		System.out.println("out: " + out);
	}

}
