package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.fieldtypes.StringField;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Wrap a List<Tuple> into a TupleTable
 */
public class MemoryTable extends AbstractTupleTable
{
	private List<Field> columns = new ArrayList<Field>();
	private List<Tuple> rows = new ArrayList<Tuple>();

	/**
	 * Construct from list of tuples. Field will be derived based on column
	 * names and value type of first tuple. Otherwise field type will be String.
	 */
	public MemoryTable(List<Tuple> rows)
	{
		if (rows == null) throw new NullPointerException("Creation of MemoryTable failed: rows == null");

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
	public List<Field> getAllColumns()
	{
		return this.columns;
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		List<String> columns = new ArrayList<String>();
		for (Field f : getColumns())
			columns.add(f.getName());

		List<Tuple> result = new ArrayList<Tuple>();
		if (getLimit() > 0 || getOffset() > 0)
		{
			int count = 0;
			int index = 1;
			for (Tuple row : this.rows)
			{
				if (index > getOffset())
				{
					SimpleTuple copy = new SimpleTuple(columns);
					for (String col : columns)
					{
						copy.set(col, row.getObject(col));
					}
					result.add(new SimpleTuple(copy));

					count++;
					if (count >= getLimit()) break;
				}
				index++;
			}
		}
		else
		{
			for (Tuple row : this.rows)
			{
				SimpleTuple copy = new SimpleTuple(columns);
				for (String col : columns)
				{
					copy.set(col, row.getObject(col));
				}
				result.add(new SimpleTuple(copy));
			}
		}
		return result;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			return this.getRows().iterator();
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getCount() throws TableException
	{
		return rows.size();
	}
}
