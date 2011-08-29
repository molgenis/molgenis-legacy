package org.molgenis.util.plink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

/**
 * Driver to query FAM files. FAM files annotate the families of BED files. See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml
 * 
 * Content of a FAM file: Family ID Individual ID Paternal ID Maternal ID Sex
 * (1=male; 2=female; other=unknown) Phenotype
 */
public class FamFileDriver
{

	private CsvFileReader reader;
	private long nrOfElements;

	/**
	 * Get the number of retrievable family elements of this FAM file.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Construct a FamFileDriver on this file
	 * 
	 * @param bimFile
	 * @throws Exception
	 */
	public FamFileDriver(File famFile) throws Exception
	{
		reader = new CsvFileReader(famFile);
		reader.disableHeader(false);

		if (reader.fileEndsWithNewlineChar())
		{
			this.nrOfElements = reader.getNumberOfLines() - 1;
		}
		else
		{
			this.nrOfElements = reader.getNumberOfLines();
		}

	}

	/**
	 * Get all FAM file entries
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<FamEntry> getAllEntries() throws Exception
	{
		reader.reset();
		final ArrayList<FamEntry> result = new ArrayList<FamEntry>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
			{
				FamEntry fe = new FamEntry(tuple.getInt(0), tuple.getInt(1),
						tuple.getInt(2), tuple.getInt(3), tuple.getInt(4)
								.byteValue(), tuple.getDouble(5));
				result.add(fe);
			}
		});
		return result;
	}

	/**
	 * Get a specific set of FAM file entries
	 * 
	 * @param from
	 *            = inclusive
	 * @param to
	 *            = exclusive
	 * @return
	 * @throws Exception
	 */
	public List<FamEntry> getEntries(final long from, final long to)
			throws Exception
	{
		reader.reset();
		final ArrayList<FamEntry> result = new ArrayList<FamEntry>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
			{
				if (line_number - 1 >= from && line_number - 1 < to)
				{
					FamEntry fe = new FamEntry(tuple.getInt(0),
							tuple.getInt(1), tuple.getInt(2), tuple.getInt(3),
							tuple.getInt(4).byteValue(), tuple.getDouble(5));
					result.add(fe);
				}
			}
		});
		return result;
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

}