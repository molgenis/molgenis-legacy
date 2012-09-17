package matrix.implementations.binary;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.naming.NamingException;

import matrix.general.VerifyCsv;
import matrix.general.VerifyCsvException;
import matrix.implementations.binary.etc.ElementLengthException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.CsvToDatabase.IntegerWrapper;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

import decorators.NameConvention;
import filehandling.generic.PerformUpload;

public class BinaryDataMatrixWriter
{
	private Logger logger = Logger.getLogger(getClass().getSimpleName());

	private String nullChar = "\5";

	/**
	 * Empty constructor to create an instance that is able to run standalone
	 * functions such as 'CsvToBin'
	 */
	public BinaryDataMatrixWriter()
	{
		//
	}

	/**
	 * Wrapper constructor Writes a binary matrix to the filesystem
	 * 
	 * @param data
	 * @param inputFile
	 * @param db
	 * @param testMode
	 * @throws Exception
	 */
	public BinaryDataMatrixWriter(Data data, File inputFile, Database db)
			throws Exception
	{
		HashMap<Data, File> dataFileMap = new HashMap<Data, File>();
		dataFileMap.put(data, inputFile);
		new BinaryDataMatrixWriter(dataFileMap, db);
	}

	/**
	 * Wrapper constructor Use filepointer as a directory of input files Dont
	 * use unless you know exactly what you're doing (filenames have to map to
	 * datanames)
	 * 
	 * @param dataList
	 * @param inputDir
	 * @param db
	 * @param testMode
	 * @throws Exception
	 */
	public BinaryDataMatrixWriter(List<Data> dataList, File inputDir,
			Database db) throws Exception
	{
		List<File> inputFiles = new ArrayList<File>();
		for (File input : inputDir.listFiles())
		{
			inputFiles.add(input);
		}

		HashMap<Data, File> dataFileMap = new HashMap<Data, File>();
		for (Data data : dataList)
		{
			File inputFile = getInputFileForName(
					NameConvention.escapeFileName(data.getName()) + ".txt",
					inputFiles);
			dataFileMap.put(data, inputFile);
		}

		new BinaryDataMatrixWriter(dataFileMap, db);
	}

	/**
	 * Core constructor. Prepare and verify a list of files and import them.
	 * 
	 * @param dataList
	 * @param inputFiles
	 * @param db
	 * @param testMode
	 * @throws Exception
	 */
	public BinaryDataMatrixWriter(HashMap<Data, File> dataFileMap, Database db)
			throws Exception
	{

		for (Data data : dataFileMap.keySet())
		{

			File src = dataFileMap.get(data);

			if (src == null || !src.exists())
			{
				throw new FileUploadException(
						"File input for BinaryMatrixWriter does not exists.");
			}

			int[] rowAndColLength = VerifyCsv.verify(src, data.getValueType());

			// make the binary file
			File dest = new File(System.getProperty("java.io.tmpdir")
					+ File.separator + "tmp_binmatrix_" + System.nanoTime());
			File binFile = makeBinaryBackend(data, src, dest,
					rowAndColLength[0], rowAndColLength[1]);

			// upload as a MolgenisFile, type 'BinaryDataMatrix'
			HashMap<String, String> extraFields = new HashMap<String, String>();
			extraFields.put("data_" + Data.ID, data.getId().toString());
			extraFields.put("data_" + Data.NAME, data.getName());

			PerformUpload.doUpload(db, true, data.getName() + ".bin",
					"BinaryDataMatrix", binFile, extraFields, false);

		}

	}

