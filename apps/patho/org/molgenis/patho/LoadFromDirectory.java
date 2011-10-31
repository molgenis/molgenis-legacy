package org.molgenis.patho;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.framework.db.DatabaseException;

import app.CsvImport;
import app.JDBCDatabase;

public class LoadFromDirectory
{
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();
		
		File directory = new File("/tmp/");
		
		JDBCDatabase db = new JDBCDatabase();
		
		CsvImport.importAll(directory, db, null);
		
		System.out.println("upload complete");
	}
}
