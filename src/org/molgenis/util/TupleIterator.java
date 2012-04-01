package org.molgenis.util;

import java.util.Iterator;

public class TupleIterator implements Iterator<Tuple>
{
	TupleIterable reader;
	Tuple next;

	TupleIterator(TupleIterable reader)
	{
		this.reader = reader;
	}

	@Override
	public boolean hasNext()
	{
		next = reader.next();
		if (next != null) return true;
		return false;
	}

	@Override
	public Tuple next()
	{
		if (next != null || hasNext()) return next;
		else
			return null;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
