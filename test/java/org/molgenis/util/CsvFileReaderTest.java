package org.molgenis.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

public class CsvFileReaderTest
{
	@Test
	public void testSimple() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "col1,col2\nval1,val2\nval3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);

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
			file0.delete();
		}
	}

	@Test
	public void testIterator() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "col1,col2\nval1,val2\nval3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);

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
			file0.delete();
		}
	}

	@Test
	public void testQuotes() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "t2col1,t2col2\nval1,\"val2 quoted \"\"test\"\" this\"\nval3,val4",
					Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);

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
			file0.delete();
		}
	}

	@Test
	public void testMultilineQuotesEscapingAndEmptyLines() throws Exception
	{
		File file0 = File.createTempFile("CsvFileReaderTest_file0", null);
		try
		{
			FileUtils.write(file0, "t2col1,t2col2\n" + "val1,\"val2 \n" + "newline and \"\"quotes\"\"\n\n work\"\n"
					+ "val3,val4", Charset.forName("UTF-8"));

			CsvFileReader reader = new CsvFileReader(file0);

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
			file0.delete();
		}
	}
}
