package org.molgenis.lifelinesresearchportal.models;

import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;

public interface PhenoMatrix<R extends ObservationTarget, C extends Measurement, V extends ObservedValue>
{
	public abstract Integer getRowCount() throws MatrixException;

	public abstract int getRowLimit();

	public abstract void setRowLimit(int rowLimit);

	public abstract int getRowOffset();

	public abstract void setRowOffset(int rowOffset);

	public abstract List<Column> getColumns();

	public abstract ScrollableResults getScrollableValues(boolean exportVisibleRows) throws Exception;

	public abstract LinkedHashMap<Protocol, List<Measurement>> getMeasurementsByProtocol();

	public abstract void setSort(Protocol protocol, Measurement measurement, String sortOrder);

	@Deprecated //should be a real column instead of the name!
	public abstract String getJoinColumn();

	public abstract Measurement getSortMeasurement();

	public abstract String getSortOrder();

	public abstract Protocol getSortProtocol();

	public abstract void addCondition(int protocolId, int measurementId, String op, Operator operator, String value);

	public abstract List<Object[]> getTypedValues() throws MatrixException;

	public List<C> getMeasurements();
	
//	public abstract List<Column> getColHeaders();
}