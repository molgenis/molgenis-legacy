package org.molgenis.framework.tupletable.impl;

import java.sql.SQLException;
import java.util.Iterator;

import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class RSIterator implements Iterator<Tuple>
{
	private ResultSetTuple entities;
	private boolean didNext = false;
	private boolean hasNext = false;

	public RSIterator(ResultSetTuple rs)
	{
		this.entities = rs;
	}

	@Override
	public Tuple next()
	{
		try
		{
			if (!didNext)
			{
				entities.next();
			}
			didNext = false;
			return new SimpleTuple(entities);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext()
	{
		try
		{
			if (!didNext)
			{
				hasNext = entities.next();
				didNext = true;
			}
			return hasNext;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove()
	{
		// TODO Auto-generated method stub

	}
}