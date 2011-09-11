package org.molgenis.util.plink.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.Biallele;
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
			this.nrOfElements = reader.getNumberOfLines()
					- reader.getAmountOfNewlinesAtFileEnd();
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
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
					throws Exception
			{

				List<Biallele> bialleles = new ArrayList<Biallele>();

				for (int col = 6; col < tuple.getNrColumns(); col += 2)
				{
					String al1 = tuple.getString(col);
					String al2 = tuple.getString(col+1);
					if (al1 == null) throw new Exception(Helper.errorMsg(line_number,col));
					if (al2 == null) throw new Exception(Helper.errorMsg(line_number,col+1));
					Biallele biallele = new Biallele(al1, al2);
					bialleles.add(biallele);				
				}
				
				for(int objIndex = 0; objIndex < 6; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number,objIndex));
				}
				PedEntry pe = new PedEntry(tuple.getString(0),
						tuple.getString(1), tuple.getString(2), tuple.getString(3),
						tuple.getInt(4).byteValue(), tuple.getDouble(5),
						bialleles);
				result.add(pe);

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
					throws Exception
			{
				if (line_number - 1 >= from && line_number - 1 < to)
				{
					List<Biallele> bialleles = new ArrayList<Biallele>();
					
					for (int col = 6; col < tuple.getNrColumns(); col += 2)
					{
						String al1 = tuple.getString(col);
						String al2 = tuple.getString(col+1);
						if (al1 == null) throw new Exception(Helper.errorMsg(line_number,col));
						if (al2 == null) throw new Exception(Helper.errorMsg(line_number,col+1));
						Biallele biallele = new Biallele(al1, al2);
						bialleles.add(biallele);			
					}
					
					for(int objIndex = 0; objIndex < 6; objIndex++)
					{
						if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number,objIndex));
					}
					PedEntry pe = new PedEntry(tuple.getString(0),
							tuple.getString(1), tuple.getString(2), tuple.getString(3),
							tuple.getInt(4).byteValue(), tuple.getDouble(5),
							bialleles);
					result.add(pe);
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
