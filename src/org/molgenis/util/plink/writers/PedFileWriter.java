package org.molgenis.util.plink.writers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.PedEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class PedFileWriter implements Closeable
{
	private CsvFileWriter writer;

	public PedFileWriter(File pedFile) throws Exception
	{
		writer = new CsvFileWriter(pedFile);
		writer.setHeaders(Arrays.asList(PedEntry.pedHeader()));
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
	public void writeSingle(PedEntry ped) throws IOException
	{
		writer.writeRow(PedEntry.pedToTuple(ped));
	}

	/**
	 * Write multiple entries in order.
	 * 
	 * @throws IOException
	 */
	public void writeMulti(List<PedEntry> peds) throws IOException
	{
		for (PedEntry ped : peds)
		{
			writer.writeRow(PedEntry.pedToTuple(ped));
		}
	}

	/**
	 * Write all entries and close the writer.
	 * 
	 * @throws IOException
	 */
	public void writeAll(List<PedEntry> peds) throws IOException
	{
		try
		{
			for (PedEntry ped : peds)
			{
				writer.writeRow(PedEntry.pedToTuple(ped));
			}
		}
		finally
		{
			writer.close();
		}
	}
}
