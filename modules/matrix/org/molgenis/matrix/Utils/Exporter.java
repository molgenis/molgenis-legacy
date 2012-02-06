package org.molgenis.matrix.Utils;

import java.io.OutputStream;

import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public interface Exporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> {
	public void exportAll(OutputStream os) throws MatrixException;
	public void exportVisible(OutputStream os) throws MatrixException;	
}