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
 * Purpose: this can be used as facade pattern to make views that are
 * reusable across heterogeneous backends without extra work, such as database
 * tables, Excel files, EAV models, binary formats, etc.
 */
public interface TupleTable extends Iterable<Tuple>
{
	/**
	 * Get meta data describing the columns in current view (within
	 * colLimit/colOffset)
	 */
	public List<Field> getColumns() throws TableException;

	/** Get the data rows in current view (within limit/offset) */
	public List<Tuple> getRows() throws TableException;

	/** Get the data in a streaming fashion (good for large data sets) */
        @Override
	public Iterator<Tuple> iterator();

	/** Closes the resources from which table reads data */
	public void close() throws TableException;

	/** Change the visible row window */
	public void setLimitOffset(int limit, int offset);

	/**
	 * Get count of all records in the TupleTable
	 * 
	 * @throws TableException
	 */
	public int getCount() throws TableException;

	/**
	 * Get count of all columns in the TupleTable
	 * 
	 * @throws TableException
	 */
	public int getColCount() throws TableException;

	/**
	 * get row limit
	 */
	public int getLimit();

	/**
	 * get column limit
	 */
	public int getColLimit();

	/**
	 * set row limit (i.e. limit the current viewport to the full underlying
	 * table). Default: 0 (no limit).
	 */
	public void setLimit(int limit);

	/**
	 * set column limit (i.e. limit the current viewport to the full underlying
	 * table). Default: 0 (no limit)
	 */
	public void setColLimit(int limit);

	/**
	 * get row offset. Default: 0
	 */
	public int getOffset();

	/**
	 * get column offset. Default: 0
	 */
	public int getColOffset();

	/**
	 * set row limit (i.e. limit the current viewport to the full underlying
	 * table). Default: 0 (no offset)
	 */
	public void setOffset(int offset);

	/**
	 * set column limit (i.e. limit the current viewport to the full underlying
	 * table). Default: 0 (no offset)
	 */
	public void setColOffset(int offset);

	/**
	 * For database centric tables you may need to refresh the database
	 * connection
	 */
	public void setDb(Database db);
}
