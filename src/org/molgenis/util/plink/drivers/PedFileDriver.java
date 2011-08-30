package org.molgenis.util.plink.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.PedEntry;

/**
 * TODO
 * 
 * Driver to query PED files. A PED file contains family- and genotyping data
 * for an individual, plus a single phenotype. Basically it is a FAM file with
 * added genotyping (typically SNP) data. However, the example file is a bit
 * peculiar: it has 'null' columns because of additional spacing between some
 * data values. This makes parsing hard. Question: can all Plink files have
 * this? or just PED? See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped
 */
public class PedFileDriver
{

	private CsvFileReader reader;
	private long nrOfElements;

	/**
	 * Get the number of retrievable family elements of this PED file.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Construct a PedFileDriver on this file
	 * 
	 * @param bimFile
	 * @throws Exception
	 */
	public PedFileDriver(File pedFile) throws Exception
	{
		reader = new CsvFileReader(pedFile);
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
	 * Get all PED file entries
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<PedEntry> getAllEntries() throws Exception
	{
		reader.reset();
		final ArrayList<PedEntry> result = new ArrayList<PedEntry>();
		final ArrayList<Integer> nullFields = new ArrayList<Integer>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
			{
				// System.out.println(tuple.getNrColumns());
				int trueIndex = 0; // correct for null columns
				for (int col = 0; col < tuple.getNrColumns(); col++)
				{
					if (!nullFields.contains(col))
					{
						Object o = tuple.getObject(col);
						if (o == null)
						{
							// file formatting may contain double spacing or so
							// add this index to list of null column indices to
							// be skipped
							// in next iteration
							nullFields.add(col);
						}
						else
						{
							// grab stuff..
							// iterate over N number of genotypes, add as
							// Bialleles to PedEntry
							// etc..
							trueIndex++;
						}
					}
					System.out.println(col + " - " + tuple.getObject(col));
				}
				// PedEntry pe = new PedEntry(tuple.getInt(0), tuple.getInt(1),
				// tuple.getInt(2), tuple.getInt(3), tuple.getInt(4)
				// .byteValue(), tuple.getDouble(5), new
				// Biallele(tuple.getString(6),
				// tuple.getString(7)));
				// result.add(pe);
			}
		});
		return result;
	}

	/**
	 * Get a specific set of PED file entries
	 * 
	 * @param from
	 *            = inclusive
	 * @param to
	 *            = exclusive
	 * @return
	 * @throws Exception
	 */
	public List<PedEntry> getEntries(final long from, final long to)
			throws Exception
	{
		reader.reset();
		final ArrayList<PedEntry> result = new ArrayList<PedEntry>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
			{
				if (line_number - 1 >= from && line_number - 1 < to)
				{
					// PedEntry pe = new PedEntry(tuple.getInt(0),
					// tuple.getInt(1), tuple.getInt(2), tuple.getInt(3),
					// tuple.getInt(4).byteValue(), tuple.getDouble(5));
					// result.add(pe);
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
