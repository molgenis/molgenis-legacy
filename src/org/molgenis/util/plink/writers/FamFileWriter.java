package org.molgenis.util.plink.writers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.FamEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class FamFileWriter implements Closeable
{
	private CsvFileWriter writer;

	public FamFileWriter(File famFile) throws Exception
	{
		writer = new CsvFileWriter(famFile);
		writer.setHeaders(Arrays.asList(FamEntry.famHeader()));
		writer.setSeparator(' ');
	}

	/**
	 * Close the underlying writer.
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException
	{
		writer.close();
	}

	/**
	 * Write a single entry.
	 * 
	 * @throws IOException
	 */
	public void writeSingle(FamEntry fam) throws IOException
	{
		writer.writeRow(FamEntry.famToTuple(fam));
	}

	/**
	 * Write multiple entries in order.
	 * 
	 * @throws IOException
	 */
	public void writeMulti(List<FamEntry> fams) throws IOException
	{
		for (FamEntry fam : fams)
		{
			writer.writeRow(FamEntry.famToTuple(fam));
		}
	}

	/**
	 * Write all entries and close the writer.
	 * 
	 * @throws IOException
	 */
	public void writeAll(List<FamEntry> fams) throws IOException
	{
		try
		{
			for (FamEntry fam : fams)
			{
				writer.writeRow(FamEntry.famToTuple(fam));
			}
		}
		finally
		{
			writer.close();
		}
	}
}
