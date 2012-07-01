package org.molgenis.datatable.model;

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

import org.molgenis.model.elements.Field;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleIterator;

/**
 * Wrap a CSV file into a TupleTable
 */
public class CsvTable implements TupleTable
{
	private CsvReader csv;
	private InputStream countStream;
	private List<Field> columns = new ArrayList<Field>();
	int limit = 0;
	int offset = 0;

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
		this.csvFile = csvFile;
		this.reset();
		loadColumns();
	}

	/**
	 * Read table from a csv string
	 * 
	 * @param csvString
	 * @throws Exception
	 */
	public CsvTable(String csvString) throws Exception
	{
		this.csvString = csvString;
		this.reset();
		loadColumns();
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
			try
			{
				LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(countStream));
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
	public List<Field> getColumns()
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
			this.reset();
		}
		catch (Exception e)
		{
			//should not happen as this is second load
			e.printStackTrace();
		}
		
		if (limit > 0 || offset > 0)
		{
			return new TupleIterator(csv, limit, offset);
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

	private void reset() throws FileNotFoundException, IOException, DataFormatException
	{
		if (csvFile != null)
		{
			csv = new CsvFileReader(new FileInputStream(csvFile));
			countStream = new FileInputStream(csvFile);
		}
		else
		{
			csv = new CsvStringReader(csvString);
			countStream = new ByteArrayInputStream(csvString.getBytes());
		}
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.limit = limit;
		this.offset = offset;
	}

	@Override
	public int getLimit()
	{
		return limit;
	}

	@Override
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	@Override
	public int getOffset()
	{
		return limit;
	}

	@Override
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
}
