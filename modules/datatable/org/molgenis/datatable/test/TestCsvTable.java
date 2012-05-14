package org.molgenis.datatable.test;

import junit.framework.Assert;

import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.annotations.Test;

public class TestCsvTable
{
	/**
	 * Test CsvTable using a csv string
	 */
	@Test
	public void testStringCsvTable() throws Exception
	{
		//create csv
		String csv = "firstName\tlastName";
		csv += "\nlucky\tluke";
		csv += "\ncalamity\tjane";
		
		//create CsvTable
		TupleTable table =  new CsvTable(csv);
		
		//verfiy
		Assert.assertEquals(2,table.getColumns().size());
		Assert.assertEquals("firstName",table.getColumns().get(0).getName());
		Assert.assertEquals("lastName",table.getColumns().get(1).getName());
		
		Tuple row = table.getRows().get(0);
		Assert.assertEquals(2, row.size());
		Assert.assertEquals("lucky", row.getString("firstName"));
		Assert.assertEquals("luke", row.getString("lastName"));
		
		row = table.getRows().get(1);
		Assert.assertEquals(2, row.size());
		Assert.assertEquals("calamity", row.getString("firstName"));
		Assert.assertEquals("jane", row.getString("lastName"));
		
	}
}
