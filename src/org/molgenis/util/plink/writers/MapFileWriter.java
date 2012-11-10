package org.molgenis.util.plink.writers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.MapEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class MapFileWriter implements Closeable
{
	private CsvFileWriter writer;

	public MapFileWriter(File mapFile) throws Exception
	{
		writer = new CsvFileWriter(mapFile);
		writer.setHeaders(Arrays.asList(MapEntry.mapHeader()));
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
	public void writeSingle(MapEntry map) throws IOException
	{
		writer.writeRow(MapEntry.mapToTuple(map));
	}

	/**
	 * Write multiple entries in order.
	 * 
	 * @throws IOException
	 */
	public void writeMulti(List<MapEntry> maps) throws IOException
	{
		for (MapEntry map : maps)
		{
			writer.writeRow(MapEntry.mapToTuple(map));
		}
	}

	/**
	 * Write all entries and close the writer.
	 * 
	 * @throws IOException
	 */
	public void writeAll(List<MapEntry> maps) throws IOException
	{
		try
		{
			for (MapEntry map : maps)
			{
				writer.writeRow(MapEntry.mapToTuple(map));
			}
		}
		finally
		{
			writer.close();
		}
	}
}
