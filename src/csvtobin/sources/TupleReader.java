package csvtobin.sources;

import java.io.IOException;
import java.util.List;

public interface TupleReader
{
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
	
	public int parse(List<Integer> rows, CsvReaderListener ... listeners) throws Exception;

	void disableHeader(boolean header);

	int parse(int noElements, List<Integer> rows, CsvReaderListener[] listeners)
			throws Exception;

}
