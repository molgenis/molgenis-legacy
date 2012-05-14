package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.fieldtypes.StringField;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * Wrap a List<Tuple> into a TupleTable
 */
public class MemoryTable implements TupleTable
{
	private List<Field> columns = new ArrayList<Field>();
	private List<Tuple> rows = new ArrayList<Tuple>();

	/**
	 * Construct from list of tuples. Field will be derived based on column
	 * names and value type of first tuple. Otherwise field type will be String.
	 */
	public MemoryTable(List<Tuple> rows)
	{
		this.rows = rows;

		// use first row
		if (rows.size() > 0)
		{
			for (String field : rows.get(0).getFieldNames())
			{
				Field f = new Field(field);
				f.setType(new StringField());
				columns.add(f);
			}
		}
	}

	@Override
	public List<Field> getColumns()
	{
		return this.columns;
	}

	@Override
	public List<Tuple> getRows()
	{
		return this.rows;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return this.rows.iterator();
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

}
