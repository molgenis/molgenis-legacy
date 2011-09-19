package org.molgenis.util;

import java.util.List;

import org.apache.log4j.Logger;


public abstract class AbstractTupleReader implements TupleReader
{

	/** for log messages */
	protected static final transient Logger logger = Logger.getLogger(CsvFileReader.class.getSimpleName());
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
	/** boolean indicating the parser is working */
	protected boolean isParsing = false;
	/** booleain indicating that the resource parsed has headers... */
	protected boolean hasHeader = true;

	public String getBlockEnd()
	{
		return blockEnd;
	}

	public void setBlockEnd(String blockEnd)
	{
		this.blockEnd = blockEnd;
	}

	public String getBlockStart()
	{
		return blockStart;
	}

	public void setBlockStart(String blockStart)
	{
		this.blockStart = blockStart;
	}

	public void setMissingValues(String missingValue)
	{
		this.missingValueIndicator = missingValue;
	}

	public String getMissingValues()
	{
		return this.missingValueIndicator;
	}

	@Override
	public void setColnames(List<String> fields)
	{
		this.columnnames = fields;
	}

	public void renameField(String from, String to) throws Exception
	{
		List<String> colnames = this.colnames();
		if (colnames.contains(from))
			colnames.set(colnames.indexOf(from), to);
		else
		{
			logger.warn("renameField(" + from + "," + to + ") failed. Known columns are: " + colnames);
		}
		this.setColnames(colnames);
	
	}

	@Override
	public void disableHeader(boolean header)
	{
		this.hasHeader = header;
	
	}

	public int parse(int noElements, CsvReaderListener... listeners) throws Exception
	{
		return this.parse(noElements, null, listeners);
	}

	@Override
	public int parse(CsvReaderListener... listeners) throws Exception
	{
		return this.parse(Integer.MAX_VALUE, listeners);
	}

	@Override
	public int parse(List<Integer> rows, CsvReaderListener... listeners) throws Exception
	{
		return this.parse(Integer.MAX_VALUE, rows, listeners);
	}
}
