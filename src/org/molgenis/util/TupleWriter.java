package org.molgenis.util;

import java.util.List;

public interface TupleWriter
{

	/**
	 * Write the header.
	 * 
	 * @throws Exception
	 */
	public void writeHeader() throws Exception;

	/**
	 * Write a row to stream.
	 * 
	 * @param e
	 *            Entity to be written.
	 * @throws Exception
	 */
	public void writeRow(Entity e) throws Exception;

	/**
	 * Write a row to stream.
	 * 
	 * @param t
	 *            Tuple to be written.
	 */
	public void writeRow(Tuple t);

	public void writeValue(Object object);

	public void setHeaders(List<String> fields);

	public void writeEndOfLine();

	/**
	 * Finish up and close the exported file. For example, close the workbook
	 * when writing to Excel, or the PrintWriter when writing to CSV. Could
	 * close the wrapped OutputStream. The returned OutputStream is closed after
	 * any download in AbstractMolgenisServlet.
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception;

	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements);

}