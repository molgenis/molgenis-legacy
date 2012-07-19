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

	int colLimit = 0;
	int colOffset = 0;

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

	public TupleIterator(TupleIterable reader, int limit, int offset, int colLimit, int colOffset)
	{
		this.reader = reader;
		this.limit = limit;
		this.offset = offset;
		this.colLimit = colLimit;
		this.colOffset = colOffset;
	}

	@Override
	public boolean hasNext()
	{
		while (index++ < offset)
		{
			reader.next();
		}

		if (limit != 0 && count++ >= limit) return false;

		if (colLimit > 0 || colOffset > 0)
		{
			next = reader.next();
			if (next != null)
			{
				SimpleTuple temp = new SimpleTuple();
				int colIndex = 1;
				int colCount = 0;
				for (String f : next.getFieldNames())
				{
					if (colOffset == 0 || colIndex > colOffset)
					{
						temp.set(f, next.getObject(f));
						colCount++;
						if (colLimit != 0 && colCount > colLimit) break;
					}
					colIndex++;
				}
				next = temp;
			}
		}
		else
		{
			next = reader.next();
		}
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