	public void CsvToBin(String[] args) throws Exception
	{

		if (args.length != 6)
		{
			throw new DataFormatException(
					"You must supply 6 arguments: data name, investigation name, row type, column type, value type, and source file name.");
		}

		// get args
		String dataName = args[0];
		String invName = args[1];
		String rowType = args[2];
		String colType = args[3];
		String valType = args[4];
		String fileString = args[5];

		// print args
		System.out.println("CsvToBin called with arguments:");
		System.out.println("data name = " + dataName);
		System.out.println("investigation name = " + invName);
		System.out.println("row type = " + rowType);
		System.out.println("column type = " + colType);
		System.out.println("value type = " + valType);
		System.out.println("source file = " + fileString);

		// check if source file exists and ends with '.txt'
		File src = new File(fileString);
		if (src == null || !src.exists())
		{
			throw new VerifyCsvException("Source file '" + fileString
					+ "' not found at location '" + src.getAbsolutePath() + "'");
		}
		if (!src.getName().endsWith(".txt"))
		{
			throw new VerifyCsvException(
					"Source file name '"
							+ fileString
							+ "' does not end with '.txt', are you sure it is a CSV matrix?");
		}

		System.out.println("Source file exists and ends with '.txt'..");

		// create Data object, validate the names and valuetype
		Data d = new Data();
		d.setName(dataName);
		d.setInvestigation_Name(invName);
		d.setTargetType(rowType);
		d.setFeatureType(colType);
		d.setValueType(valType);

		// FIXME: strict should only be applied when application is an XGAP
		NameConvention.validateEntityNameStrict(dataName);
		NameConvention.validateEntityNameStrict(invName);

		System.out.println("'Data' object created..");

		if (!valType.equals("Text") && !valType.equals("Decimal"))
		{
			throw new NamingException("Value type '" + valType
					+ "' not reckognized. Use 'Text' or 'Decimal'.");
		}

		System.out.println("Valuetype OK..");

		// verify the CSV file to be a correct matrix and get the dimensions
		int[] dims = VerifyCsv.verify(src, valType);

		System.out.println("CSV input file verified..");

		// convert to binary
		File dest = new File(src.getName().substring(0,
				(src.getName().length() - 4))
				+ ".bin");

		System.out.println("Starting conversion..");

		makeBinaryBackend(d, src, dest, dims[0], dims[1]);

		System.out.println("..done!");

	}

	/**
	 * Convert an input file into a binary matrix
	 * 
	 * @param data
	 * @param db
	 * @param inputFile
	 * @param totalRows
	 * @param totalCols
	 * @throws Exception
	 * @throws Exception
	 */
	private File makeBinaryBackend(Data data, File src, File dest,
			int totalRows, int totalCols) throws Exception

	{
		if (dest.exists())
		{
			throw new IOException("Destination file '" + dest.getName()
					+ "' already exists");
		}

		FileOutputStream fos = new FileOutputStream(dest);
		final DataOutputStream dos = new DataOutputStream(fos);

		// 0) write nullCharacter
		dos.writeBytes(this.nullChar);

		// 1) properties belonging to the 'Data' object
		dos.writeByte(data.getName().length());
		dos.writeBytes(data.getName());

		dos.writeByte(data.getInvestigation_Name().length());
		dos.writeBytes(data.getInvestigation_Name());

		dos.writeByte(data.getFeatureType().length());
		dos.writeBytes(data.getFeatureType());

		dos.writeByte(data.getTargetType().length());
		dos.writeBytes(data.getTargetType());

		if (data.getValueType().equals("Decimal"))
		{
			dos.writeBoolean(true);
		}
		else
		{
			dos.writeBoolean(false);
		}

		dos.writeInt(totalCols);
		dos.writeInt(totalRows);

		// 2) matrix content specific properties
		CsvFileReader csvFile = new CsvFileReader(src);

		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();

		// hack for xgap matrix datatype
		if (colNames.get(0).equals(""))
		{
			colNames.remove(0);
		}

		for (int i = 0; i < totalCols; i++)
		{
			dos.writeByte(colNames.get(i).length());
		}

		for (int i = 0; i < totalRows; i++)
		{
			dos.writeByte(rowNames.get(i).length());
		}

		for (int i = 0; i < totalCols; i++)
		{
			dos.writeBytes(colNames.get(i));
		}

		for (int i = 0; i < totalRows; i++)
		{
			dos.writeBytes(rowNames.get(i));
		}

		// information about text DataMatrix elements
		int textLength = -1;
		if (data.getValueType().equals("Text"))
		{
			textLength = elementLength(src);
			dos.writeByte(textLength);
			logger.info("text DataMatrix element length: " + textLength);

			if (textLength == 0)
			{
				logger.info("length zero, making variable length array");
				// determine lengths and write to binary
				byte[] textElementLenghts = getTextDataElementLengths(src,
						totalCols * totalRows);
				dos.write(textElementLenghts);
			}
		}
		logger.info("Writing elements..");
		// writing the actual elements
		if (data.getValueType().equals("Text"))
		{
			writeBinaryTextElements(dos, src, textLength);
		}
		else
		{
			writeBinaryDecimalElements(dos, src);
		}

		return dest;

	}

