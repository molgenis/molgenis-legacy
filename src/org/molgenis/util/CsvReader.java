package org.molgenis.util;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

 
/**
 * Read comma, tab, semi-colon or space separated values (csv/tab).
 * 
 * CsvReader automatically guesses the separator based on the header. Each line
 * is parsed into a Tuple that is then handled by one or more
 * {@link CsvReaderListener}. These listeners then can decide what to do with
 * each line (e.g., add to database). Optionally, the starting point and end
 * point of the parsing can be indicated using {@link #setBlockStart(String)}
 * and {@link #setBlockEnd(String)} (default: start on first line, ends on first
 * empty line).
 * 
 * 
 * Example:
 * <pre>
 * CsvFileReader(aFile).parse(new CsvReaderListener()
 * {
 * 	public void handleLine(int line_number, Tuple tuple)
 * 	{
 * 		System.out.println(&quot;parsed line &quot; + line_number + &quot;: &quot; + tuple.toString());
 * 	}
 * });
 * </pre>
 */
public interface CsvReader
{
	public static final String ROWNAME_COLUMN = "_row_name";

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
	 * Sets the reader to parse from the line that starts with the string
	 * indicated by blockStart.
	 * 
	 * @param blockStart
	 *            the line after which the reading must start. Default "": parse
	 *            from first line
	 */
	public abstract void setBlockStart(String blockStart);

	/**
	 * Parses the CsvFile and returns the values from the first column in the
	 * delimited file (ommits first row)
	 * 
	 * @throws IOException
	 */
	public abstract List<String> rownames() throws Exception;

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
	 * Each row of the file is parsed into a Tuple and then passed to
	 * {@link CsvReaderListener}s for processing. The delimiter (',','\t','
	 * ',';') is automatically guessed.
	 * 
	 * *
	 * 
	 * <pre>
	 * CsvFileReader(aFile).parse(new CsvReaderListener()
	 * {
	 * 	public void handleLine(int line_number, Tuple tuple)
	 * 	{
	 * 		System.out.println(&quot;parsed line &quot; + line_number + &quot;: &quot; + tuple.toString());
	 * 	}
	 * });
	 * </pre>
	 * 
	 * @param listeners
	 *            observervers of the parsing that can do something with it.
	 * @throws Exception
	 * 
	 * @see CsvReaderListener
	 */
	public abstract int parse(CsvReaderListener... listeners) throws Exception;

	/**
	 * Parse unto a maximum number of rows.
	 * 
	 * @param listeners
	 * @param noElements
	 *            maximum number of rows to parse
	 * @return number of elements parsed
	 * @throws Exception
	 */
	public abstract int parse(int noElements, CsvReaderListener... listeners) throws Exception;

	/**
	 * Reset the reader to start reading from scratch.
	 * 
	 * @throws IOException
	 */
	public abstract void reset() throws IOException;

	/**
	 * Close the reader.
	 * 
	 * @throws IOException
	 * 
	 */
	public abstract void close() throws IOException;

	/**
	 * Override the column names with user provided ones. E.g. when first line
	 * is not a header
	 */
	public abstract void setColnames(List<String> fields);

	/**
	 * Default the reader guesses the separator. Use this method to set it
	 * explicitly
	 * 
	 * @param string
	 */
	public abstract void setSeparator(char string);

	/**
	 * Rename the column after parsing.
	 * 
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public abstract void renameField(String from, String to) throws Exception;

	/**
	 * Disable the check of the header number.
	 * 
	 * @param b
	 */
	public abstract void disableHeader(boolean b);

	public int parse(List<Integer> rows, CsvReaderListener ... listeners) throws Exception;
}