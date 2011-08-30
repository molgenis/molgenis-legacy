package org.molgenis.util.plink.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.Biallele;
import org.molgenis.util.plink.datatypes.BimEntry;
import org.molgenis.util.plink.datatypes.MapEntry;

/**
 * Driver to query MAP files. By default, each line of the MAP file describes a
 * single marker and must contain exactly 4 columns. Content of a MAP file:
 * chromosome, SNP, cM, base-position. See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#map
 */
public class MapFileDriver
{

	private CsvFileReader reader;
	private long nrOfElements;

	/**
	 * Get the number of retrievable annotation elements of this MAP file.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Construct a MapFileDriver on this file
	 * 
	 * @param mapFile
	 * @throws Exception
	 */
	public MapFileDriver(File mapFile) throws Exception
	{
		reader = new CsvFileReader(mapFile);
		reader.disableHeader(false);

		if (reader.fileEndsWithNewlineChar())
		{
			this.nrOfElements = reader.getNumberOfLines() - reader.getAmountOfNewlinesAtFileEnd();
		}
		else
		{
			this.nrOfElements = reader.getNumberOfLines();
		}

	}

	/**
	 * Close the underlying file reader
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		this.reader.close();
	}

	/**
	 * Get a specific set of MAP file entries
	 * 
	 * @param from
	 *            = inclusive
	 * @param to
	 *            = exclusive
	 * @return
	 * @throws Exception
	 */
	public List<MapEntry> getEntries(final long from, final long to)
			throws Exception
	{
		reader.reset();
		final ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
					throws Exception
			{
				if (line_number - 1 >= from && line_number - 1 < to)
				{
					MapEntry me = new MapEntry(tuple.getString(0), tuple
							.getString(1), tuple.getDouble(2), tuple.getLong(3));
					result.add(me);
				}
			}
		});
		return result;
	}

	/**
	 * Get all MAP file entries
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<MapEntry> getAllEntries() throws Exception
	{
		reader.reset();
		final ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
					throws Exception
			{
				MapEntry me = new MapEntry(tuple.getString(0), tuple
						.getString(1), tuple.getDouble(2), tuple.getLong(3));
				result.add(me);
			}
		});
		return result;
	}

}
