package org.molgenis.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelReader extends AbstractTupleReader implements TupleReader
{
	private File excelFile;
	private String sheetName;
	private Sheet sheet;

	public ExcelReader(File excelFile, String sheetName) throws IOException
	{
		this.excelFile = excelFile;
		this.sheetName = sheetName;

		Workbook workbook;
		try
		{
			workbook = Workbook.getWorkbook(excelFile);
		}
		catch (Exception e)
		{
			throw new IOException(e);
		}

		sheet = workbook.getSheet(sheetName);
		if(sheet == null) throw new IOException("cannot find sheet: "+sheetName);
	}

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> colnames() throws Exception
	{
		List<String> result = new ArrayList<String>();
		for (Cell cell : sheet.getRow(0))
			result.add(cell.getContents());
		return result;
	}

	@Override
	public void reset() throws IOException
	{

	}

	@Override
	public int parse(int noElements, List<Integer> rows,
			CsvReaderListener[] listeners) throws Exception
	{
		List<String> headers = null;

		if (hasHeader) headers = colnames();

		// String line;
		// if (this.separator == 0)
		// {
		// goToBlockStart(reader);
		// line = reader.readLine();
		// separator = guessSeparator(line);
		// reset();
		// // logger.info("PARSE guessed separator: '" + separator + "'");
		// }

		// on first call
		if (!isParsing)
		{
			// skip to start
			// goToBlockStart(reader);
			// if (hasHeader)
			// line = reader.readLine(); // skip header line
			this.isParsing = true;

			logger.debug("parsing Excel with headers =" + headers);
		}
		else
		{
			logger.debug("restarted parsing Excel with limit " + noElements);
		}

		// logger.debug("found: " + t.toString());
		int lineCount = 0;
		// int index;

		for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++)
		{
			// template of the tuple
			Tuple t;
			if (hasHeader) t = new SimpleTuple(headers);
			else
				t = new SimpleTuple();

			lineCount++;

			// parse the row into a tuple
			Cell[] cells = sheet.getRow(rowIndex);
			if (hasHeader && cells.length > headers.size())
			{
				throw new Exception("Row " + lineCount
						+ " has more columns than there are headers ("
						+ cells.length + ">" + headers.size()
						+ "). Put double \" around columns that have '"
						+ separator + "' in their value.");
			}

			// change MISSING_VALUES to null, trim values
			String[] values = new String[cells.length];
			for (int colIndex = 0; colIndex < cells.length; colIndex++)
			{
				String value = cells[colIndex].getContents();
				
				if (value.equals(this.getMissingValues()))
				{
					value = null;
				}
				values[colIndex] = value;
			}
			
			t.set(values);
			// logger.info("found: " + t.toString());

			// handle the tuple by all handlers
			for (CsvReaderListener listener : listeners)
			{

				try
				{
					listener.handleLine(lineCount, t);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					logger.error("parsing of row " + lineCount + " failed: "
							+ e);
					logger.error("parse error on line " + lineCount + ": "
							+ e.getMessage());
					throw e;
				}

			}
		}

		return lineCount;
	}

	@Override
	public List<String> rownames() throws Exception
	{
		List<String> result = new ArrayList<String>();
		for (Cell cell : sheet.getColumn(0))
			result.add(cell.getContents());
		return result;
	}

}
