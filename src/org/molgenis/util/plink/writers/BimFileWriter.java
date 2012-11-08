package org.molgenis.util.plink.writers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.BimEntry;

/**
 * Write BIM file entries to a selected location.
 */
public class BimFileWriter implements Closeable
{
	private CsvFileWriter writer;

	public BimFileWriter(File bimFile) throws Exception
	{
		writer = new CsvFileWriter(bimFile);
		writer.setHeaders(Arrays.asList(BimEntry.bimHeader()));
	}

	/**
	 * Close the underlying writer.
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
	public void writeSingle(BimEntry bim) throws IOException
	{
		writer.writeRow(BimEntry.bimToTuple(bim));
	}

	/**
	 * Write multiple entries in order.
	 * 
	 * @throws IOException
	 */
	public void writeMulti(List<BimEntry> bims) throws IOException
	{
		for (BimEntry bim : bims)
		{
			writer.writeRow(BimEntry.bimToTuple(bim));
		}
	}

	/**
	 * Write all entries and close the writer.
	 * 
	 * @throws IOException
	 */
	public void writeAll(List<BimEntry> bims) throws IOException
	{
		try
		{
			for (BimEntry bim : bims)
			{
				writer.writeRow(BimEntry.bimToTuple(bim));
			}
		}
		finally
		{
			writer.close();
		}
	}
}
