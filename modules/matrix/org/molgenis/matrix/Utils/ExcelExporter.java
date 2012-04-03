package org.molgenis.matrix.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.hibernate.ScrollableResults;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.lifelinesresearchportal.models.MatrixModel;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class ExcelExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> 
	extends AbstractExporter<R, C, V>
{
	private final WritableCellFormat d_headerFormat;
	private final WritableCellFormat d_cellFormat;
	private WorkbookSettings d_ws;

	private final WritableWorkbook workbook;
	private final WritableSheet sheet;
	
	public ExcelExporter(MatrixModel<R, C, V> matrix, OutputStream os) throws WriteException, IOException {
		super(matrix, os);		
		d_headerFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
		d_headerFormat.setWrap(false);
		d_cellFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD));
		d_cellFormat.setWrap(false);
		
		d_ws = new WorkbookSettings();
		d_ws.setLocale(new Locale("en", "EN"));
		
		workbook = Workbook.createWorkbook(os, d_ws);
		sheet = workbook.createSheet("Sheet1", 0);
	}

	public void export(boolean exportVisibleRows) throws MatrixException {
		try {
			// Write headers
			int colIdx = 0;
			for (C colHeader : (List<C>)matrix.getColHeaders())
			{
				Label f = new Label(colIdx, 0, colHeader.getName(), d_headerFormat);
				sheet.addCell(f);
				++colIdx;
			}
			
			retrieveDataAndWriteExcelSheet(sheet, exportVisibleRows);	
	
			workbook.write();
			workbook.close();
			
			os.flush();
			os.close();			
		} catch (Exception ex) {
			throw new MatrixException(ex);
		}
	}

	private void retrieveDataAndWriteExcelSheet(WritableSheet s, boolean exportVisibleRows) throws DatabaseException {
		ScrollableResults sr = null;
		try {
			sr = matrix.getScrollableValues(exportVisibleRows);
			// FIXME : hack because an extra column is added *only* when offset is not 0 
			// (probably also database dependent, ie oracle/mysql)
			writeResults(sr, exportVisibleRows && matrix.getRowOffset() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		} finally {
			sr.close();
		}
	}


	@Override
	public void writeSingleCell(Object cellData, int iRow, int iColumn, ColumnType columnType) {  
		// excel switches rows/columns; redundant locals preserved for clarity
		int row = iColumn;
		int column = iRow + 1;
					   	
		String dataCell = cellData != null ? cellData.toString() : null;
		try { 
			sheet.addCell(createCellValue(columnType, row, column, dataCell));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private WritableCell createCellValue(ColumnType columnType, int row, int column, String dataCell)
			throws ParseException {
		if (dataCell == null) {
			return new Blank(row, column);
		} else {
			if(columnType == ColumnType.String) {
				return new Label(row, column, dataCell);
			} else if(columnType == ColumnType.Integer) {
				return new jxl.write.Number(row, column, Double.parseDouble(dataCell), new WritableCellFormat (jxl.write.NumberFormats.INTEGER)); 					
			} else if(columnType == ColumnType.Code) {
				return new jxl.write.Label(row, column, dataCell.toString());
			} else if(columnType == ColumnType.Decimal) {
				return new jxl.write.Number(row, column, Double.parseDouble(dataCell), new WritableCellFormat (jxl.write.NumberFormats.FLOAT));
			} else if(columnType == ColumnType.Timestamp || columnType == ColumnType.Datetime) {
				return writeDateCell(row, column, dataCell, "y-M-d H:m:s", "yyyy MM dd hh:mm:ss");
			} else if(columnType == ColumnType.Date) {				
				return writeDateCell(row, column, dataCell, "y-M-d", "yyyy MM dd");
			} else {
				throw new UnsupportedOperationException(String.format("Type %s not available",columnType));
			}
		}
	}

	private WritableCell writeDateCell(int row, int column, String dataCell, String inFormatStr, String outFormatStr)
			throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(inFormatStr);
		
		jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat (outFormatStr); 
		jxl.write.WritableCellFormat excelDateFormat = new jxl.write.WritableCellFormat (customDateFormat); 
		
		return new jxl.write.DateTime(row, column, dateFormat.parse(dataCell), excelDateFormat);
	}

	@Override
	public String getFileExtension()
	{
		return ".xls";
	}
	
	@Override
	public String getMimeType()
	{
		return "application/excel";
	}
}
