package org.molgenis.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface TupleWriter extends Closeable
{

	/**
	 * Write the header.
	 * 
	 * @throws Exception
	 */
	public void writeHeader() throws IOException;

	/**
	 * Write a row to stream.
	 * 
	 * @param e
	 *            Entity to be written.
	 * @throws Exception
	 */
	public void writeRow(Entity e) throws IOException;

	/**
	 * Write a row to stream.
	 * 
	 * @param t
	 *            Tuple to be written.
	 * @throws IOException
	 */
	public void writeRow(Tuple t) throws IOException;

	public void writeValue(Object object) throws IOException;

	public void setHeaders(List<String> fields);

	public void writeEndOfLine() throws IOException;

	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements) throws IOException;

}