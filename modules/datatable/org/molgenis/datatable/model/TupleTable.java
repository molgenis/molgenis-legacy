package org.molgenis.datatable.model;

import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * TupleTable is a simple abstraction for tabular data (columns X rows).
 * <ul>
 * <li>Column names are unique
 * <li>Column meta data (name,type,label,description,..) are available in
 * getColumns()
 * <li>Row data is available as Tuple (hashMap)
 * </ul>
 * 
 * Motivation: this can be used as facade pattern to make views that are
 * reusable across heterogeneous backends without extra work, such as database
 * tables, Excel files, EAV models, binary formats, etc.
 * 
 * Developer note: This class or its subclasses should
 * <ul>
 * <li>replace org.molgenis.util.TupleReader and its CsvReader, ExcelReader
 * subclasses
 * <li>be used as source for CsvWriter
 * <li>be used to replace org.molgenis.framework.db.AbstractPager (used for
 * database paging)
 * </ul>
 */
public interface TupleTable extends Iterable<Tuple>
{
	/** Get meta data describing the columns */
	public List<Field> getColumns() throws TableException;

	/** Get the data */
	public List<Tuple> getRows() throws TableException;

	/** Get the data in a streaming fashion (good for large data sets) */
	public Iterator<Tuple> iterator();

	/** Closes the resources from which table reads data */
	public void close() throws TableException;

	/** Change the visible rows */
	public void setLimitOffset(int limit, int offset);

	/**
	 * Get count of all records in the TupleTable
	 * 
	 * @throws TableException
	 */
	public int getCount() throws TableException;

	/**
	 * Get count of all columns in the TupleTable (for working with large
	 * tables)
	 * 
	 */
	public int getColCount() throws TableException;

	/**
	 * This we can inherit from Query interface?
	 */
	public int getLimit();
	
	/**
	 * In case of large tables you can have a limit on the columns shown.
	 */
	public int getColLimit();
	
	/**
	 * This we can inherit from Query interface?
	 */
	public void setLimit(int limit);
	
	/**
	 * In case of many columns one may to limit
	 */
	public void setColLimit(int limit);
	
	/**
	 * This we can inherit from Query interface?
	 */
	public int getOffset();
	
	/**
	 * In case of many columns
	 */
	public int getColOffset();

	/**
	 * This we can inherit from Query interface?
	 */
	public void setOffset(int offset);
	
	/**
	 * for column paging in larger tables.
	 */
	public void setColOffset(int offset);

	/** For database centric tables */
	public void setDb(Database db);
}
