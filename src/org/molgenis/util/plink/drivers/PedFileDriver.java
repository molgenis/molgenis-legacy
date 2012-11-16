package org.molgenis.util.plink.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class PedFileDriver extends AbstractFileDriver
{
	/**
	 * Construct a PedFileDriver on this file
	 * 
	 * @param bimFile
	 * @throws Exception
	 */
	public PedFileDriver(File pedFile) throws Exception
	{
		super(pedFile);
		validate();
	}

	/**
	 * Validates the ped file, for now it only checks if the file contains at
	 * least 6 columns
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception
	{
		reader.reset();

		Tuple tuple = reader.next();
		if (tuple.size() < 6)
		{
			throw new Exception("Incorrect ped file format. Ped file must contain at least 6 columns");
		}

		reader.reset();
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
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;
			List<Biallele> bialleles = new ArrayList<Biallele>();

			for (int col = 6; col < tuple.getNrColumns(); col += 2)
			{
				String al1 = tuple.getString(col);
				String al2 = tuple.getString(col + 1);
				if (al1 == null) throw new Exception(Helper.errorMsg(line_number, col));
				if (al2 == null) throw new Exception(Helper.errorMsg(line_number, col + 1));
				Biallele biallele = new Biallele(al1, al2);
				bialleles.add(biallele);
			}

			for (int objIndex = 0; objIndex < 6; objIndex++)
			{
				if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number, objIndex));
			}
			PedEntry pe = new PedEntry(tuple.getString(0), tuple.getString(1), tuple.getString(2), tuple.getString(3),
					tuple.getInt(4).byteValue(), tuple.getDouble(5), bialleles);
			result.add(pe);

		}

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
	public List<PedEntry> getEntries(final long from, final long to) throws Exception
	{
		reader.reset();
		final ArrayList<PedEntry> result = new ArrayList<PedEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;
			if (line_number - 1 >= from && line_number - 1 < to)
			{
				List<Biallele> bialleles = new ArrayList<Biallele>();

				for (int col = 6; col < tuple.getNrColumns(); col += 2)
				{
					String al1 = tuple.getString(col);
					String al2 = tuple.getString(col + 1);
					if (al1 == null) throw new Exception(Helper.errorMsg(line_number, col));
					if (al2 == null) throw new Exception(Helper.errorMsg(line_number, col + 1));
					Biallele biallele = new Biallele(al1, al2);
					bialleles.add(biallele);
				}

				for (int objIndex = 0; objIndex < 6; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number, objIndex));
				}
				PedEntry pe = new PedEntry(tuple.getString(0), tuple.getString(1), tuple.getString(2),
						tuple.getString(3), tuple.getInt(4).byteValue(), tuple.getDouble(5), bialleles);
				result.add(pe);
			}

			// Dirty optimazation
			if (line_number > to)
			{
				break;
			}
		}

		return result;
	}

	/**
	 * Close the underlying file reader
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException
	{
		this.reader.close();
	}

}
