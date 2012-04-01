package org.molgenis.util.plink.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.Biallele;
import org.molgenis.util.plink.datatypes.TpedEntry;

/**
 * Driver to query TPED files.
 */
public class TpedFileDriver
{

	private CsvFileReader reader;
	private long nrOfElements;

	/**
	 * Get the number of retrievable annotation elements of this TPED file.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Construct a TpedFileDriver on this file
	 * 
	 * @param tpedFile
	 * @throws Exception
	 */
	public TpedFileDriver(File tpedFile) throws Exception
	{
		reader = new CsvFileReader(tpedFile);
		reader.disableHeader(false);

		if (reader.fileEndsWithNewlineChar())
		{
			this.nrOfElements = reader.getNumberOfLines()
					- reader.getAmountOfNewlinesAtFileEnd();
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
	 * Get a specific set of TPED file entries
	 * 
	 * @param from
	 *            = inclusive
	 * @param to
	 *            = exclusive
	 * @return
	 * @throws Exception
	 */
	public List<TpedEntry> getEntries(final long from, final long to)
			throws Exception
	{
		reader.reset();
		final ArrayList<TpedEntry> result = new ArrayList<TpedEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;
			if (line_number - 1 >= from && line_number - 1 < to)
			{
				List<Biallele> bialleles = new ArrayList<Biallele>();

				for (int col = 4; col < tuple.getNrColumns(); col += 2)
				{
					String al1 = tuple.getString(col);
					String al2 = tuple.getString(col + 1);
					if (al1 == null) throw new Exception(Helper.errorMsg(
							line_number, col));
					if (al2 == null) throw new Exception(Helper.errorMsg(
							line_number, col + 1));
					Biallele biallele = new Biallele(al1, al2);
					bialleles.add(biallele);
				}

				for (int objIndex = 0; objIndex < 4; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(
							Helper.errorMsg(line_number, objIndex));
				}
				TpedEntry te = new TpedEntry(tuple.getString(0),
						tuple.getString(1), tuple.getDouble(2),
						tuple.getLong(3), bialleles);
				result.add(te);
			}
		}

		return result;
	}

	/**
	 * Get all TPED file entries
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<TpedEntry> getAllEntries() throws Exception
	{
		reader.reset();
		final ArrayList<TpedEntry> result = new ArrayList<TpedEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			List<Biallele> bialleles = new ArrayList<Biallele>();

			for (int col = 4; col < tuple.getNrColumns(); col += 2)
			{
				String al1 = tuple.getString(col);
				String al2 = tuple.getString(col + 1);
				if (al1 == null) throw new Exception(Helper.errorMsg(
						line_number, col));
				if (al2 == null) throw new Exception(Helper.errorMsg(
						line_number, col + 1));
				Biallele biallele = new Biallele(al1, al2);
				bialleles.add(biallele);
			}

			for (int objIndex = 0; objIndex < 4; objIndex++)
			{
				if (tuple.getObject(objIndex) == null) throw new Exception(
						Helper.errorMsg(line_number, objIndex));
			}
			TpedEntry te = new TpedEntry(tuple.getString(0),
					tuple.getString(1), tuple.getDouble(2), tuple.getLong(3),
					bialleles);
			result.add(te);
		}

		return result;
	}
}
