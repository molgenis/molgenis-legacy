package org.molgenis.matrix.Utils;

import java.io.OutputStream;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.molgenis.lifelinesresearchportal.models.MatrixModel;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public abstract class AbstractExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> 
	implements Exporter<ObservationTarget, Measurement, ObservedValue> {
	
	protected final MatrixModel<R, C, V> matrix;
	protected final OutputStream os;
	
	public AbstractExporter(final MatrixModel<R, C, V> matrix, final OutputStream os) {
		this.matrix = matrix;
		this.os = os;
	}	
	
	public final void writeResults(final ScrollableResults rs, final boolean exportVisibleRows) throws MatrixException {
		try {
			final List<Column> columns = matrix.getColumns();
			int iRow = 0;
			while (rs.next()) {
				final Object[] row = rs.get();
				final int nColumns = exportVisibleRows ? row.length - 1 : row.length;
				for (int iColumn = 0; iColumn < nColumns; ++iColumn) {
					writeSingleCell(row[iColumn], iRow, iColumn, columns.get(iColumn).getType());
					if(iColumn < nColumns - 1) {
						writeSeperator();
					}
				}
				writeEndOfLine();
				++iRow;
			}
		} catch (Exception e) {
			throw new MatrixException(e);
		} 
	}
	
	public void writeSeperator() {}
	public void writeEndOfLine() {}

	protected abstract void export(boolean exportVisible) throws MatrixException;
	
	public final void exportAll() throws MatrixException {
		export(false);
	}

	public final void exportVisible() throws MatrixException {
		export(true);
	};
}
