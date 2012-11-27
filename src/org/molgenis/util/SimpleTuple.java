package org.molgenis.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import org.molgenis.model.elements.Field;

/**
 * Simple Map based implementation of Tuple. By default the values are added by
 * columnName. If you use columnIndexes an implicit columnName is created: <li>
 * '_column_no1' for columnIndex 1, <li>'_column_no2' for columnIndex 2, etc.
 */
public class SimpleTuple implements Tuple
{
	/** Map where values are stored */
	private List<String> keys = new ArrayList<String>();
	private List<Field> fieldTypes = new ArrayList<Field>();
	private Map<String, Integer> lowercaseKeyToId = new LinkedHashMap<String, Integer>();
	private List<Object> values = new ArrayList<Object>();

	/**
	 * String format used for Dates: {@value} .
	 */
	public static final String DATEFORMAT = "MMMM d, yyyy";

	/**
	 * String format used for Dates: {@value} .
	 */
	public static final String DATEFORMAT2 = "dd-MM-yyyy";

	/**
	 * String format used for Dates: {@value} .
	 */
	public static final String DATEFORMAT3 = "yyyy-MM-dd";

	/**
	 * String format used for Timestamps: {@value} .
	 */
	public static final String DATETIMEFORMAT = "MMMM d, yyyy, HH:mm:ss";
	/**
	 * Alternative String format used for Dates: {@value} .
	 */
	public static final String DATETIMEFORMAT2 = "dd-MM-yyyy HH:mm";

	/**
	 * Alternative String format used for Dates: {@value} .
	 */
	public static final String DATETIMEFORMAT3 = "yyyy-MM-dd HH:mm:ss";

	/** Construct an empty Tuple */
	public SimpleTuple()
	{
	}

	/**
	 * Copy constructor
	 */
	public SimpleTuple(Tuple t)
	{
		for (String name : t.getFieldNames())
		{
			this.set(name, t.getObject(name));
		}
	}

	/**
	 * Construct an empty tuple using predefined keys. This is required if one
	 * wants to influence the order of columnNames and columnIndexes.
	 * 
	 * 
	 * Note: duplicated colum names will be mapped to last occuring column
	 * index! TODO disable use of get(String name) in this case.
	 * 
	 * @param columnNames
	 *            a list that mapes columnIndexes to columnNames.
	 */
	public SimpleTuple(List<String> columnNames)
	{
		this.keys = columnNames;

		for (Integer i = 0; i < this.keys.size(); i++)
		{
			this.lowercaseKeyToId.put(this.keys.get(i).toLowerCase(), i);
			// values need to be of same size as keys
			this.values.add(null);
			// temporary check

			// assert this.keys.size() <= this.values.size();
		}
	}

