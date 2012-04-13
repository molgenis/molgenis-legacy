package matrix.implementations.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;

import matrix.general.VerifyCsv;
import matrix.general.VerifyCsvException;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;

import decorators.NameConvention;
import filehandling.generic.PerformUpload;

public class CSVDataMatrixWriter
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	/**
	 * Empty constructor to create an instance that is able to run standalone
	 * functions such as 'BinToCsv'
	 */
	public CSVDataMatrixWriter()
	{

	}

	/**
	 * Wrapper constructor to import CSV datamatrices. The import is done
	 * immediatly. (instanciation = import)
	 * 
	 * @param data
	 * @param inputFile
	 * @param db
	 * @throws Exception
	 */
	public CSVDataMatrixWriter(Data data, File inputFile, Database db) throws Exception
	{
		HashMap<Data, File> dataFileMap = new HashMap<Data, File>();
		dataFileMap.put(data, inputFile);
		new CSVDataMatrixWriter(dataFileMap, db);
//		List<Data> dataList = new ArrayList<Data>();
//		dataList.add(data);
//		List<File> inputFiles = new ArrayList<File>();
//		inputFiles.add(inputFile);
//		new CSVDataMatrixWriter(dataList, inputFiles, db);
	}

	/**
	 * Wrapper constructor
	 * Use filepointer as a directory of input files
	 * Dont use unless you know exactly what you're doing (filenames have to map to datanames)
	 * @param dataList
	 * @param inputDir
	 * @param db
	 * @param testMode
	 * @throws Exception
	 */
	public CSVDataMatrixWriter(List<Data> dataList, File inputDir, Database db)
			throws Exception
	{
		List<File> inputFiles = new ArrayList<File>();
		for (File input : inputDir.listFiles())
		{
			inputFiles.add(input);
		}
		
		HashMap<Data, File> dataFileMap = new HashMap<Data, File>();
		for (Data data : dataList)
		{
			File inputFile = getInputFileForName(NameConvention.escapeFileName(data.getName()) + ".txt", inputFiles);
			dataFileMap.put(data, inputFile);
		}
		
		new CSVDataMatrixWriter(dataFileMap, db);
	}
	
	/**
	 * Core constructor to import CSV datamatrices. The import is done
	 * immediatly. (instanciation = import)
	 * 
	 * @param data
	 * @param inputFile
	 * @param db
	 * @throws Exception
	 */
	public CSVDataMatrixWriter(HashMap<Data, File> dataFileMap, Database db)
			throws Exception
	{

		for (Data data : dataFileMap.keySet())
		{
			
			File src = dataFileMap.get(data);

			if (src == null || !src.exists())
			{
				throw new FileUploadException("File input for CSVDataMatrixWriter does not exists.");
			}

			VerifyCsv.verify(src, data.getValueType());
					
			//upload as a MolgenisFile, type 'CSVDataMatrix'
            HashMap<String, String> extraFields = new HashMap<String, String>();
            extraFields.put("data_" + Data.ID, data.getId().toString());
            extraFields.put("data_" + Data.NAME, data.getName());
			
			PerformUpload.doUpload(db, true, data.getName() + ".txt", "CSVDataMatrix", src, extraFields, false);
		}
	}

	private File getInputFileForName(String name, List<File> inputFiles)
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

	public void BinToCsv(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			throw new DataFormatException("You must supply 1 argument: source file name.");
		}

		String fileString = args[0];

		// check if source file exists and ends with '.bin'
		File src = new File(fileString);
		if (src == null || !src.exists())
		{
			throw new VerifyCsvException("Source file '" + fileString + "' not found at location '"
					+ src.getAbsolutePath() + "'");
		}
		if (!src.getName().endsWith(".bin"))
		{
			throw new VerifyCsvException("Source file name '" + fileString
					+ "' does not end with '.bin', are you sure it is a Binary matrix?");
		}

		System.out.println("Source file exists and ends with '.bin'..");

		BinaryDataMatrixInstance instance = new BinaryDataMatrixInstance(src);

		File dest = new File(src.getName().substring(0, (src.getName().length() - 4)) + ".txt");
		if (dest.exists())
		{
			throw new IOException("Destination file '" + dest.getName() + "' already exists");
		}

		System.out.println("Starting conversion..");

		PrintWriter out = new PrintWriter(dest);
		instance.writeToCsvWriter(out);

		System.out.println("..done!");

	}

}
