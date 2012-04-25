package org.molgenis.matrix.Utils;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.PhenoMatrix;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvWriter;


public class CsvExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> 
	extends AbstractExporter<R, C, V>
{
	protected CsvWriter d_writer;
	protected SimpleDateFormat d_dateFormat;
	
	public CsvExporter(PhenoMatrix<R, C, V> matrix, OutputStream os) {
		this(matrix, os, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public CsvExporter(PhenoMatrix<R, C, V> matrix, OutputStream os, SimpleDateFormat simpleDateFormat)
	{
		super(matrix, os);
		d_writer = new CsvWriter(os);
		d_writer.setSeparator(",");
	}

	@Override	
	protected void export(boolean exportVisible) throws MatrixException {
		initHeaders();
		ScrollableResults sr = null;
		try {
			sr = matrix.getScrollableValues(exportVisible);
			// FIXME : hack because an extra column is added *only* when offset is not 0 
			// (probably also database dependent, ie oracle/mysql)
			writeResults(sr, exportVisible && matrix.getRowOffset() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatrixException(e);
		} finally {
			d_writer.close();
			sr.close();
			d_writer.close();
		}
	}

	protected void initHeaders() throws MatrixException {
		List<String> headers = new ArrayList<String>();
		for (Measurement colHeader : matrix.getMeasurements())
		{
			headers.add(colHeader.getName());
		}
		d_writer.setHeaders(headers);
		d_writer.writeHeader();
	}

	@Override
	public void writeSingleCell(Object value, int iRow, int iColumn,
			ColumnType colType) {
		if (value instanceof Date) {
			d_writer.writeValue(d_dateFormat.format(value));
		} else {
			d_writer.writeValue(value == null ? null : value.toString());
		}
	}
	
	@Override
	public void writeSeparator() {
		d_writer.writeSeparator();
	};
	
	@Override
	public void writeEndOfLine() {
		d_writer.writeEndOfLine();
	}

	@Override
	public String getFileExtension()
	{
		return ".csv";
	}

	@Override
	public String getMimeType()
	{
		return "text/csv";
	}
}
