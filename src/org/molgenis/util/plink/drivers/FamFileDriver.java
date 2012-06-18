package org.molgenis.util.plink.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.FamEntry;

/**
 * Driver to query FAM files. FAM files annotate the families of BED files. See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml
 * 
 * Content of a FAM file: Family ID Individual ID Paternal ID Maternal ID Sex
 * (1=male; 2=female; other=unknown) Phenotype
 */
public class FamFileDriver extends AbstractFileDriver
{
	/**
	 * Construct a FamFileDriver on this file
	 * 
	 * @param bimFile
	 * @throws Exception
	 */
	public FamFileDriver(File famFile) throws Exception
	{
		super(famFile);
	}

	/**
	 * Get all FAM file entries
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<FamEntry> getAllEntries() throws Exception
	{
		final ArrayList<FamEntry> result = new ArrayList<FamEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;
			for (int objIndex = 0; objIndex < 6; objIndex++)
			{
				if (tuple.getObject(objIndex) == null) throw new Exception(
						Helper.errorMsg(line_number, objIndex));
			}
			FamEntry fe = new FamEntry(tuple.getString(0), tuple.getString(1),
					tuple.getString(2), tuple.getString(3), tuple.getInt(4)
							.byteValue(), tuple.getDouble(5));
			result.add(fe);
		}
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

		final ArrayList<FamEntry> result = new ArrayList<FamEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;

			if (line_number - 1 >= from && line_number - 1 < to)
			{
				for (int objIndex = 0; objIndex < 6; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(
							Helper.errorMsg(line_number, objIndex));
				}
				FamEntry fe = new FamEntry(tuple.getString(0),
						tuple.getString(1), tuple.getString(2),
						tuple.getString(3), tuple.getInt(4).byteValue(),
						tuple.getDouble(5));
				result.add(fe);
			}
		}
		return result;
	}
}
