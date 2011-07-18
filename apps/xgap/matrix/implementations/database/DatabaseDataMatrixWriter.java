package matrix.implementations.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;

import xgap.importexport.DataElementImportByFile;
import decorators.NameConvention;

public class DatabaseDataMatrixWriter
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	/**
	 * Special constructor: imports from temp file. Does not have a 'testMode'
	 * boolean, this is always FALSE.
	 * 
	 * @param dataMatrix
	 * @param inputFile
	 * @param db
	 * @throws Exception 
	 */
	public DatabaseDataMatrixWriter(Data dataMatrix, File inputFile, Database db) throws Exception
	{
		List<File> inputFiles = new ArrayList<File>();
		List<Data> dataList = new ArrayList<Data>();
		inputFiles.add(inputFile);
		dataList.add(dataMatrix);
		new DatabaseDataMatrixWriter(dataList, inputFiles, db, false, true);
	}

	public DatabaseDataMatrixWriter(Data dataMatrix, File inputFile, Database db, boolean testMode) throws Exception
	{
		List<File> inputFiles = new ArrayList<File>();
		List<Data> dataList = new ArrayList<Data>();
		inputFiles.add(inputFile);
		dataList.add(dataMatrix);
		new DatabaseDataMatrixWriter(dataList, inputFiles, db, testMode, false);
	}

	public DatabaseDataMatrixWriter(List<Data> dataList, File inputDir, Database db, boolean testMode) throws Exception
	{
		List<File> inputFiles = new ArrayList<File>();
		for (File input : inputDir.listFiles())
		{
			inputFiles.add(input);
		}
		new DatabaseDataMatrixWriter(dataList, inputFiles, db, testMode, false);
	}

	/**
	 * Import a matrix using a list of input 'Data' and files. The filenames
	 * MUST match the file-escaped 'Data' names! Unless 'fromTmpFile' is TRUE,
	 * in that case the order of the lists must be the same. (retrieve by index)
	 * 
	 * @param dataList
	 * @param inputFiles
	 * @param db
	 * @param testMode
	 * @throws Exception
	 */
	public DatabaseDataMatrixWriter(List<Data> dataList, List<File> inputFiles, Database db, boolean testMode,
			boolean fromTmpFile) throws Exception
	{
		int index = 0;
		for (Data dataMatrix : dataList)
		{
			File inputFile = null;
			if (fromTmpFile)
			{
				inputFile = inputFiles.get(index);
			}
			else
			{
				inputFile = getInputFileForName(NameConvention.escapeFileName(dataMatrix.getName()) + ".txt",
						inputFiles);
			}

			index++;

			try
			{
				db.beginTx();
				String outcome = new DataElementImportByFile(db).ImportByFile(inputFile, dataMatrix, false, false,
						false, false);
				logger.info(outcome);
				if (!testMode)
				{
					logger.info("DatabaseMatrixWriter db.commitTx()");
					db.commitTx();
				}
				else
				{
					db.rollbackTx();
				}

			}
			catch (Exception e)
			{
				db.rollbackTx();
				throw e;
			}

		}
	}

	public File getInputFileForName(String name, List<File> inputFiles)
	{

		logger.info("getting file for name: " + name);
		for (File f : inputFiles)
		{
			logger.info("file: " + f.getAbsolutePath() + " (" + f.getName() + ")");
			if (f.getName().equals(name))
			{
				logger.info("FOUND!");
				return f;
			}
		}
		logger.info("NOT FOUND");
		return null;
	}

}
