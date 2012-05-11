package org.molgenis.datatable.view;

import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class TupleValueBinding<E> implements RowElementBinding<Tuple>
{

	@Override
	public Object getCellValue(Tuple row, Field field, int rowIndex, int colIndex)
	{
		return row.getObject(field.getName());
	}

}
