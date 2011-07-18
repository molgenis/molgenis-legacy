package plugins.archiveexportimport;

import java.io.File;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.util.SimpleTuple;

import app.CsvImport;

public class XgapCsvImport
{
	public XgapCsvImport(File extractDir, Database db, boolean skipWhenDestExists) throws Exception
	{
	
		db.beginTx();

		try
		{
			CsvImport.importAll(extractDir, db, new SimpleTuple(), false);

			File investigationFile = new File(extractDir + File.separator + "study.txt");
			List<String> investigationNames = XgapCommonImport.getInvestigationNameFromFile(investigationFile);

			XgapCommonImport.importMatrices(investigationNames, db, false, new File(extractDir + File.separator + "data"), skipWhenDestExists);
		
			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw(e);
		}

	}

}
