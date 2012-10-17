package org.molgenis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.zip.DataFormatException;

/**
 * CsvReader for a delimited text String.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvStringReader extends CsvBufferedReaderMultiline
{
	String csvString;

	/**
	 * Construct the CsvReader for a String.
	 * 
	 * @param csvString
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public CsvStringReader(String csvString) throws IOException, DataFormatException
	{
		super();
		this.csvString = csvString;
		this.reset();

	}

	@Override
	public void reset() throws IOException, DataFormatException
	{
		this.reader = new BufferedReader(new StringReader(csvString));
		if (this.hasHeader) this.columnnames = colnames();
	}
}
