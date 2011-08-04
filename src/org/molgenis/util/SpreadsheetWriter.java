package org.molgenis.util;

import java.util.List;

import jxl.write.WritableSheet;

public interface SpreadsheetWriter
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
	 * Write a row to an Excel sheet.
	 * TODO: keep or remove as it is (too) implementation-specific?
	 * 
	 * @param e
	 * @param sheet
	 */
	void writeRow(Entity e, WritableSheet sheet);
	
	/**
	 * Write a row to an Excel sheet.
	 * TODO: keep or remove as it is (too) implementation-specific?
	 * 
	 * @param e
	 * @param sheet
	 */
	void writeRow(Tuple t, WritableSheet sheet);

	public abstract void writeValue(Object object);

	public abstract void setHeaders(List<String> fields);

	public abstract void writeEndOfLine();

	public abstract void close();

	public abstract void writeMatrix(List<String> rowNames,
			List<String> colNames, Object[][] elements);

	void writeHeader(WritableSheet excelSheet);

}