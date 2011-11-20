package org.molgenis.util;


/**
 * Implement a CsvReaderListener to handle lines in {@link org.molgenis.util.CsvReader}.
 * 
 * During {@link org.molgenis.util.CsvReader#parse(CsvReaderListener[])} these listeners can be
 * added to handle each line. Each line is passed as a {@link Tuple}.
 * Typically, these listeners are implemented as anonymous classes, e.g.
 * 
 * <pre>
 * CsvFileReader(aFile).parse(new CsvReaderListener()
 * {
 * 	public void handleLine( int line_number, Tuple tuple )
 * 	{
 * 		System.out.println(&quot;parsed line &quot; + line_number + &quot;: &quot; + tuple.toString());
 * 	}
 * });
 * </pre>
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public interface CsvReaderListener extends TupleReaderListener
{
	/**
	 * Strategy method to be implemented.
	 * 
	 * @param line_number
	 *        line number in the csv file
	 * @param tuple
	 *        containing the map of csv data
	 * @throws Exception
	 *         to throw any error to the CsvReader
	 */
	public void handleLine( int line_number, Tuple tuple ) throws Exception;
}
