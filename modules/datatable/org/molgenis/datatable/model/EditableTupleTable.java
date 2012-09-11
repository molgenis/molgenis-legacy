package org.molgenis.datatable.model;

import org.molgenis.util.Tuple;

/** Methods for editing a table */
public interface EditableTupleTable extends TupleTable
{
	public void add(Tuple tuple) throws TableException;

	public void update(Tuple tuple) throws TableException;

	public void remove(Tuple tuple) throws TableException;
}
