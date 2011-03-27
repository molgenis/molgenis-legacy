package plugins.cluster.helper;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.molgenis.framework.db.DatabaseException;

import filehandling.generic.MolgenisFileHandler;
import plugins.cluster.implementations.LocalComputationResource;
import app.JDBCDatabase;

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
