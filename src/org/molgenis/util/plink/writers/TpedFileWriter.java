package org.molgenis.util.plink.writers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.TpedEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class TpedFileWriter implements Closeable
{
	private CsvFileWriter writer;

	public TpedFileWriter(File tpedFile) throws Exception
	{
		writer = new CsvFileWriter(tpedFile);
		writer.setHeaders(Arrays.asList(TpedEntry.tpedHeader()));
		writer.setSeparator(' ');
		writer.setListSeparator(' ');
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
	public void writeSingle(TpedEntry tped) throws IOException
	{
		writer.writeRow(TpedEntry.tpedToTuple(tped));
	}

	/**
	 * Write multiple entries in order.
	 * 
	 * @throws IOException
	 */
	public void writeMulti(List<TpedEntry> tpeds) throws IOException
	{
		for (TpedEntry tped : tpeds)
		{
			writer.writeRow(TpedEntry.tpedToTuple(tped));
		}
	}

	/**
	 * Write all entries and close the writer.
	 * 
	 * @throws IOException
	 */
	public void writeAll(List<TpedEntry> tpeds) throws IOException
	{
		try
		{
			for (TpedEntry tped : tpeds)
			{
				writer.writeRow(TpedEntry.tpedToTuple(tped));
			}
		}
		finally
		{
			writer.close();
		}
	}
}
