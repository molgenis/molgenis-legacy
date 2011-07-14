package org.molgenis.xgap.xqtlworkbench;
import java.io.File;
import java.util.ArrayList;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;

import regressiontest.cluster.DataLoader;

import app.servlet.MolgenisServlet;

public class XqtlUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		Database db = new MolgenisServlet().getDatabase();
		
		//remove HSQL DB (not really needed) and file storage dir
		XqtlGenerate.deleteDirectory(new File("hsqldb"));
		if(db.getFileSourceHelper().hasValidFileSource()){
			XqtlGenerate.deleteDirectory(db.getFileSourceHelper().getFilesource(false));
		}
		
		//reset DB
		try
		{
			String report = ResetXgapDb.reset(db, false);
			System.out.print(report);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//reset default storage path and try to add example data
		if(db.getFileSourceHelper().getHasSystemSettingsTable().equals("true")){
			db.getFileSourceHelper().deleteFilesource();
		}
		db.getFileSourceHelper().setFilesource("./data");
		db.getFileSourceHelper().validateFileSource();
		if(db.getFileSourceHelper().hasValidFileSource())
		{
			ArrayList<String> result = DataLoader.load(db, false);
			for (String s : result)
			{
				System.out.println(s);
			}
		}
		
	}
}
