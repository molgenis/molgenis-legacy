package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Wrap a List<Tuple> into a TupleTable.
 */
// Very naive; for performance this should be implemented using Object[][] or
// even typed versions thereof.
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
		List<Tuple> result = new ArrayList<Tuple>();

		int count = 0;
		int index = 1;
		int colIndex = 1;
		int colCount = 0;

		for (Tuple row : this.rows)
		{
			if (getOffset() == 0 || index > getOffset())
			{
				if (this.getColLimit() > 0 || this.getColOffset() > 0)
				{
					Tuple limitedRow = new SimpleTuple();
					colIndex = 1;
					colCount = 0;

					for (Field f : this.getColumns())
					{
						if (getColOffset() == 0 || colIndex > this.getColOffset())
						{
							limitedRow.set(f.getName(), row.getObject(f.getName()));
							colCount++;
							if (colCount >= this.getColLimit()) break;
						}
						colIndex++;
					}
					result.add(limitedRow);
				}
				else
				{
					result.add(row);
				}
				count++;
				if (getLimit() > 0 && count >= getLimit()) break;
			}
			index++;
		}
		return result;
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
	public int getCount() throws TableException
	{
		return rows.size();
	}

	@Override
	public void setDb(Database db)
	{
	}
}
