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

	/**
	 * Construct the CsvReader for a String.
	 * 
	 * @param csvString
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public CsvStringReader(String csvString) throws IOException, DataFormatException
	{
		super(new BufferedReader(new StringReader(csvString)));
	}
}
