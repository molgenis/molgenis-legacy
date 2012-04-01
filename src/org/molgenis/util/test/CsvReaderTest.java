package org.molgenis.util.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;
import org.testng.annotations.Test;

public class CsvReaderTest
{
	@Test
	public void testSimple() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.colnames(), Arrays.asList(new String[]
		{ "col1", "col2" }));

		final List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple tuple : reader)
		{
			result.add(tuple);
		}

		assertEquals(result.get(0).getString("col1"), "val1");
		assertEquals(result.get(0).getString("col2"), "val2");

		assertEquals(result.get(1).getString("col1"), "val3");
		assertEquals(result.get(1).getString("col2"), "val4");

	}

	@Test
	public void testIterator() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple t : reader)
		{
			result.add(t);
		}

		assertEquals(result.get(0).getString("col1"), "val1");
		assertEquals(result.get(0).getString("col2"), "val2");

		assertEquals(result.get(1).getString("col1"), "val3");
		assertEquals(result.get(1).getString("col2"), "val4");
	}

	@Test
	public void testQuotes() throws Exception
	{
		String csv = "t2col1,t2col2\nval1,\"val2 quoted \"\"test\"\" this\"\nval3,val4";

		CsvReader reader = new CsvStringReader(csv);

		assertEquals(reader.colnames(), Arrays.asList(new String[]
		{ "t2col1", "t2col2" }));

		final List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple t : reader)
			result.add(t);

		assertEquals(result.get(0).getString("t2col1"), "val1");
		assertEquals(result.get(0).getString("t2col2"),
				"val2 quoted \"test\" this");

		assertEquals(result.get(1).getString("t2col1"), "val3");
		assertEquals(result.get(1).getString("t2col2"), "val4");
	}

	@Test
	public void testMultilineQuotesEscapingAndEmptyLines() throws Exception
	{
		String csv = "t2col1,t2col2\n" + "val1,\"val2 \n"
				+ "newline and \"\"quotes\"\"\n\n work\"\n" + "val3,val4";

		CsvReader reader = new CsvStringReader(csv);
		
		//test rownames
		List<String> rowNames = reader.rownames();
		assertEquals(2, rowNames.size());
		assertEquals("val1", rowNames.get(0));
		assertEquals("val3", rowNames.get(1));
		
		final List<Tuple> rows = new ArrayList<Tuple>();
		for (Tuple t : reader)
		{
			rows.add(t);
		}

		assertEquals(rows.size(), 2);

		assertEquals(rows.get(0).getString("t2col1"), "val1");
		assertEquals(rows.get(0).getString("t2col2"),
				"val2 \nnewline and \"quotes\"\n\n work");

		assertEquals(rows.get(1).getString("t2col1"), "val3");
		assertEquals(rows.get(1).getString("t2col2"), "val4");
	}
}
