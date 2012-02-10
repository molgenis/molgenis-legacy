package org.molgenis.load.cmdline;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.framework.db.Database;

import app.CsvImport;
import app.DatabaseFactory;

public class LoadFromDirectory
{
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		
		File directory = new File("/tmp/");
		
		Database db = DatabaseFactory.create();
		
		CsvImport.importAll(directory, db, null);
		
		System.out.println("upload complete");
	}
}
