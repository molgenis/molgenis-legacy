package org.molgenis.xgap.xqtlworkbench;
import app.DatabaseFactory;
import org.molgenis.Molgenis;

import app.FillMetadata;
import org.molgenis.framework.db.Database;

public class XqtlUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		
		new Molgenis("org/molgenis/xgap/xqtlworkbench/xqtl.properties").updateDb(false);
		//ResetXgapDb.reset(new JDBCDatabase("org/molgenis/xgap/xqtlworkbench/xqtl.properties"), true);
		Database db = DatabaseFactory.create("org/molgenis/xgap/xqtlworkbench/xqtl.properties");
                FillMetadata.fillMetadata(db);
		
		// FOR SOME UNEXPLAINABLE REASON, THIS CLASS DOESN'T WORK AT ALL
		
		/**
		Database db = new MolgenisServlet().getDatabase();
		
		//remove HSQL DB (not really needed) and file storage dir
		XqtlGenerate.deleteDirectory(new File("hsqldb"));
		if(db.getFileSourceHelper().hasFilesource(false)){
			XqtlGenerate.deleteDirectory(db.getFileSourceHelper().getFilesource(false));
		}
		
		//reset DB
		
			String report = ResetXgapDb.reset(db, false);
			//new Molgenis("org/molgenis/xgap/xqtlworkbench/xqtl.properties").updateDb(true);
			System.out.print(report);
	
		
		//reset default storage path and try to add example data
		if(db.getFileSourceHelper().getHasSystemSettingsTable().equals("true")){
			System.out.println("deleting existing file source");
			db.getFileSourceHelper().deleteFilesource();
		}
		db.getFileSourceHelper().setFilesource("./data");
		db.getFileSourceHelper().validateFileSource();
		if(db.getFileSourceHelper().hasValidFileSource())
		{
			System.out.println("default file source valid, now starting data loader");
			ArrayList<String> result = DataLoader.load(db, false);
			for (String s : result)
			{
				System.out.println(s);
			}
		}
		
		*/
		
		
		
	}
}
