package org.molgenis.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

public interface TupleReader extends Iterable<Tuple>
{
	/** Iterate through available tuples */
	public abstract Iterator<Tuple> iterator();
	
	/**
	 * The values of the first row.
	 * 
	 * @return columnnames list as long as each datarow. If necessary an
	 *         artificial first column is added named
	 *         {@link CsvFileReader#ROWNAME_COLUMN}
	 * @throws Exception
	 */
	public abstract List<String> colnames() throws Exception;
	
	/**
	 * Close the reader.
	 * 
	 * @throws IOException
	 * 
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Sets the reader to parse from the line that starts with the string
	 * indicated by blockStart.
	 * 
	 * @param blockStart
	 *            the line after which the reading must start. Default "": parse
	 *            from first line
	 */
	public abstract void setBlockStart(String blockStart);
	
	/**
	 * Set the missing value indicator. For example, 'NA'. If encountered during
	 * parsing these will be translated into null.
	 * 
	 * @param missingValue
	 */
	public abstract void setMissingValues(String missingValue);

	/**
	 * Get the String that is used to indicate missing values. For example 'NA'
	 * or empty String ''. Default: ""
	 * 
	 * @return missingValue indicator.
	 */
	public abstract String getMissingValues();

	/**
	 * Get the pattern that this reader uses as endpoint to parse until.
	 * 
	 * @return String used to identify the block end.
	 */
	public abstract String getBlockEnd();

	/**
	 * Sets the reader to parse until it reads a line that starts with the
	 * string indicated by blockEnd.
	 * 
	 * @param blockEnd
	 *            the line before which the reading should end. Default "":
	 *            parse until first empty line.
	 */
	public abstract void setBlockEnd(String blockEnd);

	/**
	 * Get the pattern that this reader uses as starting point to parse from.
	 * 
	 * @return String used to identify the block start.
	 */
	public abstract String getBlockStart();

	/**
	 * Override the column names with user provided ones. E.g. when first line
	 * is not a header
	 */
	public abstract void setColnames(List<String> fields);
	
	/**
	 * Rename the column after parsing.
	 * 
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public abstract void renameField(String from, String to) throws Exception;
	
	/** Get first column values except first row, aka rownames 
	 * @throws DataFormatException 
	 * @throws IOException */
	public abstract List<String> rownames() throws IOException, DataFormatException;

	/** reset iterator
	 * @throws DataFormatException 
	 * @throws IOException */
	public void reset() throws IOException, DataFormatException;
}
