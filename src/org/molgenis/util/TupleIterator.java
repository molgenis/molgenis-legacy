package org.molgenis.util;

import java.util.Iterator;

public class TupleIterator implements Iterator<Tuple>
{
	TupleIterable reader;
	Tuple next;
	int limit = 0;
	int offset = 0;
	int index = 0;
	int count = 0;

	public TupleIterator(TupleIterable reader)
	{
		this.reader = reader;
	}

	public TupleIterator(TupleIterable reader, int limit, int offset)
	{
		this.reader = reader;
		this.limit = limit;
		this.offset = offset;
	}

	@Override
	public boolean hasNext()
	{
		while (index++ < offset)
		{
			reader.next();
		}
		
		if(limit != 0 && count++ >= limit) return false;
		
		next = reader.next();
		if (next != null) 
			return true;
		return false;
	}

	@Override
	public Tuple next()
	{
		if (next != null || hasNext()) 
			return next;
		else
			return null;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
