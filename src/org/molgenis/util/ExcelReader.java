package org.molgenis.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class ExcelReader extends AbstractTupleReader implements TupleReader, TupleIterable
{
	private Sheet sheet;
	private int rowIndex = 1;
	boolean hasData = true;

	public ExcelReader(File excelFile, String sheetName) throws IOException
	{
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
		if (sheet == null) throw new IOException("cannot find sheet: "
				+ sheetName);
	}

	@Override
	public List<String> colnames()
	{
		List<String> result = new ArrayList<String>();
		for (Cell cell : sheet.getRow(0))
			result.add(cell.getContents());
		return result;
	}
	
	private int lineCount = 0;

	@Override
	public Tuple next()
	{
		List<String> headers = null;

		if (hasHeader) headers = colnames();

		// logger.debug("found: " + t.toString());

		// int index;

		if (rowIndex < sheet.getRows() && hasData)
		{
			// template of the tuple
			Tuple t;
			if (hasHeader) t = new SimpleTuple(headers);
			else
				t = new SimpleTuple();

			lineCount++;

			// parse the row into a tuple
			Cell[] cells = sheet.getRow(rowIndex);

			// change MISSING_VALUES to null, trim values
			String[] values = new String[cells.length];
			boolean allNull = true;
			for (int colIndex = 0; colIndex < cells.length; colIndex++)
			{
				String value = cells[colIndex].getContents();
				if (value != null)
				{
					allNull = false;
				}

				if (value.equals(this.getMissingValues()))
				{
					value = null;
				}
				values[colIndex] = value;
			}

			if (allNull)
			{
				hasData = false;
			}
			else
			{
				rowIndex++;

				t.set(values);
				
				return t;
			}
		}

		return null;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new TupleIterator(this);
	}

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset()
	{
		//nothing needed
	}
}
