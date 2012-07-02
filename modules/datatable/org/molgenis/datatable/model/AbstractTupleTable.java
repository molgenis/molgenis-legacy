package org.molgenis.datatable.model;

public abstract class AbstractTupleTable implements TupleTable
{
	private int limit = 0;
	private int offset = 0;

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

}
