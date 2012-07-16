package org.molgenis.datatable.model;

import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public abstract class AbstractTupleTable implements TupleTable
{
	private int limit = 0;
	private int offset = 0;
	private int colOffset = 0;
	private int colLimit = 0;

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.limit = limit;
		this.offset = offset;
	}

	@Override
	public int getLimit()
	{
		return limit;
	}

	@Override
	public void setLimit(int limit)
	{
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
		this.offset = offset;
	}

	@Override
	public abstract List<Field> getColumns() throws TableException;

	@Override
	public abstract List<Tuple> getRows() throws TableException;

	@Override
	public abstract Iterator<Tuple> iterator();

	@Override
	public void close() throws TableException
	{
		//close resources if applicable
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
		this.colOffset = offset;
	}

	@Override
	public void setDb(Database db)
	{
		//only override if necessary
	}

}
