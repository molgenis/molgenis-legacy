package org.molgenis.matrix.Utils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvWriter;

public class CsvExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> extends AbstractExporter<ObservationTarget, Measurement, ObservedValue>
{
	private CsvWriter d_writer;
	
	public CsvExporter(SliceablePhenoMatrixMV<R, C, V> matrix, OutputStream os) {
		super((SliceablePhenoMatrixMV<ObservationTarget, Measurement, ObservedValue>) matrix, os);
		
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

	private void initHeaders() throws MatrixException {
		List<String> headers = new ArrayList<String>();
		for (Measurement colHeader : (List<C>)matrix.getColHeaders())
		{
			headers.add(colHeader.getName());
		}
		d_writer.setHeaders(headers);
		d_writer.writeHeader();
	}

	@Override
	public void writeSingleCell(Object value, int iRow, int iColumn,
			ColumnType colType) {
		d_writer.writeValue(value == null ? null : value.toString());
	}
	
	@Override
	public void writeSeperator() {
		d_writer.writeSeparator();
	};
	
	@Override
	public void writeEndOfLine() {
		d_writer.writeEndOfLine();
	}

	@Override
	public String getFileExtension()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
