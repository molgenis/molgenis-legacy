package org.molgenis.framework.tupletable.impl;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CsvTableTest
{
	private static String csvString;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		StringBuilder csvBuilder = new StringBuilder();
		csvBuilder.append("firstName").append('\t').append("lastName").append('\n');
		csvBuilder.append("lucky").append('\t').append("luke").append('\n');
		csvBuilder.append("calamity").append('\t').append("jane");
		csvString = csvBuilder.toString();
	}

	@Test
	public void testCsvTableFile() throws Exception
	{
		File file = File.createTempFile("CsvTableFile_file", null);
		try
		{
			FileUtils.write(file, csvString, Charset.forName("UTF-8"));
			CsvTable csvTable = new CsvTable(file);
			testTable(csvTable);
		}
		finally
		{
			file.delete();
		}
	}

	@Test
	public void testCsvTableString() throws Exception
	{
		TupleTable table = new CsvTable(csvString);
		testTable(table);
	}

	@Test
	public void testLimitOffsetString() throws Exception
	{
		// create csv
		String csv = "firstName\tlastName";
		csv += "\nlucky\tluke";
		csv += "\ncalamity\tjane";
		csv += "\njolly\tjumper";

		TupleTable table = new CsvTable(csv);

		table.setLimitOffset(1, 1);

		assertEquals(table.getCount(), 3);

		assertEquals(table.getRows().size(), 1);

		assertEquals(table.getRows().get(0).getString("firstName"), "calamity");
	}

	@Test
	public void testLimitOffsetString_File() throws Exception
	{
		File file = File.createTempFile("CsvTableFile_file", null);
		try
		{
			// create csv
			String csvString = "firstName\tlastName";
			csvString += "\nlucky\tluke";
			csvString += "\ncalamity\tjane";
			csvString += "\njolly\tjumper";

			FileUtils.write(file, csvString, Charset.forName("UTF-8"));
			CsvTable csvTable = new CsvTable(file);
			testTable(csvTable);

			TupleTable table = new CsvTable(file);

			table.setLimitOffset(1, 1);

			assertEquals(table.getCount(), 3);

			assertEquals(table.getRows().size(), 1);

			assertEquals(table.getRows().get(0).getString("firstName"), "calamity");

		}
		finally
		{
			file.delete();
		}

	}

	@Test
	public void testColLimit() throws Exception
	{
		// create csv
		String csv = "firstName\tlastName\tcity";
		csv += "\nlucky\tluke\tdaisy town";
		csv += "\ncalamity\tjane\tdead gulch";
		csv += "\njolly\tjumper\tapache valley";

		TupleTable table = new CsvTable(csv);

		assertEquals(table.getColumns().size(), 3);

		table.setColLimit(2);

		assertEquals(table.getColumns().size(), 2);

		table.reset();

		assertEquals(table.getColumns().size(), 3);

		table.setColOffset(1);

		assertEquals(table.getColumns().size(), 2);

		table.setColLimit(1);

		assertEquals(table.getColumns().get(0).getName(), "lastName");

		assertEquals(table.getRows().get(0).getString("lastName"), "luke");

	}

	private void testTable(TupleTable table) throws TableException
	{
		// verify
		assertEquals(table.getColumns().size(), 2);
		assertEquals(table.getColumns().get(0).getName(), "firstName");
		assertEquals(table.getColumns().get(1).getName(), "lastName");

		assertEquals(2, table.getCount(), 2);

		List<Tuple> rows = table.getRows();
		Tuple row = rows.get(0);
		assertEquals(row.size(), 2);
		assertEquals(row.getString("firstName"), "lucky");
		assertEquals(row.getString("lastName"), "luke");

		row = rows.get(1);
		assertEquals(2, row.size(), 2);
		assertEquals(row.getString("firstName"), "calamity");
		assertEquals(row.getString("lastName"), "jane");
	}
}
