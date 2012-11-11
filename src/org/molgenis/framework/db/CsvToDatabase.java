package org.molgenis.framework.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/** will be moved to generic solution */

public abstract class CsvToDatabase<E extends Entity>
{
	protected static int BATCH_SIZE = 10000;
	protected static final Logger logger = Logger.getLogger(CsvToDatabase.class);

	/**
	 * wrapper to use int inside anonymous classes (requires final, so cannot
	 * update directly)
	 */
	// FIXME move to value type elsewhere?
	public static class IntegerWrapper
	{
		private int value;

		public IntegerWrapper(int value)
		{
			this.value = value;
		}

		public void set(int value)
		{
			this.value = value;
		}

		public int get()
		{
			return this.value;
		}
	}

	/**
	 * Imports ${JavaName(entity)} from tab/comma delimited File.
	 * 
	 * @param db
	 *            where data should be imported into
	 * @param file
	 *            with the csv values
	 * @param defaults
	 *            default values that override the values in the file columns
	 */
	public int importCsv(final Database db, File file, final Tuple defaults) throws DatabaseException, IOException,
			Exception
	{
		return importCsv(db, file, defaults, DatabaseAction.ADD);
	}

	public int importCsv(Database db, File file, Tuple defaults, DatabaseAction dbAction) throws Exception
	{
		if (file.exists())
		{
			logger.info("trying to import " + file);
			CsvReader reader = new CsvFileReader(file);
			return this.importCsv(db, reader, defaults, dbAction, reader.getMissingValues());
		}
		else
		{
			logger.warn("CsvImport of " + file + " skipped: file doesn't exists");
			return 0;
		}
	}

	/**
	 * Imports ${JavaName(entity)} from tab/comma delimited File.
	 * 
	 * @param db
	 *            where data should be imported into
	 * @param file
	 *            with the csv values
	 * @param defaults
	 *            default values that override the values in the file columns
	 * @param dbAction
	 *            indicating if data needs to be added, updated, etc
	 * @param missingValues
	 *            string that indicates how missing values are specified. E.g.
	 *            "" or "NA"
	 */
	public int importCsv(final Database db, File file, final Tuple defaults, DatabaseAction dbAction,
			String missingValues) throws DatabaseException, IOException, Exception
	{
		if (file.exists())
		{
			logger.info("trying to import " + file);
			CsvReader reader = new CsvFileReader(file);
			return this.importCsv(db, reader, defaults, dbAction, missingValues);
		}
		else
		{
			logger.warn("CsvImport of " + file + " skipped: file doesn't exists");
			return 0;
		}
	}

	/**
	 * Imports ${JavaName(entity)} from tab/comma delimited File.
	 */
	public int importCsv(final Database db, CsvReader reader, final Tuple defaults) throws DatabaseException,
			IOException, Exception
	{
		return importCsv(db, reader, defaults, DatabaseAction.ADD);
	}

	public int importCsv(final Database db, CsvReader reader, final Tuple defaults, DatabaseAction action)
			throws DatabaseException, IOException, Exception
	{
		return importCsv(db, reader, defaults, DatabaseAction.ADD, reader.getMissingValues());
	}

	public abstract int importCsv(Database db, CsvReader reader, Tuple constants, DatabaseAction action,
			String missingValues) throws Exception;

	public static class ImportResult
	{

		List<String> progressLog;
		Map<String, String> messages;
		String errorItem;

		public ImportResult()
		{
			progressLog = new ArrayList<String>();
			messages = new HashMap<String, String>();
			errorItem = "no error found";
		}

		public List<String> getProgressLog()
		{
			return progressLog;
		}

		public void setProgressLog(List<String> progressLog)
		{
			this.progressLog = progressLog;
		}

		public Map<String, String> getMessages()
		{
			return messages;
		}

		public void setMessages(Map<String, String> messages)
		{
			this.messages = messages;
		}

		public String getErrorItem()
		{
			return errorItem;
		}

		public void setErrorItem(String errorItem)
		{
			this.errorItem = errorItem;
		}

	}
}
