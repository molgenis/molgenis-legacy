package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

public class CsvBufferedReaderMultilineTest
{
	@Test
	public void testSimple() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvBufferedReaderMultiline reader = new CsvBufferedReaderMultiline(new BufferedReader(new StringReader(csv)));
		try
		{
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
		finally
		{
			IOUtils.closeQuietly(reader);
		}

	}

	@Test
	public void testIterator() throws Exception
	{
		String csv = "col1,col2\nval1,val2\nval3,val4";

		CsvBufferedReaderMultiline reader = new CsvBufferedReaderMultiline(new BufferedReader(new StringReader(csv)));
		try
		{
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
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}

	@Test
	public void testQuotes() throws Exception
	{
		String csv = "t2col1,t2col2\nval1,\"val2 quoted \"\"test\"\" this\"\nval3,val4";

		CsvBufferedReaderMultiline reader = new CsvBufferedReaderMultiline(new BufferedReader(new StringReader(csv)));
		try
		{
			assertEquals(reader.colnames(), Arrays.asList(new String[]
			{ "t2col1", "t2col2" }));

			final List<Tuple> result = new ArrayList<Tuple>();
			for (Tuple t : reader)
				result.add(t);

			assertEquals(result.get(0).getString("t2col1"), "val1");
			assertEquals(result.get(0).getString("t2col2"), "val2 quoted \"test\" this");

			assertEquals(result.get(1).getString("t2col1"), "val3");
			assertEquals(result.get(1).getString("t2col2"), "val4");
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}

	@Test
	public void testMultilineQuotesEscapingAndEmptyLines() throws Exception
	{
		String csv = "t2col1,t2col2\n" + "val1,\"val2 \n" + "newline and \"\"quotes\"\"\n\n work\"\n" + "val3,val4";

		CsvBufferedReaderMultiline reader = new CsvBufferedReaderMultiline(new BufferedReader(new StringReader(csv)));
		try
		{
			// test rownames
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
			assertEquals(rows.get(0).getString("t2col2"), "val2 \nnewline and \"quotes\"\n\n work");

			assertEquals(rows.get(1).getString("t2col1"), "val3");
			assertEquals(rows.get(1).getString("t2col2"), "val4");
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}
}