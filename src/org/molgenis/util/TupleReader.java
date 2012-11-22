package org.molgenis.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

public interface TupleReader extends TupleIterable, Closeable
{
	/** Iterate through available tuples */
	@Override
	public Iterator<Tuple> iterator();

	/**
	 * The values of the first row.
	 * 
	 * @return columnnames list as long as each datarow.
	 * @throws Exception
	 */
	public List<String> colnames() throws Exception;

	/**
	 * Sets the reader to parse from the line that starts with the string
	 * indicated by blockStart.
	 * 
	 * @param blockStart
	 *            the line after which the reading must start. Default "": parse
	 *            from first line
	 */
	public void setBlockStart(String blockStart);

	/**
	 * Set the missing value indicator. For example, 'NA'. If encountered during
	 * parsing these will be translated into null.
	 * 
	 * @param missingValue
	 */
	public void setMissingValues(String missingValue);

	/**
	 * Get the String that is used to indicate missing values. For example 'NA'
	 * or empty String ''. Default: ""
	 * 
	 * @return missingValue indicator.
	 */
	public String getMissingValues();

	/**
	 * Get the pattern that this reader uses as endpoint to parse until.
	 * 
	 * @return String used to identify the block end.
	 */
	public String getBlockEnd();

	/**
	 * Sets the reader to parse until it reads a line that starts with the
	 * string indicated by blockEnd.
	 * 
	 * @param blockEnd
	 *            the line before which the reading should end. Default "":
	 *            parse until first empty line.
	 */
	public void setBlockEnd(String blockEnd);

	/**
	 * Get the pattern that this reader uses as starting point to parse from.
	 * 
	 * @return String used to identify the block start.
	 */
	public String getBlockStart();

	/**
	 * Override the column names with user provided ones. E.g. when first line
	 * is not a header
	 */
	public void setColnames(List<String> fields);

	/**
	 * Rename the column after parsing.
	 * 
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public void renameField(String from, String to) throws Exception;

	/**
	 * Get first column values except first row, aka rownames
	 * 
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public List<String> rownames() throws IOException, DataFormatException;

	/**
	 * reset iterator
	 * 
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public void reset() throws IOException;

	/** ask whether the source of the reader is closed **/
	public boolean isClosed();
}