package filehandling.generic;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;

import app.DatabaseFactory;
import decorators.MolgenisFileHandler;

public class ExampleUsage
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		//create database instance
		Database db = DatabaseFactory.create("clusterdemo.properties");
		
		//create MolgenisFileHandler
		MolgenisFileHandler mfh = new MolgenisFileHandler(db);
	
		//query a MolgenisFile, or a subtype thereof
		MolgenisFile myMolgenisFile = db.find(MolgenisFile.class).get(0);
		
		//perform actions with this MolgenisFile through the handler
		/*File myRealFile = */mfh.getFile(myMolgenisFile);

		
	}

}
