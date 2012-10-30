package org.molgenis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.zip.DataFormatException;

/**
 * CSV reader for <a href="http://tools.ietf.org/html/rfc4180">comma-separated
 * value Strings</a>
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvStringReader extends CsvBufferedReaderMultiline
{
	/** Input comma-separated value String */
	private String csvString;

	/**
	 * Construct the CsvReader for a String.
	 * 
	 * @param csvString
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public CsvStringReader(String csvString) throws IOException
	{
		this(csvString, true);

	}

	/**
	 * Construct the CsvReader for a String.
	 * 
	 * @param csvString
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public CsvStringReader(String csvString, boolean hasHeader) throws IOException
	{
		super();
		if (csvString == null) throw new IllegalArgumentException("csvString is null");
		this.csvString = csvString;
		this.hasHeader = hasHeader;
		this.reset();
	}

	@Override
	public void reset() throws IOException
	{
		if (this.reader != null) this.reader.close();
		this.reader = new BufferedReader(new StringReader(csvString));
		super.reset();
	}
}
