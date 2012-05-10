package org.molgenis.matrix.Utils;

import java.io.OutputStream;
import java.util.List;

import org.molgenis.fieldtypes.FieldType;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.SimpleTableModel;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public abstract class AbstractExporter<RowType extends Iterable<?>> 
	implements Exporter<ObservationTarget, Measurement, ObservedValue> {
	
	protected final SimpleTableModel<RowType> tableModel;
	protected final OutputStream os;
	
	public AbstractExporter(final SimpleTableModel<RowType> tableModel, final OutputStream os) {
		this.tableModel = tableModel;
		this.os = os;
	}	
	
	public final void writeResults() {
		int iRow = 0;
		List<Field> columns = tableModel.getColumns();
		for (RowType row : tableModel) {
			int iCol = 0;
			for (Object cell : row) {
				writeSingleCell(cell, iRow, iCol, columns.get(iCol).getType());
				if(iCol < columns.size() - 1) {
					writeSeparator();
				}
				++iCol;
			}
			writeEndOfLine();
			++iRow;
		}
	}
	
	protected abstract void writeSingleCell(Object value, int iRow, int iColumn, FieldType type);

	public void writeSeparator() {}
	public void writeEndOfLine() {}

	public abstract void export() throws MatrixException;
}
