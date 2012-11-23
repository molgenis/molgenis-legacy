/**
 * File: invengine.db.Result<br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved<br>
 * Changelog:
 * <ul>
 * <li>2005-03-21; 1.0.0; RA Scheltema; Creation.
 * <li>2006-04-15; 1.0.0; MA Swertz Documentation.
 * </ul>
 */
package org.molgenis.util;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.molgenis.model.elements.Field;

/**
 * Un-typed data objects that do not enforce data structure.
 * <p>
 * Tuple behaves like a decorated map with type-specific getters to ease data
 * type conversions from/to String. Important is its "null" behaviour. The map
 * knows explicity about nulls and does not transform to default values (e.g., a
 * null integer is not returned as 0).
 * <p>
 * A Tuple can be used in places where the data type is not yet clear, or when
 * data needs to be explicitly converted. It is loosely inspirated on
 * {@link java.sql.ResultSet java.sql.ResultSet} but than extended to be more
 * broadly useable than only JDBC. We also use it to map HttpRequest into the
 * Java world.
 * <p>
 * FIXME does SUN already have this data type??? Because we need 'escaping'.
 */
public interface Tuple
{
	/**
	 * Set multiple columns by using the array indexes as columnIndexes.
	 * 
	 * @param strings
	 */
	public void set(Object[] strings);

	/**
	 * Set a column.
	 * 
	 * @param columnName
	 *            name of the column
	 * @param value
	 *            value of the column
	 */
	public void set(String columnName, Object value);

	/**
	 * Set a column.
	 * 
	 * @param columnIndex
	 *            index of the column
	 * @param value
	 *            value of the column
	 */
	public void set(int columnIndex, Object value);

	/**
	 * Count the number of fields in this Tuple
	 * 
	 * @return number of columns.
	 */
	public int getNrColumns();

	/**
	 * Get the names used as aliases for the field indexes
	 * 
	 * @return column names.
	 */
	public List<String> getFieldNames();

	/**
	 * Get the names used as aliases for the field indexes TODO: Bad function
	 * name; use getFieldNames instead.
	 * 
	 * @return column names.
	 */
	@Deprecated
	public List<String> getFields();

	/**
	 * Retrieves the value of the designated column as Integer.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Integer object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Integer getInt(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Integer.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Integer object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Integer getInt(String columnName);

	/**
	 * Retrieves the value of the designated column as Long.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Long object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Long getLong(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Long.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Long object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Long getLong(String columnName);

	/**
	 * Retrieves the value of the designated column as Boolean.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Boolean object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Boolean getBool(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Boolean.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Boolean object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Boolean getBool(String columnName);

	/**
	 * Retrieves the value of the designated column as Boolean.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Boolean object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Boolean getBoolean(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Boolean.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Boolean object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public Boolean getBoolean(String columnName);

	/**
	 * Retrieves the value of the designated column as java.sql.Date.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return java.sql.Date object for the column value. If the value is NULL
	 *         then the value is null.
	 */
	public java.sql.Date getDate(String columnName) throws ParseException;

	/**
	 * Retrieves the value of the designated column as java.sql.Date.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return java.sql.Date object for the column value. If the value is NULL
	 *         then the value is null.
	 */
	public java.sql.Date getDate(int columnIndex) throws ParseException;

	/**
	 * Retrieves the value of the designated column as Double.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Double object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Double getDecimal(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Double.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Double object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Double getDecimal(String columnName);

	/**
	 * Retrieves the value of the designated column as Double.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return Double object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Double getDouble(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Double.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return Double object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Double getDouble(String columnName);

	/**
	 * Retrieves the value of the designated column as String.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return String object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public String getString(int columnIndex);

	/**
	 * Retrieves the value of the designated column as String.
	 * 
	 * @param columnName
	 *            name of the column.
	 * @return String object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public String getString(String columnName);

	public Integer getOnoff(String columnName);

	public String getNSequence(String column) throws ParseException;

	/**
	 * Retrieves the value of the designated column as Object.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return raw Object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Object getObject(int columnIndex);

	/**
	 * Retrieves the value of the designated column as Object.
	 * 
	 * @param columnName
	 *            name of the column.
	 * @return raw Object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public Object getObject(String columnName);

	/**
	 * Retrieves the value of the designated column as java.sql.Timestamp.
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return java.sql.Timestamp object for the column value. If the value is
	 *         NULL then the value is null.
	 */
	public java.sql.Timestamp getTimestamp(int columnIndex) throws ParseException;

