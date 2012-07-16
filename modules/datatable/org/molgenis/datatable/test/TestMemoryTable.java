package org.molgenis.datatable.test;

import java.util.List;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMemoryTable
{
	List<Tuple> rows;
	TupleTable table;

	@BeforeClass
	public void setup()
	{
		table = MemoryTableFactory.create(5,5);
	}

	@Test
	public void test1() throws TableException
	{
		// check columns
		Assert.assertEquals("col1", table.getColumns().get(0).getName());
		Assert.assertEquals("col2", table.getColumns().get(1).getName());

		// check rows
		Assert.assertEquals(5, table.getRows().size());

		// check iterator
		int i = 1;
		for (Tuple row : table)
		{
			Assert.assertEquals(5, row.getFieldNames().size());

			Assert.assertEquals(true, row.getFieldNames().contains("col1"));
			Assert.assertEquals(true, row.getFieldNames().contains("col2"));

			Assert.assertEquals(row.getObject("col1"), "val1," + i);
			Assert.assertEquals(row.getObject("col2"), "val2," + i);

			i = i + 1;
		}
	}

	@Test
	public void testLimitOffset() throws TableException
	{
		table.setLimitOffset(2, 3);

		// limit == 2
		Assert.assertEquals(table.getRows().size(), 2);

		// offset = 3, so we skip first1-first3 and expect first4
		Assert.assertEquals(table.getRows().get(0).getString("col1"), "val1,4");

		// remove filters again
		table.setLimitOffset(0, 0);
	}

	@Test
	public void testColLimitOffset() throws TableException
	{
		table.setColLimit(2);
		table.setColOffset(1);

		// limit == 1
		int i = 1;
		for (Tuple row : table.getRows())
		{
			Assert.assertEquals(row.getNrColumns(), 2);
			
			//we expect col2 and col3
			Assert.assertEquals(row.getColName(0), "col2");
			Assert.assertEquals(row.getColName(1), "col3");
			
			//we expect
			Assert.assertEquals(row.getObject("col2"), "val2," + i++);
		}

		// remove filters again
		table.setColLimit(0);
		table.setColOffset(0);
	}
}
