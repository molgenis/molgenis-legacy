package org.molgenis.util.tuple;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Tuple backed by a {@link java.util.Map}
 */
public class KeyValueTuple extends AbstractTuple
{
	private final Map<String, ? extends Object> valueMap;

	public KeyValueTuple(Map<String, ? extends Object> valueMap)
	{
		if (valueMap == null) throw new IllegalArgumentException("map is null");
		this.valueMap = valueMap;
	}

	@Override
	public int getNrCols()
	{
		return valueMap.size();
	}

	@Override
	public Iterator<String> getColNames()
	{
		return Collections.unmodifiableSet(valueMap.keySet()).iterator();
	}

	@Override
	public Object get(String colName)
	{
		return valueMap.get(colName);
	}

	@Override
	public Object get(int col)
	{
		int i = 0;
		for (Object obj : valueMap.values())
			if (i++ == col) return obj;
		throw new IndexOutOfBoundsException();
	}
}
