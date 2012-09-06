package org.molgenis.datatable.test;

import java.io.File;
import java.util.Iterator;

import org.molgenis.datatable.model.PedMapTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPedMapTable
{
	TupleTable table;

	@BeforeMethod
	public void setup() throws Exception
	{
		table = new PedMapTupleTable(new File(TestPedMapTable.class.getResource("test.ped").getFile()), new File(
				TestPedMapTable.class.getResource("test.map").getFile()));
	}

	@Test
	public void testGetCount() throws TableException
	{
		Assert.assertEquals(table.getCount(), 6);
	}

	@Test
	public void testGetColCount() throws TableException
	{
		Assert.assertEquals(table.getColCount(), 8);
	}

	@Test
	public void testWithoutLimit()
	{
		Iterator<Tuple> iter = table.iterator();
		int count = 0;
		while (iter.hasNext())
		{
			count++;
			Tuple tuple = iter.next();

			Assert.assertEquals(tuple.getNrColumns(), 8);
			Assert.assertEquals(tuple.getColName(0), "FamilyID");
			Assert.assertEquals(tuple.getInt(0).intValue(), count);
			Assert.assertEquals(tuple.getColName(7), "snp2");
			if (count == 1)
			{
				Assert.assertEquals(tuple.getString(7), "G T");
			}
			else if (count == 2)
			{
				Assert.assertEquals(tuple.getString(7), "T G");
			}
			else if (count == 3)
			{
				Assert.assertEquals(tuple.getString(7), "G G");
			}
			else if (count == 4)
			{
				Assert.assertEquals(tuple.getString(7), "T T");
			}
			else if (count == 2)
			{
				Assert.assertEquals(tuple.getString(7), "G T");
			}
			else if (count == 2)
			{
				Assert.assertEquals(tuple.getString(7), "T T");
			}
		}

		Assert.assertEquals(count, 6);
	}

	@Test
	public void testRowLimitOffset()
	{
		table.setOffset(2);
		Iterator<Tuple> iter = table.iterator();
		int count = 0;
		while (iter.hasNext())
		{
			count++;
			Tuple tuple = iter.next();
			if (count == 1)
			{
				Assert.assertEquals(tuple.getString("FamilyID"), "3");
			}
		}
		Assert.assertEquals(count, 4);
	}

	@Test
	public void testColLimit()
	{
		table.setColLimit(2);
		Iterator<Tuple> iter = table.iterator();
		Tuple tuple = iter.next();
		Assert.assertEquals(tuple.getNrColumns(), 2);
	}

	@Test
	public void testColOffset()
	{
		table.setColOffset(2);
		Iterator<Tuple> iter = table.iterator();
		Tuple tuple = iter.next();
		Assert.assertEquals(tuple.getNrColumns(), 6);
		Assert.assertEquals(tuple.getColName(0), "FatherID");
	}

	@Test
	public void testColLimitOffset()
	{
		table.setColOffset(1);
		table.setColLimit(4);
		Iterator<Tuple> iter = table.iterator();
		Tuple tuple = iter.next();
		Assert.assertEquals(tuple.getNrColumns(), 4);
		Assert.assertEquals(tuple.getColName(0), "IndividualID");
		Assert.assertEquals(tuple.getColName(3), "Sex");
	}

}