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
	
	@Test
	public void testLimitOffsetString() throws Exception
	{
		//create csv
		String csv = "firstName\tlastName";
		csv += "\nlucky\tluke";
		csv += "\ncalamity\tjane";
		csv += "\njolly\tjumper";
		
		TupleTable table = new CsvTable(csv);
		
		table.setLimitOffset(1,1);
		
		Assert.assertEquals(table.getCount(), 3);
		
		Assert.assertEquals(table.getRows().size(), 1);
		
		Assert.assertEquals(table.getRows().get(0).getString("firstName"), "calamity");
		
	}
	
	@Test
	public void testColLmitOffsetString() throws Exception
	{
		//create csv
		String csv = "firstName\tlastName";
		csv += "\nlucky\tluke";
		csv += "\ncalamity\tjane";
		csv += "\njolly\tjumper";
		
		TupleTable table = new CsvTable(csv);
		
		table.setColLimit(1);
		table.setColOffset(1);
		
		Assert.assertEquals(table.getRows().get(0).size(), 1);
		Assert.assertEquals(table.getRows().get(0).getNrColumns(), 1);
		Assert.assertEquals(table.getRows().get(0).getFieldNames().get(0), "lastName");
		Assert.assertEquals(table.getRows().get(0).getString("lastName"), "luke");
	}
	
	public void helper(TupleTable table) throws TableException
	{
		//verfiy
		Assert.assertEquals(2,table.getColumns().size());
		Assert.assertEquals("firstName",table.getColumns().get(0).getName());
		Assert.assertEquals("lastName",table.getColumns().get(1).getName());
		
		Assert.assertEquals(2,table.getCount());		
		
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
