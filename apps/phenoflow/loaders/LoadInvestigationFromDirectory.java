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
		String directory = "../phenoflow_data/Europhenome2";

		Database db = new JDBCDatabase("apps/phenoflow/phenoflow.properties");
		CsvImport.importAll(new File(directory), db, new SimpleTuple());
	}
}
