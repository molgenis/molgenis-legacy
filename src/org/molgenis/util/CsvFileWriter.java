package org.molgenis.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Subclass of CsvWriter to write csv/tab to a file
 */
public class CsvFileWriter extends CsvWriter
{
	public CsvFileWriter(File f) throws IOException
	{
		super(new FileOutputStream(f));
	}

	/**
	 * @param f
	 *            the file to be written to
	 * @throws IOException
	 */
	public CsvFileWriter(File f, List<String> fields) throws IOException
	{
		super(new FileOutputStream(f), fields);
	}

	/**
	 * Append to existing csv using custom headers.
	 * 
	 * @param file
	 * @param fields
	 * @param append
	 * @throws IOException
	 */
	public CsvFileWriter(File file, List<String> fields, boolean append) throws IOException
	{
		super(new FileOutputStream(file, append), fields);
	}
}
