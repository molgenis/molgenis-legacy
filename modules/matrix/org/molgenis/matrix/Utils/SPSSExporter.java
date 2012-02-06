package org.molgenis.matrix.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ScrollableResults;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import com.pmstation.spss.SPSSWriter;

public class SPSSExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> implements Exporter<R, C, V> {

	private final SliceablePhenoMatrixMV<R, C, V> d_matrix;
	private SPSSWriter d_spssWriter;

	public SPSSExporter(SliceablePhenoMatrixMV<R, C, V> matrix) throws MatrixException {
		d_matrix = matrix;
	}

	@Override
	public void exportAll(OutputStream os) throws MatrixException {
		initWriter(os);
		export(os, false);
	}

	@Override
	public void exportVisible(OutputStream os) throws MatrixException {
		initWriter(os);
		export(os, true);
	}
	
	private void export(OutputStream os, boolean exportVisibleRows) throws MatrixException {
		if (d_spssWriter == null) {
			initWriter(os);
		}
		
		try {
			writeColHeaders(os);
			d_spssWriter.addDataSection();
			ScrollableResults sr = d_matrix.getScrollableValues(exportVisibleRows);
			writeResults(sr, exportVisibleRows && d_matrix.getRowOffset() > 0);
			d_spssWriter.addFinishSection();
		} catch (Exception ex) {
			throw new MatrixException(ex);
		}
	}

	private void writeResults(ScrollableResults sr, boolean exportVisibleRows) throws MatrixException {
		try {
			List<Column> columns = d_matrix.getColumns();
			while (sr.next()) {
				Object[] row = sr.get();
				int nColumns = exportVisibleRows ? row.length - 1 : row.length;
				for (int iColumn = 0; iColumn < nColumns; ++iColumn) {
					writeSingleCell(row[iColumn], columns.get(iColumn).getType());
				}
			}
		} catch (Exception e) {
			throw new MatrixException(e);
		} 
	}

	private void writeSingleCell(Object object, ColumnType columnType) throws IOException, ParseException {
		switch(columnType) {
			case Datetime :
				if(object != null) {
					Timestamp ts = (Timestamp)object;
					d_spssWriter.addData(new Date(ts.getTime()));
				} else {
					d_spssWriter.addData((Date)null);
				}
				break;
			case Date :
				if(object != null) {
					Timestamp ts = (Timestamp)object;
					d_spssWriter.addData(new Date(ts.getTime())); 
				} else {
					d_spssWriter.addData((Date)null);
				}				
				break;
			case Decimal :
				if(object != null) {
					Number nDouble = (Number)object;
					d_spssWriter.addData(nDouble.doubleValue());
				} else {
					d_spssWriter.addData((Double)null);
				}
				break;
			case Integer : 
				if(object != null) {
					Number nInt = (Number)object;
					d_spssWriter.addData(nInt.longValue());
				} else {
					d_spssWriter.addData((Long)null);
				}
				break;
			default :
				if(object != null) {
					d_spssWriter.addData(object.toString());
				} else {
					d_spssWriter.addData((String)null);
				}
				
		}
	}

	private void writeColHeaders(OutputStream os) throws IOException, NumberFormatException, MatrixException {
		int columnIdx = 0;
		for (C colHeader : d_matrix.getColHeaders()) {			
			writeColHeader(colHeader, columnIdx);
			++columnIdx;
		}
	}

	private void writeColHeader(C colHeader, int columnIdx) throws IOException {
		String dataType = colHeader.getDataType();
		String colName = colHeader.getName();
		ColumnType colType = d_matrix.getColumns().get(columnIdx).getType();
		
		if (colType == null) {
			d_spssWriter.addStringVar(colName, 255, colName);
		} else if (colType.equals(ColumnType.Decimal)) {
			int width = 10;
			int decimal = 2;
			String precision = StringUtils.substringBetween(dataType, "(", ")");
			if (precision != null) {
				String[] parts = StringUtils.split(precision, ",");
				width = Integer.parseInt(parts[0]);
				decimal = Integer.parseInt(parts[1]);
			}
			d_spssWriter.addNumericVar(colName, width, decimal, colName);
		} else if (colType.equals(ColumnType.Integer)) {
			// so far for nondecimals is always 10,0 from what I see.
			d_spssWriter.addNumericVar(colName, 10, 0, colName);
		} else if (colType.equals(ColumnType.Date) || colType.equals(ColumnType.Datetime) || colType.equals(ColumnType.Timestamp)) {
			d_spssWriter.addDateVar(colName, SPSSWriter.DATE_TYPE_05, colName);
		} else {
			d_spssWriter.addStringVar(colName, 255, colName);
		}
	}

	private void initWriter(OutputStream os) throws MatrixException {
		d_spssWriter = new SPSSWriter(os, "windows-1252");
		d_spssWriter.setCalculateNumberOfCases(false);
		try {
			d_spssWriter.addDictionarySection(-1);
		} catch (IOException e) {
			throw new MatrixException(e);
		}
	}
}
