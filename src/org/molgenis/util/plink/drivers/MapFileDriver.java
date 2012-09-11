package org.molgenis.util.plink.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.MapEntry;

/**
 * Driver to query MAP files. By default, each line of the MAP file describes a
 * single marker and must contain exactly 4 columns. Content of a MAP file:
 * chromosome, SNP, cM, base-position. See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#map
 */
public class MapFileDriver extends AbstractFileDriver
{

	/**
	 * Construct a MapFileDriver on this file
	 * 
	 * @param mapFile
	 * @throws Exception
	 */
	public MapFileDriver(File mapFile) throws Exception
	{
		super(mapFile);
		validate();
	}

	/**
	 * Validates the map file. For now it only validates if the file has exactly
	 * 4 columns
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception
	{
		reader.reset();

		Tuple tuple = reader.next();
		if (tuple.size() != 4)
		{
			throw new Exception("Incorrect map file format. Map file must contain 4 columns");
		}

		reader.reset();
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
	public List<MapEntry> getEntries(final long from, final long to) throws Exception
	{
		reader.reset();

		final ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;

			if (line_number - 1 >= from && line_number - 1 < to)
			{
				for (int objIndex = 0; objIndex < 4; objIndex++)
				{
					if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number, objIndex));
				}
				MapEntry me = new MapEntry(tuple.getString(0), tuple.getString(1), tuple.getDouble(2), tuple.getLong(3));
				result.add(me);
			}
		}

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
		int line_number = 0;
		for (Tuple tuple : reader)
		{
			line_number++;
			for (int objIndex = 0; objIndex < 4; objIndex++)
			{
				if (tuple.getObject(objIndex) == null) throw new Exception(Helper.errorMsg(line_number, objIndex));
			}
			MapEntry me = new MapEntry(tuple.getString(0), tuple.getString(1), tuple.getDouble(2), tuple.getLong(3));
			result.add(me);
		}

		return result;
	}

}
