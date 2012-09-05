package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

public abstract class AbstractTupleTable implements TupleTable
{
	private int limit = 0;
	private int offset = 0;
	private int colOffset = 0;
	private int colLimit = 0;

	private Database db;

	@Override
	public void reset()
	{
		limit = 0;
		offset = 0;
		colOffset = 0;
		colLimit = 0;
	}

	@Override
	public int getLimit()
	{
		return limit;
	}

	@Override
	public void setLimit(int limit)
	{
		if (limit < 0) throw new RuntimeException("limit cannot be < 0");
		this.limit = limit;
	}

	@Override
	public int getOffset()
	{
		return offset;
	}

	@Override
	public void setOffset(int offset)
	{
		if (offset < 0) throw new RuntimeException("offset cannot be < 0");
		this.offset = offset;
	}

	@Override
	public abstract List<Field> getAllColumns() throws TableException;

	@Override
	public List<Field> getColumns() throws TableException
	{
		List<Field> columns = getAllColumns();
		if (getColOffset() > 0)
		{
			if (getColLimit() > 0)
			{
				return columns.subList(getColOffset(), Math.min(getColOffset() + getColLimit(), columns.size()));
			}
			else
			{
				return columns.subList(getColOffset(), columns.size());
			}
		}
		else
		{
			if (getColLimit() > 0)
			{
				return columns.subList(0, getColLimit());
			}
			else
			{
				return columns;
			}
		}
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple t : this)
		{
			result.add(t);
		}
		return result;
	}

	@Override
	public abstract Iterator<Tuple> iterator();

	@Override
	public void close() throws TableException
	{
		// close resources if applicable
	}

	@Override
	public abstract int getCount() throws TableException;

	@Override
	public int getColCount() throws TableException
	{
		return this.getColumns().size();
	}

	@Override
	public void setColLimit(int limit)
	{
		if (limit < 0) throw new RuntimeException("colLimit cannot be < 0");
		this.colLimit = limit;
	}

	@Override
	public int getColLimit()
	{
		return this.colLimit;
	}

	@Override
	public int getColOffset()
	{
		return this.colOffset;
	}

	@Override
	public void setColOffset(int offset)
	{
		if (offset < 0) throw new RuntimeException("colOffset cannot be < 0");
		this.colOffset = offset;
	}

	@Override
	public void setDb(Database db)
	{
		if (db == null) throw new NullPointerException("database cannot be null in setDb(db)");
		this.db = db;
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.setLimit(limit);
		this.setOffset(offset);
	}

	public Database getDb()
	{
		try
		{
			db = DatabaseFactory.create();
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.db;
	}
}
