package org.molgenis.util;

import java.util.List;

/**
 * This class wraps the entity. It overrides the getObject methods of
 * SimpleTuple to directly query the embedded tuple.
 */
public class EntityTuple extends SimpleTuple
{
	final private Entity entity;
	
	/** Rather inefficient implementation that copies all into simpletuple */
	public EntityTuple(Entity entity)
	{
		super();
		this.entity = entity;
	}
	
	@Override
	public List<String> getFields()
	{
		return entity.getFields();
	}
	
	@Override
	public String getColName(int i)
	{
		List<String> colNames = this.getFields();
		if(i >= 0 && i < colNames.size()) return colNames.get(i);
		return null;
	}
	
	@Override
	public Object getObject(int column)
	{
		String colName = getColName(column);
		if(colName != null) return getObject(colName);
		return null;
	}
	
	@Override
	public Object getObject(String name)
	{
		return entity.get(name);
	}
	
	@Override
	public int getNrColumns()
	{
		return this.getFields().size();
	}
	
	@Override
	public void set(String column, Object value) 
	{
		try
		{
			this.entity.set(column, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
