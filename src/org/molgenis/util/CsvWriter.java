package org.molgenis.util;

import java.util.List;

public interface CsvWriter
{

	/**
	 * Write the header.
	 */
	public abstract void writeHeader();

	/**
	 * Write a row to stream.
	 * 
	 * @param e
	 *            Entity to be written.
	 */
	public abstract void writeRow(Entity e);

	/**
	 * Write a row to stream.
	 * 
	 * @param t
	 *            Tuple to be written.
	 */
	public abstract void writeRow(Tuple t);

	/**
	 * Write a row to stream
	 * 
	 * @param values
	 */
	// public void writeRow(Object[] values)
	// {
	// // FIXME: this is probably unnecessarily slow
	// for (int i = 0; i < values.length; i++)
	// {
	// if(i > 0) writer.print(separator);
	// writeValue(values[i], writer);
	// }
	// writer.println();
	// if (count++ % 10000 == 0) logger.debug("wrote values array to line " +
	// count + " ");
	// }

	public abstract void writeValue(Object object);

	public abstract void setHeaders(List<String> fields);

	public abstract void writeEndOfLine();

	public abstract void close();

	public abstract void writeMatrix(List<String> rowNames,
			List<String> colNames, Object[][] elements);

}