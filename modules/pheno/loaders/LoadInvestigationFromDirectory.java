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
		String directory = "C:/work/workspace_web/molgenis4phenotype/data/Europhenome";

		Database db = new JDBCDatabase("C:/work/workspace_web/molgenis_apps/handwritten/apps/org/molgenis/pheno/pheno.properties");
		CsvImport.importAll(new File(directory), db, new SimpleTuple());
	}
}
