package org.molgenis.util.tuple;

import java.util.Collections;
import java.util.Iterator;

/**
 * Tuple backed by a single value
 */
public class SingletonTuple<T> extends AbstractTuple
{
	private final T value;
	private final String colName;

	public SingletonTuple(T value)
	{
		this(value, null);
	}

	public SingletonTuple(T value, String colName)
	{
		this.value = value;
		this.colName = colName;
	}

	@Override
	public int getNrCols()
	{
		return 1;
	}

	@Override
	public Iterator<String> getColNames()
	{
		return colName != null ? Collections.singletonList(colName).iterator() : null;
	}

	@Override
	public Object get(String colName)
	{
		if (this.colName == null) return null;
		return this.colName.equals(colName) ? value : null;
	}

	@Override
	public Object get(int col)
	{
		return col == 0 ? value : null;
	}
}
