package loaders;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.util.SimpleTuple;

import app.CsvImport;
import app.JDBCDatabase;

public class LoadInvestigationFromDirectory
{
	public static void main(String[] args) throws Exception
	{
		String directory = "D:/Data/dbgap/phs000006";

		Database db = new JDBCDatabase("molgenis.properties");
		CsvImport.importAll(new File(directory), db, new SimpleTuple());
	}
}
