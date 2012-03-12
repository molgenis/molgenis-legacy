package org.molgenis.matrix.Utils;

import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public interface Exporter<T extends ObservationTarget, M extends Measurement, V extends ObservedValue> {
	public void exportAll() throws MatrixException;
	public void exportVisible() throws MatrixException;	
	
	public void writeSingleCell(Object value, int iRow, int iColumn, ColumnType colType);
}