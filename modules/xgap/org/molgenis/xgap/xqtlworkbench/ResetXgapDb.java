package org.molgenis.xgap.xqtlworkbench;

import java.util.List;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;

import plugins.emptydb.emptyDatabase;
import app.FillMetadata;

public class ResetXgapDb
{
	public static String reset(Database db, boolean removeFiles)
	{

		String report = "";
		boolean databaseIsAvailable = false;

		try
		{
			if (db != null)
			{ // need some proper check here
				databaseIsAvailable = true;
			}
		}
		catch (Exception e)
		{
			report += "Database unavailable.";
			report += "\n\n";
			report += e.getMessage();
			e.printStackTrace();
		}

		if (databaseIsAvailable)
		{
			try
			{
				if (removeFiles)
				{
					report += "First deleting all database-associated files if applicable.\n";
					try
					{
						List<MolgenisFile> mfList = db.find(MolgenisFile.class);
						report += "Number of files: " + mfList.size() + "\n";
						db.remove(mfList);
						mfList = db.find(MolgenisFile.class);
						report += "Number of after delete: " + mfList.size() + "\n";
					}
					catch (DatabaseException dbe)
					{
						// database scheme does not contain MolgenisFile.class
						// ignore this and continue to load SQL
					}
				}
				report += "Now resetting datamodel/database.\n";
				if (db instanceof JDBCDatabase)
				{
					new emptyDatabase(db, false);
				}
				FillMetadata.fillMetadata(db, false);
				report += "Reset datamodel SUCCESS";
				// resetSuccess = true;
			}
			catch (Exception e)
			{
				report += "Error while trying to overwrite datamodel.";
				report += "\n\n";
				report += e.getMessage();
				e.printStackTrace();
			}
		}

		return report;
	}
}
