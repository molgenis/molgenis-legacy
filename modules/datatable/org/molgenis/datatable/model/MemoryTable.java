package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * Wrap a List<Tuple> into a TupleTable
 */
public class MemoryTable implements TupleTable
{
	private List<Field> columns = new ArrayList<Field>();
	private List<Tuple> rows = new ArrayList<Tuple>();
	int limit = 0;
	int offset = 0;

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
		if (limit > 0 || offset > 0)
		{
			List<Tuple> result = new ArrayList<Tuple>();
			
			int count = 0;
			int index = 1;
			for(Tuple row: this.rows)
			{
				if(index > offset)
				{
					result.add(row);
					count++;
					if(count >= limit) break;
				}
				index++;
			}
			return result;
		}
		else
		{
			return this.rows;
		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return this.getRows().iterator();
	}

	@Override
	public void close()
	{
		// nothing todo
	}

	@Override
	public int getRowCount() throws TableException
	{
		return rows.size();
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.limit = limit;
		this.offset = offset;

	}
}
