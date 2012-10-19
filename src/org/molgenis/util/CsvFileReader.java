package org.molgenis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.DataFormatException;

/**
 * CsvReader for delimited text files.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvFileReader extends CsvBufferedReaderMultiline
{
	/** the File that is being read */
	private File sourceFile;

	/** encodig */
	private String encoding;

	public CsvFileReader(File file, boolean hasHeader) throws IOException, DataFormatException
	{
		super();
		this.sourceFile = file;
		this.hasHeader = hasHeader;
		this.reset();
	}

	public CsvFileReader(File file) throws IOException, DataFormatException
	{
		super();
		this.sourceFile = file;
		this.reset();
	}

	public CsvFileReader(final File file, final String encoding) throws IOException, DataFormatException
	{
		super();
		this.encoding = encoding;
		this.sourceFile = file;
		this.reset();
	}

	@Override
	public void reset() throws IOException, DataFormatException
	{
		if (this.reader != null)
		{
			this.reader.close();
		}
		// create a fresh InputStream to read from, the old one is closed
		if (this.encoding != null)
		{
			this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), encoding));
		}
		else
		{
			this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
		}
	}
}
