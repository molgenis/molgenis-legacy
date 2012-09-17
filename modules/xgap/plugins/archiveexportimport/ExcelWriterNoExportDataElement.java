package plugins.archiveexportimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

import app.ExcelExport;

public class ExcelWriterNoExportDataElement extends ExcelExport
{
	/**
	 * Override to not export DataElements
	 */
	@Override
	public void copyCsvToWorkbook(String sheetName, File file,
			WritableWorkbook workbook, WritableCellFormat headerFormat,
			WritableCellFormat cellFormat) throws Exception
	{
		if (!sheetName.equals("DecimalDataElement")
				&& !sheetName.equals("TextDataElement") && file.exists())
		{
			// Create sheet
			WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);

			// Parse CSV file to tuples TODO: batch this
			final List<Tuple> tuples = new ArrayList<Tuple>();
			for (Tuple tuple : new CsvFileReader(file))
			{

				tuples.add(tuple);
			}

			// Add and store headers
			List<String> tupleFields = new ArrayList<String>();
			for (int i = 0; i < tuples.get(0).getFields().size(); i++)
			{
				tupleFields.add(tuples.get(0).getFields().get(i));
				Label l = new Label(i, 0, tuples.get(0).getFields().get(i),
						headerFormat);
				sheet.addCell(l);
			}

			// Add cells
			int rowIndex = 1;
			for (Tuple t : tuples)
			{
				for (int i = 0; i < tupleFields.size(); i++)
				{
					if (!(t.getObject(tupleFields.get(i)) == null))
					{
						Label l = new Label(i, rowIndex, t.getObject(
								tupleFields.get(i)).toString(), cellFormat);
						sheet.addCell(l);
					}
					else
					{
						sheet.addCell(new Label(i, rowIndex, "", cellFormat));
					}

				}
				rowIndex++;
			}
			sheetIndex++;
		}
	}

}
