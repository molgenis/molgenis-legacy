package org.molgenis.tableview;

import java.util.List;

import org.molgenis.util.Tuple;

public class TableModel
{
	//table source. Could be table name, or protocolName or any other identifiable source.
	String name;
	
	//limit
	int limit;
	
	//offset
	int offset;
	
	//current records
	List<Tuple> records;
	
	//current count
	int count;
	
	//current columns
	List<TableViewColumn> columns;

	public TableModel(String name)
	{
		assert(name != null);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public List<Tuple> getRecords()
	{
		return records;
	}

	public void setRecords(List<Tuple> records)
	{
		this.records = records;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public List<TableViewColumn> getColumns()
	{
		return columns;
	}

	public void setColumns(List<TableViewColumn> columns)
	{
		this.columns = columns;
	}
	
	
	
}
