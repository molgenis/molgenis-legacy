package org.molgenis.datatable.view;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class ExcelExporter extends AbstractExporter
{
    private final Workbook wb;
    private final Sheet sh;
    private final CreationHelper creationHelper;
    private final CellStyle dateCellStyle;
    private final CellStyle dateTimeCellStyle;
    
	public ExcelExporter(TupleTable tableModel)
	{
		super(tableModel);
		wb = new SXSSFWorkbook(100); // param indicates streaming buffer size
		sh = wb.createSheet();
		
        //create cell styles, required for proper date output
        creationHelper = wb.getCreationHelper();

        dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy/mm/dd"));

        dateTimeCellStyle = wb.createCellStyle();
        dateTimeCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy/m/d h:mm"));
	}

	@Override
	public void export(OutputStream os) throws TableException
	{
		createHeaders();

		int rowIdx = 1; // headers are row 0
		for(Tuple row : table) {
			writeRow(row, rowIdx++);
		}
		try
		{
			wb.write(os);
		}
		catch (IOException e)
		{
			throw new TableException(e);
		}
	}

	private void createHeaders() throws TableException {
        final Header header = sh.getHeader();
        header.setCenter("Center Header");
        header.setLeft("Left Header");
        header.setRight(HSSFHeader.font("Stencil-Normal", "Italic")
                + HSSFHeader.fontSize((short) 16) + "Right w/ Stencil-Normal Italic font and size 16");

        final Row headerRow = sh.createRow(0);
        int colIdx = 0;
        for (Field column : table.getColumns()) {
            final Cell headerCell = headerRow.createCell(colIdx++);
            headerCell.setCellValue(column.getName());
        }
    }

	
    private void writeRow(Tuple row, int rowIdx) throws TableException
	{
    	Row excelRow = sh.createRow(rowIdx);
        int colIdx = 0;
        for (Field column : table.getColumns()) {
        	Cell c = excelRow.createCell(colIdx++);
        	writeTypedCell(c, column, row);
        }
	}

	private void writeTypedCell(Cell c, Field column, Tuple row) throws TableException
	{
		try {
			String columnName = column.getName();
			switch(column.getType().getEnumType()) {
				case RICHTEXT:
				case CHAR:
				case TEXT:
				case STRING :
					c.setCellValue(row.getString(columnName));
					break;
				case INT:
				case CATEGORICAL: // TODO : check the crap out of this
				case LONG :
					c.setCellValue(row.getInt(columnName));
					break;
				case DATE:
					c.setCellStyle(dateCellStyle);
					c.setCellValue(row.getDate(columnName));
					break;
				case DATE_TIME:
					c.setCellStyle(dateTimeCellStyle);
					c.setCellValue(row.getDate(columnName));
					break;
				case DECIMAL:
					c.setCellValue(row.getDouble(columnName));
					break;
				default :
					throw new IllegalArgumentException(String.format("Type %s not available", column.getType()));
			}
		} catch(Exception e) {
			throw new TableException(e);
		}
	}
}
