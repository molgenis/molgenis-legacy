package plinkbintocsv.sources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tritoplinkslice.sources.FamEntry;
import csvtobin.sources.CsvFileReader;
import csvtobin.sources.CsvReaderListener;
import csvtobin.sources.Tuple;

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
			this.nrOfElements = reader.getNumberOfLines() - reader.getAmountOfNewlinesAtFileEnd();
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
			public void handleLine(int line_number, Tuple tuple) throws Exception
			{
				for(int objIndex = 0; objIndex < 6; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number,objIndex));
				}
				FamEntry fe = new FamEntry(tuple.getString(0),
						tuple.getString(1), tuple.getString(2), tuple.getString(3),
						tuple.getInt(4).byteValue(), tuple.getDouble(5));
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
			public void handleLine(int line_number, Tuple tuple) throws Exception
			{
				if (line_number - 1 >= from && line_number - 1 < to)
				{
					for(int objIndex = 0; objIndex < 6; objIndex++)
					{
						if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number,objIndex));
					}
					FamEntry fe = new FamEntry(tuple.getString(0),
							tuple.getString(1), tuple.getString(2), tuple.getString(3),
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
