package org.molgenis.util.tuple;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Header row backed by a {@link java.util.List}
 */
public class HeaderTuple extends AbstractTuple
{
	private final List<String> colNames;

	public HeaderTuple(List<String> colNames)
	{
		if (colNames == null) throw new IllegalArgumentException("col names is null");
		this.colNames = colNames;
	}

	@Override
	public int getNrCols()
	{
		return colNames.size();
	}

	@Override
	public Iterator<String> getColNames()
	{
		return Collections.unmodifiableList(colNames).iterator();
	}

	@Override
	public Object get(String colName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(int col)
	{
		throw new UnsupportedOperationException();
	}
}
