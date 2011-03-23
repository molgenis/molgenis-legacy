package org.molgenis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * CsvReader for a delimited text String.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvStringReader extends CsvBufferedReaderMultiline
{
	/** Copy of the string that is being parsed */
	private String csvString;
	
	/**
	 * Construct the CsvReader for a String.
	 * @param csvString 
	 */
	public CsvStringReader(String csvString)
	{
		super(new BufferedReader(new StringReader(csvString)));
		this.csvString = csvString;
	}

	//@Override
	public void reset() throws IOException
	{
		super.reset();
		this.reader = new BufferedReader(new StringReader(csvString));
	}
}
