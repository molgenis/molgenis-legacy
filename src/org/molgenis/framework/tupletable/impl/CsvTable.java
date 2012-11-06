package org.molgenis.framework.tupletable.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.io.IOUtils;
import org.molgenis.framework.tupletable.AbstractTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleIterator;

/**
 * Wrap a CSV file into a TupleTable
 */
public class CsvTable extends AbstractTupleTable
{
	private CsvReader csv;
	private InputStream countStream;
	private List<Field> columns = new ArrayList<Field>();

	File csvFile;
	String csvString;

	/**
	 * Read table from a csv file
	 * 
	 * @param csvFile
	 * @throws Exception
	 */
	public CsvTable(File csvFile) throws Exception
	{
		if (csvFile == null) throw new NullPointerException("Creation of CsvTable failed: csvFile == null");
		if (!csvFile.exists()) throw new IllegalArgumentException("Creation of CsvTable failed: csvFile does not exist");

		this.csvFile = csvFile;
		this.resetStreams();
		loadColumns();
	}

	/**
	 * Read table from a csv string
	 * 
	 * @param csvString
	 * @throws Exception
	 */
	public CsvTable(String csvString) throws TableException
	{
		if (csvString == null) throw new NullPointerException("Creation of CsvTable failed: csvString == null");

		this.csvString = csvString;
		try
		{
			resetStreams();
			loadColumns();
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
	}

	int rowCount = -1;

	/**
	 * Count rows (not including header of csv file)
	 */
	@Override
	public int getCount() throws TableException
	{
		if (rowCount == -1)
		{
			LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(countStream));
			try
			{
				String line = null;
				while ((line = lineReader.readLine()) != null)
				{
					line = line.trim();
				}

				// substract 1 because of header
				rowCount = lineReader.getLineNumber() - 1;
			}
			catch (Exception e)
			{
				throw new TableException(e);
			}
			finally
			{
				IOUtils.closeQuietly(lineReader);
			}
		}
		return rowCount;
	}

	/**
	 * Helper method to load the Field metadata
	 * 
	 * @throws Exception
	 */
	private void loadColumns() throws Exception
	{
		for (String name : csv.colnames())
		{
			Field f = new Field(name);
			columns.add(f);
		}
	}

	@Override
	public List<Field> getAllColumns()
	{
		return columns;
	}

	@Override
	public List<Tuple> getRows()
	{
		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple row : this)
		{
			result.add(row);
		}

		return result;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			this.resetStreams();
		}
		catch (Exception e)
		{
			// should not happen as this is second load
			e.printStackTrace();
		}

		if (getLimit() > 0 || getOffset() > 0 || getColOffset() > 0 || getColLimit() > 0)
		{
			// offset + 1 to skip the header
			return new TupleIterator(csv, getLimit(), getOffset() + 1, getColLimit(), getColOffset());
		}
		return csv.iterator();
	}

	@Override
	public void close() throws TableException
	{
		try
		{
			csv.close();
			countStream.close();
		}
		catch (IOException e)
		{
			throw new TableException(e);
		}
	}

	private void resetStreams() throws FileNotFoundException, IOException, DataFormatException
	{
		if (csvFile != null)
		{
			csv = new CsvFileReader(csvFile);
			countStream = new FileInputStream(csvFile);
		}
		else
		{
			csv = new CsvStringReader(csvString);
			countStream = new ByteArrayInputStream(csvString.getBytes());
		}
	}
}
