package org.molgenis.util.tuple;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Tuple backed by a deprecated {@link org.molgenis.util.Tuple}
 */
@Deprecated
public class DeprecatedTupleTuple implements Tuple
{
	private final org.molgenis.util.Tuple tuple;

	public DeprecatedTupleTuple(org.molgenis.util.Tuple tuple)
	{
		if (tuple == null) throw new IllegalArgumentException("tuple is null");
		this.tuple = tuple;
	}

	@Override
	public int getNrCols()
	{
		return tuple.getNrColumns();
	}

	@Override
	public boolean hasColNames()
	{
		return tuple.getFieldNames() != null;
	}

	@Override
	public Iterator<String> getColNames()
	{
		List<String> fieldNames = tuple.getFieldNames();
		return fieldNames != null ? Collections.unmodifiableList(fieldNames).iterator() : null;
	}

	@Override
	public boolean isNull(String colName)
	{
		return tuple.isNull(colName);
	}

	@Override
	public boolean isNull(int col)
	{
		return tuple.isNull(col);
	}

	@Override
	public Object get(String colName)
	{
		return tuple.getObject(colName);
	}

	@Override
	public Object get(int col)
	{
		return tuple.getObject(col);
	}

	@Override
	public String getString(String colName)
	{
		return tuple.getString(colName);
	}

	@Override
	public String getString(int col)
	{
		return tuple.getString(col);
	}

	@Override
	public Integer getInt(String colName)
	{
		return tuple.getInt(colName);
	}

	@Override
	public Integer getInt(int col)
	{
		return tuple.getInt(col);
	}

	@Override
	public Long getLong(String colName)
	{
		return tuple.getLong(colName);
	}

	@Override
	public Long getLong(int col)
	{
		return tuple.getLong(col);
	}

	@Override
	public Boolean getBoolean(String colName)
	{
		return tuple.getBoolean(colName);
	}

	@Override
	public Boolean getBoolean(int col)
	{
		return tuple.getBoolean(col);
	}

	@Override
	public Double getDouble(String colName)
	{
		return tuple.getDouble(colName);
	}

	@Override
	public Double getDouble(int col)
	{
		return tuple.getDouble(col);
	}

	@Override
	public Date getDate(String colName)
	{
		try
		{
			return tuple.getDate(colName);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Date getDate(int col)
	{
		try
		{
			return tuple.getDate(col);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Timestamp getTimestamp(String colName)
	{
		try
		{
			return tuple.getTimestamp(colName);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Timestamp getTimestamp(int col)
	{
		try
		{
			return tuple.getTimestamp(col);
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getList(String colName)
	{
		return (List<String>) tuple.getList(colName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getList(int col)
	{
		return (List<String>) tuple.getList(col);
	}
}
