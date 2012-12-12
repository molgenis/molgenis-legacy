package org.molgenis.util.tuple;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Values row backed by a {@link java.util.List} with an optional column names
 * index
 */
public class ValueIndexTuple extends AbstractTuple
{
	private final List<? extends Object> values;
	private final Map<String, Integer> colNamesMap;

	public ValueIndexTuple(List<? extends Object> values, Map<String, Integer> colNamesMap)
	{
		if (values == null) throw new IllegalArgumentException("values is null");
		if (colNamesMap == null) throw new IllegalArgumentException("column names map is null");
		this.values = values;
		this.colNamesMap = colNamesMap;
	}

	@Override
	public int getNrCols()
	{
		return values.size();
	}

	@Override
	public boolean hasColNames()
	{
		return true;
	}

	@Override
	public Iterator<String> getColNames()
	{
		return Collections.unmodifiableSet(colNamesMap.keySet()).iterator();
	}

	@Override
	public Object get(String colName)
	{
		Integer pos = colNamesMap.get(colName);
		return pos != null ? values.get(pos) : null;
	}

	@Override
	public Object get(int col)
	{
		return values.get(col);
	}
}
