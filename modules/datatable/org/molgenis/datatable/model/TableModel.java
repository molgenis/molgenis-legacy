package org.molgenis.datatable.model;

import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Form.SortOrder;


public interface TableModel extends TupleTable
{
	//public Integer getRowCount();

	public int getRowLimit();

	public void setRowLimit(int rowLimit);

	public int getRowOffset();

	public void setRowOffset(int rowOffset);

	public Field getSortColumn();

	public SortOrder getSortOrder();

//	public LinkedHashMap<Protocol, List<Measurement>> getMeasurementsByProtocol();
//
//	public void setSort(Protocol protocol, Measurement measurement, String sortOrder);

	// TODO :Think & push down (?)
//	public abstract void addCondition(QueryRule condition);

//	public abstract void setConditions(List<QueryRule> conditions);
	
//	public abstract void addCondition(int protocolId, int measurementId, String op, Operator operator, String value);

//	public abstract List<Object[]> getTypedValues() throws MatrixException;

}