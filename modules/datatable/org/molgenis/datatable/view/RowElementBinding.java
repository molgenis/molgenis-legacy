package org.molgenis.datatable.view;

import org.molgenis.model.elements.Field;

public interface RowElementBinding<E> {
	public Object getCellValue(E row, Field field, int rowIndex, int colIndex);
}