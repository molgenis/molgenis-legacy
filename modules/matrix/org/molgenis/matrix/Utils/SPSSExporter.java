package org.molgenis.matrix.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ScrollableResults;
import org.molgenis.matrix.PhenoMatrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import com.pmstation.spss.SPSSWriter;

public class SPSSExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> 
	extends AbstractExporter<R, C, V>
{
	private SPSSWriter 		d_spssWriter;

	public SPSSExporter(PhenoMatrix<R, C, V> matrix, OutputStream os) throws MatrixException {
		super(matrix, os);
		initWriter();
	}

	@Override
	protected void export(boolean exportVisibleRows) throws MatrixException {
		try {
			writeColHeaders(os);
			d_spssWriter.addDataSection();
			ScrollableResults sr = matrix.getScrollableValues(exportVisibleRows);
			writeResults(sr, exportVisibleRows && matrix.getRowOffset() > 0);
			d_spssWriter.addFinishSection();
		} catch (Exception ex) {
			throw new MatrixException(ex);
		}
	}
	
	@Override
	public void writeSingleCell(Object object, int iRow, int iColumn, ColumnType columnType)  {
		try {
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
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void writeColHeaders(OutputStream os) throws IOException, NumberFormatException, MatrixException {
		int columnIdx = 0;
				
		for (Column column : matrix.getColumns()) {			
			writeColHeader(column.getMeasurement(), columnIdx);
			++columnIdx;
		}
	}

	private void writeColHeader(Measurement colHeader, int columnIdx) throws IOException {
		String dataType = colHeader.getDataType();
		String colName = colHeader.getName();
		
		List<Column> columns = matrix.getColumns();
		ColumnType colType = columns.get(columnIdx).getType();
		
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

	private void initWriter() throws MatrixException {
		d_spssWriter = new SPSSWriter(os, "windows-1252");
		d_spssWriter.setCalculateNumberOfCases(false);
		try {
			d_spssWriter.addDictionarySection(-1);
		} catch (IOException e) {
			throw new MatrixException(e);
		}
	}

	@Override
	public String getFileExtension()
	{
		return ".sav";
	}
	
	@Override
	public String getMimeType()
	{
		return null;
	}
}
