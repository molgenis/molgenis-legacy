package org.molgenis.util.tuple;

import java.util.List;

public interface WritableTuple extends Tuple
{
	/**
	 * Set multiple columns by using the array indexes as columnIndexes.
	 * 
	 * @param strings
	 */
	public void set(Object[] strings);

	/**
	 * Set a column.
	 * 
	 * @param columnName
	 *            name of the column
	 * @param value
	 *            value of the column
	 */
	public void set(String columnName, Object value);

	/**
	 * Set a column.
	 * 
	 * @param columnIndex
	 *            index of the column
	 * @param value
	 *            value of the column
	 */
	public void set(int columnIndex, Object value);

	public <E extends Object> void set(List<E> values);
}
