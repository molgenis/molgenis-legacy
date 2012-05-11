package org.molgenis.datatable.view;

import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public interface Exporter<T extends ObservationTarget, M extends Measurement, V extends ObservedValue> {
	public void export() throws MatrixException;
	
	public String getFileExtension();
	public String getMimeType();
}