	private long writeBinaryTextElements(final DataOutputStream dos,
			File inputFile, int textLength) throws FileNotFoundException,
			ParseException
	{
		long start = System.currentTimeMillis();
		// adjusting the NA string to text length, if this is a fixed length, it
		// does not break the special treatment that makes fixed length
		// efficient
		String naStringCreate = "";
		if (textLength == 0)
		{
			naStringCreate = nullChar;
		}
		else if (textLength > 0)
		{
			naStringCreate = "";
			for (int i = 0; i < textLength; i++)
			{
				naStringCreate += nullChar;
			}
		}
		final String naString = naStringCreate;

		try
		{
			for (Tuple line : new CsvFileReader(inputFile))
			{
				for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
				{
					if (line.getString(columnIndex) == null)
					{ // FIXME: null: because of a parsed missing value
						// indicator..
						dos.writeBytes(naString);
					}
					else if (line.getString(columnIndex).equals(""))
					{
						dos.writeBytes(naString);
					}
					else
					{

						// FIXME: little experiment..
						// String str = line.getString(columnIndex);
						// byte[] arr = new byte[str.length()];
						// int count = 0;
						// for(char c : str.toCharArray()){
						// arr[count] = (byte) (c + 100);
						// count++;
						// }
						// dos.write(arr);
						dos.writeBytes(line.getString(columnIndex));

					}
				}
			}

		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage(), 0);
		}
		long stop = System.currentTimeMillis();
		return stop - start;
	}

	private long writeBinaryDecimalElements(final DataOutputStream dos,
			File inputFile) throws FileNotFoundException, ParseException
	{
		long start = System.currentTimeMillis();
		try
		{
			for (Tuple line : new CsvFileReader(inputFile))
			{

				for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
				{
					if (line.getDouble(columnIndex) == null)
					{
						dos.writeDouble(Double.MAX_VALUE);
					}
					else
					{
						dos.writeDouble(line.getDouble(columnIndex));
					}
				}
			}

		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage(), 0);
		}
		long stop = System.currentTimeMillis();
		return stop - start;
	}

	private byte[] getTextDataElementLengths(File inputFile, int totalElements)
			throws FileNotFoundException, ParseException
	{
		final byte[] textElementLenghts = new byte[totalElements];
		try
		{
			int index = 0;
			for (Tuple line : new CsvFileReader(inputFile))
			{

				for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
				{
					if (line.getString(columnIndex) == null)
					{ // FIXME:
						// null??
						textElementLenghts[index] = (byte) 1;
					}
					else if (line.getString(columnIndex).equals(""))
					{
						textElementLenghts[index] = (byte) 1;
					}
					else
					{
						textElementLenghts[index] = (byte) line.getString(
								columnIndex).length();
					}
					index++;
				}
			}
		}
		catch (Exception e)
		{
			throw new ParseException(e.getMessage(), 0);
		}
		return textElementLenghts;
	}

	/**
	 * Check if all TEXT elements in a matrix are of equal length. The first
	 * element is used to get the length, then each element after that must have
	 * the same length in order for this length to be returned.
	 * 
	 * @param inputFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException
	 * @throws Exception
	 */
	private int elementLength(File inputFile) throws FileNotFoundException,
			ParseException
	{
		final IntegerWrapper elementLength = new IntegerWrapper(0);

		try
		{
			for (Tuple line : new CsvFileReader(inputFile))
			{
				for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
				{
					// get one element
					if (elementLength.get() == 0)
					{
						elementLength
								.set(line.getString(columnIndex) != null ? line
										.getString(columnIndex).length() : 0);
						logger.info("First element, size: "
								+ elementLength.get());
					}
					else
					{
						if (elementLength.get() != (line.getString(columnIndex) != null ? line
								.getString(columnIndex).length() : 0)) // nullpointer
																		// ???
						{

							logger.info("Element "
									+ line.getString(columnIndex)
									+ " is not of length "
									+ elementLength.get());
							logger.info("Element of unequal size found, exiting from function by throwing error");
							elementLength.set(0);
							throw new ElementLengthException(
									"Exiting from CsvFileReader...");
						}
					}
				}
			}
		}
		catch (ElementLengthException e)
		{
			// this is okay..
		}
		catch (Exception ex)
		{
			throw new ParseException(ex.getMessage(), 0);
		}

		return elementLength.get();
	}

	private File getInputFileForName(String name, List<File> inputFiles)
	{
		logger.info("getting file for name: " + name);
		for (File f : inputFiles)
		{
			logger.info("file: " + f.getAbsolutePath() + " (" + f.getName()
					+ ")");
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
