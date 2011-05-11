package convertors.dbgap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.CsvToDatabase.ImportResult;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;

import app.CsvImport;
import app.JDBCDatabase;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * Loads dbgap into the pheno database
 * 
 * @author Morris Swertz
 * 
 */
public class LoadDbGap
{
	static Logger logger = Logger.getLogger(LoadDbGap.class);
	public static List<String> errors = new ArrayList<String>();

	public static void main(String[] args) throws DatabaseException
	{
		MolgenisOptions options = null;
		try
		{
			options = new MolgenisOptions("molgenis.properties");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		File directory = new File("D:\\Data\\dbgap");
		Database db = new JDBCDatabase(options);

		for (File dir : directory.listFiles())
		{
			logger.debug("IMPORTING " + dir);
			if (dir.isDirectory() && !dir.getName().equals("original"))
			{
				try
				{
					db.beginTx();

					ImportResult result = CsvImport.importAll(dir, db, null, null, DatabaseAction.ADD_IGNORE_EXISTING,
							"");
					if (!result.getErrorItem().equals("no error found"))
					{
						// System.err.println(result.getErrorItem());
						errors.add("IMPORT OF " + dir + " FAILED: "+result.getErrorItem() + " "+result.getMessages().get(result.getErrorItem()));
						if (db.inTx())
							db.rollbackTx();
					}
					else
					{
						if (db.inTx())
							db.commitTx();
					}
				}
				catch (MySQLIntegrityConstraintViolationException e)
				{

				}
				catch (Exception e)
				{
					// e.printStackTrace();
					if (db.inTx())
						try
						{
							db.rollbackTx();
						}
						catch (DatabaseException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}
			logger.debug("DONE IMPORTING " + dir);
			
			if(errors.size() == 0) 
				logger.info("THERE WHERE NO ERRORS");
			else for(String error: errors)
				logger.error(error);				
		}

	}
}