	public SimpleTuple(Map<String, Object> valueMap)
	{
		for (Entry<String, Object> entry : valueMap.entrySet())
		{
			this.set(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int getNrColumns()
	{
		return values.size();
	}

	@Override
	public List<String> getFieldNames()
	{
		// return a copy of the keys
		return new Vector<String>(this.keys);
	}

	/**
	 * Use getFieldNames instead.
	 * 
	 * @return
	 */
	@Override
	@Deprecated
	public List<String> getFields()
	{
		// return a copy of the keys
		return new Vector<String>(this.keys);
	}

	/**
	 * Watch out! should keep order the same
	 */
	public void setFields(List<String> fields)
	{
		if (fields.size() != this.keys.size()) throw new RuntimeException(
				"SetFields expects vector of same length as original getFields");
		this.keys = fields;

		for (Integer i = 0; i < this.keys.size(); i++)
		{
			this.lowercaseKeyToId.put(this.keys.get(i).toLowerCase(), i);
			// values need to be of same size as keys
			// this.values.add(null);
			// temporary check

			// assert this.keys.size() <= this.values.size();
		}
	}

	@Override
	public Object getObject(int column)
	{
		try
		{
			return this.values.get(column);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public Object getObject(String column)
	{
		String lowerCase = column.toLowerCase().trim();
		if (this.lowercaseKeyToId.get(lowerCase) != null)
		{
			return values.get(this.lowercaseKeyToId.get(lowerCase));
		}
		return null;
	}

	@Override
	public void set(int column, Object value)
	{
		// update existing
		if (column < this.values.size())
		{
			this.values.set(column, value);
		}
		// or add new
		else
		{
			this.set("UNNAMED" + this.values.size(), value);
		}
	}

	@Override
	public void set(String column, Object value)
	{
		if (column == null)
		{
			throw new RuntimeException("empty column");
		}

		// update existing?
		String lowerCase = column.toLowerCase().trim();
		if (this.lowercaseKeyToId.containsKey(lowerCase))
		{
			this.values.set(this.lowercaseKeyToId.get(lowerCase), value);
		}
		// else add new
		else
		{
			this.lowercaseKeyToId.put(lowerCase, this.values.size());
			this.keys.add(column);
			this.values.add(value);
		}
	}

	@Override
	public void set(Object[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			this.set(i, values[i]);
		}
	}

	@Override
	public <E> void set(List<E> values)
	{
		for (int i = 0; i < values.size(); i++)
		{
			this.set(i, values.get(i));
		}
	}

	@Override
	public Integer getInt(int column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return Integer.valueOf(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("field '" + column + "' expects integer value between " + Integer.MIN_VALUE
					+ " and " + Integer.MAX_VALUE + " instead of '" + getString(column) + "'");
		}
	}

	@Override
	public Integer getInt(String column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return Integer.valueOf(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("field '" + column + "' expects integer value between " + Integer.MIN_VALUE
					+ " and " + Integer.MAX_VALUE + " instead of '" + getString(column) + "'");
		}
	}

	@Override
	public Integer getOnoff(String column)
	{
		if (getObject(column) == null) return 0;

		if (getObject(column).equals("on") || getObject(column).equals(1) || getObject(column).equals("1")) return 1;

		return 0;
	}

	@Override
	public Long getLong(int column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return Long.valueOf(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("field '" + column + "' expects long integer value between "
					+ Long.MIN_VALUE + " and " + Long.MAX_VALUE + " instead of '" + getString(column) + "'");
		}
	}

	@Override
	public Long getLong(String column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return Long.valueOf(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			// try scientific notation
			try
			{
				DecimalFormat df = new DecimalFormat();
				// uppercase e to E, remove + as it is not parsed.
				Number n = df.parse(getString(column).toUpperCase().replace("+", ""));
				return n.longValue();
			}
			catch (Exception e)
			{
				throw new NumberFormatException("field '" + column + "' expects long long value between "
						+ Long.MIN_VALUE + " and " + Long.MAX_VALUE + " instead of '" + getString(column) + "'");
			}

		}

	}

	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Could be null under some circumstances")
	public Boolean getBoolean(int column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		return Boolean.valueOf(getString(column).toLowerCase().equals("true") || getString(column).trim().equals("1")
				|| getString(column).trim().equalsIgnoreCase("on"));
	}

	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Could be null under some circumstances")
	public Boolean getBoolean(String column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		return Boolean.valueOf(getString(column).toLowerCase().equals("true") || getString(column).trim().equals("1")
				|| getString(column).trim().equalsIgnoreCase("on"));
	}

	@Override
	public Double getDecimal(int column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return Double.valueOf(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("field '" + column + " expects decimal value instead of '"
					+ getString(column) + "'");
		}
	}

	@Override
	public Double getDecimal(String column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		try
		{
			return new Double(getString(column));
		}
		catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("field '" + column + " expects decimal value instead of '"
					+ getString(column) + "'");
		}
	}

	@Override
	public String getString(int column)
	{
		if (getObject(column) == null) return null; // MySql allows not-null
		// varchars to be null
		// (=empty string)
		return getObject(column).toString().trim();
	}

	/**
	 * Using the IUB codes for nucleotides
	 * http://www.tigr.org/tdb/CMR/IUBcodes.html
	 */
	@Override
	public String getNSequence(String column) throws ParseException
	{
		if (getObject(column) == null) return null; // MySql allows not-null

		Vector<String> IUBnuc = new Vector<String>();
		// normal nucleotides
		IUBnuc.add("a");
		IUBnuc.add("A");
		IUBnuc.add("c");
		IUBnuc.add("C");
		IUBnuc.add("g");
		IUBnuc.add("G");
		IUBnuc.add("t");
		IUBnuc.add("T");
		// rna
		IUBnuc.add("u");
		IUBnuc.add("U");
		// two possibilities
		IUBnuc.add("m");
		IUBnuc.add("M");
		IUBnuc.add("r");
		IUBnuc.add("R");
		IUBnuc.add("w");
		IUBnuc.add("W");
		IUBnuc.add("s");
		IUBnuc.add("S");
		IUBnuc.add("y");
		IUBnuc.add("Y");
		IUBnuc.add("k");
		IUBnuc.add("K");
		// three possibilities
		IUBnuc.add("v");
		IUBnuc.add("V");
		IUBnuc.add("h");
		IUBnuc.add("H");
		IUBnuc.add("d");
		IUBnuc.add("D");
		IUBnuc.add("b");
		IUBnuc.add("B");
		// four possibilities
		IUBnuc.add("x");
		IUBnuc.add("X");
		IUBnuc.add("n");
		IUBnuc.add("N");

		String result = this.getString(column);
		String[] lines = result.split("\\n");
		StringBuilder cleanresultBuilder = new StringBuilder();
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			line = line.replaceAll("\\s", "");
			line = line.replaceAll("^[0-9]*", "");
			cleanresultBuilder.append(line);
		}

		result = cleanresultBuilder.toString();

		Vector<Integer> nonIUBpos = new Vector<Integer>();

		for (int i = 0; i < result.length(); i++)
		{
			if (!IUBnuc.contains(result.substring(i, i + 1)))
			{
				nonIUBpos.add(i);
			}
		}
		if (nonIUBpos.size() > 0)
		{
			StringBuilder invalidBuilder = new StringBuilder();
			invalidBuilder.append("there are ").append(nonIUBpos.size())
					.append(" non-IUB characters in your sequence.");
			for (Integer pos : nonIUBpos)
			{
				invalidBuilder.append(" '").append(result.charAt(pos)).append("' on position ");
				invalidBuilder.append(pos + 1).append('.');
			}
			throw new ParseException(invalidBuilder.toString(), 0);
		}
		return result;
	}

	@Override
	public String getString(String column)
	{
		if (getObject(column) == null) return null;
		return getObject(column).toString().trim();
	}

	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Could be null under some circumstances")
	public Boolean getBool(int column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		return getBoolean(column);
	}

	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Could be null under some circumstances")
	public Boolean getBool(String column)
	{
		if (getObject(column) == null || getString(column).isEmpty()) return null;
		return getBoolean(column);
	}

	@Override
	public java.sql.Date getDate(int column) throws ParseException
	{
		if (this.getObject(column) == null || this.getString(column).isEmpty()) return null;
		if (this.getObject(column) instanceof java.sql.Date) return (java.sql.Date) this.getObject(column);
		if (this.getObject(column) instanceof java.sql.Timestamp) return (java.sql.Date) this.getObject(column);
		if (this.getObject(column) instanceof java.util.Date) return new java.sql.Date(
				((java.util.Date) this.getObject(column)).getTime());

		// FIXME copy of getDate(String column)
		try
		{
			DateFormat formatter = new SimpleDateFormat(DATEFORMAT, Locale.US);
			return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
		}
		catch (ParseException pe)
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat(DATEFORMAT2, Locale.US);
				return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
			}
			catch (ParseException pe2)
			{
				try
				{
					DateFormat formatter = new SimpleDateFormat(DATEFORMAT3, Locale.US);
					return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
				}
				catch (ParseException pe3)
				{
					throw new ParseException("field '" + column + " expects date value formatted '" + DATEFORMAT
							+ " instead of '" + getString(column) + "' for field '" + column + "'", pe.getErrorOffset());
				}
			}
		}
	}

	@Override
	public java.sql.Date getDate(String column) throws ParseException
	{
		if (this.getObject(column) == null || this.getString(column).isEmpty()) return null;
		if (this.getObject(column) instanceof java.sql.Date) return (java.sql.Date) this.getObject(column);
		if (this.getObject(column) instanceof java.sql.Timestamp) return (java.sql.Date) this.getObject(column);
		if (this.getObject(column) instanceof java.util.Date) return new java.sql.Date(
				((java.util.Date) this.getObject(column)).getTime());

		System.err.println(this.getString(column));

		try
		{
			DateFormat formatter = new SimpleDateFormat(DATEFORMAT, Locale.US);
			return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
		}
		catch (ParseException pe)
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat(DATEFORMAT2, Locale.US);
				return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
			}
			catch (ParseException pe2)
			{
				try
				{
					DateFormat formatter = new SimpleDateFormat(DATEFORMAT3, Locale.US);
					return new java.sql.Date(formatter.parse(this.getString(column)).getTime());
				}
				catch (ParseException pe3)
				{
					throw new ParseException("field '" + column + " expects date value formatted '" + DATEFORMAT
							+ " instead of '" + getString(column) + "' for field '" + column + "'", pe.getErrorOffset());
				}
			}
		}
	}

	@Override
	public Double getDouble(int column)
	{
		return getDecimal(column);
	}

	@Override
	public Double getDouble(String column)
	{
		return getDecimal(column);
	}

	@Override
	public String toString()
	{
		if (this.getNrColumns() == 0) return "EMPTY TUPLE";
		StringBuilder resultBuilder = new StringBuilder();
		for (int columnIndex = 0; columnIndex < this.getNrColumns(); columnIndex++)
		{
			if (getColName(columnIndex) != null)
			{
				resultBuilder.append(getColName(columnIndex)).append("='");
				resultBuilder.append(getObject(columnIndex)).append("' ");
			}
			else
			{
				resultBuilder.append(columnIndex).append("='").append(getObject(columnIndex)).append("' ");
			}
		}
		if (resultBuilder.length() > 0)
		{
			resultBuilder.deleteCharAt(resultBuilder.length() - 1);
		}
		return resultBuilder.toString();
	}

	@Override
	public File getFile(String string)
	{
		if (!isNull(string)) return (File) getObject(string);
		return null;
	}

	@Override
	public File getFile(int column)
	{
		if (!isNull(column)) return (File) getObject(column);
		return null;
	}

	@Override
	public Timestamp getTimestamp(String column) throws ParseException
	{
		if (this.getObject(column) == null || this.getString(column).isEmpty()) return null;
		if (this.getObject(column) instanceof java.sql.Timestamp) return (java.sql.Timestamp) this.getObject(column);
		try
		{
			DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT, Locale.US);
			formatter.setTimeZone(TimeZone.getDefault());
			return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
		}
		catch (ParseException pe)
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT2, Locale.US);
				formatter.setTimeZone(TimeZone.getDefault());
				return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
			}
			catch (ParseException pe2)
			{
				try
				{
					DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT3, Locale.US);
					formatter.setTimeZone(TimeZone.getDefault());
					return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
				}
				catch (ParseException pe3)
				{
					try
					{
						return new java.sql.Timestamp(this.getDate(column).getTime());
					}
					catch (ParseException pe4)
					{
						throw new ParseException("field '" + column + "' expects datetime value formatted '"
								+ DATETIMEFORMAT + "' or '" + DATETIMEFORMAT2 + "' or '" + DATEFORMAT + "' or '"
								+ DATEFORMAT2 + "' or '" + DATEFORMAT3 + "' instead of '" + getString(column) + "'",
								pe.getErrorOffset());
					}
				}
			}
		}
	}

	@Override
	public Timestamp getTimestamp(int column) throws ParseException
	{
		if (this.getObject(column) == null || this.getString(column).isEmpty()) return null;
		if (this.getObject(column) instanceof java.sql.Timestamp) return (java.sql.Timestamp) this.getObject(column);
		try
		{
			DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT, Locale.US);
			formatter.setTimeZone(TimeZone.getDefault());
			return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
		}
		// FIXME: copy of getTimestamp(String column)
		catch (ParseException pe)
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT2, Locale.US);
				formatter.setTimeZone(TimeZone.getDefault());
				return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
			}
			catch (ParseException pe2)
			{
				try
				{
					DateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT3, Locale.US);
					formatter.setTimeZone(TimeZone.getDefault());
					return new java.sql.Timestamp(formatter.parse(this.getString(column)).getTime());
				}
				catch (ParseException pe3)
				{
					try
					{
						return new java.sql.Timestamp(this.getDate(column).getTime());
					}
					catch (ParseException pe4)
					{
						throw new ParseException("field '" + column + "' expects datetime value formatted '"
								+ DATETIMEFORMAT + "' or '" + DATETIMEFORMAT2 + "' or '" + DATETIMEFORMAT3 + "' or '"
								+ DATEFORMAT + "' or '" + DATEFORMAT2 + "' or '" + DATEFORMAT3 + "' instead of '"
								+ getString(column) + "'", pe.getErrorOffset());
					}
				}
			}
		}
	}

	@Override
	public Set<Object> getSet(String column)
	{
		return new LinkedHashSet<Object>(this.getList(column, ListEscapeUtils.DEFAULT_SEPARATOR));
	}

	@Override
	public Set<Object> getSet(String column, char sep)
	{
		return new LinkedHashSet<Object>(this.getList(column, sep));
	}

	@Override
	public List<?> getList(String column)
	{
		return this.getList(column, ListEscapeUtils.DEFAULT_SEPARATOR);
	}

	@Override
	public List<?> getList(String column, char sep)
	{
		return toList(this.getObject(column), sep);
	}

	@Override
	public List<?> getList(int column)
	{
		return this.getList(column, ListEscapeUtils.DEFAULT_SEPARATOR);
	}

	@Override
	public List<?> getList(int column, char sep)
	{
		return toList(this.getObject(column), sep);
	}

	private List<?> toList(Object obj, char sep)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof List<?>)
		{
			return (List<?>) obj;
		}
		else if (obj instanceof String)
		{
			return ListEscapeUtils.toList((String) obj, sep, ListEscapeUtils.DEFAULT_ESCAPE_CHAR);
		}
		else
		{
			return Collections.singletonList(obj);
		}
	}

	@Override
	public int size()
	{
		return this.getNrColumns();
	}

	@Override
	public boolean notNull(int columnIndex)
	{
		return this.getObject(columnIndex) != null && !"".equals(this.getString(columnIndex));
	}

	@Override
	public boolean notNull(String columnName)
	{
		return this.getObject(columnName) != null && !"".equals(this.getString(columnName));
	}

	@Override
	public String getColName(int i)
	{
		if (this.keys.size() > i) return this.keys.get(i);
		return null;
	}

	@Override
	public String getAction()
	{
		return this.getString("__action");
	}

	@Override
	public boolean isNull(String columnName)
	{
		return !notNull(columnName);
	}

	@Override
	public boolean isNull(int column)
	{
		// TODO Auto-generated method stub
		return !notNull(column);
	}

	@Override
	public List<String> getStringList(String string)
	{
		List<String> result = new ArrayList<String>();
		List<?> objects = this.getList(string);
		for (Object o : objects)
		{
			if (o instanceof String) result.add((String) o);
			else if (o != null) result.add(o.toString());
		}
		return result;
	}

	@Override
	public List<String> getStringList(int column)
	{
		List<String> result = new ArrayList<String>();
		List<?> objects = this.getList(column);
		for (Object o : objects)
		{
			if (o instanceof String) result.add((String) o);
			else if (o != null) result.add(o.toString());
		}
		return result;
	}

	@Override
	public <E extends Entity> E getEntity(Class<E> entityClass)
	{
		E entity;
		try
		{
			entity = entityClass.newInstance();
			entity.set(this);
			return entity;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Field> getFieldTypes()
	{
		return fieldTypes;
	}

	public void setFieldTypes(List<Field> fieldTypes)
	{
		this.fieldTypes = fieldTypes;
	}

}
