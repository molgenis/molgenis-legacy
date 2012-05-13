package org.molgenis.datatable.model;

import java.util.Iterator;
import java.util.List;

import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * TupleTable is a simple abstraction for tabular data (columns X rows).
 * <ul>
 * <li>Column names are unique.
 * <li>Column meta data (name,type,label,description,..) are available in getColumns()
 * <li>Row data is availble as Tuple (hashMap)
 * </ul>
 * 
 * Motivation: this can be used as adapter pattern to make views that are
 * reusable accross heterogeneous backends such as database tables, Excel files,
 * EAV models, binary formats, etc.
 */
public interface TupleTable extends Iterable<Tuple>
{
	/** Get meta data describing the columns */
	public List<Field> getColumns();

	/** Get the data */
	public List<Tuple> getRows();

	/** Get the data in a streaming fashion (good for large data sets) */
	public Iterator<Tuple> iterator();

}
