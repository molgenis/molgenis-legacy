package org.molgenis.datatable.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.model.elements.Field;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;

/**
 * Wrap a CSV file into a TupleTable
 */
public class CsvTable implements TupleTable
{
	private final CsvReader csv;
	private final List<Field> columns = new ArrayList<Field>();
	
	/**
	 * Read table from a csv file
	 * 
	 * @param csvFile
	 * @throws Exception
	 */
	public CsvTable(InputStream csvStream) throws Exception
	{
		csv = new CsvFileReader(csvStream);	
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
		csv = new CsvStringReader(csvString);	
		loadColumns();
	}
	
	/**
	 * Helper method to load the Field metadata
	 * 
	 * @throws Exception
	 */
	private void loadColumns() throws Exception
	{
		for(String name: csv.colnames())
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
		for(Tuple row: this)
		{
			result.add(row);
		}
		return result;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		//TODO: can one run iterator twice?
		return csv.iterator();
	}

	@Override
	public void close() throws TableException
	{
		try
		{
			csv.close();
		}
		catch (IOException e)
		{
			throw new TableException(e); 
		}
	}
}
