package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.datatable.model.MemoryTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class MemoryTableFactory
{
	public static TupleTable create()
	{
		return create(5,5);
	}

	public static TupleTable create(int nrows, int ncols)
	{
		List<Tuple> rows = new ArrayList<Tuple>();

		for (int i = 1; i <= nrows; i++)
		{
			Tuple t = new SimpleTuple();
			for (int j = 1; j <= ncols; j++)
			{
				t.set("col" + j, "val" + j + "," + i);
			}
			rows.add(t);
		}

		return new MemoryTable(rows);
	}
}
