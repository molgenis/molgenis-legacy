package org.molgenis.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.log4j.Logger;

public abstract class AbstractTupleReader implements TupleReader
{

	/** for log messages */
	protected static final Logger logger = Logger.getLogger(CsvFileReader.class.getSimpleName());
	/**
	 * a matching String that indicates where the Csv starts; empty means first
	 * line
	 */
	protected String blockStart = "";
	/**
	 * a matching String that inidicates where the Csv is terminated; empty
	 * means last line
	 */
	protected String blockEnd = "";
	/** a string that translates to a null value when parsed */
	protected String missingValueIndicator = "";
	/** cache of the column names, may have duplicates */
	protected List<String> columnnames;
	/** guessed separator */
	protected char separator = 0;
	/** boolean indicating that the resource parsed has headers... */
	protected boolean hasHeader = true;

	@Override
	public String getBlockEnd()
	{
		return blockEnd;
	}

	@Override
	public void setBlockEnd(String blockEnd)
	{
		this.blockEnd = blockEnd;
	}

	@Override
	public String getBlockStart()
	{
		return blockStart;
	}

	@Override
	public void setBlockStart(String blockStart)
	{
		this.blockStart = blockStart;
	}

	@Override
	public void setMissingValues(String missingValue)
	{
		this.missingValueIndicator = missingValue;
	}

	@Override
	public String getMissingValues()
	{
		return this.missingValueIndicator;
	}

	@Override
	public void setColnames(List<String> fields)
	{
		this.columnnames = fields;
	}

	@Override
	public void renameField(String from, String to) throws Exception
	{
		if ((from != null && from.equals(to)) || (from == null && to == null)) return;

		List<String> colNames = this.colnames();
		for (int i = 0; i < colNames.size(); ++i)
		{
			String colName = colNames.get(i);
			if ((colName != null && colName.equals(from)) || (colName == null && from == null))
			{
				colNames.set(i, to);
			}
		}
		this.setColnames(colNames);

	}

	@Override
	public List<String> rownames() throws IOException, DataFormatException
	{
		List<String> rownames = new ArrayList<String>();
		for (Tuple t : this)
		{
			if (!t.isNull(0)) rownames.add(t.getString(0));
			else
				rownames.add(null);
		}
		this.reset();
		return rownames;
	}
}
