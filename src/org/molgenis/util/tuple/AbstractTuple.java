package org.molgenis.util.tuple;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.molgenis.util.ListEscapeUtils;

/**
 * Tuple that delegates all calls to Tuple.get
 */
public abstract class AbstractTuple implements Tuple
{
	@Override
	public boolean hasColNames()
	{
		Iterable<String> it = getColNames();
		return it != null ? it.iterator().hasNext() : false;
	}

	@Override
	public boolean isNull(String colName)
	{
		return get(colName) == null;
	}

	@Override
	public boolean isNull(int col)
	{
		return get(col) == null;
	}

	@Override
	public String getString(String colName)
	{
		Object obj = get(colName);
		return obj != null ? obj.toString() : null;
	}

	@Override
	public String getString(int col)
	{
		Object obj = get(col);
		return obj != null ? obj.toString() : null;
	}

	@Override
	public Integer getInt(String colName)
	{
		String str = getString(colName);
		return str != null ? Integer.parseInt(str) : null;
	}

	@Override
	public Integer getInt(int col)
	{
		String str = getString(col);
		return str != null ? Integer.parseInt(str) : null;
	}

	@Override
	public Long getLong(String colName)
	{
		String str = getString(colName);
		return str != null ? Long.parseLong(str) : null;
	}

	@Override
	public Long getLong(int col)
	{
		String str = getString(col);
		return str != null ? Long.parseLong(str) : null;
	}

	@Override
	public Boolean getBoolean(String colName)
	{
		String str = getString(colName);
		return str != null ? Boolean.parseBoolean(str) : null;
	}

	@Override
	public Boolean getBoolean(int col)
	{
		String str = getString(col);
		return str != null ? Boolean.parseBoolean(str) : null;
	}

	@Override
	public Double getDouble(String colName)
	{
		String str = getString(colName);
		return str != null ? Double.parseDouble(str) : null;
	}

	@Override
	public Double getDouble(int col)
	{
		String str = getString(col);
		return str != null ? Double.parseDouble(str) : null;
	}

	@Override
	public Date getDate(String colName)
	{
		String str = getString(colName);
		return str != null ? Date.valueOf(str) : null;
	}

	@Override
	public Date getDate(int col)
	{
		String str = getString(col);
		return str != null ? Date.valueOf(str) : null;
	}

	@Override
	public Timestamp getTimestamp(String colName)
	{
		String str = getString(colName);
		return str != null ? Timestamp.valueOf(str) : null;
	}

	@Override
	public Timestamp getTimestamp(int col)
	{
		String str = getString(col);
		return str != null ? Timestamp.valueOf(str) : null;
	}

	@Override
	public List<String> getList(String colName)
	{
		String str = getString(colName);
		return str != null ? ListEscapeUtils.toList(str) : null;
	}

	@Override
	public List<String> getList(int col)
	{
		String str = getString(col);
		return str != null ? ListEscapeUtils.toList(str) : null;
	}
}
