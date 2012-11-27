package org.molgenis.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Write values to an Excel file
 */
public class XlsWriter implements TupleWriter
{
	private WritableWorkbook workbook;
	private WritableSheet sheet;
	private WritableFont headerFont;
	private WritableCellFormat headerFormat;
	private WritableFont cellFont;
	private WritableCellFormat cellFormat;
	private List<String> headers = new ArrayList<String>();

	// need to keep track of the rownumber we're writing in!
	public int rowIndex = 1;

	public XlsWriter(OutputStream writer, List<String> headers) throws WriteException, IOException
	{
		this(writer);
		this.headers = headers;
	}

	/**
	 * Construct an Excel writer using a PrintWriter. This is the constructor
	 * that is used by db.find().
	 * 
	 * @param writer
	 * @throws IOException
	 * @throws WriteException
	 */
	public XlsWriter(OutputStream outputStream) throws IOException, WriteException
	{

		// Create workbook around the output stream
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		this.workbook = Workbook.createWorkbook(outputStream, ws);

		// Format the fonts
		this.headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		this.headerFormat = new WritableCellFormat(headerFont);
		this.headerFormat.setWrap(false);
		this.cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		this.cellFormat = new WritableCellFormat(cellFont);
		this.cellFormat.setWrap(false);

		// Create a sheet to write in
		// TODO: give sheet the name of the entity somehow!!
		String sheetName = "untitled";
		int sheetIndex = 0;
		sheet = workbook.createSheet(sheetName, sheetIndex);
	}

	@Override
	public void writeHeader() throws IOException
	{
		// Add and store headers
		try
		{
			for (int i = 0; i < headers.size(); i++)
			{
				String header = headers.get(i);
				Label l = new Label(i, 0, header, headerFormat);
				sheet.addCell(l);
			}
		}
		catch (RowsExceededException e)
		{
			throw new IOException(e);
		}
		catch (WriteException e)
		{
			throw new IOException(e);
		}
	}

	public void writeCell(int col, int row, String value) throws IOException
	{
		try
		{
			sheet.addCell(new Label(col, row, value));
		}
		catch (RowsExceededException e)
		{
			throw new IOException(e);
		}
		catch (WriteException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void setHeaders(List<String> headers)
	{
		this.headers = headers;
	}

	@Override
	public void writeEndOfLine()
	{
		// go to next row

	}

	@Override
	public void close() throws IOException
	{
		// close the excel file; no writing allowed anymore
		workbook.write();
		try
		{
			workbook.close();
		}
		catch (WriteException e)
		{
			throw new IOException(e);
		}
	}

	/**
	 * Write out an XGAP matrix. The inputs can be retrieved from any
	 * implementation of the XGAP matrix interface class.
	 * 
	 * @param rowNames
	 * @param colNames
	 * @param elements
	 */
	@Override
	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements)
	{

	}

	@Override
	public void writeRow(Entity e) throws IOException
	{
		for (int i = 0; i < headers.size(); i++)
		{
			String contents;
			Object fieldValue = e.get(headers.get(i));
			if (fieldValue == null) contents = "";
			else if (fieldValue instanceof List<?>) contents = ListEscapeUtils.toString((List<?>) fieldValue);
			else
				contents = fieldValue.toString();

			Label l = new Label(i, rowIndex, contents, cellFormat);
			try
			{
				sheet.addCell(l);
			}
			catch (RowsExceededException e1)
			{
				throw new IOException(e1);
			}
			catch (WriteException e1)
			{
				throw new IOException(e1);
			}
		}
		rowIndex++;
	}

	@Override
	public void writeRow(Tuple t)
	{

	}

	@Override
	public void writeValue(Object object)
	{

	}

}
