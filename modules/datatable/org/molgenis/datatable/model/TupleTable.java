package org.molgenis.datatable.model;

import java.util.Iterator;
import java.util.List;

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
 * reusable across heterogeneous backends without extra work, such as database tables, Excel files,
 * EAV models, binary formats, etc.
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
}
