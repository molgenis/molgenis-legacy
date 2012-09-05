package org.molgenis.datatable.test;

import java.io.File;
import java.util.Iterator;

import org.molgenis.datatable.model.PedMapTupleTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestPedMapTable
{
	TupleTable table;

	@BeforeClass
	public void setup() throws Exception
	{
		table = new PedMapTupleTable(new File(TestPedMapTable.class.getResource("test.ped").getFile()), new File(
				TestPedMapTable.class.getResource("test.map").getFile()));
	}

	@Test
	public void testWithoutLimit()
	{
		Iterator<Tuple> iter = table.iterator();
		while (iter.hasNext())
		{
			System.out.println(iter.next().toString());
		}
	}

	public void testRowLimitOffset()
	{

	}

	public void testColLimitOffset()
	{
		// offset only

		// limit only

		// limit + offset
	}

}