	/**
	 * Retrieves the value of the designated column as java.sql.Timestamp.
	 * 
	 * @param columnName
	 *            name of the column
	 * @return java.sql.Timestamp object for the column value. If the value is
	 *         NULL then the value is null.
	 */
	public java.sql.Timestamp getTimestamp(String columnName) throws ParseException;

	/**
	 * Retrieves the value of the designated column as List<?>.
	 * 
	 * FIXME: make generic?
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return List<?> object for the column value. If the value is NULL then
	 *         the value is null.
	 */
	public List<?> getList(int columnIndex);

	/**
	 * Retrieves the value of the designated column as List<?> by parsing the
	 * underlyingFIXME: make generic?
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @param sep
	 *            separator that needs to be used
	 * @return List<?> for the column value. If the value is NULL then the value
	 *         is null.
	 */
	public List<?> getList(int columnIndex, char sep);

	/**
	 * Retrieves the value of the designated column as List<?>. If the the value
	 * is instanceof String then | is used as separator. Alternatively use @see
	 * getList(String columnName, char sep). FIXME: make generic?
	 * 
	 * @param columnName
	 *            name of the column.
	 * @return List<?> for the column value. If the value is NULL then the value
	 *         is null.
	 */
	public List<?> getList(String columnName);

	/**
	 * Retrieves the value of the designated column as List<?> by parsing the
	 * underlyingFIXME: make generic?
	 * 
	 * @param columnName
	 *            name of the column.
	 * @param sep
	 *            separator that needs to be used
	 * @return List<?> for the column value. If the value is NULL then the value
	 *         is null.
	 */
	public List<?> getList(String columnName, char sep);

	/**
	 * Retrieves the value of the designated column as Set<Object>
	 * 
	 * @param string
	 * @return
	 */
	public Set<Object> getSet(String columnName);

	/**
	 * Retrieves the value of the designated column as Set<Object> by parsing an
	 * underlying string value
	 * 
	 * @param string
	 * @return
	 */
	public Set<Object> getSet(String columnName, char sep);

	/**
	 * Retrieves the value of the designated column as File
	 * 
	 * @param columnName
	 *            name of the column
	 * @return File object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public File getFile(String columnName);

	/**
	 * Retrieves the value of the designated column as File
	 * 
	 * @param columnIndex
	 *            the first column is 1, second is 2, etc, last is
	 *            getNrColumns()
	 * @return File object for the column value. If the value is NULL then the
	 *         value is null.
	 */
	public File getFile(int columnIndex);

	/**
	 * Pretty print this Tuple.
	 * 
	 * @return print of this Tuple.
	 */
	@Override
	public String toString();

	/**
	 * Size of this tuple
	 */
	public int size();

	public boolean notNull(int columnIndex);

	public boolean notNull(String columnName);

	/**
	 * Get the colname for a columIndex. Motivation is that columNames are not
	 * necessarily unique.
	 * 
	 * @param i
	 * @return name string of column
	 */
	public String getColName(int i);

	/** Easy helper function to getString("__action") value */
	public String getAction();

	public <E extends Object> void set(List<E> values);

	public boolean isNull(String string);

	public boolean isNull(int column);

	public List<String> getStringList(String string);

	List<String> getStringList(int column);

	/**
	 * Automatically get the entity using the tuple to set it.
	 * 
	 * This is a shorthand for: EntityClass e = entityClass.newInstance();
	 * e.set(tuple);
	 * 
	 * @throws Exception
	 *             anything can go wrong
	 */
	public <E extends Entity> E getEntity(Class<E> entityClass) throws Exception;

	List<Field> getFieldTypes();
}