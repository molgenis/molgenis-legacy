package org.molgenis.matrix.Utils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvWriter;

public class CsvExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> implements Exporter<R, C, V> {

	private final SliceablePhenoMatrixMV<R, C, V> d_matrix;
	private CsvWriter d_writer;

	public CsvExporter(SliceablePhenoMatrixMV<R, C, V> matrix) {
		d_matrix = matrix;
	}

	@Override
	public void exportAll(OutputStream os) throws MatrixException {
		export(os, false);
	}

	@Override
	public void exportVisible(OutputStream os) throws MatrixException {
		export(os, true);
	}

	private void export(OutputStream os, boolean exportVisible) throws MatrixException {
		d_writer = new CsvWriter(os);
		d_writer.setSeparator(",");
		
		initHeaders();
		ScrollableResults sr = null;
		try {
			sr = d_matrix.getScrollableValues(exportVisible);
			// FIXME : hack because an extra column is added *only* when offset is not 0 
			// (probably also database dependent, ie oracle/mysql)
			writeResults(sr, exportVisible&& d_matrix.getRowOffset() > 0);
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
		for (ObservationElement colHeader : (List<C>)d_matrix.getColHeaders())
		{
			headers.add(colHeader.getName());
		}
		d_writer.setHeaders(headers);
		d_writer.writeHeader();
	}
	
	private void writeResults(ScrollableResults sr, boolean exportVisibleRows) throws MatrixException {
		try {
			while (sr.next()) {
				writeRow(sr.get());
			}			
		} catch (Exception e) {
			throw new MatrixException(e);
		} 
	}

	private void writeRow(Object[] row) {
		for (int i = 0; i < row.length; ++i) {
			if (i != 0) {
				d_writer.writeSeparator();
			}
			d_writer.writeValue(row[i] == null ? null : row[i].toString());
		}
		d_writer.writeEndOfLine();
	}
}
