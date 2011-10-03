package loaders;

import java.io.File;
import java.io.FilenameFilter;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;

import app.CsvImport;
import app.DatabaseFactory;
import app.JDBCDatabase;

public class LoadDbGapDownloads
{
	public static void main(String[] args) throws Exception
	{
		//it is assumed that DbGapToPheno has put its results here
		File rootDir = new File("D:/Data/dbgap");
		
		Database db = DatabaseFactory.create(new MolgenisOptions("molgenis.properties"));
		for(String investigationDir: rootDir.list( new FilenameFilter(){

			@Override
			public boolean accept(File arg0, String arg1)
			{
				return arg0.isDirectory() & arg1.startsWith("phs");
			}}))
			{
				CsvImport.importAll(new File(rootDir.getAbsolutePath() + "\\" + investigationDir), db, null);
			}

		
	}
}
