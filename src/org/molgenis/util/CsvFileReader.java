package org.molgenis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.DataFormatException;

/**
 * CsvReader for delimited text files.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvFileReader extends CsvBufferedReaderMultiline
{
	public CsvFileReader(File file, boolean hasHeader) throws IOException, DataFormatException
	{
		super(new BufferedReader(new FileReader(file)), hasHeader);
	}

	public CsvFileReader(File file) throws IOException, DataFormatException
	{
		super(new BufferedReader(new FileReader(file)));
	}

	public CsvFileReader(final File file, final String encoding) throws IOException, DataFormatException
	{
		super(new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding)));
	}

	public CsvFileReader(InputStream csvStream) throws IOException, DataFormatException
	{
		super(new BufferedReader(new InputStreamReader(csvStream)));
	}
}
