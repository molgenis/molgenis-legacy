package org.molgenis.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Subclass of CsvWriter to write csv/tab to a file
 */
public class CsvStringWriter extends CsvWriter
{
	private StringWriter writer;

	public CsvStringWriter(StringWriter writer) throws IOException
	{
		super(new PrintWriter(writer));
		this.writer = writer;
	}

	/**
	 * @param f
	 *            the file to be written to
	 * @throws IOException
	 */
	public CsvStringWriter(StringWriter writer, List<String> fields) throws IOException
	{
		super(new PrintWriter(writer), fields);
		this.writer = writer;

	}

	@Override
	public String toString()
	{
		return writer.toString();
	}
}
