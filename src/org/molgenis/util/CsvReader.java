package org.molgenis.util;

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
 */
public interface CsvReader extends TupleReader
{
	public static final String ROWNAME_COLUMN = "_row_name";

	/**
	 * Default the reader guesses the separator. Use this method to set it
	 * explicitly
	 * 
	 * @param string
	 */
	public void setSeparator(char string);

	/**
	 * Disable the check of the header number.
	 * 
	 * @param b
	 */
	public void disableHeader(boolean b);
}