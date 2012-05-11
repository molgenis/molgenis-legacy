package org.molgenis.datatable.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.molgenis.datatable.view.RowElementBinding;
import org.molgenis.model.elements.Field;

public class CollectionTableModel<E> implements SimpleTableModel<E>
{	
	
	
	//public final ValueBinding<E> BEAN_VALUE_BINDING = new BeanValueBinding<E>();
	
	private final Collection<E> persons;
	private final List<Field> fields;
	private final RowElementBinding<E> valueBinding;

	public CollectionTableModel(Collection<E> persons, RowElementBinding<E> valueBinding, List<Field> fields)
	{
		this.persons = persons;
		this.valueBinding = valueBinding;
		this.fields = fields;
	}

	@Override
	public List<Field> getColumns()
	{
		return fields;
	}

	@Override
	public Iterator<E> iterator()
	{
		return persons.iterator();
	}

	@Override
	public Object getValue(E row, Field field, int rowIndex, int colIndex)
	{
		return valueBinding.getCellValue(row, field, rowIndex, colIndex);
	}

}