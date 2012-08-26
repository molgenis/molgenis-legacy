package org.molgenis.datatable.view.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class ExcelExporter extends AbstractExporter {

    private final Workbook wb;
    private final Sheet sh;
    private final CreationHelper creationHelper;
    private final CellStyle dateCellStyle;
    private final CellStyle dateTimeCellStyle;

    public ExcelExporter(TupleTable tableModel) {
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
    public void export(OutputStream os) throws TableException {
        createHeaders();

        int rowIdx = 1; // headers are row 0
        for (Tuple row : table) {
            writeRow(row, rowIdx++);
        }
        try {
            wb.write(os);
        } catch (IOException e) {
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

    private void writeRow(Tuple row, int rowIdx) throws TableException {
        final Row excelRow = sh.createRow(rowIdx);
        int colIdx = 0;
        final List<Field> columns = table.getColumns();
		for (final Field column : columns) {
            final Cell c = excelRow.createCell(colIdx++);
            writeTypedCell(c, column, row);
        }
    }

    private void writeTypedCell(Cell c, Field column, Tuple row) throws TableException {
        try {
            String columnName = column.getSqlName();
            switch (column.getType().getEnumType()) {
                case RICHTEXT:
                case CHAR:
                case TEXT:
                case STRING:
                    c.setCellValue(row.getString(columnName));
                    break;
                case INT:
                case CATEGORICAL: // TODO : check the crap out of this
                case LONG:
                    if (row.getInt(columnName) != null) {
                        c.setCellValue(row.getInt(columnName));
                    }
                    break;
                case DATE:
                    if (row.getDate(columnName) != null) {
                        c.setCellStyle(dateCellStyle);
                        c.setCellValue(row.getDate(columnName));
                    }
                    break;
                case DATE_TIME:
                    if (row.getDate(columnName) != null) {
                        c.setCellStyle(dateTimeCellStyle);
                        c.setCellValue(row.getDate(columnName));
                    }
                    break;
                case DECIMAL:
                    if (row.getDouble(columnName) != null) {
                        c.setCellValue(row.getDouble(columnName));
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Type %s not available", column.getType()));
            }
        } catch (Exception e) {
            throw new TableException(e);
        }
    }
}
