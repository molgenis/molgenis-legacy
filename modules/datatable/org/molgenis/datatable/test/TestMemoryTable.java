package org.molgenis.datatable.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.datatable.model.MemoryTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.SimpleTuple;
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
		table = MemoryTableFactory.create();
	}
	
	@Test
	public void test1()
	{
		//check columns
		Assert.assertEquals("firstName", table.getColumns().get(0).getName());
		Assert.assertEquals("lastName", table.getColumns().get(1).getName());
		
		//check rows
		Assert.assertEquals(5, table.getRows().size());
		
		//check iterator
		int i = 1;
		for(Tuple row: table)
		{
			Assert.assertEquals(2, row.getFields().size());
			
			Assert.assertEquals(true, row.getFields().contains("firstName"));
			Assert.assertEquals(true, row.getFields().contains("lastName"));
			
			Assert.assertEquals(row.getObject("firstName"),"first"+i);
			Assert.assertEquals(row.getObject("lastName"),"last"+i);
			
			i=i+1;
		}
	}
}
