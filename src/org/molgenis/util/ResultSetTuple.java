/**
 * File: molgenis.data.Tuple <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-03-21; 1.0.0; RA Scheltema; Creation.
 * <li>2006-04-15; 1.0.0; MA Swertz; Documentation.
 * <li>2007-01-21; 2.0.0; MA Swertz; Refactored.
 * </ul>
 */

package org.molgenis.util;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A {link Tuple} that wraps the current JDBC ResultSet row.
 * <p>
 * JDBCTuple class wraps the current row in a java.sql.Resultset so it can be
 * questioned as Tuple. Thus, we can use
 * {@link org.molgenis.util.Entity#set(Tuple)} to quicly load new Entities. Note
 * that a JDBCTuple is readonly!
 * <p>
 * FIXME this class can be made more efficient because the
 * resultset.getMetaData() is rather expensive? <br>
 * FIXME this class may be behave strangely if the ResultSet is .next().
 */
public class ResultSetTuple extends SimpleTuple
{
	private static final Logger logger = Logger.getLogger(ResultSetTuple.class);

	/** JDBC Resultset wrapped by this Tuple. */
	private ResultSet resultset;

	/** JDBC Resultset metadata of the ResultSet wrapped by this Tuple. */
	private ResultSetMetaData metadata;

	/** cache of the field names */
	private List<String> fieldNames = null;

	/** cach of column labels */
	private List<String> columnLabels = null;

	/**
	 * Construct a Tuple for a JDBC ResultSet
	 * 
	 * @param resultset
	 */
	public ResultSetTuple(ResultSet resultset)
	{
		try
		{
			this.resultset = resultset;
			this.metadata = resultset.getMetaData();
		}
		catch (SQLException e)
		{
		}
	}

	@Override
	public int getNrColumns()
	{
		try
		{
			return metadata.getColumnCount();
		}
		catch (SQLException e)
		{
			return 0;
		}
	}

	/**
	 * Deprecated: use getFieldNames instead.
	 * 
	 * @return
	 */
	@Override
	@Deprecated
	public List<String> getFields()
	{
		return getFieldNames();
	}

	@Override
	public List<String> getFieldNames()
	{
		if (fieldNames == null)
		{
			fieldNames = new ArrayList<String>();
			try
			{
				int colcount = metadata.getColumnCount();
				for (int i = 1; i <= colcount; i++)
				{
					String name = metadata.getColumnName(i);
					if (name == null)
					{
						throw new RuntimeException("column with id " + i + " == null");
					}
					else
					{
						fieldNames.add(metadata.getColumnName(i));
					}
				}

			}
			catch (Exception e)
			{
				final String errorMsg = "getColumnNames(): failed " + e;
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			}
		}
		return fieldNames;
	}

	public List<String> getColumnLabels()
	{
		if (columnLabels == null)
		{
			columnLabels = new ArrayList<String>();
			try
			{
				for (int colIdx = 1, n = metadata.getColumnCount(); colIdx < n; ++colIdx)
				{
					columnLabels.add(metadata.getColumnLabel(colIdx));
				}
			}
			catch (Exception ex)
			{
				final String errorMsg = "getColumnNames(): failed " + ex;
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			}
		}
		return columnLabels;
	}

	/**
	 * @deprecated Unsupported operation for a JDBCTuple
	 */
	@Deprecated
	@Override
	public void set(String columnName, Object value)
	{
		throw new UnsupportedOperationException("set(String,Object) failed: cannot set values on a "
				+ this.getClass().getSimpleName() + "!");
	}

	/**
	 * @deprecated Unsupported operation for a JDBCTuple
	 */
	@Deprecated
	@Override
	public void set(int columnIndex, Object value)
	{
		throw new UnsupportedOperationException("set(int,Object) failed: Cannot set values on a "
				+ this.getClass().getSimpleName() + "!");

	}

	/**
	 * @deprecated Unsupported operation for a JDBCTuple
	 */
	@Deprecated
	@Override
	public void set(Object[] values)
	{
		throw new UnsupportedOperationException("set(Object[]) failed: Cannot set values on a "
				+ this.getClass().getSimpleName() + "!");

	}

	@Override
	public Integer getInt(int columnIndex)
	{
		try
		{
			if (resultset.getObject(columnIndex + 1) == null)
			// watchout, null != false!
			return null;
			return resultset.getInt(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Integer getInt(String columnName)
	{
		try
		{
			if (resultset.getObject(columnName) == null)
			{
				// watchout, null != false!
				return null;
			}
			else
			{
				return resultset.getInt(columnName);
			}
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Long getLong(int columnIndex)
	{
		try
		{
			return resultset.getLong(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Long getLong(String columnName)
	{
		try
		{
			return resultset.getLong(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Boolean getBoolean(int columnIndex)
	{
		try
		{
			if (resultset.getObject(columnIndex + 1) == null)
			{
				return null;
			}
			else
			{
				return resultset.getBoolean(columnIndex + 1);
			}
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Boolean getBoolean(String columnName)
	{
		try
		{
			if (resultset.getObject(columnName) == null)
			{
				return null;
			}
			else
			{
				return resultset.getBoolean(columnName);
			}
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Double getDecimal(int columnIndex)
	{
		try
		{
			if (resultset.getObject(columnIndex + 1) == null)
			// haat, null != false!
			return null;
			return resultset.getDouble(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Double getDecimal(String columnName)
	{
		try
		{
			if (resultset.getObject(columnName) == null)
			// haat, null != false!
			return null;
			return resultset.getDouble(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public String getString(int columnIndex)
	{
		try
		{
			return resultset.getString(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public String getString(String columnName)
	{
		try
		{
			return resultset.getString(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Object getObject(int columnIndex)
	{
		try
		{
			return resultset.getString(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Object getObject(String columnName)
	{
		try
		{
			return resultset.getString(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public java.sql.Date getDate(String columnName)
	{
		try
		{
			return resultset.getDate(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Date getDate(int columnIndex) throws ParseException
	{
		try
		{
			return resultset.getDate(columnIndex);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Timestamp getTimestamp(String columnName)
	{
		try
		{
			return resultset.getTimestamp(columnName);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex)
	{
		try
		{
			return resultset.getTimestamp(columnIndex + 1);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	/**
	 * @deprecated Unsupported operation for a JDBCTuple
	 */
	@Deprecated
	@Override
	public List<?> getList(String columnName)
	{
		throw new UnsupportedOperationException("getList(String) failed: cannot retrieve a list from a "
				+ this.getClass().getSimpleName() + "!");
	}

	/**
	 * @deprecated Unsupported operation for a JDBCTuple
	 */
	@Deprecated
	@Override
	public List<?> getList(int columnIndex)
	{
		throw new UnsupportedOperationException("getList(int) failed: cannot retrieve a list from a "
				+ this.getClass().getSimpleName() + "!");
	}

	@Override
	public String getColName(int i)
	{
		if (this.getNrColumns() > i) try
		{
			// idiots, they start from 1
			return metadata.getColumnName(i + 1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean next() throws SQLException
	{
		return this.resultset.next();
	}

	public void close() throws SQLException
	{
		this.resultset.close();
	}

	public int getSqlType(int colIdx) throws SQLException
	{
		return this.metadata.getColumnType(colIdx);
	}
}
