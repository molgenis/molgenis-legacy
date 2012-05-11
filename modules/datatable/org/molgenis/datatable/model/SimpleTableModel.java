package org.molgenis.datatable.model;

import java.util.Iterator;
import java.util.List;

import org.molgenis.model.elements.Field;

public interface SimpleTableModel<RowType> extends Iterable<RowType>
{
	public List<Field> getColumns();

	public Iterator<RowType> iterator();
	
	public Object getValue(RowType row, Field field, int rowIndex, int colIndex);
}
