package org.molgenis.util;

public interface TupleReaderListener
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