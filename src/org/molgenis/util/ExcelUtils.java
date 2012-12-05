package org.molgenis.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelUtils
{
	public static boolean writeSheetToFile(Sheet sheet, File file, boolean skipEmptyColumns) throws IOException
	{
		List<String> headers = new ArrayList<String>();

		Row header = sheet.getRow(0); // assume headers are on first line
		if ((header == null) || (header.getPhysicalNumberOfCells() == 0))
		{
			return false;
		}

		ArrayList<Integer> namelessHeaderLocations = new ArrayList<Integer>(); // allow
																				// for
																				// empty
																				// columns,
																				// also
																				// column
																				// order
																				// does
																				// not
																				// matter

		for (int colIndex = 0; colIndex < header.getLastCellNum(); colIndex++)
		{
			Cell cell = header.getCell(colIndex);
			String value = cell == null ? "" : cell.getStringCellValue();

			if (StringUtils.isNotBlank(value) || !skipEmptyColumns)
			{
				headers.add(value);
			}
			else
			{
				headers.add("nameless" + colIndex);
				namelessHeaderLocations.add(colIndex);
			}

		}

		CsvWriter cw = new CsvWriter(new FileOutputStream(file), Charset.forName("UTF-8"), headers);
		try
		{
			cw.setMissingValue("");
			cw.writeHeader();

			Iterator<Row> rowIterator = sheet.rowIterator();
			if (rowIterator.hasNext())
			{
				rowIterator.next();
				while (rowIterator.hasNext())
				{
					Row row = rowIterator.next();
					Tuple t = new SimpleTuple();

					for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++)
					{
						if ((!namelessHeaderLocations.contains(colIndex) && colIndex < headers.size()))
						{
							Cell cell = row.getCell(colIndex);
							String value = "";

							if (cell != null)
							{
								cell.setCellType(Cell.CELL_TYPE_STRING);
								value = cell.getStringCellValue();
							}

							t.set(headers.get(colIndex), value);
						}
					}

					cw.writeRow(t);
				}

			}
		}
		finally
		{
			cw.close();
		}
		return true;
	}
}
