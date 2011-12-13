package tritoplinkslice.sources;

import java.util.List;

import csvtobin.sources.Tuple;

public interface TupleWriter
{

	/**
	 * Write the header.
	 * @throws Exception 
	 */
	public abstract void writeHeader() throws Exception;

	/**
	 * Write a row to stream.
	 * 
	 * @param t
	 *            Tuple to be written.
	 */
	public abstract void writeRow(Tuple t);

	public abstract void writeValue(Object object);

	public abstract void setHeaders(List<String> fields);

	public abstract void writeEndOfLine();

	/**
	 * Finish up and close the exported file. For example, close the workbook
	 * when writing to Excel, or the PrintWriter when writing to CSV. Could
	 * close the wrapped OutputStream. The returned OutputStream is closed after
	 * any download in AbstractMolgenisServlet.
	 * 
	 * @throws Exception
	 */
	public abstract void close() throws Exception;

	public abstract void writeMatrix(List<String> rowNames,
			List<String> colNames, Object[][] elements);

}