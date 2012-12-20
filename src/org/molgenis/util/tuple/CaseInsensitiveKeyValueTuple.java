package org.molgenis.util.tuple;

/**
 * {@link KeyValueTuple} with case insensitive column names
 */
public class CaseInsensitiveKeyValueTuple extends KeyValueTuple
{
	@Override
	public Object get(String colName)
	{
		return super.get(colName.toLowerCase());
	}

	@Override
	public void set(String colName, Object val)
	{
		super.set(colName.toLowerCase(), val);
	}
}
