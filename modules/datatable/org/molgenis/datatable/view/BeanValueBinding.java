package org.molgenis.datatable.view;

import org.apache.commons.beanutils.BeanUtils;
import org.molgenis.model.elements.Field;

public class BeanValueBinding<E> implements RowElementBinding<E> {
	public Object getCellValue(E row, Field field, int rowIndex, int colIndex)
	{
		try
		{
			return BeanUtils.getProperty(row, field.getName());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}