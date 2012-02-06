package org.molgenis.matrix.Utils;

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
import jxl.write.biff.RowsExceededException;

import org.hibernate.ScrollableResults;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class ExcelExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> implements Exporter<R, C, V> {

	private final SliceablePhenoMatrixMV<R, C, V> d_matrix;
	private final WritableCellFormat d_headerFormat;
	private final WritableCellFormat d_cellFormat;
	private WorkbookSettings d_ws;

	public ExcelExporter(SliceablePhenoMatrixMV<R, C, V> matrix) throws WriteException {
		d_matrix = matrix;		
		d_headerFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
		d_headerFormat.setWrap(false);
		d_cellFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD));
		d_cellFormat.setWrap(false);
		
		d_ws = new WorkbookSettings();
		d_ws.setLocale(new Locale("en", "EN"));
	}

	public void exportAll(OutputStream os) throws MatrixException {
		export(os, false);
	}
	
	public void exportVisible(OutputStream os) throws MatrixException {
		export(os, true);
	}
	
	public void export(OutputStream os, boolean exportVisibleRows) throws MatrixException {
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(os, d_ws);
			WritableSheet s = workbook.createSheet("Sheet1", 0);
			
			// Write headers
			int colIdx = 0;
			for (C colHeader : d_matrix.getColHeaders())
			{
				Label f = new Label(colIdx, 0, colHeader.getName(), d_headerFormat);
				s.addCell(f);
				++colIdx;
			}
			
			retrieveDataAndWriteExcelSheet(s, exportVisibleRows);	
	
			workbook.write();
			workbook.close();
		} catch (Exception ex) {
			throw new MatrixException(ex);
		}
	}

	private void retrieveDataAndWriteExcelSheet(WritableSheet s, boolean exportVisibleRows) throws DatabaseException {
		ScrollableResults sr = null;
		try {
			sr = d_matrix.getScrollableValues(exportVisibleRows);
			// FIXME : hack because an extra column is added *only* when offset is not 0 
			// (probably also database dependent, ie oracle/mysql)
			writeResults(s, sr, exportVisibleRows && d_matrix.getRowOffset() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		} finally {
			sr.close();
		}
	}

	private void writeResults(WritableSheet s, ScrollableResults rs, boolean exportVisibleRows) throws MatrixException {
		try {
			List<Column> columns = d_matrix.getColumns();
			int iRow = 0;
			while (rs.next()) {
				Object[] row = rs.get();
				int nColumns = exportVisibleRows ? row.length - 1 : row.length;
				for (int iColumn = 0; iColumn < nColumns; ++iColumn) {
					writeSingleCell(row[iColumn], iRow, iColumn, columns.get(iColumn).getType(), s);
				}
				++iRow;
			}
		} catch (Exception e) {
			throw new MatrixException(e);
		} 
	}

	private void writeSingleCell(Object cellData, int iRow, int iColumn, ColumnType columnType, WritableSheet sheet)
			throws WriteException, RowsExceededException, ParseException {  
		// excel switches rows/columns; redundant locals preserved for clarity
		int row = iColumn;
		int column = iRow + 1;
					   	
		String dataCell = cellData != null ? cellData.toString() : null;
		sheet.addCell(createCellValue(columnType, row, column, dataCell));
	}

	private WritableCell createCellValue(ColumnType columnType, int row, int column, String dataCell)
			throws ParseException {
		if (dataCell == null) {
			return new Blank(row, column);
		} else {
			if(columnType.equals(ColumnType.String)) {
				return new Label(row, column, dataCell);
			} else if(columnType.equals(ColumnType.Integer)) {
				return new jxl.write.Number(row, column, Double.parseDouble(dataCell)); 					
			} else if(columnType.equals(ColumnType.Code)) {
				return new jxl.write.Label(row, column, dataCell.toString());
			} else if(columnType.equals(ColumnType.Decimal)) {
				return new jxl.write.Number(row, column, Double.parseDouble(dataCell));
			} else if(columnType.equals(ColumnType.Timestamp) || columnType.equals(ColumnType.Datetime)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
				return new jxl.write.DateTime(row, column, dateFormat.parse(dataCell));
			} else {
				throw new UnsupportedOperationException(String.format("Type %s not available",columnType));
			}
		}
	}
}
