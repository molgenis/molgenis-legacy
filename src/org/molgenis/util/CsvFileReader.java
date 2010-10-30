package org.molgenis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * CsvReader for delimited text files.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvFileReader extends CsvBufferedReader
{
	/** the File that is being read */
	private File file;

	public CsvFileReader(File file) throws FileNotFoundException
	{
		super(new BufferedReader(new FileReader(file)));
		this.file = file;
	}

	// @Override
	public void reset() throws IOException
	{
		super.reset();
		this.reader.close();
		this.reader = new BufferedReader(new FileReader(file));
	}
}
