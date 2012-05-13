package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.fieldtypes.StringField;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * Simple in memory tuple table
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
			for (String field : rows.get(0).getFields())
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

}
