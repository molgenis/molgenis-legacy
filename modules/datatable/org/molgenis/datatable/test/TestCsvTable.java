package org.molgenis.datatable.test;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.annotations.Test;

public class TestCsvTable
{
	/**
	 * Test CsvTable using a csv file
	 */
	@Test
	public void testFileCsvTable() throws Exception
	{
		//create CsvTable
		helper(new CsvTable(new File(TestCsvTable.class.getResource("test.csv").getFile())));
	}
	
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
		helper(new CsvTable(csv));
	}
	
	public void helper(TupleTable table) throws TableException
	{
		//verfiy
		Assert.assertEquals(2,table.getColumns().size());
		Assert.assertEquals("firstName",table.getColumns().get(0).getName());
		Assert.assertEquals("lastName",table.getColumns().get(1).getName());
		
		Assert.assertEquals(2,table.getRowCount());		
		
		List<Tuple> rows = table.getRows();
		Tuple row = rows.get(0);
		Assert.assertEquals(2, row.size());
		Assert.assertEquals("lucky", row.getString("firstName"));
		Assert.assertEquals("luke", row.getString("lastName"));
		
		row = rows.get(1);
		Assert.assertEquals(2, row.size());
		Assert.assertEquals("calamity", row.getString("firstName"));
		Assert.assertEquals("jane", row.getString("lastName"));
	}
}
